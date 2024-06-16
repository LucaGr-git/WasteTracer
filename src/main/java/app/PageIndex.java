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

public class PageIndex implements Handler {

    // URL of this page relative to http://localhost:7001/
    public static final String URL = "/";

    @Override
    public void handle(Context context) throws Exception {
        String html = "<html>";

        html += "<head>" + 
               "<title>WasteTracer - Homepage</title>";

        html += "<link rel='stylesheet' type='text/css' href='common.css'>" +
                "<link rel='stylesheet' type='text/css' href='landing.css'>";
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

        html += """
            <div class="content" style=
            'background-image: url(landing-background.jpg);
            background-size: cover;
            height: 86vh;'>
                <div class="landing-box">
                    <h1>Waste Tracer</h1>
                    <p class="landing-slogan">Your food, your knowledge</p>
                    <div class="landing-info">
                        <p>In 2013, 65% of all Australian cauliflowers were lost</p>
                        <p>In 2001, 65% of all South Korean strawberries were lost</p>
                        <p>In 1974, 65% of all Nigerian cow peas were lost</p>
                    </div>
                    <p class="landing-slogan">Learn all about food waste in a variety of different regions from 1966 to 2022</p>
                </div>
            </div>
        """;

        html += "</body>" + "</html>";

        context.html(html);
    }
}
