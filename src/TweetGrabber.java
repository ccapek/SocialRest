import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.*;
import java.util.List;

//Klasse, die die Anfragen an Twitter stellt
public class TweetGrabber {
    // Deklarierung der Authentifizierungsschlüssel, zum Zugriff auf die Twitter API
    static private String consumerKey = "jT0fjDmo7RrP669VB7ZZNqIE1";
    static private String consumerSecret = "JOsAzaOWaNMdYzrD0EYzlR1Y7NSlMqpq4u4upab6knhcfbX52H";
    static private String accessToken = "1067786733973110785-mskNXoPWKv9AXseIJofPth3mUFYf0j";
    static private String accessTokenSecret = "kKOIrF40wACyT3wM4tJ9d3QgOyvv4POkFyLW5JUQjle0r";


    //Zugriff auf Klasse TwitterFactory --> Bestandteil der twitter4J-Bibliothek, kommuniziert im hintergrund
    // mit der Twitter-API und übernimmt Formulierung der Anfragen
    private Twitter getAuthTweetFactory() {
        // ein Objekt der Klasse TwitterFactory mit dem Namen "twitter" wird erstellt
        Twitter twitter = new TwitterFactory().getInstance();
        // Authentizifierung mittels Objekt "twitter"
        twitter.setOAuthConsumer(consumerKey, consumerSecret);
        twitter.setOAuthAccessToken(new AccessToken(accessToken, accessTokenSecret));

        return twitter;
    }

    //die Tweets werden in das JSON-Dateiformat konvertiert und als Liste ausgegeben
    public JSONObject tweetsToJSON(List<Status> tweets) {

        JSONObject obj = new JSONObject(); //ein JSONObjekt mit dem Namen "obj" wird erstellt

        //sobald ein Tweet vorhanden ist (!= null) --> Sprung in innere Schleife
        if (tweets != null) {
            for (Status tweet : tweets) {
                //es wird ein weiteres JSONObjekt mit dem Namen "tweet_json" erzeugt; Grund: in ein JSON-Objekt können
                //quasi immer nur zwei Werte gespeichert werden, wir möchten aber drei am Ende ausgeben, daher wird
                //zunächst in dem "inneren" Objekt (tweet_json) der UserName und der Inhalt des Tweets gespeichert
                //und schließlich wird dieses Objekt, welches diese zwei Informationen enthält
                //noch mit der Tweet-ID zusammen in dem "äußeren" Objekt (obj) gespeichert
                JSONObject tweet_json = new JSONObject();

                //dem Objekt "tweet_json" wird Username (ScreenName) und Inhalt des Tweets (Text) zugeordnet
                tweet_json.put("@" + tweet.getUser().getScreenName(), tweet.getText());

                //dem Objekt "obj" wird nun die Tweet-ID sowie das im Schritt zuvor erstellte Objekt "tweet_json" zugeordnet
                obj.put(Long.toString(tweet.getId()), tweet_json);

                System.out.println(tweet_json);
                System.out.println(obj);
            }
        }

        return obj; //Rückgabe des erstellten Objekts "obj" mit allen drei Informationen im JSON-Format
    }


    //Abfrage für Hashtags wird erstellt
    //query_string entspricht dem für QUERY eingegebenen Wert
    public List<Status> getTweetsByHashtag(String query_string) {
        System.out.println("getHashtag");
        //zuvor erstelltes TwitterFactory-Objekt "twitter" wird für authentifizierte Suchanfragen genutzt
        Twitter twitter = getAuthTweetFactory();

        List<Status> tweets = null; //am Anfang leere Liste

        // Mache die Twitterabfrage mit hashtag
        try {
            Query query = new Query("#"+query_string); // neues Query Objekt wird erzeugt
                                                      // query wird um Hashtag-Symbol(#) ergänzt
            QueryResult result;
            do {
                //Start der Anfrage an Twitter mittels bereitgestellten Funktionen aus der Klasse TwitterFactory
                result = twitter.search(query);
                tweets = result.getTweets();
                for (Status tweet : tweets) {

                    System.out.println("@" + tweet.getUser().getScreenName() + " - " + tweet.getText());
                }
            } while ((query = result.nextQuery()) != null);
        } catch (TwitterException te) { //Ausnahmenbehandlung
            te.printStackTrace();
            System.out.println("Failed to search tweets: " + te.getMessage());
        }

        return tweets;
    }


