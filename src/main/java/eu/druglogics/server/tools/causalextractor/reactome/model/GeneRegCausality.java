package eu.druglogics.server.tools.causalextractor.reactome.model;

import eu.druglogics.server.tools.causalextractor.Main;
import eu.druglogics.server.tools.causalextractor.causalStatement.CausalStatement;
import eu.druglogics.server.tools.causalextractor.causalStatement.Entity;
import eu.druglogics.server.tools.causalextractor.causalStatement.Term;
import eu.druglogics.server.tools.causalextractor.export.PSIWriter;
import eu.druglogics.server.tools.causalextractor.reactome.DataFactory;
import org.reactome.server.graph.domain.model.*;
import org.reactome.server.graph.service.PhysicalEntityService;
import org.reactome.server.graph.utils.ReactomeGraphCore;
import psidev.psi.mi.tab.model.*;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Transcription class with Objects from the Reactome Extraction factory.
 * Information kept for the source (TF) and targets (gene as input and protein as output), the regulation and the reaction
 * Methods to write the causal interaction.
 *
 * @author Vasundra Touré
 */

public class CausalTranscription {

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



    public Term regulationType(){
        Term effect = new Term();
        effect.setDatabaseName("MI");
        if (this.regulation instanceof NegativeGeneExpressionRegulation) {
            if (ActiveEntities.getInstance().getList().contains(output.getStId())) { //active gene product
                effect.setIdentifier(AnnotationUtils.DOWN_REGULATES_ACTIVITY.get(0).getIdentifier());
                effect.addName(AnnotationUtils.DOWN_REGULATES_ACTIVITY.get(0).getText());
            } else {
                effect.setIdentifier(AnnotationUtils.DOWN_REGULATES_REPRESSION.get(0).getIdentifier());
                effect.addName(AnnotationUtils.DOWN_REGULATES_REPRESSION.get(0).getText());
            }
        }
        else if (this.regulation instanceof PositiveGeneExpressionRegulation) {
            if (ActiveEntities.getInstance().getList().contains(output.getStId())) { //active gene product
                effect.setIdentifier(AnnotationUtils.UP_REGULATES_ACTIVITY.get(0).getIdentifier());
                effect.addName(AnnotationUtils.UP_REGULATES_ACTIVITY.get(0).getText());

            } else {
                effect.setIdentifier(AnnotationUtils.UP_REGULATES_EXPRESSION.get(0).getIdentifier());
                effect.addName(AnnotationUtils.UP_REGULATES_EXPRESSION.get(0).getText());
            }
        }
        return effect;
    }

    public CausalStatement getCausalStatement(String reactionType){
        System.out.println(rle.getStId());
        CausalStatement causalStatement = new CausalStatement();

        Entity source = new Entity();
        source.setEntity(this.source);

        Entity target = new Entity();
        target.setEntity(this.output);

        causalStatement.setSource(source);
        causalStatement.setTarget(target);
        causalStatement.setEffect(regulationType());
        Term mechanism = new Term();
        mechanism.setDatabaseName(AnnotationUtils.PSI_MI);
        if(reactionType == "transcription") {
            mechanism.setIdentifier(AnnotationUtils.TRANSCRIPTIONAL_REG.get(0).getIdentifier());
            List<String> names = new ArrayList<>();
            mechanism.addName(AnnotationUtils.TRANSCRIPTIONAL_REG.get(0).getText());

        }
        else if (reactionType == "translation"){
            mechanism.setIdentifier(AnnotationUtils.TRANSLATION_REG.get(0).getIdentifier());
            List<String> names = new ArrayList<>();
            mechanism.addName(AnnotationUtils.TRANSLATION_REG.get(0).getText());

        }
        else{
            System.out.print("Error: not transcription or translation");
        }
        causalStatement.setMechanism(mechanism);
        return causalStatement;
    }


    /**
     * Write the causal interaction occurring to the gene
     * The input of a transcription reaction from Reactome is a gene
     *
     * @param writerTranscription PSIWriter - object to write the causal interaction
     * @throws IOException
     */
//    public void writeTranscription(PSIWriter writerTranscription) throws IOException {
//        //SOURCE ENTITY = REGULATOR = always a COMPLEX of TF-TG
//        Interactor reactomeRegulator = new Interactor(this.source, 1, AnnotationUtils.REGULATOR);
//        List<psidev.psi.mi.tab.model.Interactor> regulators = reactomeRegulator.createParticipant(writerTranscription);
//
//        Interactor reactomeTarget;
//        //TARGET ENTITY = output of the reaction
//        reactomeTarget = new Interactor(this.output, DataFactory.getStoichiometry(this.rle, this.output), AnnotationUtils.TARGET);
//
//        List<psidev.psi.mi.tab.model.Interactor> targets = reactomeTarget.createParticipant(writerTranscription);
//
//        //Causal PSIWriter 1: TF-TG complex regulates the GENE
//        for (psidev.psi.mi.tab.model.Interactor regulator : regulators) {
//            for (psidev.psi.mi.tab.model.Interactor target : targets) {
//                BinaryInteraction binaryInteraction = new BinaryInteractionImpl(regulator, target);
//                AnnotationUtils.setDefaultInteraction(binaryInteraction, this.rle, this.publications);
//                binaryInteraction.setInteractionTypes(AnnotationUtils.FUNCTIONAL_ASSOCIATION);
//                if (this.regulation instanceof NegativeGeneExpressionRegulation) {
//                    if (target.getInteractorTypes().get(0).equals(AnnotationUtils.RNA.get(0))) { // RNA instead of protein
//                        binaryInteraction.setCausalStatement(AnnotationUtils.DOWN_REGULATES);
//                    } else {
//                        if(DataFactory.getActiveEntities().contains(output.getStId())){ // the protein is active in this state
//                         binaryInteraction.setCausalStatement(AnnotationUtils.DOWN_REGULATES_ACTIVITY);
//                        }
//                        else { // no activity found for this protein state
//                            binaryInteraction.setCausalStatement(AnnotationUtils.DOWN_REGULATES_REPRESSION);
//                        }
//                    }
//                    binaryInteraction.setCausalRegulatoryMechanism(AnnotationUtils.TRANSCRIPTIONAL_REG);
//                    writerTranscription.appendInFile(binaryInteraction);
//                } else {// Gene expression is up-regulated and enables the increase of the protein's quantity.
//                    if (target.getInteractorTypes().get(0).equals(AnnotationUtils.RNA.get(0))) { // RNA instead of protein
//                        binaryInteraction.setCausalStatement(AnnotationUtils.UP_REGULATES);
//                    } else {
//                        if (DataFactory.getActiveEntities().contains(output.getStId())) { // the protein is active in this state
//                            binaryInteraction.setCausalStatement(AnnotationUtils.UP_REGULATES_ACTIVITY);
//                        } else {// no activity found for this protein state
//                            binaryInteraction.setCausalStatement(AnnotationUtils.UP_REGULATES_EXPRESSION);
//                        }
//                    }
//
//                    //The biological mechanism is set as "transcriptional regulation"
//                    binaryInteraction.setCausalRegulatoryMechanism(AnnotationUtils.TRANSCRIPTIONAL_REG);
//                    writerTranscription.appendInFile(binaryInteraction);
//                }
//            }
//        }
//    }
}
