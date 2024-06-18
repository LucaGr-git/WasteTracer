package helper;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

import javax.print.DocFlavor.STRING;

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
public class FoodProcessCSV_3NF {

   // MODIFY these to load/store to/from the correct locations
   private static final String DATABASE = "jdbc:sqlite:database/foodloss_3NF.db";
   private static final String FOOD_CSV_FILE = "database/FoodLoss.csv";
   private static final String CPC_CSV_FILE = "database/CPC.csv";    
                                                                   

   //  Food SQL initialization prompt
   private static final String FOOD_SQL_FILE = "database/sql/CPC-initialization-3NF.sql"; 

   public static void main (String[] args) throws IOException{
      // Drops the date, country and class tables then recreates them
      dropTablesAndRecreateTables();
      
      // Loads the counties/regions and the m49codes from the csv file
      loadLocationM49Code();

      // Loads the CPC foods from the database
      loadFoodGroup();
      
      ArrayList<String> subClasses = new ArrayList<String>();
      subClasses = loadFoodClass(subClasses);
      
      subClasses = loadFoodSubClass(subClasses);
      


      loadFoodLossStat(subClasses);
      
      loadActivitiesTakePartIn();
      
   }

   // Drops and recreates empty date, country and class tables
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


   // Loads the COUNTRYREGION and M49CODE table in the sql database with the Countries from the csv file
   public static void loadLocationM49Code() throws IOException {  // JDBC Database Object
      Connection connection = null;
      // Prepared statement used later
      PreparedStatement statement = null;
      // Hashset to check for uniqueness
      HashSet<String> countryHashSet = new HashSet<String>();
      HashSet<String> regionHashSet = new HashSet<String>();


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
            String country = splitline[CountryFields.COUNTRYNAME];
            String region = splitline[CountryFields.REGIONAME];
            String m49Code = splitline[CountryFields.M49CODE];

            region = region.replace("\"", "");

            
            // check that the country does not already exists by trying to insert into a hash set data structure
            if(!countryHashSet.contains(country)){
               //doesn't exists - add it to hash set structure
               countryHashSet.add(country);

               // Create Insert Statement
               // statement as a string
               String countryStatement = " INSERT INTO COUNTRYREGION (location, parentlocation) VALUES (?, ?)";
               // statement object created
               statement = connection.prepareStatement(countryStatement);

               // Sets ? to proper responses
               statement.setString(1, country);
               

               // Query is printed and executed
               System.out.println("Execute: \n" + statement.toString());
               statement.executeUpdate();


               // Create Insert Statement
               // statement as a string
               String m49CodeStatement = " INSERT INTO M49CODE (M49CODE, COUNTRY) VALUES (?, ?)";
               // statement object created
               statement = connection.prepareStatement(m49CodeStatement);

               // Sets ? to proper responses
               statement.setString(1, m49Code);
               statement.setString(2, country);

               // Query is printed and executed
               System.out.println("Execute: \n" + statement.toString());
               statement.executeUpdate();
            }
         
            // check that the country does not already exists by trying to insert into a hash set data structure
            if(!regionHashSet.contains(region)){
               //doesn't exists - add it to hash set structure
               regionHashSet.add(region);

               // Create Insert Statement
               // statement as a string
               String regionStatement = " INSERT INTO COUNTRYREGION (location, parentlocation) VALUES (?, ?)";
               // statement object created
               statement = connection.prepareStatement(regionStatement);

               // Sets ? to proper responses
               statement.setString(1, region);
               statement.setString(2, country);

               // Query is printed and executed
               System.out.println("Execute: \n" + statement.toString());
               statement.executeUpdate();


            }
         
         }  
            
         
         System.out.println("\ninserted all locations and m49codes \npress enter to continue");
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
   
