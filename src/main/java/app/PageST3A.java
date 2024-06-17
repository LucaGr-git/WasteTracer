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

public class PageST3A implements Handler {

    // URL of this page relative to http://localhost:7001/
    public static final String URL = "/page3A.html";

    @Override
    public void handle(Context context) throws Exception {
        // Create a simple HTML webpage in a String
        String html = "<html>";

        // Add some Head information
        html += "<head>" + 
                "<title>WasteTracer - Search Similar Data by COuntry/Region</title>";

        // Add some CSS (external file)
        html += "<link rel='stylesheet' type='text/css' href='common.css' />";
        html += "<link rel='stylesheet' type='text/css' href='ST2common.css />'";
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
            <div class="filters">    
                <h2>Filters</h2>
                <form class="form" action='/page3A.html' method='POST' id='ST3A-form' name='ST3A-form'>
                    <div class="select-area">
                        <div>
                            <p>Countries and Regions</p>
                            <div class='custom-select-wrapper'>
                                <select id="country-region-selector" name='country-region-selector'>
                                    <option>Please Select</option>
                """;

        String selectedCountryRegion = context.formParam("country-region-selector");

        for (String countryOrRegion : JDBCConnection.getAllCountriesRegions()) {
            if (selectedCountryRegion != null && countryOrRegion.equals(selectedCountryRegion)) {
                html += "<option selected>" + countryOrRegion + "</option>";
            }
            else {
                html += "<option>" + countryOrRegion + "</option>";
            }
        }

        html += """
                                </select>
                                <span class="custom-arrow"></span>
                            </div>
                             <button type="submit" class="confirm-select">Confirm Selection</button>
                        </div>
                    </div>
                    <div class="select-area">
                        <div>
                            <p>Year</p>
                            <div class="custom-select-wrapper">
                                <select id="year-selector" name="year-selector">
                                    <option>Please Select</option>
                """;

        String selectedYear = context.formParam("year-selector");

        for (String year : JDBCConnection.getAllAvailableYearsCountryRegion(selectedCountryRegion)) {
            html += "<option value='" + year + "'>" + year + "</option>";
        }

        html += """
                                </select>
                                <span class="custom-arrow"></span>
                            </div>
                        </div>
                    </div>
                    <div class="select-area">
                        <div>
                            <p>Year</p>
                            <div class="custom-select-wrapper">
                                <select id="year-selector" name="year-selector">
                                    <option>Please Select</option>
                """;
                        
        for (int i = 1; i < JDBCConnection.getAllAvailableYearsCountryRegion(selectedCountryRegion).size(); ++i) {
            html += "<option value=" + i + ">" + i + "</option>";
        }

        html += """
                    <h4>Search Similarity by</h4>
                    <div class='radio-buttons'>
                        <div>
                            <input type='radio' name='food-in-common' id='food-in-common'>
                            <label for='food-in-common'>Foods in common</label>
                        </div>
                        <div>
                            <input type='radio' name='Loss %' id='Loss %'>
                            <label for='Loss %'>Show cause of loss</label>
                        </div>
                        <div>
                            <input type='radio' name='food-supply-show' id='food-supply-show'>
                            <label for='food-supply-show'>Show food supply stage</label>
                        </div>
                    </div>
                    <div>
                        <button type="submit">Search Data</button>
                    </div>
                </form>
            </div>
            """;

        context.html(html);
    }

}
