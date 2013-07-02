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



public class createRatLinkSets {
	/**
	 * @param args
	 * @throws FileNotFoundException 
	 * @throws UnsupportedEncodingException 
	 */
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		String dataResourceQuery = "SELECT DISTINCT *" + 
		"FROM <http://rattus_norvegicus_core_71_5.ensembl.org>" + 
		"WHERE { " + 
		"      {?s <http://hasDbName> ?dbName} ." +
		"}";
		Query dataSourceQuery = QueryFactory.create(dataResourceQuery);
		QueryExecution dataSourceQueryExecution = QueryExecutionFactory.sparqlService("http://localhost:2001/sparql", dataSourceQuery);
		//QueryExecution dataSourceQueryExecution = QueryExecutionFactory.sparqlService("http://ops.few.vu.nl:8890/sparql", dataSourceQuery);
		ResultSet dataResultSet = dataSourceQueryExecution.execSelect();
		while (dataResultSet.hasNext()) {
			Model ratLinkSetMode = ModelFactory.createDefaultModel();
			QuerySolution dataSolution = dataResultSet.next();
			String dataSource = dataSolution.get("dbName").toString();	

			Model voidDescriptionModel = ModelFactory.createDefaultModel();
			Property dulExpresses = voidDescriptionModel.createProperty("http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#expresses");
			Resource createByResource = voidDescriptionModel.createResource("http://orcid.org/0000-0001-9773-4008");
			Resource voidHeaderResource = voidDescriptionModel.createResource();
			voidHeaderResource.addProperty(RDF.type, Void.DatasetDescription);
			voidHeaderResource.addProperty(DCTerms.title, voidDescriptionModel.createLiteral("Ensembl - "+dataSource+"VoID Description", "en"));
			voidHeaderResource.addProperty(DCTerms.description, voidDescriptionModel.createLiteral("The VoID Description for a Ensembl - "+dataSource+" linkset", "en"));
			voidHeaderResource.addProperty(Pav.createdBy, createByResource);
			Calendar now = Calendar.getInstance();
			Literal nowLiteral = voidDescriptionModel.createTypedLiteral(now);
			voidHeaderResource.addProperty(Pav.createdOn, nowLiteral);
			Resource linksetResource = voidDescriptionModel.createResource(":Ensembl-"+URLEncoder.encode(dataSource.split("#")[1], "UTF-8"));
			
			voidHeaderResource.addProperty(FOAF.primaryTopic, linksetResource);
			linksetResource.addProperty(RDF.type, Void.Linkset);
			linksetResource.addProperty(DCTerms.title,  voidDescriptionModel.createLiteral("Ensembl-"+URLEncoder.encode(dataSource.split("#")[1], "UTF-8")+" VoID Description","en"));
			linksetResource.addProperty(DCTerms.description, voidDescriptionModel.createLiteral("The VoID Description for a Ensembl-"+URLEncoder.encode(dataSource.split("#")[1], "UTF-8")+ " linkset"));
			linksetResource.addProperty(Pav.createdBy, createByResource);
			linksetResource.addProperty(Pav.createdOn, nowLiteral);
			linksetResource.addProperty(Pav.retrievedFrom, voidDescriptionModel.createResource("ftp://ftp.ensembl.org/pub/release-71/mysql/"));
			linksetResource.addProperty(Pav.retrievedBy, createByResource);
			linksetResource.addProperty(Pav.createdWith, voidDescriptionModel.createResource("https://raw.github.com/andrawaag/EnsemblLinksetExtractor/master/createRatLinkSets.java"));
			linksetResource.addProperty(dulExpresses, voidDescriptionModel.createResource("http://dbpedia.org/page/Ensembl"));
			linksetResource.addLiteral(Pav.version, "71_5");
			linksetResource.addProperty(Void.subjectsTarget, voidDescriptionModel.createResource("http://identifiers.org/ensembl"));
			linksetResource.addProperty(Void.objectsTarget, voidDescriptionModel.createResource("http://identifiers.org/"+URLEncoder.encode(dataSource.split("#")[1], "UTF-8")));
			String getQuery = "SELECT DISTINCT *" + 
			"FROM <http://rattus_norvegicus_core_71_5.ensembl.org>" + 
			"WHERE { " + 
			"     ?xref ?p <http://rattus_norvegicus_core_71_5.ensembl.org/xref> ." + 
			"     ?xref <http://hasDbprimary_acc> ?dbPrimaryAcc . " + 
			"     ?xref <http://hasDisplayLabel> ?dbDisplayLabel . " + 
			"     ?xref <http://hasExternalDbId> ?externalDbId .  " + 
			"     ?xref <http://hasInternalEnsemblId> ?internalEnsemblId . " + 
			"     ?externalDbId <http://hasDbName> <"+ dataSource + "> . " + 
			"     ?internalEnsemblId <http://hasExternalEnsemblId> ?externalEnsemblId ." + 
			"     \n" + 
			"} ";
			Query query1 = QueryFactory.create(getQuery);
			QueryExecution queryExecution = QueryExecutionFactory.sparqlService("http://localhost:2001/sparql", query1);
			// QueryExecution queryExecution = QueryExecutionFactory.sparqlService("http://ops.few.vu.nl:8890/sparql", query);
			ResultSet resultSet = queryExecution.execSelect();
			Hashtable identifiersOrg = new Hashtable();
			identifiersOrg.put("http://dbName#RefSeq_peptide_predicted", "http://identifiers.org/refseq/");
			identifiersOrg.put("http://dbName#RGD", "http://identifiers.org/rgd/");
			identifiersOrg.put("http://dbName#Uniprot_genename", "http://identifiers.org/uniprot/");
			identifiersOrg.put("http://dbName#IPI", "http://identifiers.org/ipi/");
			identifiersOrg.put("http://dbName#EMBL","http://identifiers.org/embl");
			identifiersOrg.put("http://dbName#protein_id", "http://identifiers.org/");
			identifiersOrg.put("http://dbName#Uniprot/SWISSPROT", "http://identifiers.org/uniprot/");
			identifiersOrg.put("http://dbName#UniGene", "http://identifiers.org/unigene/");
			identifiersOrg.put("http://dbName#PDB", "http://identifiers.org/pdb/");
			identifiersOrg.put("http://dbName#RefSeq_peptide", "http://identifiers.org/refseq/");
			identifiersOrg.put("http://dbName#WikiGene", "http://identifiers.org/wikigene/");
			identifiersOrg.put("http://dbName#EntrezGene", "http://identifiers.org/ncbigene/");
			identifiersOrg.put("http://dbName#Uniprot/SPTREMBL", "http://identifiers.org/uniprot/");
			identifiersOrg.put("http://dbName#MEROPS", "http://identifiers.org/merops/");
			identifiersOrg.put("http://dbName#GO", "http://identifiers.org/go/");
			identifiersOrg.put("http://dbName#goslim_goa", "http://identifiers.org/goa/");
			identifiersOrg.put("http://dbName#RefSeq_mRNA_predicted", "http://identifiers.org/refseq/");
			identifiersOrg.put("http://dbName#ArrayExpress", "http://identifiers.org/arrayexpress/");
			identifiersOrg.put("http://dbName#RefSeq_mRNA", "http://identifiers.org/refseq/");
			identifiersOrg.put("http://dbName#MGI", "http://identifiers.org/mgi/");
			identifiersOrg.put("http://dbName#MGI_transcript_name", "http://identifiers.org/mgi/");
			identifiersOrg.put("http://dbName#RefSeq_ncRNA_predicted", "http://identifiers.org/refseq/");
			identifiersOrg.put("http://dbName#RefSeq_ncRNA", "http://identifiers.org/refseq/");
			identifiersOrg.put("http://dbName#miRBase", "http://identifiers.org/mirbase/");
			identifiersOrg.put("http://dbName#RFAM", "http://identifiers.org/rfam/");
			identifiersOrg.put("http://dbName#Orphanet", "http://identifiers.org/orphanet/");
			identifiersOrg.put("http://dbName#MIM_MORBID", "http://identifiers.org/omim/");
			identifiersOrg.put("http://dbName#Clone_based_vega_gene", "http://identifiers.org/vega/"); // TODO CHECK!!!!!!	
			identifiersOrg.put("http://dbName#OTTG", "http://identifiers.org/ensembl/");
			identifiersOrg.put("http://dbName#LRG", "http://identifiers.org/lrg/");
			identifiersOrg.put("http://dbName#ENS_LRG_gene", "http://identifiers.org/lrg/"); 
			identifiersOrg.put("http://dbName#HGNC", "http://identifiers.org/hgnc/");
			identifiersOrg.put("http://dbName#WikiGene", "http://identifiers.org/wikigene/");
			identifiersOrg.put("http://dbName#EntrezGene", "http://identifiers.org/ncbigene/");
			identifiersOrg.put("http://dbName#UniGene", "http://identifiers.org/unigene/");
			identifiersOrg.put("http://dbName#MIM_GENE", "http://identifiers.org/mim/");
			identifiersOrg.put("http://dbName#Uniprot_genename", "http://identifiers.org/uniprot/");
			identifiersOrg.put("http://dbName#IPI", "http://identifiers.org/ipi/");
			identifiersOrg.put("http://dbName#EMBL","http://identifiers.org/embl");
			identifiersOrg.put("http://dbName#protein_id", "http://identifiers.org/");
			identifiersOrg.put("http://dbName#Uniprot/SWISSPROT", "http://identifiers.org/uniprot/");
			identifiersOrg.put("http://dbName#Clone_based_ensembl_gene", "http://identifiers.org/ensembl/");
			identifiersOrg.put("http://dbName#PDB", "http://identifiers.org/pdb/");
			identifiersOrg.put("http://dbName#RefSeq_peptide", "http://identifiers.org/refseq/");
			identifiersOrg.put("http://dbName#EntrezGene", "http://identifiers.org/ncbigene/");
			identifiersOrg.put("http://dbName#Uniprot/SPTREMBL", "http://identifiers.org/uniprot/");
			identifiersOrg.put("http://dbName#MEROPS", "http://identifiers.org/merops/");
			identifiersOrg.put("http://dbName#GO", "http://identifiers.org/go/");
			identifiersOrg.put("http://dbName#goslim_goa", "http://identifiers.org/goa/");
			identifiersOrg.put("http://dbName#RefSeq_mRNA_predicted", "http://identifiers.org/refseq/");
			identifiersOrg.put("http://dbName#ArrayExpress", "http://identifiers.org/arrayexpress/");
			identifiersOrg.put("http://dbName#RefSeq_mRNA", "http://identifiers.org/refseq/");
			identifiersOrg.put("http://dbName#MGI", "http://identifiers.org/mgi/");
			identifiersOrg.put("http://dbName#MGI_transcript_name", "http://identifiers.org/mgi/");
			identifiersOrg.put("http://dbName#RefSeq_ncRNA_predicted", "http://identifiers.org/refseq/");
			identifiersOrg.put("http://dbName#RefSeq_ncRNA", "http://identifiers.org/refseq/");
			identifiersOrg.put("http://dbName#miRBase", "http://identifiers.org/mirbase/");
			identifiersOrg.put("http://dbName#RFAM", "http://identifiers.org/rfam/");
			identifiersOrg.put("http://dbName#ArrayExpress", "http://identifiers.org/arrayexpress/");
			
			boolean write = false;
			while (resultSet.hasNext()) {
				write = true;
				QuerySolution solution = resultSet.next();
				String externalEnsembl = solution.get("externalEnsemblId").toString();
				String primaryAcc = solution.get("dbPrimaryAcc").toString();
				;

				//System.out.println(externalEnsembl);
				Resource ensemblResource = ratLinkSetMode.createResource("http://identifiers.org/ensembl/"+externalEnsembl.split("#")[1]);
				System.out.println(ensemblResource.getURI().toString());
				Resource externalIdentifierResource = ratLinkSetMode.createResource(identifiersOrg.get(dataSource)+primaryAcc.split("#")[1]);
				System.out.println(externalIdentifierResource.getURI().toString());
				ensemblResource.addProperty(Skos.exactMatch, externalIdentifierResource);
			}
			if (write) {
				linksetResource.addLiteral(Void.triples, ratLinkSetMode.size());
				FileOutputStream fout2;
				fout2 = new FileOutputStream("/tmp/RN_ensembl_"+URLEncoder.encode(dataSource.split("#")[1]+"LinkSets.ttl", "UTF-8"));
				ratLinkSetMode.write(fout2, "TURTLE");
				
				FileOutputStream fout3;
				fout3 = new FileOutputStream("/tmp/Void_RN_ensembl_"+URLEncoder.encode(dataSource.split("#")[1]+"LinkSets.ttl", "UTF-8"));
				voidDescriptionModel.write(fout3, "TURTLE");
			}
			
		}
	}
}
