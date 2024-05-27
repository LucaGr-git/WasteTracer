package helper;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import org.eclipse.jetty.util.IO;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
// Importing IOException for reading and writing to files
import java.io.IOException;


/**
 * Stand-alone Java file for processing the database CSV files.
 * <p>
 * You can run this file using the "Run" or "Debug" options
 * from within VSCode. This won't conflict with the web server.
 * <p>
 * This program opens a CSV file from the Food Loss data set
 * and uses JDBC to load up data into the database.
 * <p>
 * To use this program you will need to change:
 * 1. The input file location
 * 2. The output file location
 * <p>
 * This assumes that the CSV files are in the **database** folder.
 * <p>
 * WARNING: This code may take quite a while to run as there could be a lot
 * of SQL insert statments and queries to run!
 *
 * @author Timothy Wiley, 2023. email: timothy.wiley@rmit.edu.au
 * @author Halil Ali, 2024. email: halil.ali@rmit.edu.au
 */
public class FoodProcessCSV_Altered {

   // MODIFY these to load/store to/from the correct locations
   private static final String DATABASE = "jdbc:sqlite:database/foodloss.db";
   private static final String FOOD_CSV_FILE = "database/FoodLoss.csv";
   private static final String CPC_CSV_FILE = "database/CPC.csv";
   private static final int RECORD_PERCENT = 27411/100;
   private static final int START_YEAR = 1966;
   private static final int END_YEAR = 2025;          // TODO Decide whether year should be added to the database as a table
                                                      // TODO Add personas to database (THROUGH sql init File ???)

   //  Food SQL initialization prompt
   private static final String FOOD_SQL_FILE = "database/sql/CPC-initialization.sql"; 

   public static void main (String[] args) throws IOException{
      
      // Drops the date, country and class tables then recreates them
      dropTablesAndRecreateTables();

      
      // Load up the CauseOfLoss table
      loadCauseOfLoss();

      // Load up the Activity table
      loadActivity();

      // Load up the FOODSUPPLY table
      loadFoodSupplyStage();


      // Load up the Date table
      // This only needs to be done once
      // Comment this out after runnning it the first time
      loadYears();


      // loads all the 'Class' level codes into class table
      // note it does not load any sub class OR sub sub classes (or divisions, sections, groups)
      // need to update this to handle this (based on your design)
      // Comment this out after runnning it the first time
       loadCpcClass();


      // Load up the Country table
      // This only needs to be done once
      // Comment this out after runnning it the first time
      // Create a copy of this and update to load data into your other tables
       loadCountries();


      // read foodloss csv and check for matching country code and class codes in created tables
      // verifies the loaded data
      // you can also copy this to insert records after a lookup into your other tables
      checkCountryAndClassCodesMatch();
      

      
      return;
   }

   // Drops and recreates empty date, country and class tables
   // Add additional create statements to create the rest of your tables
   public static void dropTablesAndRecreateTables() throws IOException{
      // JDBC Database Object
      Connection connection = null;
      Scanner s = new Scanner(System.in);
      String response = null;

      System.out.println("\nWARNING: existing tables will be dropped and recreated\nAre you sure? (y/n)");
      response = s.nextLine();
      while(!response.equalsIgnoreCase("y") && !response.equalsIgnoreCase("n"))
      {
         response = s.nextLine();
      }
      if(response.equalsIgnoreCase("n")){
         System.out.println("aborting");
         System.out.println("Comment out 'dropTablesAndRecreateTables();' to avoid deleting tables and run again");
         System.exit(0);
      }
      // Like JDBCConnection, we need some error handling.
      

      try {
         connection = DriverManager.getConnection(DATABASE);

         // Prepare a new SQL Query & Set a timeout
         Statement statement = connection.createStatement();

         // Create Insert Statement
         String query = null;
         
         // Setting up file reading from FOOD_SQL_FILE
         FileInputStream fileByteStream = new FileInputStream(FOOD_SQL_FILE);
         Scanner sqlScanner = new Scanner(fileByteStream);
         
         // Runs Sql file prompt by prompt 
         while (sqlScanner.hasNext()){

            // SQL line is created
            query = sqlScanner.nextLine();
            if (query.equals("")){continue;}

            // continues adding to the query until the end of the statement is reached
            while(!query.contains(";")){
               query += sqlScanner.nextLine();
            }
            
            // Executes and prints query
            System.out.println("Executing: \n" + query);
            statement.execute(query);
         }


         System.out.println("\ndropped and recreated tables\npress enter to continue");
         System.in.read();
         
         // Closing streams/scanners
         fileByteStream.close();
         sqlScanner.close();



      } catch (Exception e) {
         e.printStackTrace();
      }

      
   }

