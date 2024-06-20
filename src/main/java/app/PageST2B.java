package app;

import java.util.ArrayList;

import org.sqlite.JDBC;

import io.javalin.http.Context;
import io.javalin.http.Handler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Example Index HTML class using Javalin
 * <p>
 * Generate a static HTML page using Javalin
 * by writing the raw HTML into a Java String object
 *
 * @author Timothy Wiley, 2023. email: timothy.wiley@rmit.edu.au
 * @author Santha Sumanasekara, 2021. email: santha.sumanasekara@rmit.edu.au
 * @author Halil Ali, 2024. email: halil.ali@rmit.edu.au
 */

public class PageST2B implements Handler {

    // URL of this page relative to http://localhost:7001/
    public static final String URL = "/page2B.html";
    ArrayList<String> priorCountries = new ArrayList<>();

    @Override
    public void handle(Context context) throws Exception {
        // Create a simple HTML webpage in a String
        String html = "<html>";

        // Add some Head information
        html += "<head>" + 
                "<title>WasteTracer - Search Raw Data by Food Group</title>";

        // Add some CSS (external file)
        html += "<link rel='stylesheet' type='text/css' href='common.css' />";
        html += "<link rel='stylesheet' type='text/css' href='ST2common.css' />";
        html += "</head>";

        // Add the body
        html += "<body>";

        // Add the topnav
        // This uses a Java v15+ Text Block
        html += """
            <div class="topnav">
                <a href='/'><img src='logo.png' width='200'></a>
                <ul class="topnav-links">
                    <div id='option-1' class="about-us">
                        <a href="/mission.html">About Us</a>
                    </div>
                    <div class="subtask">
                        <li>Search Raw Data</li>
                        <div class="subtask-dropdown">
                            <a href="/page2A.html">Search by Country</a>
                            <a href="/page2B.html">Search by Food Group</a>
                        </div>
                    </div>
                    <div class="subtask">
                        <li>Search Similar Data</li>
                        <div class="subtask-dropdown">
                            <a href="/page3A.html">Search by Country/Region</a>
                            <a href="/page3B.html">Search by Commodity</a>
                        </div>
                    </div>
                </ul>
            </div>
        """;

        html += "<div class='content'>";

        html += """
                <div class='filters'>
                    <h2>Filters</h2>
                    <form class='form' action'/page2b.html' method='POST' id='ST2B-form' name='ST2B-form'>
                        <div class='select-area'>
                            <div>
                                <p>Food Groups</p>
                                <div class='custom-select-wrapper'>
                                    <select id="food-group-selector" name="food-group-selector" onchange="this.form.submit()" required>
                                        <option>Please Select</option>
                """;

        String selectedFoodGroup = context.formParam("food-group-selector");

        for (String foodGroup : JDBCConnection.getAllFoodGroups()) {
            if (selectedFoodGroup != null && foodGroup.equals(selectedFoodGroup)) {
                html += "<option selected>" + foodGroup + "</option>"; 
            }
            else {
                html += "<option>" + foodGroup + "</option>";
            }
        }
        // Some of the CPC code names were changed because they were too verbose

        if (selectedFoodGroup != null && !selectedFoodGroup.equals("Please Select")) {
            priorCountries.add(selectedFoodGroup);
        }

        html += """
                                    </select>
                                    <span class="custom-arrow"></span>
                                </div>
                            </div>
                        </div>
                        <div class="year-wrapper">
                            <div class="start-year-wrapper">
                                <p>Start Year</p>
                                <select id="start-year" name="start-year">
                """;
        
        String startYear = context.formParam("start-year");

        if (priorCountries.size() > 1 && priorCountries.get(priorCountries.size() - 1).equals(priorCountries.get(priorCountries.size() - 2))) {
            for (String year : JDBCConnection.getAllAvailableYearsFoodGroup(selectedFoodGroup)) {
                if (year.equals(startYear)) {
                    html += "<option selected value=" + year + ">" + year + "</option>";
                }
                else {
                    html += "<option value=" + year + ">" + year + "</option>";
                }
            }
        }
        else {
            for (String year : JDBCConnection.getAllAvailableYearsFoodGroup(selectedFoodGroup)) {
                html += "<option value=" + year + ">" + year + "</option>";
            }
        }

        html += """
                                </select>
                            </div>
                            <div class="end-year-wrapper">
                                <p>End Year</p>
                                <select id="end-year" name="end-year">
                """;

        String endYear = context.formParam("end-year");

        if (priorCountries.size() > 1 && priorCountries.get(priorCountries.size() - 1).equals(priorCountries.get(priorCountries.size() - 2))) {
            for (String year : JDBCConnection.getAllAvailableYearsFoodGroup(selectedFoodGroup)) {
                if (year.equals(endYear)) {
                    html += "<option selected value=" + year + ">" + year + "</option>";
                }
                else {
                    html += "<option value=" + year + ">" + year + "</option>";
                }
            }
        }
        else {
            for (String year : JDBCConnection.getAllAvailableYearsFoodGroup(selectedFoodGroup)) {
                html += "<option selected value=" + year + ">" + year + "</option>";
            }
        }

        html += """
                            </select>
                        </div>
                    </div>
                    <div class="checkboxes">
                        <div>
                """;
         
        if (context.formParam("all-years") != null) {
            html += "<input type='checkbox' name='all-years' id='all-years' checked>";
        } 
        else {
            html += "<input type='checkbox' name='all-years' id='all-years'>";
        }
                            
        html += """
                            <label for="all-years">Show all available years</label>
                        </div>
                    </div>
                    <h4>Filter Columns</h4>
                    <div class='checkboxes'>
                        <div>
                """;

        String activity = context.formParam("activity-show");
        if (activity != null) {
            html += "<input type='checkbox' name='activity-show' id='activity-show' checked>";
        }
        else {
            html += "<input type='checkbox' name='activity-show' id='activity-show'>";
        }

        html += """                                                                        
                            <label for='activity-show'>Show activity</label>
                        </div>
                        <div>
                """;           

        String causeOfLoss = context.formParam("cause-of-loss-show");
        if (causeOfLoss != null) {
            html += "<input type='checkbox' name='cause-of-loss-show' id='cause-of-loss-show' checked>";
        }
        else {
            html += "<input type='checkbox' name='cause-of-loss-show' id='cause-of-loss-show'>";
        }

        html += """          
                            <label for='cause-of-loss-show'>Show cause of loss</label>
                        </div>
                        <div>
                """;
        
        String foodSupply = context.formParam("food-supply-show");
        if (foodSupply != null) {
            html += "<input type='checkbox' name='food-supply-show' id='food-supply-show' checked>";
        }               
        else {
            html += "<input type='checkbox' name='food-supply-show' id='food-supply-show'>";
        }

        html += """
                            <label for='food-supply-show'>Show food supply stage</label>
                        </div>
                    </div>
                    <h4>Sort by Difference</h4>
                    <div class="radio-buttons">
                """;
        
        String sortByPercent = context.formParam("sort-by-percent");
        if (sortByPercent == null) {
            html += """
                    <div>
                        <input type="radio" name="sort-by-percent" value="sort-by-ascending" id="sort-by-ascending">
                        <label for="sort-by-ascending">Ascending</label>
                    </div>
                    <div>
                        <input type="radio" name="sort-by-percent" value="sort-by-descending" id="sort-by-descending">
                        <label for="sort-by-descending">Descending</label>
                    </div>
                    """;
        }
        else if (sortByPercent.equals("sort-by-descending")) {
            html += """
                    <div>
                        <input type="radio" name="sort-by-percent" value="sort-by-ascending" id="sort-by-ascending">
                        <label for="sort-by-ascending">Ascending</label>
                    </div>
                    <div>
                        <input type="radio" name="sort-by-percent" value="sort-by-descending" id="sort-by-descending" checked>
                        <label for="sort-by-descending">Descending</label>
                    </div>
                    """;    
        }
        else {
            html += """
                    <div>
                        <input type="radio" name="sort-by-percent" value="sort-by-ascending" id="sort-by-ascending" checked>
                        <label for="sort-by-ascending">Ascending</label>
                    </div>
                    <div>
                        <input type="radio" name="sort-by-percent" value="sort-by-descending" id="sort-by-descending">
                        <label for="sort-by-descending">Descending</label>
                    </div>
                    """;
        }
        
        html += """
                    </div>
                    <div>
                        <button type="submit">Search Data</button>
                    </div>
                </form>
            </div>
                """;

        html += """
            <div class="data-container">
                <h1>Search Loss by Food Group</h1>
                <table>
                """;

        if (selectedFoodGroup == null || selectedFoodGroup.equals("Please Select")) {
            html += """
                    <caption>Please select a food group</caption>
                    <thead>
                        <tr>
                            <th>Commodity</th>
                            <th>Start Year Loss %</th>
                            <th>End Year Loss %</th>
                            <th>Difference</th>
                        </tr>
                    </thead>
                    """;
        }
        else if (startYear == null || endYear == null) {
            html += "<caption>" + selectedFoodGroup + "</caption>";
            html += """
                    <thead>
                        <tr>
                            <th>Commodity</th>
                            <th>Start Year Loss %</th>
                            <th>End Year Loss %</th>
                            <th>Difference</th>
                        </tr>
                    </thead>
                    """;
        }
        else {
            if (context.formParam("all-years") == null) {
                html += "<caption>" + selectedFoodGroup + "</caption>";
                html += "<thead>";
                html += "<tr>";
                html += "<th>Commodity</th>";
                html += "<th>" + startYear + " Loss %" + "</th>";
                html += "<th>" + endYear + " Loss %" + "</th>";

                html += "<th>Difference</th>";

                if (activity != null) {
                    html += "<th>Activity</th>";
                }
                if (causeOfLoss != null) {
                    html += "<th>Cause of Loss</th>";
                }
                if (foodSupply != null) {
                    html += "<th>Food Supply</th>";
                }
                html += "</tr></thead>";

                String query = JDBCConnection.getST2BQuery(selectedFoodGroup, startYear, endYear, activity, causeOfLoss, foodSupply, sortByPercent);
                
                if (query != null) {
                    html += JDBCConnection.ST2ABTableHTML(query, activity, causeOfLoss, foodSupply);
                }
            }
            else {
                html += "<caption>" + selectedFoodGroup + "</caption>";
                html += "<thead>";
                html += "<tr>";
                html += "<th>Commodity</th>";
                html += "<th>Year</th>";
                html += "<th>Average Loss %</th>";

                if (activity != null) {
                    html += "<th>Activity</th>";
                }
                if (causeOfLoss != null) {
                    html += "<th>Cause of Loss</th>";
                }
                if (foodSupply != null) {
                    html += "<th>Food Supply</th>";
                }
                html += "</tr></thead>";
                
                html += JDBCConnection.getST2BQueryAllYears(selectedFoodGroup, startYear, endYear, activity, causeOfLoss, foodSupply, sortByPercent);
            }
            html += """
                </table>
            </div>
                """;

             html += "</div></body></html>";
        }

        context.html(html);
    }

}
