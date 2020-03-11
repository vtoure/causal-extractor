package eu.druglogics.server.tools.causalextractor.reactome.model;

import eu.druglogics.server.tools.causalextractor.mitab.PSIWriter;
import org.reactome.server.graph.domain.model.PhysicalEntity;
import psidev.psi.mi.tab.model.BinaryInteraction;
import psidev.psi.mi.tab.model.BinaryInteractionImpl;
import psidev.psi.mi.tab.model.CrossReferenceImpl;

import java.io.IOException;
import java.util.ArrayList;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ReactomeComplex {
    private PhysicalEntity complex;
    Map<PhysicalEntity, Long> participants_stoichiometry;

    public ReactomeComplex(PhysicalEntity complex) {
        this.complex = complex;
    }

    public ReactomeComplex(PhysicalEntity complex, Map<PhysicalEntity, Long> participants) {
        this.complex = complex;
        this.participants_stoichiometry = participants;
    }


    public PhysicalEntity getComplex() {
        return complex;
    }

    public void setComplex(PhysicalEntity complex) {
        this.complex = complex;
    }

    public Map<PhysicalEntity, Long> getParticipants() {
        return participants_stoichiometry;
    }

    public void setParticipants(Map<PhysicalEntity, Long> participants) {
        this.participants_stoichiometry = participants;
    }


    void writeExpansionComplex(PSIWriter psiWriter) throws IOException {
        final ArrayList<PhysicalEntity> components = new ArrayList<>(participants_stoichiometry.keySet());

        for (int i = 0; i < components.size(); i++) {
            for (int j = i + 1; j < components.size(); j++) {
                Interactor reactomeComponent1 = new Interactor(components.get(i),
                        (participants_stoichiometry.get(components.get(i))).intValue(),
                        AnnotationUtils.UNSPECIFIED_ROLE);
                List<psidev.psi.mi.tab.model.Interactor> component1 = reactomeComponent1.createParticipant(psiWriter);


                Interactor reactomeComponent2 = new Interactor(components.get(j),
                        (participants_stoichiometry.get(components.get(j))).intValue(),
                        AnnotationUtils.UNSPECIFIED_ROLE);
                List<psidev.psi.mi.tab.model.Interactor> component2 = reactomeComponent2.createParticipant(psiWriter);

                for (psidev.psi.mi.tab.model.Interactor c1 : component1) {
                    for (psidev.psi.mi.tab.model.Interactor c2 : component2) {
                        BinaryInteraction binaryInteraction = new BinaryInteractionImpl(c1, c2);
                        //Set interaction identifier: Complex identifier
                        binaryInteraction.setInteractionAcs(new ArrayList<>(Collections.singletonList(
                                new CrossReferenceImpl(AnnotationUtils.REACTOME,
                                        complex.getStId()))));

                        binaryInteraction.setInteractionTypes(AnnotationUtils.PHYSICAL_ASSOCIATION);
                        binaryInteraction.setComplexExpansion(AnnotationUtils.SPOKE_EXPANSION);
                        //Set date of creation of the causal interaction
                        binaryInteraction.setCreationDate(AnnotationUtils.getDate());
                        psiWriter.appendInFile(binaryInteraction);
                    }

                }
            }
        }
    }

}
