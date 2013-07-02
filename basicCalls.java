
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

public class basicCalls {
	public static Model getEnsemblLinkSets(String species, String dataSource){
        Model model = ModelFactory.createDefaultModel();
		if (VoidCreator.identifiersOrg.get(dataSource) != null) {
			String getQuery = "SELECT DISTINCT * " + 
			"FROM <http://"+species+".ensembl.org> " + 
			"WHERE { " + 
			"     ?xref ?p <http://"+species+".ensembl.org/xref> ." + 
			"     ?xref <http://hasDbprimary_acc> ?dbPrimaryAcc . " + 
			"     ?xref <http://hasDisplayLabel> ?dbDisplayLabel . " + 
			"     ?xref <http://hasExternalDbId> ?externalDbId .  " + 
			"     ?xref <http://hasInternalEnsemblId> ?internalEnsemblId . " + 
			"     ?externalDbId <http://hasDbName> <"+ dataSource + "> . " + 
			"     ?internalEnsemblId <http://hasExternalEnsemblId> ?externalEnsemblId ." +  
			"} ";
			//System.out.println(getQuery);
			Query query1 = QueryFactory.create(getQuery);
			QueryExecution queryExecution = QueryExecutionFactory.sparqlService("http://localhost:2001/sparql", query1);
			// QueryExecution queryExecution = QueryExecutionFactory.sparqlService("http://ops.few.vu.nl:8890/sparql", query);
			ResultSet resultSet = queryExecution.execSelect();		
			while (resultSet.hasNext()) {
				QuerySolution solution = resultSet.next();
				String externalEnsembl = solution.get("externalEnsemblId").toString();
				// System.out.println(externalEnsembl);
				String primaryAcc = solution.get("dbPrimaryAcc").toString();
				//System.out.println(externalEnsembl);
				Resource ensemblResource = model.createResource("http://identifiers.org/ensembl/"+externalEnsembl.split("#")[1]);
				// System.out.println(ensemblResource.getURI().toString());
				Resource externalIdentifierResource = model.createResource(VoidCreator.identifiersOrg.get(dataSource)+primaryAcc.split("#")[1]);
				//System.out.println(externalIdentifierResource.getURI().toString());
				ensemblResource.addProperty(VoidCreator.linkPredicate, externalIdentifierResource);
			}
		}
		return model;	
	}

	public static ResultSet getExternalLinkedDataSources(String species){
		String dataResourceQuery = "SELECT DISTINCT *" + 
		"FROM <http://"+species+".ensembl.org>" + 
		"WHERE { " + 
		"      {?s <http://hasDbName> ?dbName} ." +
		"}";
		Query dataSourceQuery = QueryFactory.create(dataResourceQuery);
		QueryExecution dataSourceQueryExecution = QueryExecutionFactory.sparqlService("http://localhost:2001/sparql", dataSourceQuery);
		//QueryExecution dataSourceQueryExecution = QueryExecutionFactory.sparqlService("http://ops.few.vu.nl:8890/sparql", dataSourceQuery);
		return dataSourceQueryExecution.execSelect();
	}


}
