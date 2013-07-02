import java.io.FileNotFoundException;
import java.net.URLEncoder;
import java.util.Calendar;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.DCTypes;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.VCARD;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class VoidCreator {
    
    private static final String BASE_URI = "https://raw.github.com/andrawaag/EnsemblLinksetExtractor/master/data/";
    public static final Property linkPredicate = Skos.relatedMatch;
  
    private final Model model;
    private final Resource andra;
    private final Resource  bridgeDB;
    private final Resource christian;
    private final Resource ensembl;
    private final Resource ensemblDataset;
    private final Resource ensemblLinkset;
    private final Resource ensemblLicense;
    private final Resource script;
    public static final HashMap<String,String> identifiersOrg = getIdentifiersOrg();

    public VoidCreator(){
        model = ModelFactory.createDefaultModel();
        andra = model.createResource("http://orcid.org/0000-0002-5711-4872");
        bridgeDB = model.createResource("http://www.bridgedb.org/");
		christian = model.createResource("https://wiki.openphacts.org/index.php/User:Christian");
        ensembl = model.createResource("http://www.ensembl.org/");
        ensemblDataset = model.createResource(BASE_URI + "ensemblDataset");
        ensemblLinkset = model.createResource(BASE_URI + "ensemblLinksetDataset");
        ensemblLicense = model.createResource("http://apr2013.archive.ensembl.org/info/about/legal/index.html");
        script = model.createResource("https://raw.github.com/andrawaag/EnsemblLinksetExtractor/master/createLinkSets.java");
        //TODO These don't appear to work.
        model.setNsPrefix("", BASE_URI);
        model.setNsPrefix("dcterms", DCTerms.NS);
        model.setNsPrefix("dctypes", DCTypes.NS);
        model.setNsPrefix("dul", Dul.NS);
        model.setNsPrefix("pac", Pav.NS);
        model.setNsPrefix("rdf", RDF.getURI());
        model.setNsPrefix("skos", Skos.NS);
        model.setNsPrefix("void", Void.NS);
        model.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
    }
     
    //Here I am being lazy as all species so far appear to be name_name_name_majorVersiob_minorVersion   
    //TODO add a nice lookup table here
    private String extractSpeciesName (String species){
        String[] parts = species.split("_");
        return parts[0] + " " + parts[1] + " " + parts[2];     
    }
    
    //TODO add a nice lookup table here
    private String extractSpeciesOneWord (String species){
        String[] parts = species.split("_");
        return parts[0] + "_" + parts[1] + "_" + parts[2];     
    }
    
    private String extractMajorVersion (String species){
        String[] parts = species.split("_");
        return parts[3];     
    }
    
    private String extractMinorVersion (String species){
        String[] parts = species.split("_");
        return parts[3] + "_" + parts[4];     
    }
    
    private Resource createSpeciesLinkset(String species){
        return model.createResource(BASE_URI + extractSpeciesOneWord(species) + "-linkset");
    }

    private Resource createSpeciesDataSetLinkset(String species, String dataSetName){
        return model.createResource(BASE_URI + extractSpeciesOneWord(species) + "-" + dataSetName + "-linkset");
    }

    private Resource createSpeciesDataset(String species){
        return model.createResource(BASE_URI + extractSpeciesOneWord(species) + "-dataset");
    }

    private Resource createDulResource(String species){
        return model.createResource(BASE_URI + "EnsemblMapsFor_" + extractSpeciesOneWord(species));
    }
    
    private Resource getFtp (String version){
        return model.createResource("ftp://ftp.ensembl.org/pub/release-" + version);
    }

    private Resource getFtp (String version, String species){
        return model.createResource("ftp://ftp.ensembl.org/pub/release-" + version + "/mysql/" + species);
    }
    
    private void createDatasetDescription() {
		Resource voidHeaderResource = model.createResource();
		voidHeaderResource.addProperty(RDF.type, Void.DatasetDescription);
		voidHeaderResource.addProperty(DCTerms.title, model.createLiteral("Ensembl Genome VoID Description"));
		voidHeaderResource.addProperty(Pav.createdBy, andra);
		voidHeaderResource.addProperty(Pav.createdBy, christian);
		voidHeaderResource.addProperty(Pav.createdWith, script);
		Calendar now = Calendar.getInstance();
		Literal nowLiteral = model.createTypedLiteral(now);
		voidHeaderResource.addProperty(Pav.createdOn, nowLiteral);
		voidHeaderResource.addProperty(FOAF.primaryTopic, ensemblLinkset);
	}
     
    public void createGeneralVoid(String version, Calendar issuedOn){
        createEnsemblLinksetDataset(version);
        createEnsemblDataset(version, issuedOn);
    }
    
    private void createEnsemblLinksetDataset(String version) {        
		ensemblLinkset.addProperty(RDF.type, Void.Dataset);
		ensemblLinkset.addLiteral(DCTerms.title, "Ensembl Genome Linksets");
 		ensemblLinkset.addLiteral(DCTerms.description, "Linksets extracted from the Ensembl Genome Database that link genes by species across several datasets");        
        ensemblLinkset.addProperty(DCTerms.publisher, bridgeDB);
        ensemblLinkset.addProperty(Dcat.landingPage, ensembl);
        ensemblLinkset.addProperty(DCTerms.license, ensemblLicense);
        //TODO add Andra's Lisence here
        ensemblLinkset.addProperty(Pav.createdBy, andra);
 		Calendar now = Calendar.getInstance();
		Literal nowLiteral = model.createTypedLiteral(now);
		ensemblLinkset.addProperty(Pav.createdOn, nowLiteral);
		ensemblLinkset.addProperty(Pav.createdWith, script);
		ensemblLinkset.addProperty(DCTerms.issued, nowLiteral);
        ensemblLinkset.addLiteral(Pav.version, version);
 		ensemblLinkset.addProperty(Pav.importedOn, nowLiteral);
		ensemblLinkset.addProperty(Pav.importedBy, andra);
		ensemblLinkset.addProperty(Pav.importedOn, nowLiteral);
		ensemblLinkset.addProperty(Pav.importedFrom, getFtp(version));
        ensemblLinkset.addProperty(Void.linkPredicate, linkPredicate);
    }
    
	private void createEnsemblDataset(String version, Calendar issuedOn) {
		ensemblDataset.addProperty(RDF.type, DCTypes.Dataset);
		ensemblDataset.addLiteral(DCTerms.title, "Ensembl Genome Database");
 		ensemblDataset.addLiteral(DCTerms.description, "The Ensembl project produces genome databases for vertebrates and other eukaryotic species, and makes this information freely available online.");
        ensemblDataset.addProperty(DCTerms.publisher, ensembl);
        ensemblDataset.addProperty(Dcat.landingPage, ensembl);
        ensemblDataset.addProperty(DCTerms.license, ensemblLicense);
        ensemblDataset.addProperty(Pav.createdBy, ensembl);
        Literal issuedLieteral = model.createTypedLiteral(issuedOn);
		ensemblDataset.addProperty(DCTerms.issued, issuedLieteral);
        ensemblDataset.addLiteral(Pav.version, version);
		ensemblDataset.addProperty(Dcat.downloadURL, getFtp(version));
    }
    
    public void createSpecies(String species, Calendar issuedOn) throws FileNotFoundException {
        createDulExpresses(species);
        Resource speciesLinkset = createSpeciesLinkset(species);
        Resource speciesDataset = createSpeciesDataset(species);
        ensemblLinkset.addProperty(Void.subset, speciesLinkset);
        ensemblDataset.addProperty(Void.subset, speciesDataset);
        
        String speciesName = extractSpeciesName(species);

        speciesLinkset.addProperty(RDF.type, Void.Dataset);
 		speciesLinkset.addLiteral(DCTerms.title, "Ensembl " + speciesName + " Linksets");
 		speciesLinkset.addLiteral(DCTerms.description, "Linksets extracted from the Ensembl Genome Database that link genes for " 
                + speciesName + " across several datasets");
        speciesLinkset.addProperty(Pav.importedFrom, speciesDataset); 
        speciesLinkset.addLiteral(Pav.version, this.extractMinorVersion(species));
        speciesLinkset.addProperty(Dul.expresses, createDulResource(species));
        
        speciesDataset.addProperty(RDF.type, DCTypes.Dataset);
 		speciesDataset.addLiteral(DCTerms.title, "Ensembl " + speciesName);
 		speciesDataset.addLiteral(DCTerms.description, "The subset of Ensembl that relates to " + speciesName);
        speciesDataset.addLiteral(Dcat.mediaType, "text/csv");
        speciesDataset.addLiteral(Pav.version, this.extractMinorVersion(species));
        Literal issuedLieteral = model.createTypedLiteral(issuedOn);
		speciesDataset.addProperty(DCTerms.issued, issuedLieteral);        
		speciesDataset.addProperty(Pav.importedFrom, getFtp(this.extractMajorVersion(species), species));
    }
    
    /**
     * This is under review see https://openphacts2011.atlassian.net/browse/IMS-281
     * 
     * @param model
     * @param species
     * @throws FileNotFoundException 
     */
	private void createDulExpresses(String species) throws FileNotFoundException {
		Resource dulResource = createDulResource(species);
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
	}
  
	public Resource createSpecificVoid(String species, String dataSource, long size) throws UnsupportedEncodingException {       
        String dataSourceName = URLEncoder.encode(dataSource.split("#")[1], "UTF-8");
        String speciesName = extractSpeciesName(species);
        
		Resource specificResource = createSpeciesDataSetLinkset(species, dataSourceName);	
        
		specificResource.addProperty(RDF.type, Void.Linkset);
		specificResource.addProperty(DCTerms.title,  
                model.createLiteral("Ensembl-" + dataSourceName + " Linkset for " + speciesName,"en"));
		specificResource.addProperty(DCTerms.description, 
                model.createLiteral("A linkset which links Ensembl with "+dataSourceName+ " for the species " + speciesName,"en"));
        //TODO whic is the correct approach here?
        //String object = "http://identifiers.org/" + dataSourceName;
        String object = identifiersOrg.get(dataSource);
		specificResource.addProperty(Void.objectsTarget, model.createResource(object));
        specificResource.addLiteral(Void.triples, size);
        
        //Subset back to the species linkset and indirectly the rest
        Resource speciesLinkset = createSpeciesLinkset(species);
        speciesLinkset.addProperty(Void.subset, specificResource);
        
        return specificResource;
	}
    
    public void write(String path) throws IOException{
        FileOutputStream fout;
		fout = new FileOutputStream(path);
		model.write(fout, "TURTLE"); 
        fout.close();
    }

	private static HashMap getIdentifiersOrg() {
		HashMap<String,String> identifiersOrg = new HashMap<String,String>();
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
