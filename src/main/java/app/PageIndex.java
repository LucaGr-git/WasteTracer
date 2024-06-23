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
                <a href='/'><img src='logo.png' width='200'></a>
                <ul class="topnav-links">
                    <div class="subtask">
                        <li>Search Food Loss Data</li>
                        <div class="subtask-dropdown">
                            <a href="/page2A.html">Search by Country</a>
                            <a href="/page2B.html">Search by Food Group</a>
                        </div>
                    </div>
                    <div class="subtask">
                        <li>Search Data by Similarity</li>
                        <div class="subtask-dropdown">
                            <a href="/page3A.html">Search by Country/Region</a>
                            <a href="/page3B.html">Search by Food Group</a>
                        </div>
                    </div>
                </ul>
            </div>
        """;

        html += """
            <div class="hero-content" style='background-image: url(/landing-background.jpg);background-size: cover;height: 88vh;'>
                <div class="hero-box">
                    <h1>Waste Tracer</h1>
                        <p class="hero-slogan">Your food, your knowledge</p>
                        <div class="hero-info">
                            <p>In 2013, 65% of all Australian cauliflowers were lost</p>
                            <p>In 2001, 65% of all South Korean strawberries were lost</p>
                            <p>In 1974, 65% of all Nigerian cow peas were lost</p>
                        </div>
                    <p class="hero-slogan">Learn all about food waste in a variety of different regions from 1966 to 2022</p>
                </div>
            </div>
            <hr>
            <div>
                <div class="about-content">
                    <img class="about-image" src="/mission-photo.jpg">
                    <div class="about-text-area">
                        <div class="about-header">
                            <h1>Our Mission</h1>
                        </div>
                        <div class="about-p">
                            <p>We designed this website to give people an open and unbiased resource on food waste. Big or small, we believe that making a change for the better should always be informed by reliable information. Waste tracer is a website built to provide said information and combat the problem of food waste.</p>
                            <p>It has statistics ranging from 1966 - 2022 relating to a variety of countries and regions all around the world. These statistics detail the rate of food loss for a myriad of different food groups and commoditites</p>
                            <p>This website can be used to search for food loss statistics for a specific country or a specific food group. It can also be used to find similar countries and food groups to a user-chosen option based on statistical similarity.</p>
                            <p>With this website we hope that people can use the information to make a change for the better.</p>
                        </div>
                    </div>
                </div>
            </div>
            <hr>
        """;

        html += "<div class='persona-area'>";
        html += "<h1>Our Personas</h1>";
        html += JDBCConnection.getPersonas();
        html += "</div>";

        html += "</body>" + "</html>";

        context.html(html);
    }
}
