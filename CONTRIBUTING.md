# Contributing to VizfoliCraft

First off, thank you for considering contributing to VizfoliCraft! It's people like you that make VizfoliCraft such a great tool.

## 📋 Table of Contents

- [Code of Conduct](#code-of-conduct)
- [How Can I Contribute?](#how-can-i-contribute)
- [Development Setup](#development-setup)
- [Project Structure](#project-structure)
- [Coding Guidelines](#coding-guidelines)
- [Testing Guidelines](#testing-guidelines)
- [Pull Request Process](#pull-request-process)
- [Priority Areas](#priority-areas)

---

## 📜 Code of Conduct

This project and everyone participating in it is governed by a code of conduct. By participating, you are expected to uphold this code. Please report unacceptable behavior to vizahat36@gmail.com.

### Our Standards

- Be respectful and inclusive
- Be patient with newcomers
- Focus on what is best for the community
- Show empathy towards other community members

---

## 🤝 How Can I Contribute?

### Reporting Bugs

Before creating bug reports, please check the issue list as you might find out that you don't need to create one. When you are creating a bug report, please include as many details as possible:

- **Use a clear and descriptive title**
- **Describe the exact steps to reproduce the problem**
- **Provide specific examples**
- **Describe the behavior you observed and what you expected**
- **Include screenshots if possible**
- **Include your environment details** (OS, browser, Java version, Node version, etc.)

### Suggesting Enhancements

Enhancement suggestions are tracked as GitHub issues. When creating an enhancement suggestion, please include:

- **Use a clear and descriptive title**
- **Provide a detailed description of the suggested enhancement**
- **Explain why this enhancement would be useful**
- **List some examples of how it would be used**

### Your First Code Contribution

Unsure where to begin? You can start by looking at issues tagged with:

- `good-first-issue` - Simple issues perfect for newcomers
- `help-wanted` - Issues that need assistance
- `frontend` - Frontend development tasks
- `testing` - Testing tasks

### Pull Requests

- Fill in the required template
- Follow the coding style guidelines
- Include tests when adding new features
- Update documentation as needed
- End all files with a newline

---

## 💻 Development Setup

### Prerequisites

- Java 23 or higher
- Maven 3.6+
- Node.js 18+ and npm 9+
- MongoDB Atlas account (or local MongoDB)
- Git

### Backend Setup

```bash
# Clone the repository
git clone https://github.com/vizahat36/VizfoliCraft.git
cd VizfoliCraft/backend/viztoufolicraft

# Configure application.properties with your MongoDB URI
# Edit src/main/resources/application.properties

# Run the application
mvn spring-boot:run

# Run tests
mvn test

# Build the project
mvn clean package
```

### Frontend Setup

```bash
# Navigate to frontend directory
cd frontend/portfolio-generator

# Install dependencies
npm install

# Start development server
npm start

# Run tests
npm test

# Build for production
npm run build
```

---

## 📁 Project Structure

### Backend (`/backend/viztoufolicraft`)

```
src/main/java/com/yourcompany/portfoliogenerator/
├── model/          # Data models (User, Portfolio, etc.)
├── repository/     # MongoDB repositories
├── service/        # Business logic
├── controller/     # REST API endpoints
├── config/         # Configuration classes
└── admin/          # Admin-specific controllers
```

### Frontend (`/frontend/portfolio-generator`)

```
src/
├── components/     # Reusable components
├── contexts/       # React contexts
├── services/       # API service layer
├── publicSite/     # Public-facing pages
├── adminPanel/     # Admin panel pages
├── templates/      # Portfolio templates
├── theme/          # Theme configuration
└── utils/          # Utility functions
```

---

## 📝 Coding Guidelines

### Java/Spring Boot (Backend)

#### Code Style
- Follow standard Java naming conventions
- Use meaningful variable and method names
- Keep methods small and focused (single responsibility)
- Add JavaDoc comments for public methods
- Use Spring Boot annotations appropriately

#### Example:
```java
/**
 * Creates a new user profile
 * @param request The profile creation request
 * @param userId The ID of the user
 * @return The created user profile
 */
@PostMapping("/profile")
public ResponseEntity<UserProfile> createProfile(
    @RequestBody UserProfileRequest request,
    @AuthenticationPrincipal String userId
) {
    // Implementation
}
```

#### Best Practices
- Use constructor injection over field injection
- Validate input at the controller level
- Handle exceptions properly with global exception handler
- Use DTOs for API requests/responses
- Don't expose internal models directly

### JavaScript/React (Frontend)

#### Code Style
- Use functional components and hooks
- Follow ESLint configuration
- Use meaningful component and variable names
- Keep components small and reusable
- Add PropTypes for component props

#### Example:
```javascript
import React, { useState, useEffect } from 'react';
import PropTypes from 'prop-types';

/**
 * TemplateCard component displays a portfolio template
 */
const TemplateCard = ({ template, onSelect }) => {
  const [isHovered, setIsHovered] = useState(false);

  return (
    <div 
      className="template-card"
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
    >
      {/* Component content */}
    </div>
  );
};

TemplateCard.propTypes = {
  template: PropTypes.object.isRequired,
  onSelect: PropTypes.func.isRequired
};

export default TemplateCard;
```

#### Best Practices
- Use hooks appropriately
- Implement proper error handling
- Keep state local when possible
- Use context for global state
- Implement loading and error states
- Make components responsive by default

### General Guidelines

- **Write meaningful commit messages**
  - Use present tense ("Add feature" not "Added feature")
  - Be descriptive but concise
  - Reference issues when applicable

- **Keep PRs focused**
  - One feature/fix per PR
  - Keep changes small and reviewable
  - Update tests and documentation

- **Comment your code**
  - Explain "why" not "what"
  - Document complex logic
  - Add TODO comments for future improvements

---

## 🧪 Testing Guidelines

### Backend Testing

#### Unit Tests
- Test each service method independently
- Mock dependencies using Mockito
- Aim for 70%+ code coverage
- Test both success and failure scenarios

```java
@Test
public void testCreateProfile_Success() {
    // Given
    UserProfileRequest request = new UserProfileRequest();
    request.setDisplayName("John Doe");
    
    when(userProfileRepository.save(any())).thenReturn(savedProfile);
    
    // When
    UserProfile result = userProfileService.createProfile(request, userId);
    
    // Then
    assertNotNull(result);
    assertEquals("John Doe", result.getDisplayName());
    verify(userProfileRepository).save(any());
}
```

#### Integration Tests
- Test complete workflows
- Use test database
- Test API endpoints end-to-end

### Frontend Testing

#### Component Tests
- Test component rendering
- Test user interactions
- Test edge cases
- Use React Testing Library

```javascript
import { render, screen, fireEvent } from '@testing-library/react';
import TemplateCard from './TemplateCard';

test('renders template card with correct data', () => {
  const template = { name: 'Modern Portfolio', category: 'Professional' };
  render(<TemplateCard template={template} />);
  
  expect(screen.getByText('Modern Portfolio')).toBeInTheDocument();
  expect(screen.getByText('Professional')).toBeInTheDocument();
});
```

#### Service Tests
- Test API calls
- Mock axios responses
- Test error handling

---

## 🔄 Pull Request Process

### Before Submitting

1. **Update your fork**
   ```bash
   git checkout main
   git pull upstream main
   ```

2. **Create a feature branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

3. **Make your changes**
   - Write code
   - Add tests
   - Update documentation

4. **Test your changes**
   ```bash
   # Backend
   mvn test
   
   # Frontend
   npm test
   ```

5. **Commit your changes**
   ```bash
   git add .
   git commit -m "Add feature: your feature description"
   ```

### Submitting the PR

1. **Push to your fork**
   ```bash
   git push origin feature/your-feature-name
   ```

2. **Open a Pull Request**
   - Use a clear and descriptive title
   - Fill in the PR template
   - Link related issues
   - Add screenshots if UI changes

3. **PR Review Process**
   - Maintainers will review your PR
   - Address any requested changes
   - Once approved, your PR will be merged

### PR Checklist

- [ ] Code follows the project's coding guidelines
- [ ] Self-review of code completed
- [ ] Comments added for complex logic
- [ ] Documentation updated (if needed)
- [ ] Tests added/updated
- [ ] All tests passing
- [ ] No console warnings or errors
- [ ] Responsive design tested (for frontend)
- [ ] Commit messages are clear

---

## 🎯 Priority Areas

### High Priority (Need Help!)

1. **Frontend Implementation** 🔴
   - Authentication pages (Login/Signup)
   - User dashboard
   - Template gallery
   - Portfolio customization
   - Admin panel UI
   - Responsive design

2. **Testing** 🔴
   - Backend unit tests
   - Backend integration tests
   - Frontend component tests
   - E2E tests

3. **Deployment** 🟡
   - CI/CD pipeline
   - Docker configuration
   - Deployment documentation

### Medium Priority

4. **Enhancement**
   - UI/UX improvements
   - Performance optimization
   - Accessibility improvements
   - Additional templates

5. **Documentation**
   - Code comments
   - API examples
   - Tutorial videos
   - Deployment guide

---

## 🌟 Recognition

Contributors will be:
- Listed in the README
- Mentioned in release notes
- Given credit in commit messages

---

## 📞 Getting Help

- **Questions?** Open a GitHub issue with the `question` label
- **Need guidance?** Email vizahat36@gmail.com
- **Found a bug?** Open a GitHub issue with the `bug` label
- **Have an idea?** Open a GitHub issue with the `enhancement` label

---

## 📚 Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [React Documentation](https://reactjs.org/)
- [MongoDB Documentation](https://docs.mongodb.com/)
- [Git Workflow](https://www.atlassian.com/git/tutorials/comparing-workflows)

---

Thank you for contributing to VizfoliCraft! 🎉

**Remember**: Every contribution, no matter how small, is valued and appreciated!
