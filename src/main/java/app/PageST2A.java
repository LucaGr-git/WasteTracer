package app;

import java.util.ArrayList;

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

public class PageST2A implements Handler {

    // URL of this page relative to http://localhost:7001/
    public static final String URL = "/page2A.html";

    @Override
    public void handle(Context context) throws Exception {
        // Create a simple HTML webpage in a String
        String html = "<html>";

        // Add some Head information
        html += "<head>" + 
               "<title>Waste - TracerSubtask 2.1</title>";

        // Add some CSS (external file)
        html += "<link rel='stylesheet' type='text/css' href='common.css' />" +
                "<link rel='stylesheet' type='text/css' href='ST2A.css'/>";
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
                <div class="filters">
                    <h2>Filters</h2>
                    <div class="country-select">
                        <p>Countries</p>
                        <select class="select__input" name="country-selector" id="country-selector">
                            <option value="0">Please Select</option>
                            <option value="Algeria">Algeria</option>
                            <option value="Angola">Angola</option>
                        </select>
                    </div>
                    <hr>
                    <div class="year-wrapper">
                        <div class="start-year-wrapper">

                        </div>
                        <div class="end-year-wrapper">

                        </div>
                    </div>
                    <hr>
                    <hr>
                    <hr>
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

}
