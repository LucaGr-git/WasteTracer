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

public class PageST3B implements Handler {

    // URL of this page relative to http://localhost:7001/
    public static final String URL = "/page3B.html";

    @Override
    public void handle(Context context) throws Exception {
        // Create a simple HTML webpage in a String
        String html = "<html>";

        // Add some Head information
        html += "<head>" + 
                "<title>Waste Tracer - Search Similar Data by Commodity</title>";

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
                <form class='form' action='/page3B.html' method='POST' id='ST3B-form' name='ST3B-form'>
                    <div class='select-area'>
                        <div>
                            <p>Food Commodities</p>
                            <div class='custom-select-wrapper'>
                                <select id='commodity-selector' name='commodity-selector'>
                                    <option>Please Select</option>
                """;

        String selectedCommodity = context.formParam("commodity-selector");

        for (String commodity : JDBCConnection.getAllCommodities()) {
            if (selectedCommodity != null && commodity.equals(selectedCommodity)) {
                html += "<option selected>" + commodity + "</option>";
            }
            else {
                html += "<option>" + commodity + "</option>";
            }
        }

        html += """
                                </select>
                                <span class='custom-arrow'></span>
                            </div>        
                        </div>
                    </div>
                    <div class='select-area'>
                        <div>
                            <p>No. of similar foods shown</p>
                            <div class='custom-select-wrapper'>
                                <select id='amount-selector' name='amount-selector' required>
                                    <option>Please Select</option>
                """;
            
        String selectedAmount = context.formParam("amount-selector");

        //23 is the amount of food groups with available data
        for (int i = 1; i < 23; ++i) {
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
                            <input type='radio' name='similarity-choice' value='avg-loss-percent' id='avg-loss-percent' required>
                            <label for='avg-loss-percent'>Average Loss %</label>
                        </div>
                        <div>
                            <input type='radio' name='similarity-choice' value='highest-percent' id='highest-percent'>
                            <label for='highest-percent'>Item with highest % of loss within the Food Group</label>
                        </div>
                        <div>
                            <input type='radio' name='similarity-choice' value='lowest-percent' id='lowest-percent'>
                            <label for='lowest-percent'>Item with lowest % of loss within the Food Group</label>
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
                <h1>Search Commodity by Similarity</h1>
                <table>
                """;

        if (selectedCommodity == null || selectedCommodity.equals("Please Select") ||
            selectedAmount == null || selectedAmount.equals("Please Select")) {}
        else {
            html += "<caption>" + selectedCommodity + "</caption>";
            html += "<thead>";
            html += "<tr>";
            html += "<th>Similarity Rank</th>";
            html += "<th>Food Group</th>";

            String foodGroupCPC = JDBCConnection.getST3BGroupFromCommodity(selectedCommodity);

            if (similarityChoice.equals("avg-loss-percent")) {
                html += "<th>Average Loss %</th>";
                html += "</thead>";

                html += JDBCConnection.getST3BavgLossTable(foodGroupCPC, selectedAmount);
            }
            else if (similarityChoice.equals("highest-percent")) {
                html += "<th>Highest Loss % Commodity</th>";
                html += "<th>Average Loss %</th>";

                html += JDBCConnection.getST3BHighestPercentTable(foodGroupCPC, selectedAmount);
            }
            else {

            }
        }
        context.html(html);
    }

}