   // Loads the FOODGROUP table in the sql database with the group cpccode + description from the cpc csv file
   private static void loadFoodGroup() {
   // JDBC Database Object
   Connection connection = null;
   // Prepared statement used later
   PreparedStatement statement = null;
   // Hashset to check for uniqueness
   HashSet<String> groupHashSet = new HashSet<String>();


   BufferedReader reader = null; // reader for the csv file
   String line; // Each individual line from the csv file

   // We need some error handling.
   try {
      // Open A CSV File to process, one line at a time
      reader = new BufferedReader(new FileReader(CPC_CSV_FILE));

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
         String group = splitline[ClassFields.GROUP_SECTION_DIVISION];
         String desc = splitline[ClassFields.DESCRIPTION];

         // Group is trimmed for leading/trailing whitespaces
         group = group.trim();
         // If group is null or a section/division then it is skipped
         if (group.equals("")){continue;}
         if (group.contains("Section")){continue;}
         if (group.contains("Division")){continue;}


         // Any quotation marks are removed
         desc = desc.replace("\"", "");   
         
         // check that the group does not already exists by trying to insert into a hash set data structure
         if(!groupHashSet.contains(group)){
            //doesn't exists - add it to hash set structure
            groupHashSet.add(group);

            // Create Insert Statement
            // statement as a string
            String myStatement = " INSERT INTO FOODGROUP (groupcode, groupdescriptor) VALUES (?, ?)";
            // statement object created
            statement = connection.prepareStatement(myStatement);

            // Sets ? to proper responses
            statement.setString(1, group);
            statement.setString(2, desc);

            // Query is printed and executed
            System.out.println("Execute: \n" + statement.toString());
            statement.executeUpdate();
         }
      }  
         
      
      System.out.println("\ninserted all food groups \npress enter to continue");
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

   // Loads the FOODCLASS table in the sql database with the class cpccode + description from the cpc csv file
   // These are also loaded into the FOODSUBCLASS table with subclass = 0
   private static ArrayList<String> loadFoodClass(ArrayList<String> subClasses) {
      // JDBC Database Object
      Connection connection = null;
      // Prepared statement used later
      PreparedStatement statement = null;

      BufferedReader reader = null; // reader for the csv file
      String line; // Each individual line from the csv file

      // We need some error handling.
      try {
         // Open A CSV File to process, one line at a time
         reader = new BufferedReader(new FileReader(CPC_CSV_FILE));

         // Read the first line of "headings"
         String header = reader.readLine();
         System.out.println("Heading row" + header + "\n");

         // Setup JDBC
         // Connect to JDBC database
         connection = DriverManager.getConnection(DATABASE);
         int i = subClasses.size();
         //read CSV file line by line, stop if not more lines
         while ((line = reader.readLine())!=null) {

            // split the line up by commas (ignoring commas within quoted fields)
            String[] splitline = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);

            // Get all of the columns in order
            String totalCPCcode = splitline[ClassFields.CLASS];
            String foodClass = splitline[ClassFields.CLASS];
            String desc = splitline[ClassFields.DESCRIPTION];

            // If class is null
            if (foodClass.equals("")){continue;}
            // food class and food group is drawn from cpc code
            String group = foodClass.substring(0, 3);
            foodClass = foodClass.substring(3);

            // Any quotation marks are removed from descripton
            desc = desc.replace("\"", "");   

            
      
            // Create Insert Statement to add food class
            
            // statement as a string
            String myStatement = " INSERT INTO FOODCLASS (classcode, groupcode, classdescriptor) VALUES (?, ?, ?)";
            // statement object created
            statement = connection.prepareStatement(myStatement);

            // Sets ? to proper responses
            statement.setString(1, foodClass);
            statement.setString(2, group);
            statement.setString(3, desc);

            // Query is printed and executed
            System.out.println("Execute: \n" + statement.toString());
            statement.executeUpdate();


            // statement as a string
            String foodStatement = " INSERT INTO FOOD (FOODID, FOODCODE, DESCRIPTOR, CLASSCODE, GROUPCODE) VALUES (?, ?, ?, ?, ?)";
            // statement object created
            statement = connection.prepareStatement(foodStatement);

            // Sets ? to proper responses
            statement.setString(1, "" + i);
         
            statement.setString(3, desc);
            statement.setString(4, foodClass);
            statement.setString(5, group);


            // Query is printed and executed
            System.out.println("Execute: \n" + statement.toString());
            statement.executeUpdate();

            ++i;
            subClasses.add(totalCPCcode);
            
            
         }
         System.out.println("\ninserted all food classes and a subsequent entry in foodsubclass \npress enter to continue");
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

      return subClasses;
   };

   // Loads the FOOD table in the sql database with the subclass cpccode + description from the cpc csv file
   private static ArrayList<String> loadFoodSubClass(ArrayList<String> subclasses) {
      // JDBC Database Object
      Connection connection = null;
      // Prepared statement used later
      PreparedStatement statement = null;

      BufferedReader reader = null; // reader for the csv file
      String line; // Each individual line from the csv file

      
      

      // We need some error handling.
      try {
         // Open A CSV File to process, one line at a time
         reader = new BufferedReader(new FileReader(CPC_CSV_FILE));

         // Read the first line of "headings"
         String header = reader.readLine();
         System.out.println("Heading row" + header + "\n");

         // Setup JDBC
         // Connect to JDBC database
         connection = DriverManager.getConnection(DATABASE);

         int i = subclasses.size();

         //read CSV file line by line, stop if not more lines
         while ((line = reader.readLine())!=null) {

            // split the line up by commas (ignoring commas within quoted fields)
            String[] splitline = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);

            // Get all of the columns in order
            String totalCPCcode = splitline[ClassFields.SUBCLASS].trim();
            String foodSubClass = splitline[ClassFields.SUBCLASS];
            String desc = splitline[ClassFields.DESCRIPTION];

            // If subclass is null the row is skipped
            if (foodSubClass.equals("")){continue;}

            // food subclass, class and food group is drawn from cpc code
            String group = foodSubClass.substring(0, 3);
            String foodClass = foodSubClass.substring(3,4);
            foodSubClass = foodSubClass.substring(4);

            // Any quotation marks are removed from descripton
            desc = desc.replace("\"", "");   

            // If subclass is 0 (AKA it is the main class) the row is skipped
            if (foodSubClass.equals("0")){continue;}
            
            // Create Insert Statement to add food class
            
            // statement as a string
            String myStatement = " INSERT INTO FOOD (FOODID, FOODCODE, DESCRIPTOR, CLASSCODE, GROUPCODE) VALUES (?, ?, ?, ?, ?)";
            // statement object created
            statement = connection.prepareStatement(myStatement);

            // Sets ? to proper responses
            statement.setString(1, ""+ i);
            statement.setString(2, foodSubClass);
            statement.setString(3, desc);
            statement.setString(4, foodClass);
            statement.setString(5, group);


            // Query is printed and executed
            System.out.println("Execute: \n" + statement.toString());
            statement.executeUpdate();

            ++i;
            subclasses.add(totalCPCcode);
            
         }
         System.out.println("\ninserted all food sub classes \npress enter to continue");
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

      return subclasses;
   };

   // Loads the LOSSSTAT table in the sql database with all fields from the csv
   private static void loadFoodLossStat(ArrayList<String> subClasses) {
      // JDBC Database Object
      Connection connection = null;
      // Prepared statement used later
      PreparedStatement statement = null;

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
         int rowid = 0;
         //read CSV file line by line, stop if not more lines
         while ((line = reader.readLine())!=null) {

            // split the line up by commas (ignoring commas within quoted fields)
            String[] splitline = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);

            
            // Get all of the columns in order
            String location = splitline[CountryFields.REGIONAME];
            if (location.equals("")){ location = splitline[CountryFields.COUNTRYNAME];}
            

            String cpccode = splitline[CountryFields.CPCCODE];

            

            int year = Integer.parseInt(splitline[CountryFields.YEAR]);
            double losspercent = Double.parseDouble(splitline[CountryFields.LOSSPERCENT]);
            String supplystage = splitline[CountryFields.SUPPLYSTAGE];
            String losscause = splitline[CountryFields.LOSSCAUSE];
            ++rowid;

            // Region is formatted and checked for null values
            location = location.replace("\"", "");



            // supply stage is formatted and checked for null
            supplystage = supplystage.replace("\"", "");
            if (supplystage.equals("")){supplystage = "NULL";}

            // loss cause is formatted and checked for null
            losscause = losscause.replace("\"", "");
            if (losscause.equals("")){losscause = "NULL";}
            

            
            // Create Insert Statement to add loss statistics
            
            // statement as a string
            String myStatement = " INSERT INTO LOSSSTAT (ROW_ID, LOSSPERCENTAGE, FOODSUPPLY, YEAR, LOCATION, FOODID, CAUSEOFLOSS) VALUES (?, ?, ?, ?, ?, ?, ?)";
            // statement object created
            statement = connection.prepareStatement(myStatement);

            // Sets ? to proper responses
            statement.setInt(1, rowid);
            statement.setDouble(2, losspercent);
            statement.setString(3, supplystage);
            statement.setInt(4, year);
            statement.setString(5, location);
            statement.setInt(6, subClasses.indexOf(cpccode));
            statement.setString(7, losscause);
            


            // Query is printed and executed
            System.out.println("Execute: \n" + statement.toString());
            statement.executeUpdate();
               
            
         }
         System.out.println("\ninserted all loss statistics \npress enter to continue");
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
   }

