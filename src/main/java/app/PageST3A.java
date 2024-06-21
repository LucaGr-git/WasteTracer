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

    public static final String URL = "/page3A.html";
    public static final String DATABASE = "jdbc:sqlite:database/foodloss.db";
    ArrayList<String> priorCountries = new ArrayList<>();

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

        String selectedCountry = context.formParam("country-selector");

        for (String country : JDBCConnection.getAllCountriesRegions()) {
            if (selectedCountry != null && country.equals(selectedCountry)) {
                html += "<option selected>" + country + "</option>";
            }
            else {
                html += "<option>" + country + "</option>";
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
                                <select id='amount-selector' name='amount-selector' required>
                                    <option>Please Select</option>
                """;
            
        String selectedAmount = (context.formParam("amount-selector") == null || context.formParam("amount-selector").equals("Please Select")) ?
                                "0" :
                                context.formParam("amount-selector");

        //23 is the amount of food groups with available data
        for (int i = 1; i < 392; ++i) {
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
        

        if (selectedCountry != null && !selectedCountry.equals("Please Select")) {
            priorCountries.add(selectedCountry);
        }

        html +=  """
                                </select>                            
                    
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
                    <h4>Search Options</h4>
                    <div class='radio-buttons'>
                        
                """;

        String searchOption = context.formParam("search-options");
        if (searchOption == null) {
            html += """
                    <div>
                        <input type="radio" name="search-options" value="search-common-foods" id="search-common-foods">
                        <label for="search-common-foods">Search by common foods</label>
                    </div>
                    <div>
                        <input type="radio" name="search-options" value="search-loss-%" id="search-loss-%">
                        <label for="search-loss-%">Search by loss %</label>
                    </div>
                    <div>
                        <input type="radio" name="search-options" value="search-loss-%-common-foods" id="search-loss-%-common-foods">
                        <label for="search-loss-%-common-foods">Search by loss % and common foods</label>
                    </div>
                    """;
        }
        else if (searchOption.equals("search-common-foods")) {
            html += """
                    <div>
                        <input type="radio" name="search-options" value="search-common-foods" id="search-common-foods" checked>
                        <label for="search-common-foods">Search by common foods</label>
                    </div>
                    <div>
                        <input type="radio" name="search-options" value="search-loss-%" id="search-loss-%">
                        <label for="search-loss-%">Search by loss %</label>
                    </div>
                    <div>
                        <input type="radio" name="search-options" value="search-loss-%-common-foods" id="search-loss-%-common-foods">
                        <label for="search-loss-%-common-foods">Search by loss % and common foods</label>
                    </div>
                    """;    
        }
        else if (searchOption.equals("search-loss-%")) {
            html += """
                    <div>
                        <input type="radio" name="search-options" value="search-common-foods" id="search-common-foods">
                        <label for="search-common-foods">Search by common foods</label>
                    </div>
                    <div>
                        <input type="radio" name="search-options" value="search-loss-%" id="search-loss-%" checked>
                        <label for="search-loss-%">Search by loss %</label>
                    </div>
                    <div>
                        <input type="radio" name="search-options" value="search-loss-%-common-foods" id="search-loss-%-common-foods">
                        <label for="search-loss-%-common-foods">Search by loss % and common foods</label>
                    </div>
                    """;
        }
        else {
            html += """
                    <div>
                        <input type="radio" name="search-options" value="search-common-foods" id="search-common-foods">
                        <label for="search-common-foods">Search by common foods</label>
                    </div>
                    <div>
                        <input type="radio" name="search-options" value="search-loss-%" id="search-loss-%">
                        <label for="search-loss-%">Search by loss %</label>
                    </div>
                    <div>
                        <input type="radio" name="search-options" value="search-loss-%-common-foods" id="search-loss-%-common-foods" checked>
                        <label for="search-loss-%-common-foods">Search by loss % and common foods</label>
                    </div>
                    """;
        }

        
        
        
        html += """
           
                        </select>
                    
                </div>
                <h4>Sort Search By</h4>
                <div class='radio-buttons'>
                    
                    """;
                            
        String sortBySim = context.formParam("sort-similarity");
        if (sortBySim == null) {
            html += """
                    <div>
                        <input type="radio" name="sort-similarity" value="most-least" id="most-least" checked>
                        <label for="most-least">Most -> Least Similarity</label>
                    </div>
                    <div>
                        <input type="radio" name="sort-similarity" value="least-most" id="least-most">
                        <label for="least-most">Least -> Most Similarity</label>
                    </div>
                    """;
        }
        else if (sortBySim.equals("sort-by-descending")) {
            html += """
                    <div>
                        <input type="radio" name="sort-similarity" value="most-least" id="most-least">
                        <label for="most-least">Most -> Least Similarity</label>
                    </div>
                    <div>
                        <input type="radio" name="sort-similarity" value="least-most" id="least-most" checked>
                        <label for="least-most">Least -> Most Similarity</label>
                    </div>
                    """;    
        }
        else {
            html += """
                    <div>
                        <input type="radio" name="sort-similarity" value="most-least" id="most-least" checked>
                        <label for="most-least">Most -> Least Similarity</label>
                    </div>
                    <div>
                        <input type="radio" name="sort-similarity" value="least-most" id="least-most">
                        <label for="least-most">Least -> Most Similarity</label>
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
                <h1>Search Country/Region by Similarity</h1>
                <table>
                """;

        if (searchOption == null) {
            html += """
                    <caption>Please select select filters and search</caption>
                    <thead>
                        <tr>
                            <th>Similarity Rank</th>
                            <th>Food Group</th>
                        </tr>
                    <thead>
                    """;
        }
        else if (selectedCountry == null || selectedCountry.equals("Please Select")) {
            html += """
                    <caption>Please select a commodity</caption>
                    <thead>
                        <tr>
                            <th>Similarity Rank</th>
                            <th>Food Group</th>
                        </tr>
                    <thead>
                    """;
        }
        else if (selectedAmount == null || selectedAmount.equals("Please Select")) {
            html += "<caption>" + selectedCountry + "</caption>";
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
            html += "<caption>" + selectedCountry + "</caption>";
            html += "<thead>";
            html += "<tr>";

            boolean ascendingSearch;
            if (sortBySim.equals("most-least")){ascendingSearch = false;}
            else{ascendingSearch = true;}


            if (searchOption.equals("search-common-foods")) {
                

                html += JDBCConnection.getST3ACommonFoodTable(selectedCountry, Integer.parseInt(startYear), Integer.parseInt(endYear), true, selectedAmount, ascendingSearch);
            }
            else if (searchOption.equals("search-loss-%")) {

                

                html += JDBCConnection.getST3ALossPercentageTable(selectedCountry, Integer.parseInt(startYear), Integer.parseInt(endYear), selectedAmount, ascendingSearch);
            }
            else {

                
                
                html += JDBCConnection.getST3ACommonFoodAndLossPercentageTable(selectedCountry, Integer.parseInt(startYear), Integer.parseInt(endYear), selectedAmount, true, ascendingSearch);
            }
        }
        

        
        context.html(html);
    }
}