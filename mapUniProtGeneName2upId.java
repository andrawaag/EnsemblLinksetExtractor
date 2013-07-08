import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class mapUniProtGeneName2upId {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		int teller = 0;
		Model model = ModelFactory.createDefaultModel();
		Model outputModel = ModelFactory.createDefaultModel();
		model.read(new File("/tmp/homo_sapiens_core_71_37_ensembl_Uniprot_genenameLinkSets.ttl").toURL().toString(), "TURTLE");
		//model.read(, "TURTLE");
		
		
		String getQuery = "SELECT DISTINCT * " + 
		"WHERE { " + 
		"     ?ensembl ?p ?upGeneName ." + 
		"} ";
		//System.out.println(getQuery);
		Query query1 = QueryFactory.create(getQuery);
		QueryExecution queryExecution = QueryExecutionFactory.create(query1, model);
		ResultSet resultSet = queryExecution.execSelect();
		int filecounter = 1;
		FileOutputStream fout3;
		while (resultSet.hasNext()) {
			System.out.println(Runtime.getRuntime().freeMemory());
			if (outputModel.size() >10000){
				fout3 = new FileOutputStream("/tmp/Ensembl_uniprot"+filecounter+".ttl");
				filecounter++;
				outputModel.write(fout3, "TURTLE");
				Model tempModel = ModelFactory.createDefaultModel();
				outputModel = tempModel;
			}
			QuerySolution solution = resultSet.next();
			String upGeneName = solution.get("upGeneName").toString();
			String ensemblName = solution.get("ensembl").toString();
			Resource ensemblResoruce = outputModel.createResource(solution.get("ensembl").toString());
			String geneName = upGeneName.split("/")[4];
			String upSparql = "PREFIX up: <http://purl.uniprot.org/core/> " +
			        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
					"SELECT ?gene ?protein " + 
					"WHERE " + 
					"{ " + 
					"?gene rdfs:comment  '"+geneName+"' ." + 
					"?protein rdfs:seeAlso ?gene . " + 
					"}";
			Query query2 = QueryFactory.create(upSparql);
			QueryExecution queryExecution2 = QueryExecutionFactory.sparqlService("http://beta.sparql.uniprot.org", query2);
			ResultSet resultSet2 = 	queryExecution2.execSelect();
			System.out.println(resultSet2.getResultVars());
			
			while (resultSet2.hasNext()){
				QuerySolution solution2 = resultSet2.next();
				String upURL = solution2.get("protein").toString();
				ensemblResoruce.addProperty(Skos.relatedMatch, outputModel.createResource(upURL));
				System.out.println(upURL);
			}	
		}
		fout3 = new FileOutputStream("/tmp/Ensembl_uniprot"+filecounter+".ttl");
		filecounter++;
		outputModel.write(fout3, "TURTLE");
	}

}
