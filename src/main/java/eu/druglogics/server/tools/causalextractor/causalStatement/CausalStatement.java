package eu.druglogics.server.tools.causalextractor.causalStatement;

import eu.druglogics.server.tools.causalextractor.export.PSIWriter;
import eu.druglogics.server.tools.causalextractor.reactome.model.Interactor;
import eu.druglogics.server.tools.causalextractor.reactome.AnnotationUtils;
import psidev.psi.mi.tab.model.BinaryInteraction;
import psidev.psi.mi.tab.model.BinaryInteractionImpl;
import psidev.psi.mi.tab.model.CrossReference;
import java.io.IOException;
import java.util.List;

/**
 * Generation of a causal statement
 *
 * @author Vasundra Touré
 */

public class CausalStatement {
    Entity source;
    List<CrossReference> effect;
    Entity target;

    List<CrossReference> reference;

    List<CrossReference> interactionId;
    List<CrossReference> interactionType;

    Term effectCompartment;
    List<CrossReference> mechanism;
    Modification effectModif;
    Term taxon;

    public Entity getSource() {
        return source;
    }

    public void setSource(Entity source) {
        this.source = source;
    }

    public List<CrossReference> getEffect() {
        return effect;
    }

    public void setEffect(List<CrossReference> effect) {
        this.effect = effect;
    }

    public Entity getTarget() {
        return target;
    }

    public void setTarget(Entity target) {
        this.target = target;
    }

    public List<CrossReference> getReference() {
        return reference;
    }

    public void setReference(List<CrossReference> reference) {
        this.reference = reference;
    }

    public Term getEffectCompartment() {
        return effectCompartment;
    }

    public void setEffectCompartment(Term effectCompartment) {
        this.effectCompartment = effectCompartment;
    }

    public List<CrossReference> getMechanism() {
        return mechanism;
    }

    public void setMechanism(List<CrossReference> mechanism) {
        this.mechanism = mechanism;
    }

    public Modification getEffectModif() {
        return effectModif;
    }

    public void setEffectModif(Modification effectModif) {
        this.effectModif = effectModif;
    }

    public Term getTaxon() {
        return taxon;
    }

    public void setTaxon(Term taxon) {
        this.taxon = taxon;
    }

    public void setInteractionType(List<CrossReference> interactionType) {
        this.interactionType = interactionType;
    }

    public void setInteractionId(List<CrossReference> interactionId) {
        this.interactionId = interactionId;
    }

    public void writeMitab(PSIWriter writerMitab) throws IOException {
        //SOURCE ENTITY = REGULATOR = always a COMPLEX of TF-TG
        Interactor cs_regulator = new Interactor(this.source.entity, 1, AnnotationUtils.REGULATOR, this.source.biologicalActivity);
        List<psidev.psi.mi.tab.model.Interactor> regulators = cs_regulator.createParticipant(writerMitab);


        //Target entity = REGULATOR TARGET
        Interactor cs_regulatorTarget = new Interactor(this.target.entity, 1, AnnotationUtils.TARGET, this.target.biologicalActivity);
        List<psidev.psi.mi.tab.model.Interactor> regulatorTargets = cs_regulatorTarget.createParticipant(writerMitab);
        for (psidev.psi.mi.tab.model.Interactor regulator : regulators) {
            for (psidev.psi.mi.tab.model.Interactor regulatorTarget : regulatorTargets) {
                BinaryInteraction binaryInteraction = new BinaryInteractionImpl(regulator, regulatorTarget);

                binaryInteraction.setDetectionMethods(AnnotationUtils.INTERACTION_PREDICTION);
                binaryInteraction.setPublications(reference);
                binaryInteraction.setInteractionTypes(interactionType);
                binaryInteraction.setSourceDatabases(AnnotationUtils.REACTOME_DB);
                binaryInteraction.setInteractionAcs(interactionId);
                binaryInteraction.setCausalRegulatoryMechanism(mechanism);
                binaryInteraction.setCausalStatement(effect);

                writerMitab.appendInFile(binaryInteraction);
            }
        }
    }
}

