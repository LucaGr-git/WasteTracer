package app;

import java.util.ArrayList;

import javax.naming.spi.DirStateFactory.Result;
import javax.swing.plaf.nimbus.State;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Class for Managing the JDBC Connection to a SQLLite Database.
 * Allows SQL queries to be used with the SQLLite Databse in Java.
 *
 * @author Timothy Wiley, 2023. email: timothy.wiley@rmit.edu.au
 * @author Santha Sumanasekara, 2021. email: santha.sumanasekara@rmit.edu.au
 * @author Halil Ali, 2024. email: halil.ali@rmit.edu.au
 */

public class JDBCConnection {

    // Name of database file (contained in database folder)
    public static final String DATABASE = "jdbc:sqlite:database/foodloss.db";

    /**
     * This creates a JDBC Object so we can keep talking to the database
     */
    public JDBCConnection() {
        System.out.println("Created JDBC Connection Object");
    }

    
    /**
     * Get all of the Countries in the database.
     * @return
     *    Returns an ArrayList of Country objects
     */

    public static ArrayList<Country> getAllCountries() {
        ArrayList<Country> countries = new ArrayList<Country>();

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String query = "SELECT * FROM Country ORDER BY country";
  
            ResultSet results = statement.executeQuery(query);

            while (results.next()) {
                String m49Code     = results.getString("m49code");
                String name  = results.getString("country");

                Country country = new Country(m49Code, name);

                countries.add(country);
            }

            statement.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }

        // Finally we return all of the countries
        return countries;
    }

    // TODO: Add your required methods here

     /**
     * Changes an arraylist of countries into string 
     * @return an arraylist of strings with wach countries name
     */
    public static ArrayList<String> getAllCountriesString(ArrayList<Country> countryList) {
        ArrayList<String> countriesArrayList = new ArrayList<String>();

        for (Country country : countryList){
            countriesArrayList.add(country.getName());
        }

        return countriesArrayList;
    }

    public static ArrayList<Integer> getAllAvailableYears(String country) {
        ArrayList<Integer> availableYears = new ArrayList<Integer>();
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            if (country == null || country.equals("Please Select")) {
                availableYears.add(0);
            }
            else {
                String query = "SELECT DISTINCT year FROM Lossstats WHERE country = '" + 
                            country + 
                            "' ORDER BY year ASC ";

                ResultSet results = statement.executeQuery(query);

                while (results.next()) {
                    int year = results.getInt("Year");
                    availableYears.add(year);
                }   
            }

            
            statement.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
        return availableYears;
    }

    public static ResultSet getST2AResults(
        String country, 
        String startYear,
        String endYear,
        String activity, 
        String causeOfLoss, 
        String foodSupply) {

        Connection connection = null;

        String query = "";
        ResultSet results = null;
        
        try {
            connection = DriverManager.getConnection(DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            if (country == null || country.equals("Please Select")) {}
            else if (Integer.parseInt(startYear) > Integer.parseInt(endYear)) {}
            else {
                
            }

            results = statement.executeQuery(query);

            statement.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
        return results;
    }
}

