package com.yourcompany.portfoliogenerator.service;

import com.yourcompany.portfoliogenerator.model.User;
import com.yourcompany.portfoliogenerator.model.UserProfile;
import com.yourcompany.portfoliogenerator.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PortfolioDataGenerationService {
    
    private final UserProfileRepository userProfileRepository;
    private final GitHubIntegrationService gitHubIntegrationService;
    private final LinkedInIntegrationService linkedInIntegrationService;
    
    public Mono<PortfolioData> generatePortfolioData(User user) {
        return Mono.fromCallable(() -> userProfileRepository.findByUser(user).orElse(null))
                .flatMap(profile -> {
                    if (profile == null) {
                        return Mono.just(createBasicPortfolioData(user));
                    }
                    
                    return generateEnhancedPortfolioData(user, profile);
                })
                .doOnSuccess(data -> log.info("Generated portfolio data for user {}", user.getUsername()))
                .doOnError(error -> log.error("Error generating portfolio data for user {}: {}", user.getUsername(), error.getMessage()));
    }
    
    public Mono<PortfolioData> generatePortfolioDataWithSync(User user, boolean syncGitHub, boolean syncLinkedIn) {
        return Mono.fromCallable(() -> userProfileRepository.findByUser(user).orElse(null))
                .flatMap(profile -> {
                    Mono<PortfolioData> portfolioMono = Mono.just(createBasicPortfolioData(user));
                    
                    if (profile != null) {
                        portfolioMono = generateEnhancedPortfolioData(user, profile);
                    }
                    
                    if (syncGitHub && profile != null && profile.getGithubUrl() != null) {
                        String githubUsername = extractGitHubUsername(profile.getGithubUrl());
                        if (githubUsername != null) {
                            portfolioMono = portfolioMono.flatMap(portfolioData -> 
                                    enhanceWithGitHubData(portfolioData, githubUsername));
                        }
                    }
                    
                    return portfolioMono;
                });
    }
    
    private Mono<PortfolioData> generateEnhancedPortfolioData(User user, UserProfile profile) {
        return Mono.fromCallable(() -> {
            PortfolioData.PersonalInfo personalInfo = PortfolioData.PersonalInfo.builder()
                    .name(profile.getDisplayName() != null ? profile.getDisplayName() : user.getFirstName() + " " + user.getLastName())
                    .title(profile.getProfession())
                    .bio(profile.getBio())
                    .location(profile.getLocation())
                    .email(user.getEmail())
                    .phone(profile.getPhoneNumber())
                    .profileImage(profile.getProfileImageUrl())
                    .resume(profile.getResumeUrl())
                    .availableForHire(profile.getAvailableForHire() != null ? profile.getAvailableForHire() : false)
                    .build();
            
            PortfolioData.SocialLinks socialLinks = PortfolioData.SocialLinks.builder()
                    .linkedin(profile.getLinkedinUrl())
                    .github(profile.getGithubUrl())
                    .twitter(profile.getTwitterUrl())
                    .website(profile.getWebsite())
                    .build();
            
            List<String> skills = parseSkills(profile.getSkills());
            List<PortfolioData.WorkExperience> experience = parseExperience(profile.getExperience());
            List<PortfolioData.Education> education = parseEducation(profile.getEducation());
            
            PortfolioData.Stats stats = PortfolioData.Stats.builder()
                    .yearsOfExperience(profile.getYearsOfExperience())
                    .totalProjects(0)
                    .totalStars(0)
                    .totalForks(0)
                    .totalRepositories(0)
                    .githubFollowers(0)
                    .topLanguages(skills.stream().limit(5).collect(Collectors.toList()))
                    .build();
            
            return PortfolioData.builder()
                    .personalInfo(personalInfo)
                    .projects(new ArrayList<>())
                    .skills(skills)
                    .experience(experience)
                    .education(education)
                    .socialLinks(socialLinks)
                    .stats(stats)
                    .lastUpdated(LocalDateTime.now())
                    .build();
        });
    }
    
    private PortfolioData createBasicPortfolioData(User user) {
        PortfolioData.PersonalInfo personalInfo = PortfolioData.PersonalInfo.builder()
                .name(user.getFirstName() + " " + user.getLastName())
                .email(user.getEmail())
                .availableForHire(false)
                .build();
        
        return PortfolioData.builder()
                .personalInfo(personalInfo)
                .projects(new ArrayList<>())
                .skills(new ArrayList<>())
                .experience(new ArrayList<>())
                .education(new ArrayList<>())
                .socialLinks(PortfolioData.SocialLinks.builder().build())
                .stats(PortfolioData.Stats.builder()
                        .totalProjects(0)
                        .totalStars(0)
                        .totalForks(0)
                        .totalRepositories(0)
                        .githubFollowers(0)
                        .topLanguages(new ArrayList<>())
                        .yearsOfExperience(0)
                        .build())
                .lastUpdated(LocalDateTime.now())
                .build();
    }
    
    private Mono<PortfolioData> enhanceWithGitHubData(PortfolioData portfolioData, String githubUsername) {
        return gitHubIntegrationService.fetchGitHubProfile(githubUsername)
                .map(githubProfile -> {
                    // Update personal info with GitHub data
                    if (portfolioData.getPersonalInfo().getProfileImage() == null && githubProfile.getAvatarUrl() != null) {
                        portfolioData.getPersonalInfo().setProfileImage(githubProfile.getAvatarUrl());
                    }
                    
                    if (portfolioData.getPersonalInfo().getBio() == null && githubProfile.getBio() != null) {
                        portfolioData.getPersonalInfo().setBio(githubProfile.getBio());
                    }
                    
                    // Convert repositories to projects
                    List<PortfolioData.Project> projects = githubProfile.getRepositories().stream()
                            .filter(repo -> !repo.isFork()) // Exclude forked repositories
                            .sorted((a, b) -> Integer.compare(b.getStargazersCount(), a.getStargazersCount()))
                            .limit(10) // Top 10 repositories
                            .map(repo -> PortfolioData.Project.builder()
                                    .name(repo.getName())
                                    .description(repo.getDescription())
                                    .githubUrl(repo.getHtmlUrl())
                                    .technologies(repo.getLanguage() != null ? List.of(repo.getLanguage()) : new ArrayList<>())
                                    .stars(repo.getStargazersCount())
                                    .forks(repo.getForksCount())
                                    .lastUpdated(repo.getUpdatedAt())
                                    .status("active")
                                    .build())
                            .collect(Collectors.toList());
                    
                    portfolioData.setProjects(projects);
                    
                    // Extract and update skills/languages
                    Map<String, Integer> languageCount = githubProfile.getRepositories().stream()
                            .filter(repo -> repo.getLanguage() != null)
                            .collect(Collectors.groupingBy(
                                    GitHubProfile.Repository::getLanguage,
                                    Collectors.summingInt(repo -> 1)
                            ));
                    
                    List<String> topLanguages = languageCount.entrySet().stream()
                            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                            .limit(10)
                            .map(Map.Entry::getKey)
                            .collect(Collectors.toList());
                    
                    // Merge with existing skills
                    Set<String> allSkills = new HashSet<>(portfolioData.getSkills());
                    allSkills.addAll(topLanguages);
                    portfolioData.setSkills(new ArrayList<>(allSkills));
                    
                    // Update stats
                    PortfolioData.Stats updatedStats = PortfolioData.Stats.builder()
                            .totalProjects(projects.size())
                            .totalStars(projects.stream().mapToInt(PortfolioData.Project::getStars).sum())
                            .totalForks(projects.stream().mapToInt(PortfolioData.Project::getForks).sum())
                            .totalRepositories(githubProfile.getPublicRepos())
                            .githubFollowers(githubProfile.getFollowers())
                            .topLanguages(topLanguages.stream().limit(5).collect(Collectors.toList()))
                            .yearsOfExperience(portfolioData.getStats().getYearsOfExperience())
                            .build();
                    
                    portfolioData.setStats(updatedStats);
                    portfolioData.setLastUpdated(LocalDateTime.now());
                    
                    return portfolioData;
                })
                .onErrorReturn(portfolioData); // Return original data on error
    }
    
    private List<String> parseSkills(String skillsString) {
        if (skillsString == null || skillsString.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        return Arrays.stream(skillsString.split("[,;]"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
    
    private List<PortfolioData.WorkExperience> parseExperience(String experienceString) {
        if (experienceString == null || experienceString.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        // Simple parsing - in production, you might want more sophisticated parsing
        String[] experiences = experienceString.split("\n\n");
        return Arrays.stream(experiences)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(exp -> {
                    // Extract title and company from "Title at Company" format
                    String[] parts = exp.split(" at ");
                    String title = parts.length > 0 ? parts[0].trim() : "";
                    String company = parts.length > 1 ? parts[1].split("\\(")[0].trim() : "";
                    
                    return PortfolioData.WorkExperience.builder()
                            .title(title)
                            .company(company)
                            .description(exp)
                            .current(exp.toLowerCase().contains("present"))
                            .build();
                })
                .collect(Collectors.toList());
    }
    
    private List<PortfolioData.Education> parseEducation(String educationString) {
        if (educationString == null || educationString.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String[] educations = educationString.split("\n\n");
        return Arrays.stream(educations)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(edu -> {
                    // Extract degree and institution from "Degree from Institution" format
                    String[] parts = edu.split(" from ");
                    String degree = parts.length > 0 ? parts[0].trim() : "";
                    String institution = parts.length > 1 ? parts[1].split("\\(")[0].trim() : "";
                    
                    return PortfolioData.Education.builder()
                            .degree(degree)
                            .institution(institution)
                            .description(edu)
                            .build();
                })
                .collect(Collectors.toList());
    }
    
    private String extractGitHubUsername(String githubUrl) {
        if (githubUrl == null || !githubUrl.contains("github.com/")) {
            return null;
        }
        
        try {
            String[] parts = githubUrl.split("github.com/");
            if (parts.length > 1) {
                String username = parts[1].split("/")[0].split("\\?")[0];
                return username.isEmpty() ? null : username;
            }
        } catch (Exception e) {
            log.error("Error extracting username from GitHub URL: {}", e.getMessage());
        }
        
        return null;
    }
}
