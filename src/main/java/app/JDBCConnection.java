package app;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.naming.spi.DirStateFactory.Result;
import javax.swing.plaf.nimbus.State;

import org.eclipse.jetty.server.LowResourceMonitor.ConnectorsThreadPoolLowResourceCheck;

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

        /*Methods to populate select inputs*/
    public static ArrayList<String> getAllCountries() {
        ArrayList<String> countries = new ArrayList<String>();


        Connection connection = null;


        try {
            connection = DriverManager.getConnection(DATABASE);


            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);


            String query = "SELECT DISTINCT * FROM CountryRegion WHERE PARENTLOCATION is NULL ORDER BY location";
            ResultSet results = statement.executeQuery(query);


            while (results.next()) {
                countries.add(results.getString("location"));
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


            String query = "SELECT DISTINCT groupDescriptor\n" + //
                                "    FROM LOSSSTAT LS\n" + //
                                "    JOIN FOOD F ON F.FOODID = LS.FOODID\n" + //
                                "    JOIN FOODGROUP FG ON FG.GROUPCODE = F.GROUPCODE\n" + //
                                " ORDER BY groupDescriptor; ";


            System.out.println(query);
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

    public static ArrayList<String> getAllCountriesRegions() {
        ArrayList<String> countriesAndRegions = new ArrayList<>();
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String query = "SELECT DISTINCT COUNTRYREGION.location, parentLocation FROM COUNTRYREGION JOIN LOSSSTAT ON COUNTRYREGION.location = LOSSSTAT.location";

            ResultSet results = statement.executeQuery(query);

            while (results.next()) {
                String countryOrRegion = results.getString("location");

                countriesAndRegions.add(countryOrRegion);
            }

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
        return countriesAndRegions;
    }

    public static ArrayList<String> getAllCommodities() {
        ArrayList<String> commodities = new ArrayList<>();
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            // TODO why is this different from the original results?
            String query = "SELECT DISTINCT F.DESCRIPTOR FROM LOSSSTAT LS JOIN FOOD F ON F.FOODID = LS.FOODID ORDER BY DESCRIPTOR;";

            ResultSet results = statement.executeQuery(query);

            while (results.next()) {
                commodities.add(results.getString("descriptor"));
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
        return commodities;
    }   

    public static ArrayList<String> getAllAvailableYearsCountryRegion(String area) {
        ArrayList<String> availableYears = new ArrayList<String>();
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            if (area == null || area.equals("Please Select")) {
                availableYears.add("");
                return availableYears;
            }
            else {
                String query = "SELECT DISTINCT year FROM LossStat WHERE location = \"" + area + "\" ORDER BY year";

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
                String query = "SELECT DISTINCT year FROM LossStat l \n" + //
                                        "\n" + //
                                        "JOIN FOOD f ON f.FOODID = l.FOODID\n" + //
                                        "JOIN FoodGroup fg ON fg.groupCode = f.groupCode \n" + //
                                        "\n" + //
                                        "WHERE groupDescriptor = '" + foodGroup + "' ORDER BY f.groupCode, year;\n" + //
                                        "";


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

/*ST2A/ST2B Methods*/
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

            query += "JOIN  COUNTRYREGION ON LOSSSTAT.LOCATION = COUNTRYREGION.LOCATION  ";
            query += "JOIN  FOOD ON FOOD.FOODID = LOSSSTAT.FOODID  ";

            if (activity != null) {
                query += "LEFT JOIN TakesPartIn ON row_id = statsRowId ";
            }
            query += "WHERE IFNULL(COUNTRYREGION.Parentlocation, COUNTRYREGION.location) = \"" + country + "\" ";
            
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

            query += "JOIN  COUNTRYREGION ON LOSSSTAT.LOCATION = COUNTRYREGION.LOCATION  ";
            query += "JOIN  FOOD ON FOOD.FOODID = LOSSSTAT.FOODID  ";

            if (activity != null) {
                query += "LEFT JOIN TakesPartIn ON row_id = statsRowId ";
            }

            query += "WHERE IFNULL(COUNTRYREGION.Parentlocation, COUNTRYREGION.location) = \"" + country + "\" "; 
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

            System.out.println(query);

            html += ST2ABTableHTMLAllYears(query, activity, causeOfLoss, foodSupply);
                
            html += "</tbody>";
            return html;
    }

    public static String ST2ABTableHTML(
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
                        difference = String.format("%+.3f", results.getFloat("difference") * -1) + "%";
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
    
    public static String ST2ABTableHTMLAllYears(
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
            
            query += "JOIN FOOD f ON f.FOODID = LOSSSTAT.FOODID  ";
            query += "JOIN FOODGROUP fg ON fg.GROUPCODE = f.GROUPCODE  ";


            if (activity != null) {
                query += "LEFT JOIN TakesPartIn ON row_id = statsRowId ";
            }
            query += "WHERE groupdescriptor = \"" + foodGroup + "\" ";
            
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
        return query;
    }
    
    public static String getST2BQueryAllYears(
        String foodGroup, 
        String startYear,
        String endYear,
        String activity, 
        String causeOfLoss, 
        String foodSupply,
        String sortByPercent) {

            String html = "<tbody>";

            if (foodGroup == null || foodGroup.equals("Please Select")) {return null;}

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

            query += "JOIN FOOD f ON f.FOODID = LOSSSTAT.FOODID  ";
            query += "JOIN FOODGROUP fg ON fg.GROUPCODE = f.GROUPCODE  ";

            if (activity != null) {
                query += "LEFT JOIN TakesPartIn ON row_id = statsRowId ";
            }

            query += "WHERE groupdescriptor = \"" + foodGroup + "\" "; 
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

            System.out.println(query);

            html += ST2ABTableHTMLAllYears(query, activity, causeOfLoss, foodSupply);
                
            html += "</tbody>";
            return html;
    }
    
/*ST3B/ST3A Methods*/
    public static String getST3BGroupFromCommodity(String commodity) {
        String group = "";

        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String query = "SELECT DISTINCT groupCode FROM Food WHERE descriptor = \"" + commodity + "\"";

            ResultSet results = statement.executeQuery(query);

            while (results.next()) {
                group = results.getString("groupCode");
            }

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
        return group;
    }

    public static String getST3BavgLossTable(
        String foodGroupCPC,
        String selectedAmount ) {
        String avgSimilarityTable = "";

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String query = "SELECT *, avg0 - avg1 as difference FROM ( ";
            query += "        SELECT groupDescriptor, AVG(lossPercentage) AS avg0, ";
            query += "           (";
            query += "           SELECT AVG(LossPercentage)";
            query += "           FROM LossStat";
            query += "           JOIN FOOD ON FOOD.FOODID = LOSSSTAT.FOODID";
            query += "         WHERE groupCode = '" + foodGroupCPC + "'";
            query += "           GROUP BY groupCode";
            query += "           ) AS avg1";
            query += "    FROM LossStat";
            query += "    JOIN FOOD ON FOOD.FOODID = LOSSSTAT.FOODID";
            query += "    JOIN FoodGroup ON FoodGroup.groupCode = FOOD.groupCode";
            query += "     GROUP BY FoodGroup.groupCode";
            query += "     ORDER BY ABS(avg0 - avg1), FOOD.groupCode != \"" + foodGroupCPC + "\"); ";


            System.out.println(query);

            ResultSet results = statement.executeQuery(query);

            int i = 0;
            if (Integer.parseInt(selectedAmount) != 0) {
                avgSimilarityTable += "<th>Average Loss %</th>";
                avgSimilarityTable += "<th>Difference</th>";
                avgSimilarityTable += "</thead>";

                while (results.next()) {
                    if (i == 0) {
                        avgSimilarityTable += "<tr>";
                        avgSimilarityTable += "<td><b>Group of Choice</b></td>";
                        avgSimilarityTable += "<td>" + results.getString("groupDescriptor") + "</td>";
                        avgSimilarityTable += "<td>" + String.format("%.3f", (results.getFloat("avg0"))) + "%</td>";
                        avgSimilarityTable += "<td>" + String.format("%+.3f", (results.getFloat("difference"))) + "%</td>";
                        avgSimilarityTable += "</tr>"; 
                    }
                    else {
                        avgSimilarityTable += "<tr>";
                        avgSimilarityTable += "<td><b>" + i + ")</b></td>";
                        avgSimilarityTable += "<td>" + results.getString("groupDescriptor") + "</td>";
                        avgSimilarityTable += "<td>" + String.format("%.3f", (results.getFloat("avg0"))) + "%</td>";
                        avgSimilarityTable += "<td>" + String.format("%+.3f", (results.getFloat("difference")))+ "%</td>";
                        avgSimilarityTable += "</tr>";
                    }
                    ++i;
                    if (i > Integer.parseInt(selectedAmount)) {
                        break;
                    }
                }
            }
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
        return avgSimilarityTable;
    }

    public static String getST3BHighestLowestPercentTable(
        String foodGroupCPC,
        String selectedAmount,
        String similarityChoice) {
        String highLowPercentTable = "";
        
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String highOrLow = (similarityChoice.equals("highest-percent")) ?
                                "MAX" :
                                "MIN";


            String query = "SELECT " + highOrLow + "(lossPercentage) max0, descriptor, groupDescriptor, ( ";
            query += "            SELECT " + highOrLow + "(lossPercentage) ";
            query += "            FROM LossStat";
            query += "            JOIN FOOD ON LossStat.FOODID = FOOD.FOODID";
            query += "            WHERE groupCode = '" + foodGroupCPC + "') AS max1, " + highOrLow + "(lossPercentage) - (";
            query += "                                 SELECT " + highOrLow + "(lossPercentage) ";
            query += "                                 FROM LossStat";
            query += "                                 JOIN FOOD ON LossStat.FOODID = FOOD.FOODID";
            query += "                                 WHERE groupCode = \"" + foodGroupCPC + "\") AS difference";
            query += "  FROM LossStat";
            query += "      JOIN FOOD ON LossStat.FOODID = FOOD.FOODID";
            query += "      JOIN FoodGroup ON FoodGroup.groupCode = FOOD.groupCode";
            query += " GROUP BY FoodGroup.groupCode";   
            query += " ORDER BY FOOD.groupCode != '" + foodGroupCPC + "', ABS(max0 - max1); ";

            System.out.println(query);

            ResultSet results = statement.executeQuery(query);

            int i = 0;
            if (Integer.parseInt(selectedAmount) != 0) {
                if (highOrLow.equals("MAX")) {
                    highLowPercentTable += "<th>Highest Loss Commodity</th>";
                }
                else {
                    highLowPercentTable += "<th>Lowest Loss Commodity</th>";
                }
                highLowPercentTable += "<th>Loss %</th>";
                highLowPercentTable += "<th>Difference</th>";
                highLowPercentTable += "</thead>";

                while (results.next()) {
                    if (i == 0) {
                        highLowPercentTable += "<tr>";
                        highLowPercentTable += "<td><b>Group of Choice</b></td>";
                        highLowPercentTable += "<td>" + results.getString("groupDescriptor") + "</td>";
                        highLowPercentTable += "<td>" + results.getString("descriptor") + "</td>";
                        highLowPercentTable += "<td>" + String.format("%.3f", (results.getFloat("max0"))) + "%</td>";
                        highLowPercentTable += "<td>" + String.format("%+.3f", (results.getFloat("difference")))+ "%</td>";
                        highLowPercentTable += "</tr>"; 
                    }
                    else {
                        highLowPercentTable += "<tr>";
                        highLowPercentTable += "<td><b>" + i + ")</b></td>";
                        highLowPercentTable += "<td>" + results.getString("groupDescriptor") + "</td>";
                        highLowPercentTable += "<td>" + results.getString("descriptor") + "</td>";
                        highLowPercentTable += "<td>" + String.format("%.3f", (results.getFloat("max0"))) + "%</td>";
                        highLowPercentTable += "<td>" + String.format("%+.3f", (results.getFloat("difference"))) + "%</td>";
                        highLowPercentTable += "</tr>";
                    }
                    ++i;
                    if (i > Integer.parseInt(selectedAmount)) {
                        break;
                    }
                }
            }
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
        return highLowPercentTable;
    }

    public static String getST3ACommonFoodTable(
        String countryRegion,
        int startYear,
        int endYear,
        boolean showFoods,
        String selectedAmount, 
        boolean ascendingSearch) {
        String highLowPercentTable = "";

        String ascOrDesc;
        if (ascendingSearch){ascOrDesc = "ASC";}
        else{ascOrDesc = "DESC";}
        
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            
            String query = "SELECT l2.countryregion, COUNT(l2.countryregion) as numShared, group_concat(l1.DESCRIPTOR, ' | ') as sharedCommodities FROM ( ";
            query += "    SELECT DISTINCT  DESCRIPTOR, IFNULL(PARENTLOCATION, l.LOCATION) as countryregion ";
            query += "    FROM LOSSSTAT  as l ";
            query += "    JOIN COUNTRYREGION ON l.LOCATION = COUNTRYREGION.LOCATION ";
            query += "    JOIN FOOD ON l.FOODID = FOOD.FOODID ";
            query += "    WHERE countryregion = 'Australia' AND YEAR >= " + startYear +" AND YEAR <= " + endYear + ") as l1 ";
            query += "    JOIN (SELECT DISTINCT DESCRIPTOR, IFNULL(PARENTLOCATION, LOSSSTAT.LOCATION) as countryregion ";
            query += "        FROM LOSSSTAT ";
            query += "        JOIN COUNTRYREGION ON LOSSSTAT.LOCATION = COUNTRYREGION.LOCATION ";
            query += "        JOIN FOOD ON LOSSSTAT.FOODID = FOOD.FOODID ";
            query += "        WHERE YEAR >= " + startYear + " AND YEAR <= " + endYear + "";
            query += "        ) as l2 ON l1.DESCRIPTOR = l2.DESCRIPTOR ";
            query += "    GROUP BY l2.countryregion ";
            query += "    ORDER BY l2.countryregion = '" + countryRegion + "' DESC, numShared " + ascOrDesc + " ; ";
            



            System.out.println(query);

            ResultSet results = statement.executeQuery(query);

            int i = 0;
            if (Integer.parseInt(selectedAmount) != 0) {
                highLowPercentTable += "<th>Similarity Rank</th>";   
                highLowPercentTable += "<th>Country/Region</th>";    
                highLowPercentTable += "<th>Number of Shared foods</th>";  
                if (showFoods){highLowPercentTable += "<th>Shared Foods</th>";}    



                while (results.next()) {
                    if (i == 0) {

                  
                        
                        highLowPercentTable += "<tr>";
                        highLowPercentTable += "<td><b>Country/Region of Choice</b></td>";
                        highLowPercentTable += "<td>" + results.getString("countryregion") + "</td>";
                        highLowPercentTable += "<td>" + results.getInt("numShared") + "</td>";
                        if (showFoods){highLowPercentTable += "<td>" + results.getString("sharedCommodities") + "</td>";}    
                        highLowPercentTable += "</tr>"; 
                    }
                    else {
                       
                        
                        highLowPercentTable += "<tr>";
                        highLowPercentTable += "<td><b>" + i + ")</b></td>";
                        highLowPercentTable += "<td>" + results.getString("countryregion") + "</td>";
                        highLowPercentTable += "<td>" + results.getInt("numShared") + "</td>";
                        if (showFoods){highLowPercentTable += "<td>" + results.getString("sharedCommodities") + "</td>";}    
                        highLowPercentTable += "</tr>"; 
                    }
                    ++i;
                    if (i > Integer.parseInt(selectedAmount)) {
                        break;
                    }
                }
            }
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
        return highLowPercentTable;
    }
    
    public static String getST3ALossPercentageTable(
        String countryRegion,
        int startYear,
        int endYear,
        String selectedAmount, 
        boolean ascendingSearch) {
        String highLowPercentTable = "";
        
        Connection connection = null;
        
        String ascOrDesc;
        if (ascendingSearch){ascOrDesc = "DESC";}
        else{ascOrDesc = "ASC";}

        try {
            connection = DriverManager.getConnection(DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            

            String query = "SELECT *, ABS(avg0 - avg1) as diff FROM (SELECT AVG(LOSSPERCENTAGE) as avg0, IFNULL(PARENTLOCATION, LOSSSTAT.LOCATION) as countryregion, ( ";
            query += "        SELECT AVG(LOSSPERCENTAGE) as avg0 ";
            query += "        FROM LOSSSTAT ";
            query += "        JOIN COUNTRYREGION ON LOSSSTAT.LOCATION = COUNTRYREGION.LOCATION ";
            query += "        WHERE YEAR >= " + startYear + " AND YEAR <= " + endYear + " ";
            query += "        GROUP BY IFNULL(PARENTLOCATION, LOSSSTAT.LOCATION) ";
            query += "        HAVING IFNULL(PARENTLOCATION, LOSSSTAT.LOCATION) = '" + countryRegion + "') as avg1 ";
            query += "    FROM LOSSSTAT ";
            query += "    JOIN COUNTRYREGION ON LOSSSTAT.LOCATION = COUNTRYREGION.LOCATION ";
            query += "    WHERE YEAR >= " + startYear + " AND YEAR <= " + endYear + " ";
            query += "    GROUP BY countryregion ";
            query += "    ORDER BY countryregion = '" + countryRegion + "' DESC, ABS(avg0 - avg1) " + ascOrDesc + "); ";


            System.out.println(query);

            ResultSet results = statement.executeQuery(query);

            int i = 0;
            if (Integer.parseInt(selectedAmount) != 0) {
                highLowPercentTable += "<th>Similarity Rank</th>";   
                highLowPercentTable += "<th>Country/Region</th>";    
                highLowPercentTable += "<th>Average Loss%</th>";  
                highLowPercentTable += "<th>Difference %</th>";}   



                while (results.next()) {
                    if (i == 0) {

                  
                        
                        highLowPercentTable += "<tr>";
                        highLowPercentTable += "<td><b>Country/Region of Choice</b></td>";
                        highLowPercentTable += "<td>" + results.getString("countryregion") + "</td>";
                        highLowPercentTable += "<td>" + String.format("%.3f", (results.getFloat("avg0"))) + "%</td>";
                        highLowPercentTable += "<td>" + "N/A" + "</td>"; 
                        highLowPercentTable += "</tr>"; 
                    }
                    else {
                       
                        highLowPercentTable += "<tr>";
                        highLowPercentTable += "<td><b>" + i + ")</b></td>";
                        highLowPercentTable += "<td>" + results.getString("countryregion") + "</td>";
                        highLowPercentTable += "<td>" + String.format("%.3f", (results.getFloat("avg0"))) + "%</td>";
                        highLowPercentTable += "<td>" + String.format("%.3f", (results.getFloat("diff"))) + "%</td>";
                        highLowPercentTable += "</tr>"; 
                    }
                    ++i;
                    if (i > Integer.parseInt(selectedAmount)) {
                        break;
                    }
                }
            }
         catch (SQLException e) {
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
        return highLowPercentTable;
    }

    public static String getST3ACommonFoodAndLossPercentageTable(
        String countryRegion,
        int startYear,
        int endYear,
        String selectedAmount,
        boolean showFoods,
        boolean ascendingSearch) {
        String highLowPercentTable = "";
        
        String ascOrDesc;
        if (ascendingSearch){ascOrDesc = "ASC";}
        else{ascOrDesc = "DESC";}

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            

            String query = "SELECT countryregion, numShared, sharedCommodities, diff, IFNULL( 10 *(numshared * ( 0.5 *numshared)/ (0.35 + diff)), 10000000) as simScore FROM (SELECT l2.countryregion, COUNT(l2.countryregion) as numShared, group_concat(l1.DESCRIPTOR, ' | ') as sharedCommodities FROM ( ";
            query += "    SELECT DISTINCT  DESCRIPTOR, IFNULL(PARENTLOCATION, l.LOCATION) as countryregion ";
            query += "    FROM LOSSSTAT  as l  ";
            query += "    JOIN COUNTRYREGION ON l.LOCATION = COUNTRYREGION.LOCATION";
            query += "    JOIN FOOD ON l.FOODID = FOOD.FOODID";
            query += "    WHERE countryregion = '" + countryRegion + "' AND YEAR >= " + startYear + " AND YEAR <= " + endYear + ") as l1";
            query += "    JOIN (SELECT DISTINCT DESCRIPTOR, IFNULL(PARENTLOCATION, LOSSSTAT.LOCATION) as countryregion";
            query += "        FROM LOSSSTAT ";
            query += "        JOIN COUNTRYREGION ON LOSSSTAT.LOCATION = COUNTRYREGION.LOCATION";
            query += "        JOIN FOOD ON LOSSSTAT.FOODID = FOOD.FOODID";
            query += "        WHERE YEAR >= " + startYear + " AND YEAR <= " + endYear + "";
            query += "        ) as l2 ON l1.DESCRIPTOR = l2.DESCRIPTOR ";
            query += "    GROUP BY l2.countryregion ";
            query += "    ORDER BY l2.countryregion = '" + countryRegion + "' DESC, numShared DESC) as commonT ";
            query += "    JOIN (SELECT *, ABS(avg0 - avg1) as diff FROM (SELECT AVG(LOSSPERCENTAGE) as avg0, IFNULL(PARENTLOCATION, LOSSSTAT.LOCATION) as countryregion1, ( ";
            query += "        SELECT AVG(LOSSPERCENTAGE) as avg0";
            query += "        FROM LOSSSTAT ";
            query += "        JOIN COUNTRYREGION ON LOSSSTAT.LOCATION = COUNTRYREGION.LOCATION ";
            query += "        WHERE YEAR >= " + startYear + " AND YEAR <= " + endYear + " ";
            query += "        GROUP BY IFNULL(PARENTLOCATION, LOSSSTAT.LOCATION) ";
            query += "        HAVING IFNULL(PARENTLOCATION, LOSSSTAT.LOCATION) = '" + countryRegion + "') as avg1 ";
            query += "    FROM LOSSSTAT";
            query += "    JOIN COUNTRYREGION ON LOSSSTAT.LOCATION = COUNTRYREGION.LOCATION ";
            query += "    WHERE YEAR >= " + startYear + " AND YEAR <= " + endYear + " ";
            query += "    GROUP BY countryregion1 ";
            query += "    ORDER BY countryregion1 ='" + countryRegion + "' DESC, ABS(avg0 - avg1) ASC)) as losspT ON commonT.countryregion = losspT.countryregion1 ";
            query += "    ORDER BY countryregion = '" + countryRegion + "' DESC, simScore " + ascOrDesc + "; ";



            System.out.println(query);

            ResultSet results = statement.executeQuery(query);

            int i = 0;
            if (Integer.parseInt(selectedAmount) != 0) {
                highLowPercentTable += "<th>Similarity Rank</th>";   
                highLowPercentTable += "<th>Country/Region</th>";    
                highLowPercentTable += "<th>Number of Shared foods</th>";  
                if (showFoods){highLowPercentTable += "<th>Shared Foods</th>";}
                highLowPercentTable += "<th>Difference %</th>";
                highLowPercentTable += "<th>Similarity Score</th>";
            
            }   



                while (results.next()) {
                    if (i == 0) {

                  
                        
                        highLowPercentTable += "<tr>";

                        highLowPercentTable += "<td><b>Country/Region of Choice</b></td>";
                        highLowPercentTable += "<td>" + results.getString("countryregion") + "</td>";
                        highLowPercentTable += "<td>" + results.getInt("numShared") + "</td>";
                        if (showFoods){highLowPercentTable += "<td>" + results.getString("sharedCommodities") + "</td>";}    
                        highLowPercentTable += "<td>" + "N/A" + "</td>"; 
                        highLowPercentTable += "<td>Country of choice</td>";
                        
                        highLowPercentTable += "</tr>"; 
                    }
                    else {
                       
                        highLowPercentTable += "<tr>";

                        highLowPercentTable += "<td><b>" + i + ")</b></td>";
                        highLowPercentTable += "<td>" + results.getString("countryregion") + "</td>";
                        highLowPercentTable += "<td>" + results.getInt("numShared") + "</td>";
                        if (showFoods){highLowPercentTable += "<td>" + results.getString("sharedCommodities") + "</td>";}    
                        highLowPercentTable += "<td>" + String.format("%.3f", (results.getFloat("diff"))) + "%</td>";
                        highLowPercentTable += "<td>" + String.format("%.3f", (results.getFloat("simScore"))) + "</td>";


                        highLowPercentTable += "</tr>"; 
                    }
                    ++i;
                    if (i > Integer.parseInt(selectedAmount)) {
                        break;
                    }
                }
            }
         catch (SQLException e) {
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
        return highLowPercentTable;
    }
    
    
    public static void main(String[] args) {
        // System.out.println(getST3ACommonFoodTable("Australia", 1990, 2020, true, "10"));
        // System.out.println(getST3BavgLossTable("012", "7"));
 
 
        //ArrayList<String> countryregions = getAllAvailableYearsFoodGroup("Cereals");
 
        /* 
        for (String countryregion : countryregions){
            System.out.println(countryregion);
        }
       */
      System.out.println(getST2BQueryAllYears("Cereals", "2015", "2017", "yes", "yes", "yes", "sort-by-descending"));
    }
 
}