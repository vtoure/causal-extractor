package eu.druglogics.server.tools.causalextractor.reactome.model;

import eu.druglogics.server.tools.causalextractor.export.PSIWriter;
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
     * Write the causal interaction occurring to the output of Reactome's reaction
     * The output is a Protein in the case of a translation reaction
     *
     * @param writerTranslation PSIWriter - object to write the causal interaction
     * @throws IOException
     */
    public void writeTranslation(PSIWriter writerTranslation) throws IOException {
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
                    if(DataFactory.getActiveEntities().contains(output.getStId())){ // the protein is active in this state
                        binaryInteraction.setCausalStatement(AnnotationUtils.DOWN_REGULATES_ACTIVITY);
                    }
                    else{
                        //Write interaction between the complex and the protein
                        binaryInteraction.setCausalStatement(AnnotationUtils.DOWN_REGULATES_REPRESSION);
                    }
                } else { // Gene expression is up-regulated and enables the increase of the protein's quantity.
                    if(DataFactory.getActiveEntities().contains(output.getStId())){ // the protein is active in this state
                        binaryInteraction.setCausalStatement(AnnotationUtils.UP_REGULATES_ACTIVITY);
                    }
                    else{
                        binaryInteraction.setCausalStatement(AnnotationUtils.UP_REGULATES_EXPRESSION);
                    }

                }
                binaryInteraction.setCausalRegulatoryMechanism(AnnotationUtils.TRANSLATION_REG);
                writerTranslation.appendInFile(binaryInteraction);
            }
        }
    }


}
