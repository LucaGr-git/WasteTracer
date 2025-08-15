# 🌍 WasteTracer — Food Loss & Waste Data Web App (Javalin + SQLite)

**WasteTracer** is a lightweight Java web application built with **Javalin** and **SQLite** that allows users to **search** and **filter** through country-level food loss and waste data.  
The app allows for waste data to be searched by 4 main categories (each with their own filtering options)
- Searching data by Food Group
- Searching data by Country
- Search Food Groups by similarity in statistics with other Food Groups
- Search Countries by similarity in statistics with other Countries

The app aggregates this data by updating a Third Normal Form SQL database by parsing csv data, and performing modular queries built directly with Java. 

## Group Members
| Name            | 
|---------------------|
| Luca Grosso           |
| Joe Czerniecki           | 

## 🚀 Key Features

| Category            | Feature                                                                 |
|---------------------|-------------------------------------------------------------------------|
| Search           | Search food loss by **country**                                         |
| Filter           | Filter results by **year**, **commodity type**, or **category**         |
| Data View        | View country data on the **Index page** (Level 1 – Subtask A)           |
| Static Pages     | Mission Statement, About Page (from starter code)                       |
| 🗃Helper Program   | Import CSV files into SQLite using JDBC. this is how the database is updated with newer statistics                                 |

---


## ⚙️ How to Run

### ▶️ Run the Main Web Server

```bash
# 1. Open the project in Visual Studio Code
# 2. Allow VSCode to detect and import the Maven project (pom.xml)

# 3. Open:
src/main/java/app/App.java

# 4. Click “Run” (or right-click → Run Java)
# 5. Navigate to:
http://localhost:7000
# or whatever is linked in the javalin terminal message
```

### 🧰 Run the Optional Helper Program (CSV → SQLite Import)

```bash
# Open:
src/main/java/helper/FoodProcessCSV.java

# Click “Run” above the main() method
# (By default this will DROP and recreate tables,
#  comment out dropTablesAndRecreateTables() if you want to preserve them)
```

---

## 🌐 Technologies Used

| Category         | Tools / Libraries                           |
|------------------|---------------------------------------------|
| Web Framework    | Javalin (Java)                               |
| Database         | SQLite (+ JDBCConnection class)              |
| Helper Program   | FoodProcessCSV (Java → JDBC CSV import)      |
| Styling          | CSS (common.css)                             |
| Build Tool       | Maven (pom.xml)                              |

---

## 🖼️ Screenshot Placeholders

| Page                          | Screenshot |
|------------------------------|------------|
| **Gome Page**      | <img width="1902" height="913" alt="Screenshot 2025-08-16 at 12 37 03 am" src="https://github.com/user-attachments/assets/59637eae-ab08-4c70-9cd0-b39835912858" />|
| **Country Search Page**    | <img width="1885" height="914" alt="Screenshot 2025-08-16 at 12 37 29 am" src="https://github.com/user-attachments/assets/9e7ee4d1-27c8-4c7c-a745-ac678aedf808" />|
| **Food Group Similarity Search Page**             | <img width="1899" height="908" alt="Screenshot 2025-08-16 at 12 37 44 am" src="https://github.com/user-attachments/assets/6e9bd450-6251-4772-b9f8-cf64684b9cf4" />|


---

## 👥 Acknowledgements

Built off the templates designed by: 

- Dr. Halil Ali — RMIT University  
- Dr. Timothy Wiley — RMIT University  
- Prof. Santha Sumanasekara — RMIT University  

