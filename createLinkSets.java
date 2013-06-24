import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Hashtable;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.VCARD;



public class createLinkSets {
	/**
	 * @param args
	 * @throws FileNotFoundException 
	 * @throws UnsupportedEncodingException 
	 */
	public static void justDoIt(String species) throws UnsupportedEncodingException, FileNotFoundException{
		System.out.println(species);
		ResultSet humanDataSourceResultSet = basicCalls.getExternalLinkedDataSources(species);	 
		while (humanDataSourceResultSet.hasNext()) {		
			QuerySolution dataSolution = humanDataSourceResultSet.next();
			String dataSource = dataSolution.get("dbName").toString();	
			Model voidDescriptionModel = ModelFactory.createDefaultModel();
			basicCalls.createVoidHeaders(voidDescriptionModel, species, dataSource);			
			Model linkSetModel = basicCalls.getEnsemblLinkSets(species, dataSource);				
			if (linkSetModel.size()>0) {
				//linksetResource.addLiteral(Void.triples, humanLinkSetMode.size());
				FileOutputStream fout2;
				fout2 = new FileOutputStream("/tmp/"+species+"_ensembl_"+URLEncoder.encode(dataSource.split("#")[1]+"LinkSets.ttl", "UTF-8"));
				linkSetModel.write(fout2, "TURTLE");

				FileOutputStream fout3;
				fout3 = new FileOutputStream("/tmp/Void_"+species+"_ensembl_"+URLEncoder.encode(dataSource.split("#")[1]+"LinkSets.ttl", "UTF-8"));
				voidDescriptionModel.write(fout3, "TURTLE");
			}	
		}
	}

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		Hashtable identifiersOrg = basicCalls.identifiersOrg;
		//Get the Ensembl Human Linksets.
		justDoIt("homo_sapiens_core_71_37");
		justDoIt("rattus_norvegicus_core_71_5");
		justDoIt("mus_musculus_core_71_38");
		justDoIt("canis_familiaris_core_71_31");
		justDoIt("bos_taurus_core_71_31");
		justDoIt("caenorhabditis_elegans_core_71_235");
		justDoIt("canis_familiaris_core_71_31");
		justDoIt("danio_rerio_core_71_9");
		justDoIt("drosophila_melanogaster_core_71_546");
		justDoIt("equus_caballus_core_71_2");
		justDoIt("gallus_gallus_core_71_4");
		justDoIt("pan_troglodytes_core_71_214");
		justDoIt("saccharomyces_cerevisiae_core_71_4");
		
	}
}

