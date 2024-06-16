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

    @Override
    public void handle(Context context) throws Exception {
        // Create a simple HTML webpage in a String
        String html = "<html>";

        // Add some Head information
        html += "<head>" + 
                "<title>Subtask 2.2</title>";

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
                <img src="logo.png">
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
                <div class='filters'>
                    <h2>Filters</h2>
                    <form class='form' action'/page2b.html' method='POST' id='ST2B-form' name='ST2B-form'>
                        <div class='select-area'>
                            <div>
                                <p>Food Groups</p>
                                <div class='custom-select-wrapper'>
                                    <select id="food-group-selector" name="food-group-selector">
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

        html += """
                                    </select>
                                    <span class="custom-arrow"></span>
                                </div>
                                <button type="submit" class="confirm-select">Confirm Food Group</button>
                            </div>
                        </div>
                        <div class="year-wrapper">
                            <div class="start-year-wrapper">
                                <p>Start Year</p>
                                <select id="start-year" name="start-year">
                """;
        
        String startYear = context.formParam("start-year");

        for (String year : JDBCConnection.getAllAvailableYearsFoodGroup(selectedFoodGroup)) {
            html += "<option value='" + year + "'>" + year + "</option>";
        }

        html += """
                                </select>
                            </div>
                            <div class="end-year-wrapper">
                                <p>End Year</p>
                                <select id="end-year" name="end-year">
                """;

        String endYear = context.formParam("end-year");

        for (String year : JDBCConnection.getAllAvailableYearsFoodGroup(selectedFoodGroup)) {
            html += "<option selected value='" + year + "'>" + year + "</option>";
        }

        html += """
                            </select>
                        </div>
                    </div>
                    <div class="checkboxes">
                        <div>
                            <input type="checkbox" name="all-years" id="all-years">
                            <label for="all-years">Show all available years</label>
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

        String activity = context.formParam("activity-show");
        String causeOfLoss = context.formParam("cause-of-loss-show");
        String foodSupply = context.formParam("food-supply-show");
        String sortByPercent = context.formParam("sort-by-percent");

        html += """
            <div class="data-container">
                <h1>Search Loss by Food Group</h1>
                <table>
                """;

        html += """
                </table>
            </div>
                """;

        html += "</div></body></html>";

        context.html(html);
    }

}
