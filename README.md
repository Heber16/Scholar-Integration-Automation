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
1. Clone the repository.
2. Add your SerpApi API key in AuthorController.java
3. Run Main.java
4. Change `author_id` to the target author ID.

## Notes
- Uses Java 11+ HttpClient for GET requests.
- Uses Jackson for JSON parsing.
- Exceptions are handled for API errors.
