import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.zip.GZIPInputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPListParseEngine;

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
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;


public class getLinkset {

	/**
	 * @param args
	 * @throws IOException 
	 */
	/*public static InputStream downLoad(String file, String directory) throws IOException{
		//Here the Ensembl ftp server is contacted and the given file is downloaded and stored in the /tmp directory
	    FTPClient ftp = new FTPClient();
	    ftp.setControlEncoding("UTF-16");
	    ftp.connect("ftp.ensembl.org");
	    ftp.setFileType(FTPClient.COMPRESSED_TRANSFER_MODE); 
	   ftp.setFileType(FTP.COMPRESSED_TRANSFER_MODE);
	    ftp.login("anonymous", "");
	    InputStream inputStream = ftp.retrieveFileStream(directory+file);
	    System.out.println(ftp.getReplyString());
	    return inputStream;
	} */
	
	public static void convertGene2RDF(Model model, String geneFile, String species, String directory) throws IOException{
		System.out.println("Gene");
		InputStream fileStream = new FileInputStream(directory+"/gene.txt.gz");
		InputStream gzipStream = new GZIPInputStream(fileStream);
		BufferedReader buffered = new BufferedReader(new InputStreamReader(gzipStream));
		String line;
		Property hasStableId = model.createProperty("http://hasExternalEnsemblId");
		while ( ((line = buffered.readLine()) != null)){
			//System.out.println(line);
			String[] fields = line.split("\\t");
			Resource internalIdResource = model.createResource("http://internalEnsemblId#"+fields[0]);
			Resource stableIdResource = model.createResource("http://externalEnsemblId#"+fields[14]);
			internalIdResource.addProperty(hasStableId, stableIdResource);
		}
	}
	
	public static void convertXref2RDF(Model model, String geneFile, String species, String directory) throws IOException{
		System.out.println("http://xref");
		InputStream fileStream = new FileInputStream(directory+"/xref.txt.gz");
		InputStream gzipStream = new GZIPInputStream(fileStream);
		BufferedReader buffered = new BufferedReader(new InputStreamReader(gzipStream));
		String line;
		Property hasExternalDbId = model.createProperty("http://hasExternalDbId");
		Property hasDbPrimary_acc = model.createProperty("http://hasDbprimary_acc");
		Property hasDisplayLabel = model.createProperty("http://hasDisplayLabel");
		Resource xrefType = model.createResource("xref");
		while ( ((line = buffered.readLine()) != null)){
			String[] fields = line.split("\\t");
			Resource xrefidResource = model.createResource("http://xrefid#"+fields[0]);
			xrefidResource.addProperty(RDF.type, xrefType);
			Resource externaldbidResource = model.createResource("http://external_db_id#"+fields[1]);
			Resource dbprimaryaccResource = model.createResource("http://dbprimary_acc#"+URLEncoder.encode(fields[2], "UTF-8"));
			Resource displayLabelResource = model.createResource("http://display_label#"+URLEncoder.encode(fields[3], "UTF-8"));
			xrefidResource.addProperty(hasExternalDbId, externaldbidResource);
			xrefidResource.addProperty(hasDisplayLabel, displayLabelResource);
			xrefidResource.addProperty(hasDbPrimary_acc, dbprimaryaccResource);
		}
	}
	
	public static void convertExternalDb2RDF(Model model, String geneFile, String species, String directory) throws IOException{
		System.out.println("externalDb");
		InputStream fileStream = new FileInputStream(directory+"/external_db.txt.gz");
		InputStream gzipStream = new GZIPInputStream(fileStream);
		BufferedReader buffered = new BufferedReader(new InputStreamReader(gzipStream));
		String line;
		Property hasDbName = model.createProperty("http://hasDbName");
		Property hasDbRelease = model.createProperty("http://hasDbRelease");
		while ( ((line = buffered.readLine()) != null)){
			String[] fields = line.split("\\t");
			Resource externalDbIdResource = model.createResource("http://external_db_id#"+fields[0]);
			Resource dbNameResource = model.createResource("http://dbName#"+fields[1].replace(" ", ""));
			//Resource dbRelease = model.createResource("dbRelease#"+fields[2]);
			externalDbIdResource.addProperty(hasDbName, dbNameResource);
			//externalDbIdResource.addProperty(hasDbRelease, dbRelease);
		}
	}
	
	public static void convertObjectXref2RDF(Model model, String geneFile, String species, String directory) throws IOException{
		System.out.println("objectXref");
		InputStream fileStream = new FileInputStream(directory+"/object_xref.txt.gz");
		InputStream gzipStream = new GZIPInputStream(fileStream);
		BufferedReader buffered = new BufferedReader(new InputStreamReader(gzipStream));
		String line;
		Property hasInternalEnsemblId = model.createProperty("http://hasInternalEnsemblId");
		while ( ((line = buffered.readLine()) != null)){
			String[] fields = line.split("\\t");
			Resource objectXrefId = model.createResource("http://xrefid#"+fields[3]);
			Resource ensemblId = model.createResource("http://internalEnsemblId#"+fields[1]);
			//System.out.println("xrefid#"+fields[3] + " - "+ "internalEnsemblId#"+fields[1]);
			objectXrefId.addProperty(hasInternalEnsemblId, ensemblId);
		}
	}
	