    //Abfrage für Ort wird erstellt
    public List<Status> getTweetsByLocation(String country, String city) {
        System.out.println("getLocation");
        Twitter twitter = getAuthTweetFactory();

        List<Status> tweets = null;
        List<Place> places = null;
        String place_id = null;


        try {
            System.out.println("Search for locations");

            //in Twitter werden die  unterschiedlichen Orte mit einer place_id verwaltet,
            //hier wird zum eingegebenen Ort (city) die richtige place_id ermittelt
            GeoQuery geo = new GeoQuery((String)null);
            geo.setQuery(city);
            places = twitter.searchPlaces(geo);

            // Überprüfung des Länder-Codes
            for (Place place : places) {
                // if place DE == DE => 0 -> links wird zu false -> place city = city => 0 => false
                if(place.getCountryCode().compareToIgnoreCase(country) != 0 || place.getPlaceType().compareToIgnoreCase("city") != 0) {
                    continue;
                } else {
                    place_id = place.getId();
                    break;
                }
            }

        }catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to search location: " + te.getMessage());
        }

        // Mache die Twitterabfrage mit Ort mittels zuvor ermittelter place_id
        try{
            Query query = new Query("place:"+place_id);
            QueryResult result;
            do {
                result = twitter.search(query);
                tweets = result.getTweets();
                for (Status tweet : tweets) {

                    System.out.println("@" + tweet.getUser().getScreenName() + " - " + tweet.getText());
                }
            } while ((query = result.nextQuery()) != null);
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to search tweets: " + te.getMessage());
        }

        return tweets;
    }

    //Abfrage für den Ort unter Angabe der Koordinaten
    public List <Status> getTweetsByGeoLocation(double breite, double laenge, double radius)
    {
        System.out.println("getGeoLocation");
        Twitter twitter = getAuthTweetFactory();
        List<Status> tweets = null;

        try {
            Query query = new Query();
            //erstellen der GeoLocation mittels übergebenen Werten für breite, länge und Radius,
            //wird dem Query-Objekt als GeoCode zugewiesen
            query.setGeoCode(new GeoLocation(breite, laenge), radius , Query.KILOMETERS);
            QueryResult result;

            do {
                result = twitter.search(query);
                tweets = result.getTweets();

                for (Status tweet : tweets) {
                    System.out.println("@" + tweet.getUser().getScreenName() + " - " + tweet.getText());
                }
            } while ((query = result.nextQuery()) != null);
        }
        catch (TwitterException te) {
            System.out.println("Failed to search tweets: " + te.getMessage());

        }

        return tweets;
    }

    //Kombination aus Ort und Hashtag
    public List<Status> getTweetsByLocation(String country, String city, String hashtag) {
        System.out.println("getCombination");
        Twitter twitter = getAuthTweetFactory();

        List<Status> tweets = null;
        List<Place> places = null;
        String place_id = null;

        try {
            System.out.println("Search for locations");

            GeoQuery geo = new GeoQuery((String)null);
            geo.setQuery(city);
            places = twitter.searchPlaces(geo);

            // finde richtigen Ort
            for (Place place : places) {
                // if place DE == DE => 0 -> links wird zu false -> place city = city => 0 => false
                if(place.getCountryCode().compareToIgnoreCase(country) != 0 || place.getPlaceType().compareToIgnoreCase("city") != 0) {
                    continue;
                } else {
                    place_id = place.getId();
                    break;
                }
            }

        }catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to search location: " + te.getMessage());
        }

        // Mache die Twitterabfrage mit hashtag und ort
        try{
            Query query = new Query("#"+hashtag+"&place:"+place_id);
            QueryResult result;
            do {
                result = twitter.search(query);
                tweets = result.getTweets();
                for (Status tweet : tweets) {

                    System.out.println("@" + tweet.getUser().getScreenName() + " - " + tweet.getText());
                }
            } while ((query = result.nextQuery()) != null);
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to search tweets: " + te.getMessage());
        }

        return tweets;
    }
}