   // Loads the TAKESPARTIN table in the sql database with the activities from the csv file keeping in mind comma seperation 
   public static void loadActivitiesTakePartIn() throws IOException
   {
      // JDBC Database Object
      Connection connection = null;

      BufferedReader reader = null; // reader for the csv file
      String line; // Each individual line from the csv file

      // Prepared statement used later
      PreparedStatement statement = null;

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
         int rowid = 0;
         //read CSV file line by line, stop if not more lines
         while ((line = reader.readLine())!=null) {

            // split the line up by commas (ignoring commas within quoted fields)
            String[] splitline = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);

            // Get all of the columns in order
            String activities = splitline[CountryFields.ACTIVITY];
            ++rowid;

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

               // Create Insert Statement

               String myStatement = " INSERT INTO TAKESPARTIN (STATSROWID, ACTIVITY) VALUES (?, ?)";
               // statement object created
               statement = connection.prepareStatement(myStatement);

               // Sets ? to proper responses
               statement.setInt(1, rowid);
               statement.setString(2, eachActivity);
               


               // Query is printed and executed
               System.out.println("Execute: \n" + statement.toString());
               statement.executeUpdate(); 
               
                        
            }
               
               
            
         }
         System.out.println("\ninserted all activities that have taken part in a loss stat \npress enter to continue");
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
   

}