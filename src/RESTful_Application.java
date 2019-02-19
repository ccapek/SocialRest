import javax.ws.rs.ApplicationPath;
import java.util.HashSet;
import java.util.Set;

//definiert die Base-URI für alle Resourcen-URIs --> /
@ApplicationPath("/")
//Die Java Klasse deklariert die Root-Ressource und Provider-Klassen
public class RESTful_Application extends javax.ws.rs.core.Application {
    //Die Methode gibt eine nicht-leere Sammlung mit Klassen zurück,
    //welche in der veröffentlichten JAX-RS Anwendung enthalten sein muss
    @Override
    public Set<Class<?>> getClasses() {
        HashSet h = new HashSet<Class<?>>();
        h.add( REST_API.class );
        return h;
    }
}