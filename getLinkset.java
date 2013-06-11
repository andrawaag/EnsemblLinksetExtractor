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
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
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
	
	public static void convertGene2RDF(Model model, String geneFile, String species) throws IOException{
		System.out.println("Gene");
		InputStream fileStream = new FileInputStream("/tmp/gene.txt.gz");;
		InputStream gzipStream = new GZIPInputStream(fileStream);
		BufferedReader buffered = new BufferedReader(new InputStreamReader(gzipStream));
		String line;
		Property hasStableId = model.createProperty("hasExternalEnsemblId");
		while ( ((line = buffered.readLine()) != null)){
			//System.out.println(line);
			String[] fields = line.split("\\t");
			Resource internalIdResource = model.createResource("internalEnsemblId#"+fields[0]);
			Resource stableIdResource = model.createResource("externalEnsemblId#"+fields[14]);
			internalIdResource.addProperty(hasStableId, stableIdResource);
		}
	}
	
	public static void convertXref2RDF(Model model, String geneFile, String species) throws IOException{
		System.out.println("xref");
		InputStream fileStream = new FileInputStream("/tmp/xref.txt.gz");
		InputStream gzipStream = new GZIPInputStream(fileStream);
		BufferedReader buffered = new BufferedReader(new InputStreamReader(gzipStream));
		String line;
		Property hasExternalDbId = model.createProperty("hasExternalDbId");
		Property hasDbPrimary_acc = model.createProperty("hasDbprimary_acc");
		Property hasDisplayLabel = model.createProperty("hasDisplayLabel");
		Resource xrefType = model.createResource("xref");
		while ( ((line = buffered.readLine()) != null)){
			String[] fields = line.split("\\t");
			Resource xrefidResource = model.createResource("xrefid#"+fields[0]);
			xrefidResource.addProperty(RDF.type, xrefType);
			Resource externaldbidResource = model.createResource("external_db_id#"+fields[1]);
			Resource dbprimaryaccResource = model.createResource("dbprimary_acc#"+URLEncoder.encode(fields[2], "UTF-8"));
			Resource displayLabelResource = model.createResource("display_label#"+URLEncoder.encode(fields[3], "UTF-8"));
			xrefidResource.addProperty(hasExternalDbId, externaldbidResource);
			xrefidResource.addProperty(hasDisplayLabel, displayLabelResource);
			xrefidResource.addProperty(hasDbPrimary_acc, dbprimaryaccResource);
		}
	}
	
	public static void convertExternalDb2RDF(Model model, String geneFile, String species) throws IOException{
		System.out.println("externalDb");
		InputStream fileStream = new FileInputStream("/tmp/external_db.txt.gz");
		InputStream gzipStream = new GZIPInputStream(fileStream);
		BufferedReader buffered = new BufferedReader(new InputStreamReader(gzipStream));
		String line;
		Property hasDbName = model.createProperty("hasDbName");
		Property hasDbRelease = model.createProperty("hasDbRelease");
		while ( ((line = buffered.readLine()) != null)){
			String[] fields = line.split("\\t");
			Resource externalDbIdResource = model.createResource("external_db_id#"+fields[0]);
			Resource dbNameResource = model.createResource("dbName#"+fields[1].replace(" ", ""));
			//Resource dbRelease = model.createResource("dbRelease#"+fields[2]);
			externalDbIdResource.addProperty(hasDbName, dbNameResource);
			//externalDbIdResource.addProperty(hasDbRelease, dbRelease);
		}
	}
	
	public static void convertObjectXref2RDF(Model model, String geneFile, String species) throws IOException{
		System.out.println("objectXref");
		InputStream fileStream = new FileInputStream("/tmp/object_xref.txt.gz");
		InputStream gzipStream = new GZIPInputStream(fileStream);
		BufferedReader buffered = new BufferedReader(new InputStreamReader(gzipStream));
		String line;
		Property hasInternalEnsemblId = model.createProperty("hasInternalEnsemblId");
		while ( ((line = buffered.readLine()) != null)){
			String[] fields = line.split("\\t");
			Resource objectXrefId = model.createResource("xrefid#"+fields[3]);
			Resource ensemblId = model.createResource("internalEnsemblId#"+fields[1]);
			//System.out.println("xrefid#"+fields[3] + " - "+ "internalEnsemblId#"+fields[1]);
			objectXrefId.addProperty(hasInternalEnsemblId, ensemblId);
		}
	}
	
	public static void main(String[] args) throws IOException {
	    // Download the following species file
		//downloadSpecies("homo_sapiens");
		
		Model ensemblModel = ModelFactory.createDefaultModel();
		convertObjectXref2RDF(ensemblModel, "/tmp/object_xref.txt.gz", "homo_sapiens");
		convertGene2RDF(ensemblModel, "/tmp/gene.txt.gz", "homo_sapiens");
		convertXref2RDF(ensemblModel, "/tmp/xref.txt.gz", "homo_sapiens");
	    convertExternalDb2RDF(ensemblModel, "/tmp/external_db.txt.gz", "homo_sapiens");
	    
	    
		String constructQueryString = "CONSTRUCT {?externalEnsemblId <"+Skos.relatedMatch.getURI()+"> ?dbPrimaryAcc ." + 
				"		   ?dbPrimaryAcc <"+DC.source.getURI()+"> ?dbName .}" + 
				"	WHERE {" + 
				"		?xref a <xref> ." + 
				"		?xref <hasDbprimary_acc> ?dbPrimaryAcc ." + 
				"		?xref <hasDisplayLabel> ?dbDisplayLabel ." + 
				"		?xref <hasExternalDbId> ?externalDbId ." + 
				"		?xref <hasInternalEnsemblId> ?internalEnsemblId ." + 
				"		?externalDbId <hasDbName> ?dbName ." + 
				"		?internalEnsemblId <hasExternalEnsemblId> ?externalEnsemblId ;" + 
				"	}"; 
	    Query query = QueryFactory.create(constructQueryString);
		QueryExecution queryExecution = QueryExecutionFactory.create(query, ensemblModel);
		Model resultSetModel = queryExecution.execConstruct();
		

		FileOutputStream fout;
		fout = new FileOutputStream("/tmp/ensembl.ttl");
		resultSetModel.write(fout, "TURTLE");
		FileOutputStream fout2;
		fout2 = new FileOutputStream("/tmp/ensemblAll.ttl");
		ensemblModel.write(fout2, "TURTLE");

		
	}

}
