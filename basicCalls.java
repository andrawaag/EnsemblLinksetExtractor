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
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public class basicCalls {
	public static void createDulExpressesGraphs( String species) throws FileNotFoundException {
		Model model = ModelFactory.createDefaultModel();
		Resource dulResource = model.createResource("https://raw.github.com/andrawaag/EnsemblLinksetExtractor/blob/master/data/Dulexpresses_"+species+".ttl");
		dulResource.addProperty(RDF.type, FOAF.Organization);
		Resource geneResource = model.createResource("ftp://ftp.ensembl.org/pub/release-71/mysql/"+species+"/gene.txt.gz");
		geneResource.addLiteral(Rr.column, "stable_id");
		geneResource.addLiteral(Rr.column, "gene_id");
		geneResource.addProperty(DCTerms.description, model.createResource("http://www.ensembl.org/info/docs/api/core/core_schema.html#gene"));
		Resource externalDbResource = model.createResource("ftp://ftp.ensembl.org/pub/release-71/mysql/"+species+"/external_db.txt.gz");
		externalDbResource.addLiteral(Rr.column, "external_db_id");
		externalDbResource.addLiteral(Rr.column, "db_name");
		externalDbResource.addProperty(DCTerms.description, model.createResource("http://www.ensembl.org/info/docs/api/core/core_schema.html#external_db"));		
		Resource xrefResource = model.createResource("ftp://ftp.ensembl.org/pub/release-71/mysql/"+species+"/xref.txt.gz");
		xrefResource.addLiteral(Rr.tableName, "xref_id");
		xrefResource.addLiteral(Rr.tableName, "external_db_id");
		xrefResource.addLiteral(Rr.tableName, "dbprimary_acc");
		xrefResource.addLiteral(Rr.tableName, "display_label");
		xrefResource.addProperty(DCTerms.description, model.createResource("http://www.ensembl.org/info/docs/api/core/core_schema.html#external_db"));		
		Resource objectXrefResource = model.createResource("ftp://ftp.ensembl.org/pub/release-71/mysql/"+species+"/object_xref.txt.gz");
		objectXrefResource.addLiteral(Rr.tableName, "object_xref_id");
		objectXrefResource.addLiteral(Rr.tableName, "ensembl_id");
		objectXrefResource.addProperty(DCTerms.description, model.createResource("http://www.ensembl.org/info/docs/api/core/core_schema.html#external_db"));		
		dulResource.addProperty(VCARD.Orgname, model.createResource("http://www.ensembl.org"));
		dulResource.addProperty(Rr.tableName, geneResource);
		dulResource.addProperty(Rr.tableName, externalDbResource);
		dulResource.addProperty(Rr.tableName, xrefResource);
		dulResource.addProperty(Rr.tableName, model.createResource("ftp://ftp.ensembl.org/pub/release-71/mysql/"+species+"/object_xref.txt.gz"));
		FileOutputStream fout3;
		fout3 = new FileOutputStream("/tmp/Dulexpresses_"+species+".ttl");
		model.write(fout3, "TURTLE");
	}
	
	public static void createVoidHeaders(Model model, String species, String dataSource) throws UnsupportedEncodingException {
		Resource dulResource = model.createResource("https://raw.github.com/andrawaag/EnsemblLinksetExtractor/blob/master/data/Dulexpresses_"+species+".ttl");
		Property dulExpresses = model.createProperty("http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#expresses");
		Resource createByResource = model.createResource("http://orcid.org/0000-0001-9773-4008");
		Resource voidHeaderResource = model.createResource();
		voidHeaderResource.addProperty(RDF.type, Void.DatasetDescription);
		voidHeaderResource.addProperty(DCTerms.title, model.createLiteral("Ensembl - "+dataSource+"VoID Description", "en"));
		voidHeaderResource.addProperty(DCTerms.description, model.createLiteral("The VoID Description for a Ensembl - "+dataSource+" linkset", "en"));
		voidHeaderResource.addProperty(Pav.createdBy, createByResource);
		Calendar now = Calendar.getInstance();
		Literal nowLiteral = model.createTypedLiteral(now);
		voidHeaderResource.addProperty(Pav.createdOn, nowLiteral);
		Resource linksetResource = model.createResource(":Ensembl-"+URLEncoder.encode(dataSource.split("#")[1], "UTF-8"));			
		voidHeaderResource.addProperty(FOAF.primaryTopic, linksetResource);
		linksetResource.addProperty(RDF.type, Void.Linkset);
		linksetResource.addProperty(DCTerms.title,  model.createLiteral("Ensembl-"+URLEncoder.encode(dataSource.split("#")[1], "UTF-8")+" VoID Description","en"));
		linksetResource.addProperty(DCTerms.description, model.createLiteral("The VoID Description for a Ensembl-"+URLEncoder.encode(dataSource.split("#")[1], "UTF-8")+ " linkset"));
		linksetResource.addProperty(Pav.createdBy, createByResource);
		linksetResource.addProperty(Pav.createdOn, nowLiteral);
		linksetResource.addProperty(Pav.retrievedFrom, model.createResource("ftp://ftp.ensembl.org/pub/release-71/mysql/"));
		linksetResource.addProperty(Pav.retrievedBy, createByResource);
		linksetResource.addProperty(Pav.createdWith, model.createResource("https://raw.github.com/andrawaag/EnsemblLinksetExtractor/master/createLinkSets.java"));
		linksetResource.addProperty(dulExpresses, dulResource);
		linksetResource.addLiteral(Pav.version, species.split("_")[3]+"_"+species.split("_")[4]);
		linksetResource.addProperty(Void.subjectsTarget, model.createResource("http://identifiers.org/ensembl"));
		linksetResource.addProperty(Void.objectsTarget, model.createResource("http://identifiers.org/"+URLEncoder.encode(dataSource.split("#")[1], "UTF-8")));
        voidHeaderResource.addProperty(Void.linkPredicate, Skos.exactMatch);
        voidHeaderResource.addProperty(DCTerms.license, model.createResource("http://www.ensembl.org/info/about/legal/index.html"));
	}

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



		return identifiersOrg;
	}

}
