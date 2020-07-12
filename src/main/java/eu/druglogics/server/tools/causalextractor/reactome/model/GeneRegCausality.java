package eu.druglogics.server.tools.causalextractor.reactome.model;

import eu.druglogics.server.tools.causalextractor.causalStatement.CausalStatement;
import eu.druglogics.server.tools.causalextractor.causalStatement.Entity;
import eu.druglogics.server.tools.causalextractor.causalStatement.Term;
import eu.druglogics.server.tools.causalextractor.reactome.ActiveEntities;
import eu.druglogics.server.tools.causalextractor.reactome.AnnotationUtils;
import org.reactome.server.graph.domain.model.*;

import java.util.*;

/**
 * Transcription class with Objects from the Reactome Extraction factory.
 * Information kept for the source (TF) and targets (gene as input and protein as output), the regulation and the reaction
 * Methods to write the causal interaction.
 *
 * @author Vasundra Touré
 */

public class GeneRegCausality {

    private ReactionLikeEvent rle;
    private PhysicalEntity input; //input of the reaction - mainly a gene
    private PhysicalEntity output; // output of the reaction - mainly a protein

    private Regulation regulation;
    private PhysicalEntity source; // regulator of the reaction - mainly a TF-TG complex

    private Collection<Publication> publications;

    public ReactionLikeEvent getRle() {
        return rle;
    }

    public PhysicalEntity getInput() {
        return input;
    }

    public PhysicalEntity getOutput() {
        return output;
    }

    public Regulation getRegulation() {
        return regulation;
    }

    public PhysicalEntity getSource() {
        return source;
    }

    public Collection<Publication> getPublications() {
        return publications;
    }


    /**
     * Define the causal relationship for gene expression cases:
     * the source either regulates the activity (if output is active) or the quantity by expression|repression
     * @param causalStatement
     * @return
     */
    public Term regulationType(CausalStatement causalStatement){
        Term effect = new Term();
        effect.setDatabaseName(AnnotationUtils.PSI_MI);
        if (this.regulation instanceof NegativeGeneExpressionRegulation) { //negative regulation
            if (ActiveEntities.getInstance().getList().contains(output.getStId())) { //active gene product
                causalStatement.setEffect(AnnotationUtils.DOWN_REGULATES_ACTIVITY);
            } else {
                causalStatement.setEffect(AnnotationUtils.DOWN_REGULATES_REPRESSION);
            }
        }
        else if (this.regulation instanceof PositiveGeneExpressionRegulation) { //positive regulation
            if (ActiveEntities.getInstance().getList().contains(output.getStId())) { //active gene product
                causalStatement.setEffect(AnnotationUtils.UP_REGULATES_ACTIVITY);

            } else {
                causalStatement.setEffect(AnnotationUtils.UP_REGULATES_EXPRESSION);
            }
        }
        return effect;
    }

    /**
     * Obtain the causal statement from gene regulation reaction
     * @param reactionType - distinguish between transcription and translation for setting the correct mechanism
     * @return
     */
    public CausalStatement getCausalStatement(String reactionType){
        CausalStatement causalStatement = new CausalStatement();

        Entity source = new Entity(this.source);
        source.setEntity(this.source);

        Entity target = new Entity(this.output);
        target.setEntity(this.output);

        causalStatement.setSource(source);
        causalStatement.setTarget(target);
        regulationType(causalStatement);

        if(reactionType == "transcription") {
            causalStatement.setMechanism(AnnotationUtils.TRANSCRIPTIONAL_REG);

        }
        else if (reactionType == "translation"){
            causalStatement.setMechanism(AnnotationUtils.TRANSLATION_REG);

        }
        else{
            System.out.print("Error: not transcription or translation");
        }
        return causalStatement;
    }
}
