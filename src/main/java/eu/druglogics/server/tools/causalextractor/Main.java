
package eu.druglogics.server.tools.causalextractor;

import com.martiansoftware.jsap.*;
import com.martiansoftware.jsap.Parameter;
import eu.druglogics.server.tools.causalextractor.causalStatement.CausalStatement;
import eu.druglogics.server.tools.causalextractor.export.Mitab28;
import eu.druglogics.server.tools.causalextractor.export.Sif;
import eu.druglogics.server.tools.causalextractor.reactome.DataFactory;
import eu.druglogics.server.tools.causalextractor.reactome.model.CausalCatalysis;
import eu.druglogics.server.tools.causalextractor.reactome.model.GeneRegCausality;
import org.reactome.server.graph.service.GeneralService;
import org.reactome.server.graph.utils.ReactomeGraphCore;
import eu.druglogics.server.tools.causalextractor.config.ReactomeNeo4jConfig;
import java.io.*;
import java.util.*;


/**
 * Main class: Causal extraction from Reactome database:
 * Use of the Reactome graph-core to query Reactome (https://github.com/reactome/graph-core)
 * Use of the psimitab to write a MITAB2.8 file (http://psicquic.github.io/MITAB28Format.html)
 *
 * @author Vasundra Touré
 */
public class Main {

    public static void main(String[] args) throws JSAPException, IOException {
        // Program Arguments -h, -p, -u, -k
        SimpleJSAP jsap = new SimpleJSAP(Main.class.getName(), "Connect to Reactome Graph Database",
                new Parameter[]{
                        new FlaggedOption("output", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.REQUIRED, 'o', "output", "The directory where the output files are written to."),
                        new FlaggedOption("host", JSAP.STRING_PARSER, "localhost", JSAP.NOT_REQUIRED, 'h', "host", "The neo4j host"),
                        new FlaggedOption("port", JSAP.STRING_PARSER, "7474", JSAP.NOT_REQUIRED, 'p', "port", "The neo4j port"),
                        new FlaggedOption("user", JSAP.STRING_PARSER, "neo4j", JSAP.NOT_REQUIRED, 'u', "user", "The neo4j user"),
                        new FlaggedOption("password", JSAP.STRING_PARSER, "neo4j", JSAP.REQUIRED, 'd', "password", "The neo4j password")
                }
        );

        JSAPResult config = jsap.parse(args);
        if (jsap.messagePrinted()) System.exit(1);

        // Initialising ReactomeCore Neo4j configuration
        ReactomeGraphCore.initialise(config.getString("host"),
                config.getString("port"),
                config.getString("user"),
                config.getString("password"),
                ReactomeNeo4jConfig.class);

        // Access to Reactome Neo4j
        GeneralService genericService = ReactomeGraphCore.getService(GeneralService.class);
        System.out.println("Database name: " + genericService.getDBInfo().getName());
        System.out.println("Database version: " + genericService.getDBInfo().getVersion());

        long startTime = System.currentTimeMillis();

        // output directory
        String dir = config.getString("output");


        //Initialize PSI-MI TAB2.8 file (will contain all data)
        String mitabFileName = dir + "mitab_V" + genericService.getDBInfo().getVersion() + ".txt";
        Mitab28 mitab = new Mitab28(mitabFileName);
        System.out.println(mitab.getPsiWriter().getFileName());

        // Initialize SIF file
        String sifFileName = dir + "sif_V" + genericService.getDBInfo().getVersion() + ".sif";
        Sif sif = new Sif(sifFileName);

        // Causal interactions extracted from Transcription events
        Collection<GeneRegCausality> causalTranscriptions = DataFactory.getCausalTranscription();
        System.out.println("Number of transcription reactions: " + causalTranscriptions.size() + "\n");

        for (GeneRegCausality causalTranscription : causalTranscriptions) {
            CausalStatement cs = causalTranscription.getCausalStatement("transcription");

            //write MITAB2.8
            cs.writeMitab(mitab.getPsiWriter());

            //write SIF
            sif.appendInteraction(sifFileName, cs.getSource().getName().get(0).split(":", 2)[1],
                    cs.getEffect().get(0).getText(),
                    cs.getTarget().getName().get(0).split(":", 2)[1]);
        }

        // Causal interactions extracted from Translation events
        Collection<GeneRegCausality> causalTranslations = DataFactory.getCausalTranslation();
        System.out.println("Number of translation reactions: " + causalTranslations.size() + "\n");

        for (GeneRegCausality causalTranslation : causalTranslations) {
            CausalStatement cs = causalTranslation.getCausalStatement("translation");

            //write MITAB2.8
            cs.writeMitab(mitab.getPsiWriter());

            //write SIF
            sif.appendInteraction(sifFileName, cs.getSource().getName().get(0).split(":", 2)[1],
                    cs.getEffect().get(0).getText(),
                    cs.getTarget().getName().get(0).split(":", 2)[1]);
        }
        //Causal interactions extracted from Catalysis events
        Collection<CausalCatalysis> causalCatalyses = DataFactory.getListCatalysts();
        System.out.println("Number of catalysts: " + causalCatalyses.size());
        int count = 0;
        for (CausalCatalysis causalCatalysis : causalCatalyses) {
            count += 1;
            System.out.println(count);
            List<CausalStatement> listCs = causalCatalysis.getCausalStatement();
            if (listCs != null) {
                for (CausalStatement cs : listCs) {
                    if (cs.getSource() != null && cs.getTarget() != null) {
                        //write SIF
                        sif.appendInteraction(sifFileName, cs.getSource().getName().get(0).split(":", 2)[1],
                                cs.getEffect().get(0).getText(),
                                cs.getTarget().getName().get(0).split(":", 2)[1]);

                        //Write MITAB2.8
                        cs.writeMitab(mitab.getPsiWriter());
                    }
                }
            }

        }

        System.out.println();
        long stopTime = System.currentTimeMillis();
        System.out.println("Execution time: " + ((stopTime - startTime) / 1000) + "sec");
    }

}