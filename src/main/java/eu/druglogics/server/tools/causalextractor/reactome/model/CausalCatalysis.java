package eu.druglogics.server.tools.causalextractor.reactome.model;

import eu.druglogics.server.tools.causalextractor.causalStatement.CausalStatement;
import eu.druglogics.server.tools.causalextractor.causalStatement.Entity;
import eu.druglogics.server.tools.causalextractor.reactome.ActiveEntities;
import eu.druglogics.server.tools.causalextractor.reactome.AnnotationUtils;
import org.reactome.server.graph.domain.model.*;
import psidev.psi.mi.tab.model.CrossReference;
import psidev.psi.mi.tab.model.CrossReferenceImpl;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
/**
 * Causal interactions implicating catalysis reactions
 *
 * @author Vasundra Touré
 */

public class CausalCatalysis {

    CatalystActivity catalystActivity;
    PhysicalEntity catalyst;
    Collection<PhysicalEntity> source;
    Collection<PhysicalEntity> target;
    Boolean targetIsActive;
    Boolean negativeFeedback;
    ReactionLikeEvent reaction;

    public CatalystActivity getCatalystActivity() {
        return catalystActivity;
    }

    public Collection<PhysicalEntity> getSource() {
        return source;
    }

    public Collection<PhysicalEntity> getTarget() {
        return target;
    }

    public PhysicalEntity getCatalyst() {
        return catalyst;
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

    private CausalStatement createCS(Entity source, PhysicalEntity pe, List<CrossReference> effect) {
        Entity target = new Entity(pe);
        target.setEntity(pe);

        CausalStatement causalStatement = new CausalStatement();
        causalStatement.setSource(source);
        causalStatement.setEffect(effect);
        causalStatement.setTarget(target);
        return causalStatement;

    }

    public List<CausalStatement> getCausalStatement() {
        ArrayList<CausalStatement> listCs = new ArrayList<>();

        // CATALYST = source entity
        Entity source = new Entity(this.catalyst);
        source.setEntity(this.catalyst);
        List<CrossReference> bioActivity = new ArrayList<>(
                Collections.singleton(new CrossReferenceImpl(this.catalystActivity.getActivity().getDatabaseName(),
                        this.catalystActivity.getActivity().getAccession(), this.catalystActivity.getActivity().getName())));
        source.setBiologicalActivity(bioActivity);

        if (this.source.isEmpty() || this.target.isEmpty()) { //at least one source or target missing in the reaction
            return null;

        } else { //both source and target are present
            for (PhysicalEntity peSource : this.source) {
                boolean activeReactant = ActiveEntities.getInstance().getList().contains(peSource.getStId());
                if (activeReactant) {
                    if (peSource instanceof SimpleEntity) {
                        if (((SimpleEntity) peSource).getReferenceEntity().getTrivial().booleanValue() == false) { //we do no generate causality on trivial entities
                            CausalStatement causalStatement = createCS(source, peSource, AnnotationUtils.DOWN_REGULATES_ACTIVITY);
                            listCs.add(causalStatement);
                        }
                    } else {
                        CausalStatement causalStatement = createCS(source, peSource, AnnotationUtils.DOWN_REGULATES_ACTIVITY);
                        listCs.add(causalStatement);
                    }

                }
            }

            for (PhysicalEntity peTarget : this.target) {
                boolean activeProduct = ActiveEntities.getInstance().getList().contains(peTarget.getStId());
                if (activeProduct) {
                    if (peTarget instanceof SimpleEntity) {
                        if (((SimpleEntity) peTarget).getReferenceEntity().getTrivial().booleanValue() == false) {
                            CausalStatement causalStatement = createCS(source, peTarget, AnnotationUtils.UP_REGULATES_ACTIVITY);
                            listCs.add(causalStatement);
                        }
                    } else {
                        CausalStatement causalStatement = createCS(source, peTarget, AnnotationUtils.UP_REGULATES_ACTIVITY);
                        listCs.add(causalStatement);

                    }
                }
            }

        }

        return listCs;
    }

}