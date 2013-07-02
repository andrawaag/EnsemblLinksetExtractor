/* CVS $Id: $ */
 
import com.hp.hpl.jena.rdf.model.*;
 
public class Dcat {
    /** <p>The RDF model that holds the vocabulary terms</p> */
    private static final Model m_model = ModelFactory.createDefaultModel();
    
    /** <p>The namespace of the vocabulary as a string</p> */
    public static final String NS = "http://www.w3.org/ns/dcat#";
    
    /** <p>The namespace of the vocabulary as a string</p>
     *  @see #NS */
    public static String getURI() {return NS;}
    
    /** <p>The namespace of the vocabulary as a resource</p> */
    public static final Resource NAMESPACE = m_model.createResource( NS );
    
    public static final Property landingPage = m_model.createProperty("http://www.w3.org/ns/dcat#landingPage" );

    public static final Property downloadURL = m_model.createProperty("http://www.w3.org/ns/dcat#downloadURL" );

    public static final Property mediaType = m_model.createProperty("http://www.w3.org/ns/dcat#mediaType" );
}
