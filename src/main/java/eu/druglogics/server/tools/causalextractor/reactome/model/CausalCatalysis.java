package eu.druglogics.server.tools.causalextractor.reactome.model;

import eu.druglogics.server.tools.causalextractor.mitab.PSIWriter;
import eu.druglogics.server.tools.causalextractor.reactome.DataFactory;
import org.reactome.server.graph.domain.model.*;
import psidev.psi.mi.tab.model.BinaryInteraction;
import psidev.psi.mi.tab.model.BinaryInteractionImpl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;


public class CausalCatalysis {

    CatalystActivity catalystActivity;
    PhysicalEntity source;
    PhysicalEntity target;
    Boolean targetIsActive;
    Boolean negativeFeedback;
    ReactionLikeEvent reaction;

    public CatalystActivity getCatalystActivity() {
        return catalystActivity;
    }

    public PhysicalEntity getSource() {
        return source;
    }

    public PhysicalEntity getTarget() {
        return target;
    }

    public ReactionLikeEvent getReaction() {
        return reaction;
    }

    public Boolean getTargetIsActive() {
        return targetIsActive;
    }

    public Boolean getNegativeFeedback() {
        return negativeFeedback;
    }


    public void writeCatalysis(PSIWriter writerCatalysis, Collection<String> activEntities) throws IOException {
       // Create Source entity of causal interaction
        Interactor reactomeRegulator = new Interactor(this.source,
                1,
                AnnotationUtils.REGULATOR);
        List<psidev.psi.mi.tab.model.Interactor> regulators = reactomeRegulator.createParticipant(writerCatalysis);

        if (activEntities.contains(this.target.getStId())) {
            //Create Target entity of causal interaction which is the output of the reaction
            createTarget(this.target, reactomeRegulator, writerCatalysis, regulators);
        }

        if(activEntities.contains(this.source.getStId())){
            //Create Target entity of causal interaction which is the output of the reaction
             createTarget(this.source, reactomeRegulator, writerCatalysis, regulators);
        }
    }

    public void createTarget(PhysicalEntity entity, Interactor reactomeRegulator, PSIWriter writerCatalysis, List<psidev.psi.mi.tab.model.Interactor> regulators) throws IOException {
        Interactor reactomeTarget = new Interactor(entity,
                DataFactory.getStoichiometry(this.reaction, entity),
                AnnotationUtils.TARGET);
        List<psidev.psi.mi.tab.model.Interactor> regulated = reactomeTarget.createParticipant(writerCatalysis);
        for (psidev.psi.mi.tab.model.Interactor regulator : regulators) {
            for (psidev.psi.mi.tab.model.Interactor regulatedEntity : regulated) {
                BinaryInteraction binaryInteraction1 = new BinaryInteractionImpl(regulator, regulatedEntity);
                AnnotationUtils.setDefaultInteraction(binaryInteraction1, this.reaction, null);
                binaryInteraction1.setCausalStatement(AnnotationUtils.DOWN_REGULATES_ACTIVITY);
                writerCatalysis.appendInFile(binaryInteraction1);
            }
        }
    }
}