package com.yourcompany.portfoliogenerator.service;

import lombok.Data;

import java.util.List;

@Data
public class LinkedInProfile {
    private String id;
    private String firstName;
    private String lastName;
    private String headline;
    private String summary;
    private String profilePictureUrl;
    private String industry;
    private Location location;
    private List<Position> positions;
    private List<Education> educations;
    private List<String> skills;
    
    @Data
    public static class Location {
        private String name;
        private String country;
    }
    
    @Data
    public static class Position {
        private String title;
        private String companyName;
        private String description;
        private String startDate;
        private String endDate;
        private boolean isCurrent;
        private Location location;
    }
    
    @Data
    public static class Education {
        private String schoolName;
        private String degreeName;
        private String fieldOfStudy;
        private String startDate;
        private String endDate;
        private String description;
    }
}
