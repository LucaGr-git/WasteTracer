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
            <div class="filters">
                <h2>Filters</h2>
                <form class="form" action='/' method='post' id='ST2A-form' name='ST2A-form'>
                    <div class="country-select">
                        <div>
                            <p>Countries</p>
                            <div class='custom-select'>
                                <select id="country-selector" name='country-selector'>
                 """;

        for (String country : JDBCConnection.getAllCountriesString(JDBCConnection.getAllCountries())) {
            html += "<option name='country-selector'>" + country + "</option>";
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
                            <select>
                 """;
        getAllAvailableYears(context, html);                               
        html +=  """
                            </select>
                        </div>
                        <div class="end-year-wrapper">
                            <p>End Year</p>
                            <select>
                 """;
       
                 
        html +=  """

                            </select>
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
                            <input type="radio" name="sort-by-percent" id="sort-by-ascending">
                            <label for="sort-by-ascending">Ascending</label>
                        </div>
                        <div>
                            <input type="radio" name="sort-by-percent" id="sort-by-descending">
                            <label for="sort-by-descending">Descending</label>
                        </div>
                    </div>
                    <div>
                        <button type="submit">Search Data</button>
                    </div>
                </form>
            </div>
            """;

        html += """
            <div class="data-container">
                <h1>Search Data by Country</h1>
            </div>
                """;

        html += "</div></body></html>";

        


        context.html(html);
    }



    public static void getAllAvailableYears(Context context, String html) {
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String country_selector = context.formParam("country-selector");
            if (country_selector == null) {
                html += "<option>N/A</option>";
            } 
            else {
                String Query = "SELECT DISTINCT year FROM Lossstats WHERE country = " + country_selector;

                ResultSet results = statement.executeQuery(Query);

                while (results.next()) {
                    html += "<option>" +  results.getInt("year") + "</option>";
                }

                statement.close();
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
    }
}