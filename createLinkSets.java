import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.GregorianCalendar;

public class createLinkSets {
       
	/**
	 * @param args
	 * @throws FileNotFoundException 
	 * @throws UnsupportedEncodingException 
	 */
	public static void justDoIt(VoidCreator voidCreator, String species) throws UnsupportedEncodingException,  IOException{
		System.out.println(species);
        
		ResultSet humanDataSourceResultSet = basicCalls.getExternalLinkedDataSources(species);	
		while (humanDataSourceResultSet.hasNext()) {		
			QuerySolution dataSolution = humanDataSourceResultSet.next();
			String dataSource = dataSolution.get("dbName").toString();	
            		Model linkSetModel = basicCalls.getEnsemblLinkSets(species, dataSource);
			if (linkSetModel.size()>0) {
                		Resource linksetVoid = voidCreator.createSpecificVoid(species, dataSource, linkSetModel.size());
                		Resource linkset = linkSetModel.createResource(":linkset");
                		linkset.addProperty(Void.inDataset, linksetVoid);
				FileOutputStream fout;
				fout = new FileOutputStream("/tmp/"+species+"_ensembl_"+URLEncoder.encode(dataSource.split("#")[1]+"LinkSets.ttl", "UTF-8"));
				linkSetModel.write(fout, "TURTLE");
                		fout.close();
			}	
		}
	}/**/

	/**
     * Test version without connection to database
	 * @param args
	 * @throws FileNotFoundException 
	 * @throws UnsupportedEncodingException 
	 * /
	public static void justDoIt(VoidCreator voidCreator, String species) throws UnsupportedEncodingException,  IOException{
		System.out.println(species);
        String dataSource = "http://dbName#HGNC"; //Testing alternative
        Model linkSetModel = ModelFactory.createDefaultModel();
        Resource linksetVoid = voidCreator.createSpecificVoid(species, dataSource, linkSetModel.size());
        Resource linkset = linkSetModel.createResource(":linkset");
        linkset.addProperty(Void.inDataset, linksetVoid);
        FileOutputStream fout;
        fout = new FileOutputStream("/tmp/"+species+"_ensembl_"+URLEncoder.encode(dataSource.split("#")[1]+"LinkSets.ttl", "UTF-8"));
        linkSetModel.write(fout, "TURTLE");
        fout.close();
	}/**/

    public static void createMainVoid(VoidCreator voidCreator) throws IOException{ 
    	// Needs to be changed urgently, because this makes the code ineffective with future releases of Ensembl
    	// Should be only mentioned in the main class
        voidCreator.createGeneralVoid("71", new GregorianCalendar(2013, 4, 10));
        voidCreator.createSpecies("homo_sapiens_core_71_37", new GregorianCalendar(2013, 3, 27));
	voidCreator.createSpecies("rattus_norvegicus_core_71_5", new GregorianCalendar(2013, 3, 27));
	voidCreator.createSpecies("mus_musculus_core_71_38", new GregorianCalendar(2013, 3, 27));
	voidCreator.createSpecies("canis_familiaris_core_71_31", new GregorianCalendar(2013, 3, 27));
	voidCreator.createSpecies("bos_taurus_core_71_31", new GregorianCalendar(2013, 3, 27));
	voidCreator.createSpecies("caenorhabditis_elegans_core_71_235", new GregorianCalendar(2013, 3, 27));
	voidCreator.createSpecies("canis_familiaris_core_71_31", new GregorianCalendar(2013, 3, 27));
	voidCreator.createSpecies("danio_rerio_core_71_9", new GregorianCalendar(2013, 3, 27));
	voidCreator.createSpecies("drosophila_melanogaster_core_71_546", new GregorianCalendar(2013, 3, 27));
	voidCreator.createSpecies("equus_caballus_core_71_2", new GregorianCalendar(2013, 3, 27));
	voidCreator.createSpecies("gallus_gallus_core_71_4", new GregorianCalendar(2013, 3, 27));
	voidCreator.createSpecies("pan_troglodytes_core_71_214", new GregorianCalendar(2013, 3, 27));
	voidCreator.createSpecies("saccharomyces_cerevisiae_core_71_4", new GregorianCalendar(2013, 3, 27));
	voidCreator.createSpecies("homo_sapiens_funcgen_71_37", new GregorianCalendar(2013, 3, 27));
    }

	public static void main(String[] args) throws UnsupportedEncodingException, IOException {
        VoidCreator voidCreator = new VoidCreator();
        createMainVoid(voidCreator);
		//Get the Ensembl Human Linksets.
		justDoIt(voidCreator, "homo_sapiens_core_71_37");
		justDoIt(voidCreator, "rattus_norvegicus_core_71_5");
		justDoIt(voidCreator, "mus_musculus_core_71_38");
		justDoIt(voidCreator, "canis_familiaris_core_71_31");
		justDoIt(voidCreator, "bos_taurus_core_71_31");
		justDoIt(voidCreator, "caenorhabditis_elegans_core_71_235");
		justDoIt(voidCreator, "canis_familiaris_core_71_31");
		justDoIt(voidCreator, "danio_rerio_core_71_9");
		justDoIt(voidCreator, "drosophila_melanogaster_core_71_546");
		justDoIt(voidCreator, "equus_caballus_core_71_2");
		justDoIt(voidCreator, "gallus_gallus_core_71_4");
		justDoIt(voidCreator, "pan_troglodytes_core_71_214");
		justDoIt(voidCreator, "saccharomyces_cerevisiae_core_71_4");
		justDoIt(voidCreator, "homo_sapiens_funcgen_71_37");
        //TODO check this.
        Resource ensemblUniprot = voidCreator.createSpecificVoid("homo_sapiens_core_71_37", "http://dbName#Uniprot/SPTREMBL", -1);
        voidCreator.write("/tmp/Ensembl_71.ttl");
        System.out.println("Please add the following statement to the output of mapUniProtGeneName2upId");
        System.out.println("<:linkset>  <http://rdfs.org/ns/void#inDataset> <" + ensemblUniprot + ">.");
	}
}

