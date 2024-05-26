package helper;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.HashMap;
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
   private static final int END_YEAR = 2025;

   //  Food SQL initialization prompt
   private static final String FOOD_SQL_FILE = "database/sql/CPC-initialization.sql"; 

   public static void main (String[] args) throws IOException{
      
      // Drops the date, country and class tables then recreates them
      // This only needs to be done once (unless your tables need to be updated and recreated)
      dropTablesAndRecreateTables();


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
         s.close();


      } catch (Exception e) {
         e.printStackTrace();
      }

      
   }

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