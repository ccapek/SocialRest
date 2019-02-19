import twitter4j.JSONObject;
import javax.ws.rs.*;

@Path(REST_API.webContextPath)
//Klasse die die unterschiedlichen Ressourcen bereitstellt
public class REST_API {

    //definiert die Base-URI für alle Resourcen-URIs --> /
    static final String webContextPath = "/";


    @GET //lesender Zugriff auf Ressource mittels HTTP GET-Operation
    @Produces("text/plain")  //Ausgabe in MIME-type "text/plain" --> einfacher Text
    //Anzeige beim Aufruf der Base-URI im Browser
    public String getClichedMessage() {
        return "Stellen Sie ihre Anfrage bitte in einer der folgenden Formen:\n\n" +
                "Suche nach Hashtag: /hashtag/QUERY\n" +
                "Suche nach Ort: /location/COUNTRY/CITY\n"+
                "Suche nach Ort unter Angabe der Koordinaten: /geolocation/QUERY/LATITUDE/LONGITUDE/RADIUS\n"+
                "kombinierte Suche: /hashtag/QUERY/COUNTRY/CITY oder: /location/COUNTRY/CITY/QUERY\n\n" +
                "Ergaenzen Sie dabei \n" +
                "QUERY durch den gesuchten Hashtag,\n" +
                "COUNTRY durch das entsprechende Laenderkuerzel, \n" +
                "CITY durch den Namen der Stadt,\n" +
                "LATITUDE durch den gesuchten Breitengrad,\n" +
                "LONGITUDE durch den gesuchten Laengengrad\n" +
                "und RADIUS durch den Radius[km], den sie ausgehend von den Koordinaten betrachten moechten.";
    }

    @GET
    @Produces("application/json")  //Ausgabe in Datenformat JSON
    @Path("/hashtag/{hashtag}") // Adressierung der Ressource über den Pfad BASE-URI/hashtag/<eingegebener Hashtag>

    //Bereitstellen der Ressource für die Hashtag-Suche; eingegebener Wert für Hashtag
    //wird in Variable "hashtag" gespeichert
    public String getHashtags(@PathParam("hashtag") String hashtag) {
        TweetGrabber myGrabber = new TweetGrabber(); //Erstellt neues Objekt der Klasse TweetGrabber mit Name "myGrabber"
        JSONObject result = null;

        try{
            // --> Verbindung zwischen Webservice und Twitter-Client
            //-mit myGrabber.getTweetsByHashtag(hashtag) greift die REST_API auf den TweetGrabber zu, übergibt ihm den
            // eingegebenen Parameter "hashtag", fordert TweetGrabber auf die Hashtag-Anfrage an Twitter zu stellen
            //-mit myGrabber.tweetsToJSON fordert die REST_API den TweetGrabber dazu auf, das Ergebnis der Hashtag-
            //Anfrage aus der Klammer in der Methode tweetsToJSON in das JSON-Dateiformat zu konvertieren
            result = myGrabber.tweetsToJSON(myGrabber.getTweetsByHashtag(hashtag));
                    System.out.println(result);
        }
        catch (Exception e){  //Ausnahmenbehandlung
            System.out.println(e);
        }
        //result als String ausgeben
        return result.toString();
    }

    @GET
    @Produces("application/json")
    @Path("/location/{country}/{city}") // Adressierung der Ressource über
    // BASE-URI/location/<eingegebenes Länderkürzel>/<eingegebene Stadt>

    //Bereitstellen der Ressource zur Orts-Suche;
    // eingegebener Wert für Länderkürzel wird in Variable "country" gespeichert
    // eingegebener Wert für Stadt wird in Variable "city" gespeichert
    public String getLocations(@PathParam("country") String country, @PathParam("city") String city) {
        TweetGrabber myGrabber = new TweetGrabber();
        JSONObject result = null;

        try{
            result = myGrabber.tweetsToJSON(myGrabber.getTweetsByLocation(country, city));
            System.out.println(result);
        }
        catch (Exception e){
            System.out.println(e);
        }
        return result.toString();
    }

    //Bereitstellen der Ressource zur Orts-Suche unter Angabe der Geo-Koordinaten;
    @GET
    @Produces("application/json")
    @Path("/geolocation/{breite}/{laenge}/{radius}")
    // eingegebener Wert für Breitengrad wird in Variable "breite" gespeichert
    // eingegebener Wert für Längengrad wird in Variable "laenge" gespeichert
    // eingegebener Wert für Radius wird in Variable "radius" gespeichert

    //double anstatt String, da in diesem Fall ausschließlich Zahlen übergeben werden
    public String getGeoLocation( @PathParam("breite") double breite, @PathParam("laenge") double laenge,
                                  @PathParam("radius") double radius) {
        TweetGrabber myGrabber = new TweetGrabber();
        JSONObject result = null;

        try{
            result = myGrabber.tweetsToJSON(myGrabber.getTweetsByGeoLocation(breite, laenge, radius));
            System.out.println(result);
        }
        catch (Exception e){
            System.out.println(e);
        }
        return result.toString();
    }

    @GET
    @Produces("application/json")
    @Path("/hashtag/{hashtag}/{country}/{city}")
    //Bereitstellung der Ressource zur Hashtag- und Ort-Suche (Kombination)
    public String getCombination(@PathParam("hashtag") String hashtag, @PathParam("country") String country, @PathParam("city") String city) {
        TweetGrabber myGrabber = new TweetGrabber();
        JSONObject result = null;

        try{
            result = myGrabber.tweetsToJSON(myGrabber.getTweetsByLocation(country, city, hashtag));
            System.out.println(result);
        }
        catch (Exception e){
            System.out.println(e);
        }
        return result.toString();
    }



    @GET
    @Produces("application/json")
    @Path("/location/{country}/{city}/{hashtag}") //wie getCombination, ermöglicht Adressierung über alternativen Pfad
    public String getCombinationAlt(@PathParam("country") String country, @PathParam("city") String city, @PathParam("hashtag") String hashtag) {
        TweetGrabber myGrabber = new TweetGrabber();
        JSONObject result = null;

        try {
            result = myGrabber.tweetsToJSON(myGrabber.getTweetsByLocation(country, city, hashtag));
            System.out.println(result);
        } catch (Exception e) {
            System.out.println(e);
        }
        return result.toString();
    }
}