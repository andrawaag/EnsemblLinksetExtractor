/* CVS $Id: $ */
 
import com.hp.hpl.jena.rdf.model.*;
 
public class Dul {
    /** <p>The RDF model that holds the vocabulary terms</p> */
    private static final Model m_model = ModelFactory.createDefaultModel();
    
    /** <p>The namespace of the vocabulary as a string</p> */
    public static final String NS = "\"http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#";
    
    /** <p>The namespace of the vocabulary as a string</p>
     *  @see #NS */
    public static String getURI() {return NS;}
    
    /** <p>The namespace of the vocabulary as a resource</p> */
    public static final Resource NAMESPACE = m_model.createResource( NS );
    
    public static final Property expresses = m_model.createProperty("http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#expresses");
}
