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

    @Override
    public void handle(Context context) throws Exception {
        String html = "<html>";

        html += "<head>" + 
               "<title>Waste - TracerSubtask 2.1</title>";

        html += "<link rel='stylesheet' type='text/css' href='common.css' />" +
                "<link rel='stylesheet' type='text/css' href='ST2A.css'/>";
        html += "</head>";

        html += "<body>";

        html += """
            <div class="topnav">
                <a href='/'><img src='logo.png' width='200'></a>
                <ul class="topnav-links">
                    <div class="about-us">
                        <a href="/mission.html">About Us</a>
                    </div>
                    <div class="subtask">
                        <li>Subtasks 2</li>
                        <div class="subtask-dropdown">
                            <a href="/page2A.html">Subtask 2a</a>
                            <a href="/page2B.html">Subtask 2b</a>
                        </div>
                    </div>
                    <div class="subtask">
                        <li>Subtasks 3</li>
                        <div class="subtask-dropdown">
                            <a href="/page3A.html">Subtask 3a</a>
                            <a href="/page3B.html">Subtask 3b</a>
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
                    <div class="country-select">
                        <div>
                            <p>Countries</p>
                            <div class='custom-select'>
                                <select id="country-selector" name='country-selector'>
                                    <option>Please Select</option>
                 """;

        String selectedCountry = context.formParam("country-selector");
        for (String country : JDBCConnection.getAllCountriesString(JDBCConnection.getAllCountries())) {
            if (selectedCountry != null && country.equals(selectedCountry)) {
                html += "<option selected>" + country + "</option>";
            }
            html += "<option>" + country + "</option>";
        }

        html +=  """
                                </select>
                                <span class="custom-arrow"></span>
                            </div>
                            <button type="submit" class="confirm-select">Confirm Country</submit>
                        </div>
                    </div>
                    <div class="year-wrapper">
                        <div class="start-year-wrapper">
                            <p>Start Year</p>
                            <select id="start-year" name="start-year">
                 """;

        for (String year : JDBCConnection.getAllAvailableYears(selectedCountry)) {
            html += "<option value=" + year + ">" + year + "</option>";
        }
        
        html +=  """
                            </select>
                        </div>
                        <div class="end-year-wrapper">
                            <p>End Year</p>
                            <select id="end-year" name="end-year">
                 """;
        
        for (String year : JDBCConnection.getAllAvailableYears(selectedCountry)) {
            html += "<option selected value=" + year + ">" + year + "</option>";
        }
                 
        html +=  """

                            </select>
                        </div>
                    </div>
                    <div class='checkboxes'>
                        <div>
                            <input type='checkbox' name='all-years' id='all-years'>
                            <label for='all-years'>Show all available years</label>
                        </div>
                    </div>
                    <h4>Filter Columns</h4>
                    <div class='checkboxes'>
                        <div>
                            <input type='checkbox' name='activity-show' id='activity-show'>
                            <label for='activity-show'>Show activity</label>
                        </div>
                        <div>
                            <input type='checkbox' name='cause-of-loss-show' id='cause-of-loss-show'>
                            <label for='cause-of-loss-show'>Show cause of loss</label>
                        </div>
                        <div>
                            <input type='checkbox' name='food-supply-show' id='food-supply-show'>
                            <label for='food-supply-show'>Show food supply stage</label>
                        </div>
                    </div>
                    <h4>Sort by Loss %</h4>
                    <div class="radio-buttons">
                        <div>
                            <input type="radio" name="sort-by-percent" value="sort-by-ascending" id="sort-by-ascending">
                            <label for="sort-by-ascending">Ascending</label>
                        </div>
                        <div>
                            <input type="radio" name="sort-by-percent" value="sort-by-descending" id="sort-by-descending">
                            <label for="sort-by-descending">Descending</label>
                        </div>
                    </div>
                    <div>
                        <button type="submit">Search Data</button>
                    </div>
                </form>
            </div>
            """;

        String startYear = context.formParam("start-year");
        String endYear = context.formParam("end-year");
        String activity = context.formParam("activity-show");
        String causeOfLoss = context.formParam("cause-of-loss-show");
        String foodSupply = context.formParam("food-supply-show");
        String sortByPercent = context.formParam("sort-by-percent");

        html += """
            <div class="data-container">
                <h1>Search Loss by Country</h1>
                <table>
                """;

        String query = JDBCConnection.getST2AQuery(
            selectedCountry,
            startYear,
            endYear,
            activity,
            causeOfLoss,
            foodSupply,
            sortByPercent
        );

        if (query != null) {
            html += "<caption>" + selectedCountry + "</caption>";
            html += "<thead>";
            html += "<tr>";
            html += "<th>Commodity</th>";
            html += "<th>" + startYear + "</th>";
            html += "<th>" + endYear + "</th>";

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

            html += JDBCConnection.ST2ATableHTML(query, activity, causeOfLoss, foodSupply);
        }

        html += """
                </table>
            </div>
                """;

        html += "</div></body></html>";

        context.html(html);
    }
}