import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Hashtable;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;


public class createHumanLinkSets {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		Model ratLinkSetMode = ModelFactory.createDefaultModel();


		String getQuery = "SELECT DISTINCT *" + 
		"FROM <http://homo_sapiens_core_71_37.ensembl.org>" + 
		"WHERE { " + 
		"     ?xref ?p <http://homo_sapiens_core_71_37.ensembl.org/xref> ." + 
		"     ?xref <http://hasDbprimary_acc> ?dbPrimaryAcc . " + 
		"     ?xref <http://hasDisplayLabel> ?dbDisplayLabel . " + 
		"     ?xref <http://hasExternalDbId> ?externalDbId .  " + 
		"     ?xref <http://hasInternalEnsemblId> ?internalEnsemblId . " + 
		"     ?externalDbId <http://hasDbName> ?dbName . " + 
		"     ?internalEnsemblId <http://hasExternalEnsemblId> ?externalEnsemblId ." + 
		"     " + 
		"} ";

		Query query = QueryFactory.create(getQuery);
		QueryExecution queryExecution = QueryExecutionFactory.sparqlService("http://ops.few.vu.nl:8890/sparql", query);
		ResultSet resultSet = queryExecution.execSelect();
		Hashtable identifiersOrg = new Hashtable();
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


		while (resultSet.hasNext()) {
			QuerySolution solution = resultSet.next();
			String externalEnsembl = solution.get("externalEnsemblId").toString();
			String primaryAcc = solution.get("dbPrimaryAcc").toString();
			String dbName = solution.get("dbName").toString();

			//System.out.println(externalEnsembl);
			Resource ensemblResource = ratLinkSetMode.createResource("http://identifiers.org/ensembl/"+externalEnsembl.split("#")[1]);
			System.out.println(ensemblResource.getURI().toString());
			Resource externalIdentifierResource = ratLinkSetMode.createResource(identifiersOrg.get(dbName)+primaryAcc.split("#")[1]);
			System.out.println(externalIdentifierResource.getURI().toString());
			ensemblResource.addProperty(Skos.relatedMatch, externalIdentifierResource);

		}
		FileOutputStream fout2;
		fout2 = new FileOutputStream("/tmp/ensemblHumanLinkSets.ttl");
		ratLinkSetMode.write(fout2, "TURTLE");

	}

}
