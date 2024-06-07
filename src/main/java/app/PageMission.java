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

public class PageMission implements Handler {

    // URL of this page relative to http://localhost:7001/
    public static final String URL = "/mission.html";

    @Override
    public void handle(Context context) throws Exception {

        String html = "<html>";

        html += "<head>" + 
            "<title>Waste Tracer - Mission</title>";

        html += "<link rel='stylesheet' type='text/css' href='common.css'>" +
                "<link rel='stylesheet' type='text/css' href='mission.css'>";
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

        html += """
            <div class="content">
                <img class="mission-image" src="mission-photo.jpg">
                <div>
                    <h1>Our Mission</h1>
                    <p>We designed this website to give people an open and unbiased resource on food waste. Big or small, we believe that making a change for the better should always be informed by reliable information. Waste tracer is a website built to provie said information and combat the problem of food waste.</p>
                    <p>It has statistics ranging from 1966 - 2022 relating to a variety of countries and regions all around the world. These statistics detail the rate of food loss for a myriad of different food groups and commoditites</p>
                    <p>This website cna be used to search for food loss statistic for a speicifc country or a specific food group. It can alos be used to find similar countries and food groups to a user-chosen option based on statistical similarity.</p>
                    <p>With this website we hope that people find relevant information to them and make a change for the better.</p>
                </div>
            </div>
        """;

        
        html += "</body>" + "</html>";
        
        context.html(html);
    }

}
