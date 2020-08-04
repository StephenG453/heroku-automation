# Heroku Backend Automation 
This is a quick project developed for backend automation of heroku that also contains a jenkinsFile for CI/CD integration.    

## Improvements
1. Integrate Jenkins plugins (like Test Results Analyzer) for a simple reporting layout within your CI/CD pipeline 

### Project Setup in IntelliJ:
1. Use IntelliJ 2018.X
2. Download JDK 1.8
3. Add JDK 1.8 to IntelliJ (Project Structure -> Platform Settings -> SDKs -> + 1.8)
6. Project Structure -> Project Settings -> Project -> Project SDK: choose 1.8 -> Project language level: choose 8
7. Project Structure -> Project Settings -> Modules -> Language Level: choose 8 ->
8. Use Maven window (on the right) to build: clean -> build