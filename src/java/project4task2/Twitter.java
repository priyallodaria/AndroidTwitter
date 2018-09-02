package project4task2;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

@WebServlet(name = "Twitter1", urlPatterns = {"/Twitter", "/Twitter/*"})
public class Twitter extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //name gives the path after / containing name=value&count=value
        String name = request.getPathInfo().substring(1);
        //get the search term given as the value in name=value
        String search = name.substring(5, name.indexOf("&"));
        //gets the count term given as the value in count=value
        String count_input = name.substring(name.indexOf("&") + 7);
        //converts the count to integer
        int count = Integer.parseInt(count_input);
        //reference : https://github.com/yusuke/twitter4j/blob/master/twitter4j-examples/src/main/java/twitter4j/examples/search/SearchTweets.java
        //reference : http://twitter4j.org/en/configuration.html
        try {
            //use ConfigurationBuilder class to configure Twitter4J
            ConfigurationBuilder cf = new ConfigurationBuilder();
            //setup twitter client and puts the debug level logging as enabled
            cf.setDebugEnabled(true)
                    .setOAuthConsumerKey("9yq1cG0ZDcBw4RzTCPT8eRN2X")
                    .setOAuthConsumerSecret("1oOWHFaLwm6J9T29pIxKCKFVyji5pIvMq03FuRNPVDxaWnMhEb")
                    .setOAuthAccessToken("975066452305817600-VKP5ujZ0o0CBltaeKZAZtkrzYLLf2CV")
                    .setOAuthAccessTokenSecret("fVZqf4aX07RDmFvWjo6cGouLAHAvPVnpDU98C7x3JZKB6")
                    //allows the retrieval of the entire tweet
                    .setTweetModeExtended(true);
            //create an instance of twitter factory using the credentials
            TwitterFactory tf = new TwitterFactory(cf.build());
            //creates an instance of twitter4j
            twitter4j.Twitter twitter = tf.getInstance();
            //puts the search term in the Query class to retrieve the tweet for a given search term
            Query query = new Query(search);
            //sets the count to 15
            query.setCount(15);
            //sets the language to english
            query.setLang("en");
            //stores the results in the QueryResult class for the given query
            QueryResult result = twitter.search(query);
            //uses out object to write
            PrintWriter out = response.getWriter();
            //stores the json string to display the username, tweet, retweet count and createdDate
            String json;
            //get the user agent for the android device used
            String response_Android = request.getHeader("User-Agent");
            //get the start time of the header
            String timestamp = request.getHeader("x-request-start");
            //get the ip address where the header is forwaded to
            String ip = request.getHeader("x-forwarded-for");
            //get the total route time of retrieving the values from heroku to the device
            String routeTime = request.getHeader("total-route-time");
            //get the types it accepts
            String accept = request.getHeader("accept-encoding");
            //get the server of the response
            String proto = request.getHeader("x-forwarded-proto");
            //hostconnection user-agent accept-encoding x-request-id x-forwarded-for x-forwarded-proto x-forwarded-port viaconnect-time x-request-start total-route-time
            int i = 1;          
            //for each status in result.getTweets
            for (Status status : result.getTweets()) {
                //and for i <=count, and if the username, tweet, retweet count and createdDate are not null
                if (i <= count && !(status.getUser().getScreenName() == null || status.getText() == null || status.getCreatedAt().toString() == null || String.valueOf(status.getRetweetCount()) == null)) {
                    //increament i till i<= count entered by the user
                    i++;
                    //format the json string and sendPost to mlab
                    json = "{\"Username\": \"" + status.getUser().getScreenName() + "\",\"Tweet\": \"" + status.getText().replaceAll("\\n", "") + "\",\"RetweetCount\": \"" + status.getRetweetCount() + "\",\"CreatedAt\": \"" + status.getCreatedAt() + "\",\"Device\": \"" + response_Android + "\",\"Search\": \"" + search + "\",\"Count\": \"" + count + "\",\"Timestamp\": \"" + timestamp + "\",\" IP\": \"" + ip + "\",\"RouteTime\": \"" + routeTime + "\",\"Accept\": \"" + accept + "\",\"Protocol\": \"" + proto + "\"}";
                    int responseCode = sendPOST(json);
                    //format the json string and display it on the web
                    if (responseCode == 200) {
                        String json_android = "{\"Username\": \"" + status.getUser().getScreenName() + "\",\"Tweet\": \"" + status.getText().replaceAll("\\n", "") + "\",\"RetweetCount\": \"" + status.getRetweetCount() + "\",\"CreatedAt\": \"" + status.getCreatedAt() + "\"}";
                        out.println(json_android);
                    }
                }
            }
        } catch (TwitterException ex) {
            Logger.getLogger(Twitter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static int sendPOST(String post_params) throws IOException {
        //URL of mlab object is created
        URL obj = new URL("https://api.mlab.com/api/1/databases/project4task2/collections/Twitter?apiKey=6RVAgrsMlE94FQTQkT-ZuZ_DanXeX6OK");
        //connect to the URL and open connection
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        //connection of type POST, set request property to json
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        //set do output to true
        con.setDoOutput(true);
        // write to POST data area and display on mlab
        OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
        out.write(post_params);
        out.close();
        //get response code
        int responseCode = con.getResponseCode();
        return responseCode;
    }
}
