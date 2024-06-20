package app;

import java.util.ArrayList;

import io.javalin.http.Context;
import io.javalin.http.Handler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PageST2A implements Handler {

    public static final String URL = "/page2A.html";
    public static final String DATABASE = "jdbc:sqlite:database/foodloss.db";
    ArrayList<String> priorCountries = new ArrayList<>();

    @Override
    public void handle(Context context) throws Exception {
        String html = "<html>";

        html += "<head>" + 
                "<title>WasteTracer - Search Raw Data by Country</title>";

        html += "<link rel='stylesheet' type='text/css' href='common.css' />" +
                "<link rel='stylesheet' type='text/css' href='ST2common.css'/>";
        html += "</head>";

        html += "<body>";

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
                            <a href="/page3B.html">Search by Food Group</a>
                        </div>
                    </div>
                </ul>
            </div>
        """;

        html += "<div class='content'>";

        html += """
            <div class="filters">
                <h2>Filters</h2>
                <form class="form" action='/page2A.html' method='POST' id='ST2A-form' name='ST2A-form'>
                    <div class="select-area">
                        <div>
                            <p>Countries</p>
                            <div class='custom-select-wrapper'>
                                <select id="country-selector" name='country-selector' onchange="this.form.submit()" required>
                                    <option>Please Select</option>
                """;

        String selectedCountry = context.formParam("country-selector");

        for (String country : JDBCConnection.getAllCountries()) {
            if (selectedCountry != null && country.equals(selectedCountry)) {
                html += "<option selected>" + country + "</option>";
            }
            else {
                html += "<option>" + country + "</option>";
            }
        } 

        if (selectedCountry != null && !selectedCountry.equals("Please Select")) {
            priorCountries.add(selectedCountry);
        }

        html +=  """
                                </select>
                                <span class="custom-arrow"></span>
                            </div>
                        </div>
                    </div>
                    <div class="year-wrapper">
                        <div class="start-year-wrapper">
                            <p>Start Year</p>
                            <select id="start-year" name="start-year"r>
                 """;

        String startYear = context.formParam("start-year");

        if (priorCountries.size() > 1 && priorCountries.get(priorCountries.size() - 1).equals(priorCountries.get(priorCountries.size() - 2))) {
            for (String year : JDBCConnection.getAllAvailableYearsCountryRegion(selectedCountry)) {
                if (year.equals(startYear)) {
                    html += "<option selected value=" + year + ">" + year + "</option>";
                }
                else {
                    html += "<option value=" + year + ">" + year + "</option>";
                }
            }
        }
        else {
            for (String year : JDBCConnection.getAllAvailableYearsCountryRegion(selectedCountry)) {
                html += "<option value=" + year + ">" + year + "</option>";
            }
        }
        
        html +=  """
                            </select>
                        </div>
                        <div class="end-year-wrapper">
                            <p>End Year</p>
                            <select id="end-year" name="end-year">
                 """;
        
        String endYear = context.formParam("end-year");

        if (priorCountries.size() > 1 && priorCountries.get(priorCountries.size() - 1).equals(priorCountries.get(priorCountries.size() - 2))) {
            for (String year : JDBCConnection.getAllAvailableYearsCountryRegion(selectedCountry)) {
                if (year.equals(endYear)) {
                    html += "<option selected value=" + year + ">" + year + "</option>";
                }
                else {
                    html += "<option value=" + year + ">" + year + "</option>";
                }
            }
        }
        else {
            for (String year : JDBCConnection.getAllAvailableYearsCountryRegion(selectedCountry)) {
                html += "<option selected value=" + year + ">" + year + "</option>";
            }
        }

        html +=  """
                            </select>
                        </div>
                    </div>
                    <div class='checkboxes'>
                        <div>
                """;

        if (context.formParam("all-years") != null) {
            html += "<input type='checkbox' name='all-years' id='all-years' checked>";
        }
        else {
            html += "<input type='checkbox' name='all-years' id='all-years'>";
        }
                                

        html += """ 
                            <label for='all-years'>Show all available years</label>
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
                <h1>Search Loss by Country</h1>
                <table>
                """;

        if (selectedCountry == null || selectedCountry.equals("Please Select")) {
            html += """
                    <caption>Please select a country</caption>
                    <thead>
                        <tr>
                            <th>Food Group</th>
                            <th>Start Year Loss %</th>
                            <th>End Year Loss %</th>
                            <th>Difference</th>
                        </tr>
                    </thead>
                    """;
        }
        else if (startYear.equals("") || endYear.equals("")) {
            html += "<caption>" + selectedCountry + "</caption>";
            html += """    
                    <thead>
                        <tr>
                            <th>Food Group</th>
                            <th>Start Year Loss %</th>
                            <th>End Year Loss %</th>
                            <th>Difference</th>
                        </tr>
                    </thead>
                    """;
        }
        else {
            if (context.formParam("all-years") == null) {
                html += "<caption>" + selectedCountry + "</caption>";
                html += "<thead>";
                html += "<tr>";
                html += "<th>Food Group</th>";
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

                String query = JDBCConnection.getST2AQuery(selectedCountry, startYear, endYear, activity, causeOfLoss, foodSupply, sortByPercent);

                if (query != null) {
                    html += JDBCConnection.ST2ABTableHTML(query, activity, causeOfLoss, foodSupply);
                }
            } 
            else {
                html += "<caption>" + selectedCountry + "</caption>";
                html += "<thead>";
                html += "<tr>";
                html += "<th>Food Group</th>";
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

                html += JDBCConnection.getST2AQueryAllYears(selectedCountry, startYear, endYear, activity, causeOfLoss, foodSupply, sortByPercent);
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