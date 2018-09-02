package project4task2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@WebServlet(name = "mongoDB", urlPatterns = {"/mongoDB"})
public class MongoDB extends HttpServlet {

    //create arrays of the every componenet of the json string on mlab
    String username[];
    int retweet[];
    String tweet[];
    String date[];
    String version[];
    String search[];
    int count[];
    String ip[];
    String routeTime[];
    String timestamp[];
    String accept[];
    String proto[];
    String limit[];
    //p is used at a counter at multiple places
    int p;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //get the json string from mlab containing the entire database
        String output = sendGET();
        //used to write to the browser
        PrintWriter out = response.getWriter();
        if (output != null) {
            //parse the output to get inidividual parameter arrays
            jsonParse(output);
            String log = "";
            // reference : https://www.w3schools.com/html/html_tables.asp
            //making a table for the logging information
            for (int i = 0; i < version.length; i++) {
                log += "<tr><th>" + version[i] + "</th><th>" + ip[i] + "</th><th>" + timestamp[i] + "</th><th>" + routeTime[i] + "</th><th>" + accept[i] + "</th><th>" + proto[i]+ "</th><th>" + limit[i] + "</th></tr><br>";
                i = i + count[i] - 1;
            }
            //sending the logging information
            request.setAttribute("log", log);
            int counter = 0;
            //used to display the count of the rows in the output recieved
            p = 1;
            //vertemp stores all different types of usernames used
            String[] vertemp = new String[username.length];
            //store sorted username array
            String usernameSort[] = new String[version.length];
            //copy from username to usernamesort
            System.arraycopy(username, 0, usernameSort, 0, version.length);
            //sort username array
            Arrays.sort(usernameSort);
            //for each tweet
            for (int i = 0; i < usernameSort.length - 1; i++) {
                //don't put duplicate usernames
                if (!usernameSort[i].equals(usernameSort[i + 1])) {
                    vertemp[counter++] = usernameSort[i];
                }
            }
            //display the different types of usernames
            vertemp[counter++] = usernameSort[usernameSort.length - 1];
            //display the different types of ips
            request.setAttribute("counter_username", counter);
            String username_send = "";
            for (int i = 0; i < counter; i++) {
                username_send += p++ + " : " + vertemp[i] + "<br>";
            }
            request.setAttribute("username_send", username_send);
            //n is the length of the array
            int n = retweet.length;
            //temp_retweet is used to sort the retweet array
            int temp_retweet;
            //temp_username is used to sort the username Array
            String temp_username;
            //used to carry out retweet and string operations 
            int retweet_temp[] = new int[n];
            String username_temp[] = new String[n];
            //copy the username and retweet arrays to their respective temp arrays
            for (int i = 0; i < n; i++) {
                retweet_temp[i] = retweet[i];
                username_temp[i] = username[i];
            }
            //sort the retweet array in a descending order and the username accordingly
            for (int i = 0; i < n; i++) {
                for (int j = 1; j < (n - i); j++) {
                    if (retweet_temp[j - 1] > retweet_temp[j]) {
                        temp_retweet = retweet_temp[j - 1];
                        retweet_temp[j - 1] = retweet_temp[j];
                        retweet_temp[j] = temp_retweet;

                        temp_username = username_temp[j - 1];
                        username_temp[j - 1] = username_temp[j];
                        username_temp[j] = temp_username;
                    }
                }
            }
            //stores the username and retweets respectively after removing duplicate usernames
            String[] usernameTemp = new String[n];
            int retweetTemp[] = new int[n];
            //user is a counter for the new temp arrays
            int user = 0;
            //removes duplicate usernames and stores the new values in usernameTemp and retweetTemp
            for (int i = 0; i < n - 1; i++) {
                if (!username_temp[i].equals(username_temp[i + 1])) {
                    usernameTemp[user++] = username_temp[i];
                    retweetTemp[user - 1] = retweet_temp[i];
                }
            }
            //store the last value
            usernameTemp[user++] = username_temp[n - 1];
            retweetTemp[user - 1] = retweet_temp[n - 1];
            String retweet_send = "";
            //display the order of the users by maximum number of retweets
            for (int i = user - 1; i >= 0; i--) {
                if (retweetTemp[i] != 0) {
                    retweet_send += (retweetTemp[i] + ": " + usernameTemp[i] + "<br>");
                }
            }
            request.setAttribute("retweet_send", retweet_send);
            //display the number of usernames
            request.setAttribute("user", user);
            int total_tweet = 0;
            //display the number of tweets requested
            request.setAttribute("tweet", count.length);
            //display the average number of tweets requested = (total number of tweets requeted/total no of users)
            request.setAttribute("average_tweet", ((double) count.length / user));
            //calculate the total length of tweets
            for (int i = 0; i < tweet.length; i++) {
                total_tweet += tweet[i].length();
            }
            //display the total length of the tweets
            request.setAttribute("total_tweet", total_tweet);
            //display the average tweet length
            request.setAttribute("tweet_length", (double) total_tweet / tweet.length);
            RequestDispatcher view = request.getRequestDispatcher("Result.jsp");
            view.forward(request, response);
        }
    }

    private static String sendGET() throws IOException {
        try {
            //pass the name on the URL line
            URL obj = new URL("https://api.mlab.com/api/1/databases/project4task2/collections/Twitter?apiKey=6RVAgrsMlE94FQTQkT-ZuZ_DanXeX6OK");
            //connect to the url using method = GET
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            //get the response code
            int responseCode = con.getResponseCode();
            //if it is OK
            if (responseCode == HttpURLConnection.HTTP_OK) {
                //store the entire database
                StringBuilder response;
                try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                    String inputLine;
                    response = new StringBuilder();
                    //store the entire database line by line in response
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                        response.append("\n");
                    }
                }
                //return the database
                return response.toString();

            }
        } catch (ProtocolException | MalformedURLException ex) {
            Logger.getLogger(MongoDB.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        //if any error return null
        return null;
    }

    void jsonParse(String output) {
        //http://www.java67.com/2016/10/3-ways-to-convert-string-to-json-object-in-java.html
        try {
            //parser the entire database into a json array
            JSONParser parser = new JSONParser();
            JSONArray jsonArray = (JSONArray) parser.parse(output);
            //create arrays for each parameter of the json string
            username = new String[jsonArray.size()];
            tweet = new String[jsonArray.size()];
            retweet = new int[jsonArray.size()];
            date = new String[jsonArray.size()];
            version = new String[jsonArray.size()];
            search = new String[jsonArray.size()];
            count = new int[jsonArray.size()];
            timestamp = new String[jsonArray.size()];
            routeTime = new String[jsonArray.size()];
            ip = new String[jsonArray.size()];
            accept = new String[jsonArray.size()];
            proto = new String[jsonArray.size()];
            limit = new String[jsonArray.size()];
            //for each tweet in the database, retrieve each parameter and store it in the respective parameter array
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject json = (JSONObject) jsonArray.get(i);
                username[i] = (String) json.get("Username");
                tweet[i] = (String) json.get("Tweet");
                retweet[i] = Integer.parseInt((String) json.get("RetweetCount"));
                date[i] = (String) json.get("CreatedAt");
                version[i] = (String) json.get("Device");
                search[i] = (String) json.get("Search");
                count[i] = Integer.parseInt((String) json.get("Count"));
                routeTime[i] = (String) json.get("RouteTime");
                timestamp[i] = (String) json.get("Timestamp");
                ip[i] = (String) json.get(" IP");
                accept[i] = (String) json.get("Accept");
                limit[i] = (String) json.get("Limit");
                proto[i] = (String) json.get("Protocol");
            }
        } catch (ParseException ex) {
            Logger.getLogger(MongoDB.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }
}
