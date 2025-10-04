# Scholar Integration Automation

## Project Purpose
The main goal of this project is to automate the integration of information for the institution’s Top 3 researchers.  
By using the Google Scholar API (via SerpApi), the project aims to reduce the manual effort currently required to gather and process research data.

## Key Functionalities
- Connect to the Google Scholar API through SerpApi.
- Retrieve and parse JSON data from researchers’ profiles.
- Prepare the extracted information for integration into the research database.
- Ensure proper version control and documentation in GitHub.

## Project Relevance
Currently, the collection of researcher information is done manually, which is time-consuming and prone to errors.  
This project improves efficiency and reliability by automating the process, ensuring that data is collected consistently and accurately.

## Repository Structure
- **/docs/** → Contains the technical report on the Google Scholar API.  
- **/src/** → Will contain Java code developed in future stages.  
- **README.md** → Project overview and documentation.  

# Google Scholar Author MVC Project

This Java project implements a simple MVC application to fetch author information from Google Scholar using SerpApi.

## Structure
- **Model:** Represents author data.
- **View:** Displays author search results.
- **Controller:** Handles API requests and updates the view.

## How to run
 Installation
1. Clone the Repository
2. Set Up Database
Option A: MySQL
sqlCREATE DATABASE scholar_db;
Option B: SQLite (No setup required - auto-creates file)
3. Configure Database Connection
Edit DatabaseManager.java:
java// For MySQL
private static final String DB_URL = "jdbc:mysql://localhost:3306/scholar_db";
private static final String DB_USER = "your_username";
private static final String DB_PASSWORD = "your_password";

// For SQLite
private static final String DB_URL = "jdbc:sqlite:scholar_articles.db";
4. Add API Key

Sign up at SerpAPI
Get your free API key (100 searches/month)
Edit ScholarAPIService.java:

javaprivate static final String SERPAPI_KEY = "api_key";
5. Install Dependencies
If using Maven:
bashmvn clean install
Or let IntelliJ automatically download dependencies from pom.xml
6. Run the Application
bashmvn exec:java -Dexec.mainClass="Main"
Or run Main.java directly from IntelliJ

## Notes
- Uses Java 11+ HttpClient for GET requests.
- Uses Jackson for JSON parsing.
- Exceptions are handled for API errors.
