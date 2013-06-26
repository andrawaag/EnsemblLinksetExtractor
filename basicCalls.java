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


public class basicCalls {
	public static void createDulExpressesGraphs( String species) throws FileNotFoundException {
		Model model = ModelFactory.createDefaultModel();
		Resource dulResource = model.createResource("https://github.com/andrawaag/EnsemblLinksetExtractor/blob/master/data/Dulexpresses_"+species+".ttl.?raw=true");
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
		Resource dulResource = model.createResource("https://github.com/andrawaag/EnsemblLinksetExtractor/blob/master/data/Dulexpresses_"+species+".ttl.?raw=true");
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

	}

	public static Model getEnsemblLinkSets(String species, String dataSource){
		Model humanLinkSetModel = ModelFactory.createDefaultModel();
		if (identifiersOrg.get(dataSource) != null) {
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
				Resource ensemblResource = humanLinkSetModel.createResource("http://identifiers.org/ensembl/"+externalEnsembl.split("#")[1]);
				// System.out.println(ensemblResource.getURI().toString());
				Resource externalIdentifierResource = humanLinkSetModel.createResource(identifiersOrg.get(dataSource)+primaryAcc.split("#")[1]);
				//System.out.println(externalIdentifierResource.getURI().toString());
				ensemblResource.addProperty(Skos.exactMatch, externalIdentifierResource);
			}
		}
		return humanLinkSetModel;	
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

	public static final Hashtable identifiersOrg = getIdentifiersOrg() ;
	public static Hashtable getIdentifiersOrg() {
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
		identifiersOrg.put("http://dbName#BioGRID", "http://identifiers.org/biogrid/");
		identifiersOrg.put("http://dbName#GO", "http://identifiers.org/go/");
		identifiersOrg.put("http://dbName#HGNC", "http://identifiers.org/hgnc/");
		identifiersOrg.put("http://dbName#Interpro", "http://identifiers.org/interpro/");
		identifiersOrg.put("http://dbName#MGI", "http://identifiers.org/mgd/");
		identifiersOrg.put("http://dbName#MIM", "http://identifiers.org/omim/");
		identifiersOrg.put("http://dbName#PDB", "http://identifiers.org/pdb/");
		identifiersOrg.put("http://dbName#Superfamily", "http://identifiers.org/supfam/");
		identifiersOrg.put("http://dbName#GeneDB", "http://identifiers.org/genedb/");
		identifiersOrg.put("http://dbName#RGD", "http://identifiers.org/rgd/");
		identifiersOrg.put("http://dbName#SGD", "http://identifiers.org/sgd/");
		identifiersOrg.put("http://dbName#IPI", "http://identifiers.org/ipi/");
		identifiersOrg.put("http://dbName#CCDS", "http://identifiers.org/ccds/");
		identifiersOrg.put("http://dbName#PUBMED", "http://identifiers.org/pubmed/");
		identifiersOrg.put("http://dbName#UniGene", "http://identifiers.org/unigene/");
		identifiersOrg.put("http://dbName#RFAM", "http://identifiers.org/rfam/");
		identifiersOrg.put("http://dbName#EPD", "http://identifiers.org/epd/");
		identifiersOrg.put("http://dbName#MEROPS", "http://identifiers.org/merops/");
		identifiersOrg.put("http://dbName#HPA", "http://identifiers.org/hpa/");
		identifiersOrg.put("http://dbName#UniParc", "http://identifiers.org/uniparc/");
		identifiersOrg.put("http://dbName#BRENDA", "http://identifiers.org/brenda/");
		identifiersOrg.put("http://dbName#BioCyc", "http://identifiers.org/biocyc/");
		identifiersOrg.put("http://dbName#EchoBASE", "http://identifiers.org/echobase/");
		identifiersOrg.put("http://dbName#EcoGene", "http://identifiers.org/ecogene/");
		identifiersOrg.put("http://dbName#Leproma", "http://identifiers.org/myco.lepra/");
		identifiersOrg.put("http://dbName#TubercuList", "http://identifiers.org/myco.tuber/");
		identifiersOrg.put("http://dbName#ArrayExpress", "http://identifiers.org/arrayexpress/");
		identifiersOrg.put("http://dbName#DIP", "http://identifiers.org/dip/");
		identifiersOrg.put("http://dbName#DisProt", "http://identifiers.org/disprot/");
		identifiersOrg.put("http://dbName#DrugBank", "http://identifiers.org/drugbank/");
		identifiersOrg.put("http://dbName#HAMAP", "http://identifiers.org/hamap/");
		identifiersOrg.put("http://dbName#HOGENOM", "http://identifiers.org/hogenom/");
		identifiersOrg.put("http://dbName#HSSP", "http://identifiers.org/hssp/");
		identifiersOrg.put("http://dbName#IntAct", "http://identifiers.org/intact/");
		identifiersOrg.put("http://dbName#PeroxiBase", "http://identifiers.org/peroxibase/");
		identifiersOrg.put("http://dbName#REBASE", "http://identifiers.org/rebase/");
		identifiersOrg.put("http://dbName#Reactome", "http://identifiers.org/reactome/");
		identifiersOrg.put("http://dbName#PGD", "http://identifiers.org/pseudomonas/");
		identifiersOrg.put("http://dbName#PomBase", "http://identifiers.org/pombase/");
		identifiersOrg.put("http://dbName#CGD", "http://identifiers.org/cgd/");
		identifiersOrg.put("http://dbName#ProSite", "http://identifiers.org/prosite/");
		identifiersOrg.put("http://dbName#SMART", "http://identifiers.org/smart/");
		identifiersOrg.put("http://dbName#MOD", "http://identifiers.org/obo.psi-mod/");
		identifiersOrg.put("http://dbName#AGD", "http://identifiers.org/agd/");
		identifiersOrg.put("http://dbName#PO", "http://identifiers.org/obo.po/");
		identifiersOrg.put("http://dbName#LRG", "http://identifiers.org/lrg/");
		identifiersOrg.put("http://dbName#PFAM", "http://identifiers.org/pfam/");
		identifiersOrg.put("http://dbName#PFAM", "http://identifiers.org/pfam/");
		identifiersOrg.put("http://dbName#PFAM", "http://identifiers.org/pfam/");
		identifiersOrg.put("http://dbName#PFAM", "http://identifiers.org/pfam/");
		identifiersOrg.put("http://dbName#PFAM", "http://identifiers.org/pfam/");
		identifiersOrg.put("http://dbName#PFAM", "http://identifiers.org/pfam/");
		identifiersOrg.put("http://dbName#ToxoDB", "http://identifiers.org/toxoplasma/");
		identifiersOrg.put("http://dbName#Orphanet", "http://identifiers.org/orphanet/");
		identifiersOrg.put("http://dbName#SO", "http://identifiers.org/obo.so/");
		identifiersOrg.put("http://dbName#SGN", "http://identifiers.org/sgn/");
		identifiersOrg.put("http://dbName#INSDC", "http://identifiers.org/insdc/");
		identifiersOrg.put("http://dbName#GOA", "http://identifiers.org/goa/");
		identifiersOrg.put("http://dbName#Rhea", "http://identifiers.org/rhea/");

		return identifiersOrg;
	}

}
