package eu.druglogics.server.tools.causalextractor.reactome.model;

import eu.druglogics.server.tools.causalextractor.export.PSIWriter;
import eu.druglogics.server.tools.causalextractor.reactome.AnnotationUtils;
import eu.druglogics.server.tools.causalextractor.reactome.Complexes;
import org.reactome.server.graph.domain.model.*;

import org.reactome.server.graph.service.PhysicalEntityService;
import org.reactome.server.graph.utils.ReactomeGraphCore;
import psidev.psi.mi.tab.model.*;


import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Interactor {

    private PhysicalEntity entity;
    private int stoichiometry;
    private List<CrossReference> biologicalRole;
    private List<CrossReference> bioAct;
    private psidev.psi.mi.tab.model.Interactor interactorPSI = new psidev.psi.mi.tab.model.Interactor();


    public Interactor(PhysicalEntity entity, int stoichiometry, List<CrossReference> biologicalRole, List<CrossReference> bioActivity) {
        this.entity = entity;
        this.stoichiometry = stoichiometry;
        this.biologicalRole = biologicalRole;
        this.bioAct = bioActivity;
    }

    private void setPhysicalEntity(PhysicalEntity entity) {
        this.entity = entity;
    }

    private void setStoichiometry(int stoichiometry) {
        this.stoichiometry = stoichiometry;
    }

    private psidev.psi.mi.tab.model.Interactor getInteractorPSI() {
        return interactorPSI;
    }


    public List<psidev.psi.mi.tab.model.Interactor> createParticipant(PSIWriter psiWriter) throws IOException {
        List<psidev.psi.mi.tab.model.Interactor> participants = new ArrayList<>();
        interactorPSI.setAliases(getAliasesFromReactome(this.entity));

        if (this.entity instanceof EntityWithAccessionedSequence) {
            //Set Identifier, Alternative ID
            interactorPSI.setIdentifiers(new ArrayList<>(getReactomeId(this.entity)));
            interactorPSI.setAlternativeIdentifiers(new ArrayList<>(getReactomeAlternativeId(this.entity)));
            //Set Organism with Taxonomy Identifier
            interactorPSI.setOrganism(new OrganismImpl(Integer.parseInt(((EntityWithAccessionedSequence) this.entity).getSpecies().getTaxId())));
            //Set Type: Protein, RNA, gene, etc...
            interactorPSI.setInteractorTypes(getInteractorType(this.entity.getSchemaClass(),
                    ((EntityWithAccessionedSequence) this.entity).getReferenceType()));
            participants.add(interactorPSI);

        } else { //Complex | CandidateSet | DefinedSet
            //TODO: add cases for simplechemical, drugs?
            interactorPSI.setInteractorTypes(getInteractorType(this.entity.getSchemaClass(), null));
            CrossReference identifier = new CrossReferenceImpl();

            //Set Reactome id as Identifier
            identifier.setDatabase(AnnotationUtils.REACTOME);
            identifier.setIdentifier(this.entity.getStId());
            interactorPSI.setIdentifiers(new ArrayList<>(Collections.singleton(identifier)));

            //Set list of organism's taxids for complexes
            interactorPSI.setOrganism(new OrganismImpl(getMultipleSpecies(this.entity)));

            if (this.entity instanceof Complex) {

                if (!(Complexes.getInstance().getList().contains(this.entity))) { //A complex that hasn't been parsed yet
                    Complexes.getInstance().addComplex(this.entity); //Add complex to the general list of complexes

                    //Collect components of a complex: PhysicalEntities connected to their Stoichiometry
                    Map<PhysicalEntity, Long> members = ((Complex) this.entity).getHasComponent().stream().collect(
                            Collectors.groupingBy(Function.identity(), Collectors.counting()));

                    if (members.size() == 1) { //No binaries created
                        // The current PhysicalEntity (complex) is changed to the participant, which can be a multimer or sets for example
                        setPhysicalEntity(members.keySet().iterator().next());
                        setStoichiometry(members.values().iterator().next().intValue());
                        this.createParticipant(psiWriter);

                    } else { // Multiple components: create binaries between them
                        ReactomeComplex reactomeComplex = new ReactomeComplex(entity, members);
                        reactomeComplex.writeExpansionComplex(psiWriter);

                        for (PhysicalEntity component : ((Complex) this.entity).getHasComponent()) {
                            Interactor reactomeInteractor = new Interactor(component,
                                    (members.values().iterator().next().intValue()),
                                    AnnotationUtils.UNSPECIFIED_ROLE, null);
                            reactomeInteractor.createParticipant(psiWriter);
                        }
                    }
                } else { // Complex already processed - update the PhysicalEntity to the processes complex
                    setPhysicalEntity(Complexes.getInstance().getList().get(Complexes.getInstance().getList().indexOf(this.entity)));
                }

                participants.add(interactorPSI);

            } else if (this.entity instanceof DefinedSet) {
                //Create one interaction for each member of the set
                Map<PhysicalEntity, Long> members = ((DefinedSet) this.entity).getHasMember().stream().collect(
                        Collectors.groupingBy(Function.identity(), Collectors.counting()));

                for (Map.Entry<PhysicalEntity, Long> entry : members.entrySet()) {
                    Interactor participantFromSet = new Interactor(entry.getKey(), entry.getValue().intValue(), this.biologicalRole, null);
                    participantFromSet.createParticipant(psiWriter);

                    //The participant comes from a defined set: keep the information as an annotation
                    Annotation comment = new AnnotationImpl("comment");
                    comment.setText("defined set:" + this.entity.getStId());

                    List<Annotation> annotationList = new ArrayList<>();
                    annotationList.add(comment);
                    participantFromSet.interactorPSI.setAnnotations(annotationList);
                    participants.add(participantFromSet.interactorPSI);
                }
            } else if (this.entity instanceof CandidateSet) {
                //Create one interaction for each member of the set
                Map<PhysicalEntity, Long> members = ((CandidateSet) this.entity).getHasCandidate().stream().collect(
                        Collectors.groupingBy(Function.identity(), Collectors.counting()));

                for (Map.Entry<PhysicalEntity, Long> entry : members.entrySet()) {
                    Interactor participantFromSet = new Interactor(entry.getKey(), entry.getValue().intValue(), this.biologicalRole, null);
                    participantFromSet.createParticipant(psiWriter);

                    //The participant comes from a candidate set: keep the information as an annotation
                    Annotation comment = new AnnotationImpl("comment");
                    comment.setText("candidate set:" + this.entity.getStId());

                    List<Annotation> annotationList = new ArrayList<>();
                    annotationList.add(comment);
                    participantFromSet.interactorPSI.setAnnotations(annotationList);
                    participants.add(participantFromSet.interactorPSI);
                }
            }
        }

        if (this.bioAct != null) {
            interactorPSI.setBiologicalEffects(this.bioAct);
        }
        //The following metadata is the same setting for all interactors
        //Set Compartment as Xrefs
        interactorPSI.setXrefs(AnnotationUtils.getCompartmentPE(this.entity));
        //Set Stoichiometry
        interactorPSI.setStoichiometry(Collections.singletonList(stoichiometry));
        //Set Biological Role: regulator or target
        interactorPSI.setBiologicalRoles(biologicalRole);
        //Unspecified parameters:
        interactorPSI.setExperimentalRoles(AnnotationUtils.UNKNOWN_ROLE);
        return participants;
    }

    private Set<CrossReference> getReactomeId(PhysicalEntity pe) {
        CrossReference id = new CrossReferenceImpl();
        id.setDatabase(((EntityWithAccessionedSequence) pe).getReferenceEntity().getDatabaseName().toLowerCase());
        id.setIdentifier(String.valueOf(((EntityWithAccessionedSequence) pe).getReferenceEntity().getIdentifier()));
        return (Collections.singleton(id));

    }

    private Set<CrossReference> getReactomeAlternativeId(PhysicalEntity pe) {
        CrossReference alternativeId = new CrossReferenceImpl();
        alternativeId.setDatabase(AnnotationUtils.REACTOME);
        alternativeId.setIdentifier(this.entity.getStId());
        return (Collections.singleton(alternativeId));
    }

    private List<Alias> getAliasesFromReactome(PhysicalEntity pe) {
        List<Alias> aliases = new ArrayList<>();
        if (pe instanceof EntityWithAccessionedSequence) {
            if (((EntityWithAccessionedSequence) pe).getReferenceEntity().getGeneName() != null) {
                //Set alias for original database source
                Alias alias = new AliasImpl();
                alias.setDbSource(((EntityWithAccessionedSequence) pe).getReferenceEntity().getDatabaseName().toLowerCase());
                alias.setName(((EntityWithAccessionedSequence) pe).getReferenceEntity().getGeneName().get(0));
                aliases.add(alias);
            }
        }
        // Set alias from reactome database
        Alias alias_reactome = new AliasImpl();
        alias_reactome.setDbSource(AnnotationUtils.REACTOME);
        alias_reactome.setName(pe.getName().get(0));
        aliases.add(alias_reactome);
        return aliases;
    }

    private static List<CrossReference> getInteractorType(String schemaClass, String referenceType) {
        switch (schemaClass) {
            case "EntityWithAccessionedSequence":
                switch (referenceType) {
                    case "ReferenceDNASequence":
                        return AnnotationUtils.GENE;
                    case "ReferenceRNASequence":
                        return AnnotationUtils.RNA;
                    case "ReferenceGeneProduct":
                        return AnnotationUtils.PROTEIN;
                    case "ReferenceIsoform":
                        return AnnotationUtils.PROTEIN;
                    case "ReferenceMolecule":
                        return AnnotationUtils.CHEMICAL;
                }
            case "DefinedSet":
                return AnnotationUtils.DEFINED_SET;
            case "CandidateSet":
                return AnnotationUtils.CANDIDATE_SET;
            case "Complex":
                return AnnotationUtils.COMPLEX;
            case "Polymer":
                return AnnotationUtils.BIOPOLYMER;
            case "SimpleEntity":
                return AnnotationUtils.CHEMICAL;
            default:
                return AnnotationUtils.UNKNOWN_PARTICIPANT;
        }
    }

    private List<CrossReference> getMultipleSpecies(PhysicalEntity pe) {
        List<CrossReference> taxIds = new ArrayList<>();
        PhysicalEntityService ps = ReactomeGraphCore.getService(PhysicalEntityService.class);
        for (PhysicalEntity component : ps.getPhysicalEntitySubunitsNoStructures(pe)) {
            if (component instanceof EntityWithAccessionedSequence) {
                CrossReference organism = new CrossReferenceImpl();
                organism.setDatabase(AnnotationUtils.TAX_ID);
                organism.setIdentifier(((EntityWithAccessionedSequence) component).getSpecies().getTaxId());
                if (!(taxIds.contains(organism))) {
                    taxIds.add(organism);
                }
            }
        }
        return taxIds;
    }
}

