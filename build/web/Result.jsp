<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Dashboard</title>
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <style>
            /* reference : https://www.w3schools.com/howto/tryit.asp?filename=tryhow_js_vertical_tabs 
            used to style vertical tabs to display analtyical information*/
            * {box-sizing: border-box}
            body {font-family: "Lato", sans-serif;}

            /* Style the tab */
            .tab {
                float: left;
                border: 1px solid #ccc;
                background-color: #f1f1f1;
                width: 30%;
                height: 300px;
            }

            /* Style the buttons inside the tab */
            .tab button {
                display: block;
                background-color: inherit;
                color: black;
                padding: 22px 16px;
                width: 100%;
                border: none;
                outline: none;
                text-align: left;
                cursor: pointer;
                transition: 0.3s;
                font-size: 17px;
            }

            /* Change background color of buttons on hover */
            .tab button:hover {
                background-color: #ddd;
            }

            /* Create an active/current "tab button" class */
            .tab button.active {
                background-color: #ccc;
            }

            /* Style the tab content */
            .tabcontent {
                float: left;
                padding: 0px 12px;
                border: 1px solid #ccc;
                width: 70%;
                border-left: none;
                height: 300px;
            }
            /* reference : https://www.w3schools.com/html/tryit.asp?filename=tryhtml_table_cellpadding 
            formats the table
            */
            th, td {
                border: 1px solid black;
                border-collapse: collapse;
            }
            th, td {
                padding: 8px;
            }
        </style>
    </head>
    <body>       
        <!-- reference : https://www.w3schools.com/html/html_tables.asp -->
        <!-- display the logging information -->
        <h2>Logging Information</h2>
        <table style="width:100%">  
            <tr><th>Device</th><th>IP crossed</th><th>Start Timestamp</th><th>Route Time</th><th>Accept Encoding</th><th>Protocol</th><th>Limit</th></tr>
                    <%= request.getAttribute("log")%>  
        </table>
        <!-- display the analytics -->
        <h2>Analytics from mobile interactions with Twitter</h2>
        <!-- reference : https://www.w3schools.com/howto/tryit.asp?filename=tryhow_js_vertical_tabs -->
        <p>Click on the buttons inside the tabbed menu:</p>
        <!-- formatting of the analytics -->
        <div class="tab">
            <button class="tablinks" onclick="openParameter(event, 'username')" id="defaultOpen"><%= request.getAttribute("counter_username")%> Different username(s) tweeted</button>
            <button class="tablinks" onclick="openParameter(event, 'retweet')">Order of the users by maximum number of retweets</button>
            <button class="tablinks" onclick="openParameter(event, 'username_count')">Number of different usernames</button>
            <button class="tablinks" onclick="openParameter(event, 'tweet')">Number of tweets requested</button>
            <button class="tablinks" onclick="openParameter(event, 'average_tweet')">Average number of tweets requested</button>
            <button class="tablinks" onclick="openParameter(event, 'total_tweet')">Total length of all tweets</button>
            <button class="tablinks" onclick="openParameter(event, 'tweet_length')">Average number of tweet length</button>
        </div>
        <div id="username" class="tabcontent">
            <h4> <%= request.getAttribute("username_send")%> </h4>
        </div>
        <div id="retweet" class="tabcontent">
            <h4>Count:Username</h4>
            <h4> <%= request.getAttribute("retweet_send")%> </h4> 
        </div>
        <div id="username_count" class="tabcontent">  
            <h4> <%= request.getAttribute("user")%> </h4>
        </div>
        <div id="tweet" class="tabcontent"> 
            <h4> <%= request.getAttribute("tweet")%> </h4>
        </div> 
        <div id="average_tweet" class="tabcontent">
            <h4> <%= request.getAttribute("average_tweet")%> </h4> 
        </div>
        <div id="total_tweet" class="tabcontent">  
            <h4> <%= request.getAttribute("total_tweet")%> </h4>
        </div>
        <div id="tweet_length" class="tabcontent"> 
            <h4> <%= request.getAttribute("tweet_length")%></h4>
        </div>  
        <!-- reference : https://www.w3schools.com/howto/tryit.asp?filename=tryhow_js_vertical_tabs -->
        <script>
            function openParameter(evt, parameter) {
                var i, tabcontent, tablinks;
                tabcontent = document.getElementsByClassName("tabcontent");
                for (i = 0; i < tabcontent.length; i++) {
                    tabcontent[i].style.display = "none";
                }
                tablinks = document.getElementsByClassName("tablinks");
                for (i = 0; i < tablinks.length; i++) {
                    tablinks[i].className = tablinks[i].className.replace(" active", "");
                }
                document.getElementById(parameter).style.display = "block";
                evt.currentTarget.className += " active";
            }

            // Get the element with id="defaultOpen" and click on it
            document.getElementById("defaultOpen").click();
        </script>
    </body>
</html>

