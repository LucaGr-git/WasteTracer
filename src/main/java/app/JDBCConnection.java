package app;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.naming.spi.DirStateFactory.Result;
import javax.swing.plaf.nimbus.State;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;

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

    public static ArrayList<String> getAllCountries() {
        ArrayList<String> countries = new ArrayList<String>();

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String query = "SELECT DISTINCT * FROM Country ORDER BY country";
  
            ResultSet results = statement.executeQuery(query);

            while (results.next()) {
                countries.add(results.getString("country"));
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
        return countries;
    }

    public static ArrayList<String> getAllFoodGroups() {
        ArrayList<String> foodGroups = new ArrayList<>();

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String query = "SELECT DISTINCT groupDescriptor FROM FoodGroup fg JOIN LossStat l ON fg.groupCode = l.groupCode ORDER BY groupDescriptor";
  
            ResultSet results = statement.executeQuery(query);

            while (results.next()) {
                foodGroups.add(results.getString("groupDescriptor"));
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
        return foodGroups;
    }

    public static ArrayList<String> getAllAvailableYearsCountry(String country) {
        ArrayList<String> availableYears = new ArrayList<String>();
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            if (country == null || country.equals("Please Select")) {
                availableYears.add("");
                return availableYears;
            }
            else {
                String query = "SELECT DISTINCT year FROM Lossstat ";
                query += "WHERE country = \"" + country + "\" ORDER BY year ASC";

                ResultSet results = statement.executeQuery(query);

                while (results.next()) {
                    String year = String.valueOf(results.getInt("Year"));
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

    public static ArrayList<String> getAllAvailableYearsFoodGroup(String foodGroup) {
        ArrayList<String> availableYears = new ArrayList<String>();
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            if (foodGroup == null || foodGroup.equals("Please Select")) {
                return availableYears;
            }
            else {
                String query = "SELECT DISTINCT year FROM LossStat l JOIN FoodGroup fg ON fg.groupCode = l.groupCode ";
                query += "WHERE groupDescriptor = \"" + foodGroup + "\" ORDER BY l.groupCode, year";


                ResultSet results = statement.executeQuery(query);

                while (results.next()) {
                    String year = String.valueOf(results.getInt("Year"));
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

    public static String getST2AQuery(
        String country, 
        String startYear,
        String endYear,
        String activity, 
        String causeOfLoss, 
        String foodSupply,
        String sortByPercent) {
            
        String query = "";
        
        if (country == null || country.equals("Please Select")) {return null;}
        if (startYear == null || endYear == null) {return null;}
        if (startYear.equals("selected")) {return null;}
        if (!startYear.equals("") && (Integer.parseInt(startYear) > Integer.parseInt(endYear))) {return null;}
        
        for (int i = 0; i < 2; ++i) {
            if (i == 0) {
                query += "SELECT DISTINCT *, IFNULL(avg0, avg1) - IFNULL(avg1, avg0) AS difference FROM (";
            }

            query += "SELECT year AS year" + i + ", descriptor AS descriptor" + i + ", AVG(lossPercentage) AS avg" + i + " ";

            if (activity != null) {
                query += ", IFNULL(activity, 'N/A') AS activity" + i + " ";
            }
            if (foodSupply != null) {
                query += ", IFNULL(foodSupply, 'N/A') AS foodSupply" + i + " ";
            }
            if (causeOfLoss != null) {
                query += ", IFNULL(causeOfLoss, 'N/A') AS causeOfLoss" + i + " ";
            }

            query += "FROM LossStat ";
            if (activity != null) {
                query += "LEFT JOIN TakesPartIn ON row_id = statsRowId ";
            }
            query += "WHERE country = \"" + country + "\" ";
            
            if (i == 0) {
                query += "AND year = " + startYear + " ";
            }
            else {
                query += "AND year = " + endYear + " ";
            }

            query += "GROUP BY descriptor" + i + " ";

            if (activity != null) {
                query += ", activity" + i + " ";
            }
            if (foodSupply != null) {
                query += ", foodSupply" + i + " ";
            }
            if (causeOfLoss != null) {
                query += ", causeOfLoss" + i + " ";
            }

            if (i == 0) {
                query += ") AS min FULL OUTER JOIN (";
            }
            else {
                query += ") AS max ON descriptor0 = descriptor1 ";
                
                if (activity != null) {
                    query += "AND activity0 = activity1 ";
                }
                if (foodSupply != null) {
                    query += "AND foodSupply0 = foodSupply1 ";
                }
                if (causeOfLoss != null) {
                    query += "AND causeOfLoss0 = causeOfLoss1 ";
                }
                if (sortByPercent == null) {
                    query += "ORDER BY IFNULL(descriptor0, descriptor1)"; 
                }
                else {
                    if (sortByPercent.equals("sort-by-descending")) {
                        query += "ORDER BY difference ASC";
                    }
                    if (sortByPercent.equals("sort-by-ascending")) {
                        query += "ORDER BY difference DESC";
                    }
                }
            }
        }
        System.out.println(query);

        return query;
    }

    public static String getST2AQueryAllYears(
        String country, 
        String startYear,
        String endYear,
        String activity, 
        String causeOfLoss, 
        String foodSupply,
        String sortByPercent) {

            String html = "<tbody>";

            if (country == null || country.equals("Please Select")) {return null;}

            String query = "SELECT DISTINCT year, descriptor, AVG(losspercentage) AS avg ";
            
            if (activity != null) {
                query += ", IFNULL(activity, 'N/A') AS activity ";
            }
            if (causeOfLoss != null) {
                query += ", IFNULL(causeOfLoss, 'N/A') AS causeOfLoss ";
            }
            if (foodSupply != null) {
                query += ", IFNULL(foodSupply, 'N/A') AS foodSupply ";
            }

            query += "FROM LossStat ";

            if (activity != null) {
                query += "LEFT JOIN TakesPartIn ON row_id = statsRowId ";
            }

            query += "WHERE country = \"" + country + "\" "; 
            query += "AND year >= " + startYear + " AND year <= " + endYear + " "; 
            query += "GROUP BY descriptor, year ";

            if (activity != null) {
                query += ", activity ";
            }
            if (causeOfLoss != null) {
                query += ", causeOfLoss ";
            }
            if (foodSupply != null) {
                query += ", foodSupply ";
            }

            if (sortByPercent == null) {
                query += "ORDER BY descriptor";
            }
            else if (sortByPercent.equals("sort-by-descending")) {
                System.out.print("WORKING");
                query += "ORDER BY avg DESC";
            }
            else {
                query += "ORDER BY avg ASC";
            }

            html += ST2ATableHTMLAllYears(query, activity, causeOfLoss, foodSupply);
            System.out.println(query);
                
            html += "</tbody>";
            return html;
    }

    public static String ST2ATableHTML(
        String query,
        String activty,
        String causeOfLoss,
        String foodSupply) {

        String html = "";
        Connection connection = null;

            try {
                connection = DriverManager.getConnection(DATABASE);

                Statement statement = connection.createStatement();
                statement.setQueryTimeout(30);

                ResultSet results = statement.executeQuery(query);

                html += "<tbody>";
                while (results.next()) {
                    html += "<tr>";

                    String commodity = (
                    results.getString("descriptor0") != null) ? 
                    results.getString("descriptor0") : 
                    results.getString("descriptor1");

                    String startYearData = (
                    results.getFloat("avg0") != 0.0) ?
                    (String.format("%.3f", results.getFloat("avg0")) + "%") :
                    "N/A";

                    String endYearData = (
                    results.getFloat("avg1") != 0.0) ?
                    (String.format("%.3f", results.getFloat("avg1")) + "%") :
                    "N/A";

                    String difference;
                    if (startYearData.equals("N/A") || endYearData.equals("N/A")) {
                        difference = "N/A";
                    }
                    else {
                        difference = String.format("%.3f", results.getFloat("difference") * -1) + "%";
                        if (difference.equals("-0.000%")) {
                            difference = "0%";
                        }
                    }

                    html += "<td>" + commodity + "</td>";
                    html += "<td>" + startYearData + "</td>";
                    html += "<td>" + endYearData + "</td>";
                    html += "<td>" + difference + "</td>";

                    String activityString = "", causeOfLossString = "", foodSupplyString = "";

                    if (activty != null) {
                        activityString = (
                        results.getString("activity0") != null) ?
                        results.getString("activity0") :
                        results.getString("activity1");


                        if (activityString.equals("NULL")) {
                            activityString = "N/A";
                        }

                        html += "<td>" + activityString + "</td>";
                    }

                    if (causeOfLoss != null) {
                        causeOfLossString = (
                        results.getString("causeOfLoss0") != null) ?
                        results.getString("causeOfLoss0") :
                        results.getString("causeOfLoss1");

                        if (causeOfLossString.equals("NULL")) {
                            causeOfLossString = "N/A";
                        }

                        html += "<td>" + causeOfLossString + "</td>";
                    }

                    if (foodSupply != null) {
                        foodSupplyString = (
                        results.getString("foodSupply0") != null) ?
                        results.getString("foodSupply0") :
                        results.getString("foodSupply1");

                        if (foodSupplyString.equals("NULL")) {
                            foodSupplyString = "N/A";
                        }

                        html += "<td>" + foodSupplyString + "</td>";
                    }

                    html += "</tr>";
                }
            html += "</tbody>";
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
        return html;
    }
    
    public static String ST2ATableHTMLAllYears(
        String query,
        String activty,
        String causeOfLoss,
        String foodSupply ) {

        String html = "";
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            ResultSet results = statement.executeQuery(query);

            while (results.next()) {
                html += "<tr>";

                String Data = (
                results.getFloat("avg") != 0.0) ?
                (String.format("%.3f", results.getFloat("avg")) + "%") :
                "N/A";

                html += "<td>" + results.getString("descriptor") + "</td>";
                html += "<td>" + results.getInt("year") + "</td>";
                html += "<td>" + Data + "</td>";

                String activityString = "", causeOfLossString = "", foodSupplyString = "";

                if (activty != null) {
                    activityString = (
                    results.getString("activity") == null || 
                    results.getString("activity").equals("NULL")) ?
                    "N/A" :
                    results.getString("activity") ;

                    html += "<td>" + activityString + "</td>";
                }

                if (causeOfLoss != null) {
                    causeOfLossString = (
                    results.getString("causeOfLoss") == null ||
                    results.getString("causeOfLoss").equals("NULL")) ?
                    "N/A" :
                    results.getString("causeOfLoss");

                    html += "<td>" + causeOfLossString + "</td>";
                }

                if (foodSupply != null) {
                    foodSupplyString = (
                    results.getString("foodSupply") == null ||
                    results.getString("foodSupply").equals("NULL")) ?
                    "N/A" :
                    results.getString("foodSupply");

                    html += "<td>" + foodSupplyString + "</td>";
                }

                html += "</tr>";
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
        System.out.println(html);
        return html;
    }

    public static String getST2BQuery (
        String foodGroup, 
        String startYear,
        String endYear,
        String activity, 
        String causeOfLoss, 
        String foodSupply,
        String sortByPercent) {
        
        String foodGroupCode = getGroupCode(foodGroup);
            
        String query = "";
        
        if (foodGroup == null || foodGroup.equals("Please Select")) {return null;}
        if (startYear == null || endYear == null) {return null;}
        if (startYear.equals("selected")) {return null;}
        if (!startYear.equals("") && (Integer.parseInt(startYear) > Integer.parseInt(endYear))) {return null;}
        
        for (int i = 0; i < 2; ++i) {
            if (i == 0) {
                query += "SELECT DISTINCT *, IFNULL(avg0, avg1) - IFNULL(avg1, avg0) AS difference FROM (";
            }

            query += "SELECT year AS year" + i + ", descriptor AS descriptor" + i + ", AVG(lossPercentage) AS avg" + i + " ";

            if (activity != null) {
                query += ", IFNULL(activity, 'N/A') AS activity" + i + " ";
            }
            if (foodSupply != null) {
                query += ", IFNULL(foodSupply, 'N/A') AS foodSupply" + i + " ";
            }
            if (causeOfLoss != null) {
                query += ", IFNULL(causeOfLoss, 'N/A') AS causeOfLoss" + i + " ";
            }

            query += "FROM LossStat ";
            if (activity != null) {
                query += "LEFT JOIN TakesPartIn ON row_id = statsRowId ";
            }
            query += "WHERE groupcode = \"" + foodGroupCode + "\" ";
            
            if (i == 0) {
                query += "AND year = " + startYear + " ";
            }
            else {
                query += "AND year = " + endYear + " ";
            }

            query += "GROUP BY descriptor" + i + " ";

            if (activity != null) {
                query += ", activity" + i + " ";
            }
            if (foodSupply != null) {
                query += ", foodSupply" + i + " ";
            }
            if (causeOfLoss != null) {
                query += ", causeOfLoss" + i + " ";
            }

            if (i == 0) {
                query += ") AS min FULL OUTER JOIN (";
            }
            else {
                query += ") AS max ON descriptor0 = descriptor1 ";
                
                if (activity != null) {
                    query += "AND activity0 = activity1 ";
                }
                if (foodSupply != null) {
                    query += "AND foodSupply0 = foodSupply1 ";
                }
                if (causeOfLoss != null) {
                    query += "AND causeOfLoss0 = causeOfLoss1 ";
                }
                if (sortByPercent == null) {
                    query += "ORDER BY IFNULL(descriptor0, descriptor1)"; 
                }
                else {
                    if (sortByPercent.equals("sort-by-descending")) {
                        query += "ORDER BY difference ASC";
                    }
                    if (sortByPercent.equals("sort-by-ascending")) {
                        query += "ORDER BY difference DESC";
                    }
                }
            }
        }
        System.out.println(query);

        return query;
    }

    private static String getGroupCode(String groupDescriptor){
        String matchingGroupCode = "";

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String query = "SELECT * FROM FOODGROUP";
  
            ResultSet results = statement.executeQuery(query);

            String groupCode;
            String groupDesc;
            while (results.next()) {
                groupCode = results.getString("groupcode");
                groupDesc = results.getString("groupdescriptor");
                if (groupDesc.equals(groupDescriptor)){
                    matchingGroupCode = groupCode;
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
        return matchingGroupCode;
    }
}