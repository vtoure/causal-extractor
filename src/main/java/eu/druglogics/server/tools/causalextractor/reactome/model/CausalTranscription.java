package eu.druglogics.server.tools.causalextractor.reactome.model;

import eu.druglogics.server.tools.causalextractor.mitab.PSIWriter;
import eu.druglogics.server.tools.causalextractor.reactome.DataFactory;
import org.reactome.server.graph.domain.model.*;
import psidev.psi.mi.tab.model.*;

import java.io.IOException;
import java.util.*;

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

    /**
     * Write the causal interaction occurring to the gene
     * The input of a transcription reaction from Reactome is a gene
     *
     * @param writerTranscription PSIWriter - object to write the causal interaction
     * @throws IOException
     */
    public void writeGeneRegulation(PSIWriter writerTranscription) throws IOException {
        Interactor reactomeTarget = new Interactor(this.output, 1, AnnotationUtils.TARGET);
        List<psidev.psi.mi.tab.model.Interactor> targets = reactomeTarget.createParticipant(writerTranscription);
//        //SOURCE ENTITY = REGULATOR = always a COMPLEX of TF-TG
//        Interactor reactomeRegulator = new Interactor(this.source, 1, AnnotationUtils.REGULATOR);
//
//        List<psidev.psi.mi.tab.model.Interactor> regulators = reactomeRegulator.createParticipant(writerTranscription);
//        //TARGET ENTITY = GENE
//        Interactor reactomeTarget = new Interactor(this.input, DataFactory.getStoichiometry(this.rle, this.input), AnnotationUtils.TARGET);
//        List<psidev.psi.mi.tab.model.Interactor> targets = reactomeTarget.createParticipant(writerTranscription);
//
//        //Causal PSIWriter 1: TF-TG complex regulates the GENE
//        for (psidev.psi.mi.tab.model.Interactor regulator : regulators) {
//            for (psidev.psi.mi.tab.model.Interactor target : targets) {
//                BinaryInteraction binaryInteraction = new BinaryInteractionImpl(regulator, target);
//                AnnotationUtils.setDefaultInteraction(binaryInteraction, this.rle, this.publications);
//                binaryInteraction.setInteractionTypes(AnnotationUtils.FUNCTIONAL_ASSOCIATION);
//                if (this.regulation instanceof NegativeGeneExpressionRegulation) {
//                    // Gene's transcription is down-regulated
//                    binaryInteraction.setCausalStatement(AnnotationUtils.DOWN_REGULATES);
//                    binaryInteraction.setCausalRegulatoryMechanism(AnnotationUtils.TRANSCRIPTIONAL_REG);
//                    writerTranscription.appendInFile(binaryInteraction);
//                } else {// Gene expression is up-regulated and enables the increase of the protein's quantity.
//                    binaryInteraction.setCausalStatement(AnnotationUtils.UP_REGULATES);
//                    binaryInteraction.setCausalRegulatoryMechanism(AnnotationUtils.TRANSCRIPTIONAL_REG);
//                    writerTranscription.appendInFile(binaryInteraction);
//                }
//            }
//        }
    }

    /**
     * Write the causal interaction occurring to the output of Reactome's reaction
     * The output can be a mRNA or a Protein in the case of a transcription reaction
     *
     * @param writerTranscription PSIWriter - object to write the causal interaction
     * @throws IOException
     */
    public void writeOutputRegulation(PSIWriter writerTranscription) throws IOException {
        //SOURCE ENTITY = REGULATOR = always a COMPLEX of TF-TG
        Interactor reactomeRegulator = new Interactor(this.source, 1, AnnotationUtils.REGULATOR);
        List<psidev.psi.mi.tab.model.Interactor> regulators = reactomeRegulator.createParticipant(writerTranscription);

        //TARGET ENTITY = mRNA OR PROTEIN but the causality effects are the same (regulation of quantity by expression or repression)
        Interactor reactomeTargetProt = new Interactor(this.output, DataFactory.getStoichiometry(this.rle, this.output), AnnotationUtils.TARGET);
        List<psidev.psi.mi.tab.model.Interactor> regulatedProteins = reactomeTargetProt.createParticipant(writerTranscription);

        //Causal interaction 2: TF-TG complex regulates the PROTEIN
        for (psidev.psi.mi.tab.model.Interactor regulator : regulators) {
            for (psidev.psi.mi.tab.model.Interactor regulatedProtein : regulatedProteins) {
                BinaryInteraction binaryInteraction = new BinaryInteractionImpl(regulator, regulatedProtein);
                AnnotationUtils.setDefaultInteraction(binaryInteraction, this.rle, this.publications);
                binaryInteraction.setInteractionTypes(AnnotationUtils.FUNCTIONAL_ASSOCIATION);

                if (this.regulation instanceof NegativeGeneExpressionRegulation) {
                    //Write interaction between the complex and the protein
                    binaryInteraction.setCausalStatement(AnnotationUtils.DOWN_REGULATES_REPRESSION);
                    binaryInteraction.setCausalRegulatoryMechanism(AnnotationUtils.TRANSCRIPTIONAL_REG);
                    writerTranscription.appendInFile(binaryInteraction);
                } else { // Gene expression is up-regulated and enables the increase of the protein's quantity.
                    binaryInteraction.setCausalStatement(AnnotationUtils.UP_REGULATES_EXPRESSION);
                    binaryInteraction.setCausalRegulatoryMechanism(AnnotationUtils.TRANSCRIPTIONAL_REG);
                    writerTranscription.appendInFile(binaryInteraction);
                }
            }
        }
    }
}
