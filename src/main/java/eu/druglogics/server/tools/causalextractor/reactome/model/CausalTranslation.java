package eu.druglogics.server.tools.causalextractor.reactome.model;

import eu.druglogics.server.tools.causalextractor.mitab.PSIWriter;
import eu.druglogics.server.tools.causalextractor.reactome.DataFactory;
import org.reactome.server.graph.domain.model.*;
import psidev.psi.mi.tab.model.BinaryInteraction;
import psidev.psi.mi.tab.model.BinaryInteractionImpl;

import java.io.IOException;
import java.util.*;

public class CausalTranslation {

    private ReactionLikeEvent rle;
    private PhysicalEntity input;
    private PhysicalEntity output;

    private Regulation regulation;
    private Complex source;

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

    public Complex getSource() {
        return source;
    }

    public Collection<Publication> getPublications() {
        return publications;
    }

    /**
     * Write the causal interaction occurring to the gene
     * The input of a translation reaction from Reactome is an mRNA
     *
     * @param writerTranslation PSIWriter - object to write the causal interaction
     * @throws IOException
     */
    public void writeRNARegulation(PSIWriter writerTranslation) throws IOException {
        //SOURCE ENTITY = REGULATOR = always a COMPLEX of Ribosomes and mRNA
        Interactor reactomeRegulator = new Interactor(this.source,1, AnnotationUtils.REGULATOR);
        List<psidev.psi.mi.tab.model.Interactor> regulators = reactomeRegulator.createParticipant(writerTranslation);

        //TARGET ENTITY = mRNA
        Interactor reactomeTarget = new Interactor(this.input, DataFactory.getStoichiometry(this.rle, this.input), AnnotationUtils.TARGET);
        List<psidev.psi.mi.tab.model.Interactor> targets = reactomeTarget.createParticipant(writerTranslation);

        for (psidev.psi.mi.tab.model.Interactor regulator : regulators) {
            for (psidev.psi.mi.tab.model.Interactor target : targets) {
                //Causal PSIWriter 1: ribosome + mRNA complex regulates mRNA
                BinaryInteraction binaryInteraction = new BinaryInteractionImpl(regulator, target);
                AnnotationUtils.setDefaultInteraction(binaryInteraction, this.rle, this.publications);

                if (this.regulation instanceof NegativeGeneExpressionRegulation) {
                    // Gene's transcription is down-regulated
                    binaryInteraction.setCausalStatement(AnnotationUtils.DOWN_REGULATES);
                    binaryInteraction.setCausalRegulatoryMechanism(AnnotationUtils.TRANSLATION_REG);
                    writerTranslation.appendInFile(binaryInteraction);
                } else {// Gene expression is up-regulated and enables the increase of the protein's quantity.
                    binaryInteraction.setCausalStatement(AnnotationUtils.UP_REGULATES);
                    binaryInteraction.setCausalRegulatoryMechanism(AnnotationUtils.TRANSLATION_REG);
                    writerTranslation.appendInFile(binaryInteraction);
                }
            }
        }
    }

    /**
     * Write the causal interaction occurring to the output of Reactome's reaction
     * The output is aProtein in the case of a translation reaction
     *
     * @param writerTranslation PSIWriter - object to write the causal interaction
     * @throws IOException
     */
    public void writeProteinRegulation(PSIWriter writerTranslation) throws IOException {
        //SOURCE ENTITY = REGULATOR = always a COMPLEX of ribosomes and mRNA
        Interactor reactomeRegulator = new Interactor(this.source, 1, AnnotationUtils.REGULATOR);
        List<psidev.psi.mi.tab.model.Interactor> regulators = reactomeRegulator.createParticipant(writerTranslation);

        //TARGET ENTITY = PROTEIN regulation of quantity by expression or repression
        Interactor reactomeTargetProt = new Interactor(this.output, DataFactory.getStoichiometry(this.rle, this.output), AnnotationUtils.TARGET);
        List<psidev.psi.mi.tab.model.Interactor> targets = reactomeTargetProt.createParticipant(writerTranslation);

        for (psidev.psi.mi.tab.model.Interactor regulator : regulators) {
            for (psidev.psi.mi.tab.model.Interactor target : targets) {
                //Causal interaction 2: ribosome + mRNA complex regulates the PROTEIN
                BinaryInteraction binaryInteraction = new BinaryInteractionImpl(regulator, target);
                AnnotationUtils.setDefaultInteraction(binaryInteraction, this.rle, this.publications);
                binaryInteraction.setInteractionTypes(AnnotationUtils.FUNCTIONAL_ASSOCIATION);

                if (this.regulation instanceof NegativeGeneExpressionRegulation) {
                    //Write interaction between the complex and the protein
                    binaryInteraction.setCausalStatement(AnnotationUtils.DOWN_REGULATES_REPRESSION);
                    binaryInteraction.setCausalRegulatoryMechanism(AnnotationUtils.TRANSLATION_REG);
                    writerTranslation.appendInFile(binaryInteraction);
                } else { // Gene expression is up-regulated and enables the increase of the protein's quantity.
                    binaryInteraction.setCausalStatement(AnnotationUtils.UP_REGULATES_EXPRESSION);
                    binaryInteraction.setCausalRegulatoryMechanism(AnnotationUtils.TRANSLATION_REG);
                    writerTranslation.appendInFile(binaryInteraction);
                }
            }
        }
    }


}
