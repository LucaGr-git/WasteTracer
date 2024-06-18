package app;

import java.util.ArrayList;

import io.javalin.http.Context;
import io.javalin.http.Handler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PageST3A implements Handler {

    // URL of this page relative to http://localhost:7001/
    public static final String URL = "/page3A.html";

    @Override
    public void handle(Context context) throws Exception {
        String html = "<html>";

        html += "<head>" + 
                "<title>WasteTracer - Search Similar Data by COuntry/Region</title>";

        html += "<link rel='stylesheet' type='text/css' href='common.css' />";
        html += "<link rel='stylesheet' type='text/css' href='ST2common.css' />";
        html += "</head>";

        html += "<body>";

        html += """
            <div class='topnav'>
                <a href='/'><img src='logo.png' width='200'></a>
                <ul class='topnav-links'>
                    <div id='option-1' class='about-us'>
                        <a href='/mission.html'>About Us</a>
                    </div>
                    <div class='subtask'>
                        <li>Search Raw Data</li>
                        <div class='subtask-dropdown'>
                            <a href='/page2A.html'>Search by Country</a>
                            <a href='/page2B.html'>Search by Food Group</a>
                        </div>
                    </div>
                    <div class='subtask'>
                        <li>Search Similar Data</li>
                        <div class='subtask-dropdown'>
                            <a href='/page3A.html'>Search by Country/Region</a>
                            <a href='/page3B.html'>Search by Commodity</a>
                        </div>
                    </div>
                </ul>
            </div>
        """;
        
        html += "<div class='content'>";

        html += """
            <div class='filters'>    
                <h2>Filters</h2>
                <form class='form' action='/page3A.html' method='POST' id='ST3A-form' name='ST3A-form'>
                    <div class='select-area'>
                        <div>
                            <p>Countries and Regions</p>
                            <div class='custom-select-wrapper'>
                                <select id='country-region-selector' name='country-region-selector'>
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
                                <span class='custom-arrow'></span>
                            </div>
                            <button type='submit' class='confirm-select'>Confirm Selection</button>
                        </div>
                    </div>
                    <div class='select-area'>
                        <div>
                            <p>Available Years</p>
                            <div class='custom-select-wrapper'>
                                <select id='year-selector' name='year-selector'>
                                    <option>Please Select</option>
                """;

        String selectedYear = context.formParam("year-selector");

        for (String year : JDBCConnection.getAllAvailableYearsCountryRegion(selectedCountryRegion)) {
            html += "<option value='" + year + "'>" + year + "</option>";
        }

        html += """
                                </select>
                                <span class='custom-arrow'></span>
                            </div>
                        </div>
                    </div>
                    <div class='select-area'>
                        <div>
                            <p>No. of similar areas shown</p>
                            <div class='custom-select-wrapper'>
                                <select id='amount-selector' name='amount-selector' required>
                                    <option>Please Select</option>
                """;
        
        String selectedAmount = context.formParam("amount-selector");

        //392 is the amount of available regions/countries
        for (int i = 1; i < 392; ++i) {
            html += "<option value='" + i + "'>" + i + "</option>";
        }

        html += """
                                </select>
                                <span class='custom-arrow'></span>
                            </div>
                        </div>
                    </div>
                    <h4>Search Similarity by</h4>
                    <div class='radio-buttons'>
                        <div>
                            <input type='radio' name='similarity-choice' value='food-in-common' id='food-in-common'>
                            <label for='food-in-common'>Foods in common</label>
                        </div>
                        <div>
                            <input type='radio' name='similarity-choice' value='loss-percent' id='loss-percent'>
                            <label for='loss-percent'>Loss %</label>
                        </div>
                        <div>
                            <input type='radio' name='similarity-choice' value='common-and-loss' id='common-and-loss'>
                            <label for='common-and-loss'>Common foods and loss % (Country Exclusive)</label>
                        </div>
                    </div>
                    <div>
                        <button type='submit'>Search Data</button>
                    </div>
                </form>
            </div>
            """;

        String similarityChoice = context.formParam("similarity-choice");
        
        html += """
            <div class="data-container">
                <h1>Search Area by Similarity</h1>
                <table>
                """;
        
        if (selectedCountryRegion == null || selectedCountryRegion.equals("Please Select")) {
            html += """
                    <caption>Please select a country or region</caption>
                    <thead>
                        <tr>
                            <th>Similarity Rank</th>
                            <th>Area</th>
                        </tr>
                    </thead>
                    """; 
        }
        else if (selectedYear == null || selectedYear.equals("Please Select")) {
            html += "<caption>" + selectedCountryRegion + "</caption>";
            html += """
                    <thead>
                        <tr>
                            <th>Similarity Rank</th>
                            <th>Area</th>
                        </tr>
                    </thead>
                    """;
        }
        else {
 
        }

        context.html(html);
    }

}
