package app;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import io.javalin.http.Context;
import io.javalin.http.Handler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PageST3A implements Handler {

    public static final String URL = "/page3A.html";
    public static final String DATABASE = "jdbc:sqlite:database/foodloss.db";
    ArrayList<String> priorAreas = new ArrayList<>();

    @Override
    public void handle(Context context) throws Exception {
        String html = "<html>";

        html += "<head>" + 
                "<title>WasteTracer - Search Similar Data by Country/Region</title>";

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
                <form class="form" action='/page3A.html' method='POST' id='STA-form' name='ST3A-form'>
                    <div class="select-area">
                        <div>
                            <p>Countries/Regions</p>
                            <div class='custom-select-wrapper'>
                                <select id="country-selector" name='country-selector' onchange="this.form.submit()" required>
                                    <option>Please Select</option>
                """;

        String selectedArea = context.formParam("country-selector");

        LinkedHashMap<String, String> countriesAndRegions = JDBCConnection.getAllCountriesRegions();

        for (String area : countriesAndRegions.keySet()) {
            if (selectedArea != null && area.equals(selectedArea)) {
                if (countriesAndRegions.get(area) == null) {
                    html += "<option value=" + area + " selected>" + area + "</option>";
                }
                else {
                    html += "<option value=" + area + " selected>" + area + " -- " + countriesAndRegions.get(area) + "</option>";
                }
            }
            else {
                if (countriesAndRegions.get(area) == null) {
                    html += "<option value=" + area + ">" + area + "</option>";
                }
                else {
                    html += "<option value=" + area + ">" + area + " -- " + countriesAndRegions.get(area) + "</option>";
                }
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
                            <p>No. of Similar Countries Shown</p>
                            <div class='custom-select-wrapper'>
                                <select id='amount-selector' name='amount-selector' required onchange="this.form.submit()">
                                    <option>Please Select</option>
                """;
            
        String selectedAmount = (context.formParam("amount-selector") == null || context.formParam("amount-selector").equals("Please Select")) ?
                                "0" :
                                context.formParam("amount-selector");

        // 389 is the amount of countries/regions with available data
        for (int i = 1; i < 389; ++i) {
            if (Integer.parseInt(selectedAmount) == i) {
                html += "<option selected value='" + i + "'>" + i + "</option>";
            }
            else {
                html += "<option value='" + i + "'>" + i + "</option>";
            }
        }

        html += """
                                </select>
                                <span class='custom-arrow'></span>
                            </div>
                        </div>
                    </div>
                """;
        

        if (selectedArea != null && !selectedArea.equals("Please Select")) {
            priorAreas.add(selectedArea);
        }

        html +=  """                         
                    <div class="select-area">
                        <div>
                            <p>Available years</p>
                            <div class="custom-select-wrapper">
                                <select id="year-selector" name="year-selector" onchange="this.form.submit()">
                 """;

        String selectedYear = context.formParam("year-selector");

        if (priorAreas.size() > 1 && priorAreas.get(priorAreas.size() - 1).equals(priorAreas.get(priorAreas.size() - 2))) {
            for (String year : JDBCConnection.getAllAvailableYearsCountryRegion(selectedArea)) {
                if (year.equals(selectedYear)) {
                    html += "<option selected value=" + year + ">" + year + "</option>";
                }
                else {
                    html += "<option value=" + year + ">" + year + "</option>";
                }
            }
        }
        else {
            for (String year : JDBCConnection.getAllAvailableYearsCountryRegion(selectedArea)) {
                html += "<option value=" + year + ">" + year + "</option>";
            }
        }
        
        html +=  """
                                </select>
                                <span class='custom-arrow'></span>
                            </div>
                        </div>
                    </div>
                    <h4>Search Options</h4>
                    <div class='radio-buttons'>
                        
                """;

        String searchOption = context.formParam("search-options");
        if (searchOption == null || searchOption.equals("search-common-foods")) {
            html += """
                    <div>
                        <input type="radio" name="search-options" value="search-common-foods" id="search-common-foods" checked onchange="this.form.submit()">
                        <label for="search-common-foods">Search by common foods (with at least 1 food in common)</label>
                    </div>
                    <div>
                        <input type="radio" name="search-options" value="search-loss-%" id="search-loss-%" onchange="this.form.submit()">
                        <label for="search-loss-%">Search by loss %</label>
                    </div>
                    <div>
                        <input type="radio" name="search-options" value="search-loss-%-common-foods" id="search-loss-%-common-foods" onchange="this.form.submit()">
                        <label for="search-loss-%-common-foods">Search by loss % and common foods</label>
                    </div>
                    """;    
        }
        else if (searchOption.equals("search-loss-%")) {
            html += """
                    <div>
                        <input type="radio" name="search-options" value="search-common-foods" id="search-common-foods" onchange="this.form.submit()">
                        <label for="search-common-foods">Search by common foods (with at least 1 food in common)</label>
                    </div>
                    <div>
                        <input type="radio" name="search-options" value="search-loss-%" id="search-loss-%" checked onchange="this.form.submit()">
                        <label for="search-loss-%">Search by loss %</label>
                    </div>
                    <div>
                        <input type="radio" name="search-options" value="search-loss-%-common-foods" id="search-loss-%-common-foods" onchange="this.form.submit()">
                        <label for="search-loss-%-common-foods">Search by loss % and common foods</label>
                    </div>
                    """;
        }
        else {
            html += """
                    <div>
                        <input type="radio" name="search-options" value="search-common-foods" id="search-common-foods" onchange="this.form.submit()">
                        <label for="search-common-foods">Search by common foods (with at least 1 food in common)</label>
                    </div>
                    <div>
                        <input type="radio" name="search-options" value="search-loss-%" id="search-loss-%" onchange="this.form.submit()">
                        <label for="search-loss-%">Search by loss %</label>
                    </div>
                    <div>
                        <input type="radio" name="search-options" value="search-loss-%-common-foods" id="search-loss-%-common-foods" checked onchange="this.form.submit()">
                        <label for="search-loss-%-common-foods">Search by loss % and common foods</label>
                    </div>
                    """;
        }

        html += """
           
                        </select>
                    
                </div>
                <h4>Sort by Similarity</h4>
                <div class='radio-buttons'>
                    
                    """;
                            
        String sortBySim = context.formParam("sort-similarity");
        if (sortBySim == null) {
            html += """
                    <div>
                        <input type="radio" name="sort-similarity" value="most-least" id="most-least" checked onchange="this.form.submit()">
                        <label for="most-least">Descending</label>
                    </div>
                    <div>
                        <input type="radio" name="sort-similarity" value="least-most" id="least-most" onchange="this.form.submit()">
                        <label for="least-most">Ascending</label>
                    </div>
                    """;
        }
        else if (sortBySim.equals("least-most")) {
            html += """
                    <div>
                        <input type="radio" name="sort-similarity" value="most-least" id="most-least" onchange="this.form.submit()">
                        <label for="most-least">Descending</label>
                    </div>
                    <div>
                        <input type="radio" name="sort-similarity" value="least-most" id="least-most" checked onchange="this.form.submit()">
                        <label for="least-most">Ascending</label>
                    </div>
                    """;    
        }
        else {
            html += """
                    <div>
                        <input type="radio" name="sort-similarity" value="most-least" id="most-least" checked onchange="this.form.submit()">
                        <label for="most-least">Descending</label>
                    </div>
                    <div>
                        <input type="radio" name="sort-similarity" value="least-most" id="least-most" onchange="this.form.submit()">
                        <label for="least-most">Ascending</label>
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
                <h1>Search Area by Similarity</h1>
                <table>
                """;

        if (selectedArea == null || selectedArea.equals("Please Select")) {
            html += """
                    <caption>Please select a country or region</caption>
                    <thead>
                        <tr>
                            <th>Similarity Rank</th>
                            <th>Food Group</th>
                        </tr>
                    <thead>
                    """;
        }
        else if (selectedAmount == null || selectedAmount.equals("0")) {
            html += "<caption>" + selectedArea + "</caption>";
            html += """
                    <thead>
                        <tr>
                            <th>Similarity Rank</th>
                            <th>Food Group</th>
                        </tr>
                    <thead>
                    """;
        }
        else {
            html += "<caption>" + selectedArea + "</caption>";
            html += "<thead>";
            html += "<tr>";

            boolean ascendingSearch;
            if (sortBySim.equals("most-least")){ascendingSearch = false;}
            else {ascendingSearch = true;}

            try {
                if (selectedYear == null || selectedAmount == null || selectedArea == null){}
                else if (searchOption.equals("search-common-foods")) {
                    html += JDBCConnection.getST3ACommonFoodTable(selectedArea, Integer.parseInt(selectedYear), Integer.parseInt(selectedYear), true, selectedAmount, ascendingSearch);
                }
                else if (searchOption.equals("search-loss-%")) {
                    html += JDBCConnection.getST3ALossPercentageTable(selectedArea, Integer.parseInt(selectedYear), Integer.parseInt(selectedYear), selectedAmount, ascendingSearch);
                }
                else {
                    html += JDBCConnection.getST3ACommonFoodAndLossPercentageTable(selectedArea, Integer.parseInt(selectedYear), Integer.parseInt(selectedYear), selectedAmount, true, ascendingSearch);
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }

        html += """
                </table>
            </div>
                """;
        html += "<h1>" + selectedArea + " " + selectedAmount + "</h1>";
        
        html += "</div></body></html>";

        context.html(html);
    }
}