package eu.druglogics.server.tools.causalextractor.reactome;

import eu.druglogics.server.tools.causalextractor.reactome.model.CausalCatalysis;
import eu.druglogics.server.tools.causalextractor.reactome.model.GeneRegCausality;
import org.reactome.server.graph.domain.model.*;
import org.reactome.server.graph.exception.CustomQueryException;
import org.reactome.server.graph.service.AdvancedDatabaseObjectService;
import org.reactome.server.graph.utils.ReactomeGraphCore;

import java.util.*;

/**
 * Queries to Reactome graph database.
 *
 * @author Vasundra Touré
 */

public class DataFactory {


    /**
     * Get list of active physical entities (act as regulators or catalysts)
     */

    private static final String ACTIVE_ENTITIES = "" +
            "MATCH (rle:ReactionLikeEvent)-[:regulatedBy|catalystActivity]-(o)-[:regulator|physicalEntity]->(pe:PhysicalEntity)" +
            " RETURN DISTINCT pe.stId ";

    public static Collection<String> getActiveEntities() {
        AdvancedDatabaseObjectService ads = ReactomeGraphCore.getService(AdvancedDatabaseObjectService.class);
        try {
            return ads.getCustomQueryResults(String.class, ACTIVE_ENTITIES, Collections.emptyMap());
        } catch (CustomQueryException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * Query: get causal interaction of a transcription (transcription factor bound to target gene)
     */
    private static final String TRANSCRIPTION = "" +
            "MATCH (rle:ReactionLikeEvent{speciesName:'Homo sapiens'})-[:regulatedBy]->(reg:Regulation)-[:regulator]->(source:PhysicalEntity)-[:hasComponent|hasMember|hasCandidate*]->(entity:EntityWithAccessionedSequence),\n" +
            "(rle)-[:input]->(input:PhysicalEntity), (rle)-[:output]->(output:PhysicalEntity)\n" +
            "WHERE (reg.schemaClass='PositiveGeneExpressionRegulation' OR reg.schemaClass='NegativeGeneExpressionRegulation') AND entity.referenceType='ReferenceDNASequence'\n" +
            "RETURN rle, reg AS regulation, source, input, output";

    public static Collection<GeneRegCausality> getCausalTranscription() {
        AdvancedDatabaseObjectService ads = ReactomeGraphCore.getService(AdvancedDatabaseObjectService.class);
        try {
            return ads.getCustomQueryResults(GeneRegCausality.class, TRANSCRIPTION, Collections.emptyMap());
        } catch (CustomQueryException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }


    /**
     * Query: get causal interaction structure from translation (entity bound to mRNA regulating the mRNA)
     */
    private static final String TRANSLATION = "" +
            "MATCH (rle:ReactionLikeEvent{speciesName:'Homo sapiens'})-[:regulatedBy]->(reg:Regulation)-[:regulator]->(source:Complex)-[:hasComponent|hasMember|hasCandidate*]->(entity:EntityWithAccessionedSequence),\n" +
            "(rle)-[:input]->(input:PhysicalEntity), (rle)-[:output]->(output:PhysicalEntity)\n" +
            "WHERE (reg.schemaClass='PositiveGeneExpressionRegulation' OR reg.schemaClass='NegativeGeneExpressionRegulation') AND entity.referenceType='ReferenceRNASequence'\n" +
            "RETURN rle, reg AS regulation, source, input, output";

    public static Collection<GeneRegCausality> getCausalTranslation() {
        AdvancedDatabaseObjectService ads = ReactomeGraphCore.getService(AdvancedDatabaseObjectService.class);
        try {
            return ads.getCustomQueryResults(GeneRegCausality.class, TRANSLATION, Collections.emptyMap());
        } catch (CustomQueryException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * Query: get catalystActivities in all reactions
     */
    private static final String CATALYST_EXTRACTION = "" +
            "MATCH (rle:ReactionLikeEvent{speciesName:'Homo sapiens'})-[:catalystActivity]->(ca:CatalystActivity)-[:physicalEntity]->(catalyst:PhysicalEntity)\n" +
            "OPTIONAL MATCH (rle)-[:input]->(input)\n" +
            "OPTIONAL MATCH (rle)-[:output]->(output)\n" +
            "RETURN distinct rle as reaction, ca as catalystActivity, catalyst, COLLECT(DISTINCT input) as source, COLLECT(DISTINCT output) as target";

    public static Collection<CausalCatalysis> getListCatalysts() {
        AdvancedDatabaseObjectService ads = ReactomeGraphCore.getService(AdvancedDatabaseObjectService.class);
        try {
            return ads.getCustomQueryResults(CausalCatalysis.class, CATALYST_EXTRACTION, Collections.emptyMap());
        } catch (CustomQueryException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * Get stoichiometry of entities involved in ReactionLikeEvents or entities in a Complex
     *
     * @param object - correspond to the Complex or Reaction from which the entities' stoichiometry is needed
     * @param entity - corresponds to the entity for which the stoichiometry is needed
     * @return int - corresponds to the stoichiometry
     */
    public static int getStoichiometry(DatabaseObject object, DatabaseObject entity) {
        //Default stoichiometry is 1
        int stoichiometry = 1;
        String QUERY = "";
        if (object instanceof ReactionLikeEvent) {
            QUERY = "" +
                    "MATCH (:ReactionLikeEvent{stId:'" + object.getStId() + "'})-[output]->(:DatabaseObject{stId:'" + entity.getStId() + "'}) " +
                    "RETURN output.stoichiometry";
        } else if (object instanceof Complex) {
            QUERY = "" +
                    "MATCH (:Complex{stId:'" + object.getStId() + "'})-[output:hasComponent]->(DatabaseObject{stId:'" + entity.getStId() + "'}) " +
                    "RETURN output.stoichiometry";
        }
        AdvancedDatabaseObjectService ads = ReactomeGraphCore.getService(AdvancedDatabaseObjectService.class);
        try {
            stoichiometry = ads.getCustomQueryResults(int.class, QUERY, Collections.emptyMap()).iterator().next();
        } catch (CustomQueryException e) {
            e.printStackTrace();
        }
        return stoichiometry;
    }

    //TODO: extract pathway top level information for each reaction
    private static final String PATHWAY_QUERY = "" +
            "MATCH (rle:ReactionLikeEvent{stId:'R-HSA-199299'})<-[:hasEvent*]-(n:TopLevelPathway)\n" +
            "RETURN n, rle";

    public static void getInteractor(String stId) {


    }
}