   // Loads the ACTIVITY table in the sql database with the  activities from the csv file keeping in mind comma seperation 
   public static void loadActivity() throws IOException
   {
      // JDBC Database Object
      Connection connection = null;
      // Hash set is used as a unique list as there is only 1 column
      HashSet<String> activitiesHashSet = new HashSet<String>();

      BufferedReader reader = null; // reader for the csv file
      String line; // Each individual line from the csv file

      // We need some error handling.
      try {
         // Open A CSV File to process, one line at a time
         reader = new BufferedReader(new FileReader(FOOD_CSV_FILE));

         // Read the first line of "headings"
         String header = reader.readLine();
         System.out.println("Heading row" + header + "\n");

         // Setup JDBC
         // Connect to JDBC database
         connection = DriverManager.getConnection(DATABASE);

         //read CSV file line by line, stop if not more lines
         while ((line = reader.readLine())!=null) {

            // split the line up by commas (ignoring commas within quoted fields)
            String[] splitline = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);

            // Get all of the columns in order
            String activities = splitline[CountryFields.ACTIVITY];

            // If activities is null then it is skipped
            if (activities.equals("")){continue;}

            // Any quotation markes are removed
            activities = activities.replace("\"", "");

            // split the line up by commas (ignoring commas within quoted fields)
            String[] splitActivities = activities.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);

            // Each comma seperated activity is iterated through
            for (String eachActivity : splitActivities){

               // Trim leading + trailing spaces
               eachActivity = eachActivity.trim();
               // Replace sinqle quotes by the proper sql syntax convention ('')
               eachActivity = eachActivity.replace("'", "''");

               // check that the activity does not already exists by trying to insert into a hashmap data structure
               if(!activitiesHashSet.contains(eachActivity)){
                  //doesn't exists - insert it
                  activitiesHashSet.add(eachActivity);
                  // Create Insert Statement

                  // statement as a string
                  String myStatement = " INSERT INTO ACTIVITY (activity) VALUES ('" + eachActivity + "')";
                  // statement object created
                  Statement statement = connection.createStatement();
                  // execute and print
                  System.out.println("Executing: \n" + myStatement);
                  statement.execute(myStatement);  
                  
               }        
            }
               
               
            
         }
         System.out.println("\ninserted all activities that are taken part in a loss stat \npress enter to continue");
         System.in.read();

      } catch (Exception e) { // catch any errors and print them 
         e.printStackTrace();
      }
      finally { // afterwards try and close the reader and print any errors
         if(reader!=null) {
            try{
            reader.close();
            } catch (Exception e) {
               e.printStackTrace();
            }
         }
      }
   };
   
   // Loads the CAUSEOFLOSS table in the sql database with the cause of losses from the csv file
   public static void loadCauseOfLoss() throws IOException {
      // JDBC Database Object
      Connection connection = null;
      // Hash set is used as a unique list as there is only 1 column
      HashSet<String> lossCauseHashSet = new HashSet<String>(); 

      BufferedReader reader = null; // reader for the csv file
      
      String line; // Each individual line from the csv file

      // We need some error handling.
      try {
         // Open A CSV File to process, one line at a time
         reader = new BufferedReader(new FileReader(FOOD_CSV_FILE));

         // Read the first line of "headings"
         String header = reader.readLine();
         System.out.println("Heading row" + header + "\n");

         // Setup JDBC
         // Connect to JDBC database
         connection = DriverManager.getConnection(DATABASE);

         //read CSV file line by line, stop if not more lines
         while ((line = reader.readLine())!=null) {

            // split the line up by commas (ignoring commas within quoted fields)
            String[] splitline = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);

            // Get the losscause column
            String lossCause = splitline[CountryFields.LOSSCAUSE];

            // Skips if there is no cause of loss
            if (lossCause.equals("")){continue;}
            
            // Replaces ' (single quote) with '' (double quote) to follow sql syntax
            lossCause = lossCause.replace("'", "''");

            // Removes any " quotation marks
            lossCause = lossCause.replace("\"", "");

            // check that the loss cause does not already exists by trying to insert into a hash set data structure
            if(!lossCauseHashSet.contains(lossCause)){
               //doesn't exists - add it to hash set structure
               lossCauseHashSet.add(lossCause);
               // Create Insert Statement
            
               

               // statement as a string
               String myStatement = " INSERT INTO CAUSEOFLOSS (causeofloss) VALUES ('" + lossCause + "')";
               // statement object created
               Statement statement = connection.createStatement();
               // execute and print
               System.out.println("Executing: \n" + myStatement);
               statement.execute(myStatement); 
               }
               
         }

         System.out.println("\ninserted all causes of loss\npress enter to continue");
         System.in.read(); 

      } catch (Exception e) { // catch any errors and print them 
         e.printStackTrace();
      }
      finally { // afterwards try and close the reader and print any errors
         if(reader!=null) {
            try{
            reader.close();
            } catch (Exception e) {
               e.printStackTrace();
            }
         }
      }
   };

   // Loads the FOODSUPPLY table in the sql database with the food supply stage from the csv file
   public static void loadFoodSupplyStage() throws IOException {
         // JDBC Database Object
         Connection connection = null;
         // Hash set is used as a unique list as there is only 1 column
         HashSet<String> foodSupplyHashSet = new HashSet<String>(); 
   
         BufferedReader reader = null; // reader for the csv file
         
         String line; // Each individual line from the csv file
   
         // We need some error handling.
         try {
            // Open A CSV File to process, one line at a time
            reader = new BufferedReader(new FileReader(FOOD_CSV_FILE));
   
            // Read the first line of "headings"
            String header = reader.readLine();
            System.out.println("Heading row" + header + "\n");
   
            // Setup JDBC
            // Connect to JDBC database
            connection = DriverManager.getConnection(DATABASE);
   
            //read CSV file line by line, stop if not more lines
            while ((line = reader.readLine())!=null) {
   
               // split the line up by commas (ignoring commas within quoted fields)
               String[] splitline = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
   
               // Get the food supply stage column
               String supplyStage = splitline[CountryFields.SUPPLYSTAGE];
   
               // Skips if there is no food supply stage
               if (supplyStage.equals("")){continue;}
               
               // Replaces ' (single quote) with '' (double quote) to follow sql syntax
               supplyStage = supplyStage.replace("'", "''");
   
               // Removes any " quotation marks
               supplyStage = supplyStage.replace("\"", "");
   
               // check that the supply stage does not already exists by trying to insert into a hash set data structure
               if(!foodSupplyHashSet.contains(supplyStage)){
                  //doesn't exists - add it to hash set structure
                  foodSupplyHashSet.add(supplyStage);
                  // Create Insert Statement
               
                  
   
                  // statement as a string
                  String myStatement = " INSERT INTO FOODSUPPLY (foodsupply) VALUES ('" + supplyStage + "')";
                  // statement object created
                  Statement statement = connection.createStatement();
                  // execute and print
                  System.out.println("Executing: \n" + myStatement);
                  statement.execute(myStatement); 
                  }
                  
            }
   
            System.out.println("\ninserted all food supply stages\npress enter to continue");
            System.in.read(); 
   
         } catch (Exception e) { // catch any errors and print them 
            e.printStackTrace();
         }
         finally { // afterwards try and close the reader and print any errors
            if(reader!=null) {
               try{
               reader.close();
               } catch (Exception e) {
                  e.printStackTrace();
               }
            }
         }
      };
   
   
   
   public static void loadYears() {
      {}};

   // loads all the 'Class' level codes into class table
   // note it does not load any sub class OR sub sub classes (or divisions, sections, groups)
   // need to update this to handle this (based on your design)
   public static void loadCpcClass() {
      {}}
   
   public static void loadCountries() {
      {}}


   public static void checkCountryAndClassCodesMatch() {
      {}}

}