	public static void main(String[] args) throws IOException {
	    // Download the following species file
		//downloadSpecies("homo_sapiens");
		String directory = args[0];
		String[] directorySplit = directory.split("_");
		String species = directorySplit[0] + directorySplit[1];
	
		Model ensemblModel = ModelFactory.createDefaultModel();
		Model voidModel = ModelFactory.createDefaultModel();
		Resource sourceResource = voidModel.createResource(directory);
		sourceResource.addLiteral(Pav.version, directorySplit[3]+"_"+directorySplit[4]);
		sourceResource.addLiteral(DCTerms.subject, species);
		System.out.println(directory);
		convertObjectXref2RDF(ensemblModel, directory+"/object_xref.txt.gz", "homo_sapiens", directory);
		convertGene2RDF(ensemblModel, directory+"/gene.txt.gz", "homo_sapiens", directory);
		convertXref2RDF(ensemblModel, directory+"/xref.txt.gz", "homo_sapiens", directory);
	    convertExternalDb2RDF(ensemblModel, directory+"/external_db.txt.gz", "homo_sapiens", directory);
	    
	    
		String constructQueryString = "CONSTRUCT {?externalEnsemblId <"+Skos.relatedMatch.getURI()+"> ?dbPrimaryAcc ." + 
				"?dbPrimaryAcc <"+DC.source.getURI()+"> ?dbName .}" + 
				"WHERE {" + 
				" ?xref a <http://xref> ." + 
				" ?xref <http://hasDbprimary_acc> ?dbPrimaryAcc ." + 
				" ?xref <http://hasDisplayLabel> ?dbDisplayLabel ." + 
				" ?xref <http://hasExternalDbId> ?externalDbId ." + 
				" ?xref <http://hasInternalEnsemblId> ?internalEnsemblId ." + 
				" ?externalDbId <http://hasDbName> ?dbName ." + 
				" ?internalEnsemblId <http://hasExternalEnsemblId> ?externalEnsemblId ." + 
				"}"; 
		
		System.out.println(constructQueryString);
	    Query constructquery = QueryFactory.create(constructQueryString);
		QueryExecution queryExecution = QueryExecutionFactory.create(constructquery, ensemblModel);
		Model resultSetModel = queryExecution.execConstruct();
		System.out.println();
		
		FileOutputStream fout;
		System.out.println("Is the model empty?: "+resultSetModel.isEmpty());
		fout = new FileOutputStream(directory+ "/Stringensembl.ttl");
		resultSetModel.write(fout, "TURTLE");
		
		//All the links between Ensembl and external db are extracted and stored in resultSetModel
		//The next block separates each 
		
		String selectDatasourceString = "SELECT DISTINCT ?dbName" +
				"WHERE {" +
				"	?externalDbId <http://hasDbName> ?dbName ."+
				"}";
		Query seperateQuery = QueryFactory.create(selectDatasourceString);
		queryExecution = QueryExecutionFactory.create(seperateQuery, resultSetModel);
		ResultSet dbResultSet = queryExecution.execSelect();
		while (dbResultSet.hasNext()) {
			String datasource = dbResultSet.next().get("dbName").toString();
			System.out.println(datasource);
			String constructseperateQueryString = "CONSTRUCT {?externalEnsemblId <"+Skos.relatedMatch.getURI()+"> ?dbPrimaryAcc ." + 
			"		   ?dbPrimaryAcc <"+DC.source.getURI()+"> ?dbName .}" + 
			"	WHERE {" + 
			"		?xref a <xref> ." + 
			"		?xref <http://hasDbprimary_acc> ?dbPrimaryAcc ." + 
			"		?xref <http://hasDisplayLabel> ?dbDisplayLabel ." + 
			"		?xref <http://hasExternalDbId> ?externalDbId ." + 
			"		?xref <http://hasInternalEnsemblId> ?internalEnsemblId ." + 
			"		?externalDbId <http://hasDbName> "+datasource+" ." + 
			"		?internalEnsemblId <http://hasExternalEnsemblId> ?externalEnsemblId ;" + 
			"	}";	
			Query query = QueryFactory.create(constructseperateQueryString);
			queryExecution = QueryExecutionFactory.create(query, resultSetModel);
			Model seperatedLinkSetModel = queryExecution.execConstruct();
			FileOutputStream foutlocal;
			foutlocal = new FileOutputStream(directory+"/ensembl_"+datasource+".ttl");
			seperatedLinkSetModel.write(foutlocal, "TURTLE");
		}
		
       
		System.out.println(directory);
		FileOutputStream fout2;
		fout2 = new FileOutputStream(directory+"/ensemblAll.ttl");
		ensemblModel.write(fout2, "TURTLE");
		
		FileOutputStream voidOut;
		voidOut = new FileOutputStream(directory+"/void.ttl");

		
	}

}
