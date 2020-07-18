package eu.druglogics.server.tools.causalextractor.causalStatement;

import org.reactome.server.graph.domain.model.*;
import org.reactome.server.graph.service.PhysicalEntityService;
import org.reactome.server.graph.utils.ReactomeGraphCore;
import psidev.psi.mi.tab.model.CrossReference;
import java.util.ArrayList;
import java.util.List;


public class Entity extends Term {
    PhysicalEntity entity;

    String databaseName;
    ArrayList<Term> alternativeId;
    ArrayList<Modification> modifications;
    List<CrossReference> biologicalActivity;
    Term biologicalType;
    ArrayList<Term> taxon;
    ArrayList<Term> experimentalSetup;

    List<Term> compartment;


    public Entity(PhysicalEntity pe) {
        super();
        this.entity = pe;
        this.alternativeId = new ArrayList<>();
        this.taxon = new ArrayList<>();
        this.modifications = new ArrayList<>();
    }

    public PhysicalEntity getEntity() {
        return entity;
    }

    @Override
    public String getDatabaseName() {
        return databaseName;
    }

    @Override
    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public ArrayList<Modification> getModifications() {
        return modifications;
    }

    public void setModifications(ArrayList<Modification> modifications) {
        this.modifications = modifications;
    }

    public void addModifications(Modification modification) {
        this.modifications.add(modification);
    }

    public List<CrossReference> getBiologicalActivity() {
        return biologicalActivity;
    }

    public void setBiologicalActivity(List<CrossReference> biologicalActivity) {
        this.biologicalActivity = biologicalActivity;
    }

    public Term getBiologicalType() {
        return biologicalType;
    }

    public void setBiologicalType(Term biologicalType) {
        this.biologicalType = biologicalType;
    }

    public ArrayList<Term> getTaxon() {
        return taxon;
    }

    public void setTaxon(ArrayList<Term> taxon) {
        this.taxon = taxon;
    }

    public void addTaxon(Term taxon) {
        this.taxon.add(taxon);
    }

    public ArrayList<Term> getExperimentalSetup() {
        return experimentalSetup;
    }

    public void setExperimentalSetup(ArrayList<Term> experimentalSetup) {
        this.experimentalSetup = experimentalSetup;
    }

    public List<Term> getCompartment() {
        return compartment;
    }

    public void setCompartment(List<Term> compartment) {
        this.compartment = compartment;
    }

    public ArrayList<Term> getAlternativeId() {
        return alternativeId;
    }

    public void setAlternativeId(ArrayList<Term> alternativeId) {
        this.alternativeId = alternativeId;
    }


    private ArrayList<String> getNames(PhysicalEntity pe) {
        ArrayList<String> names = new ArrayList<>();
        if (pe instanceof EntityWithAccessionedSequence) {
            if (((EntityWithAccessionedSequence) pe).getReferenceEntity().getGeneName() != null) {
                //Set name from the original database source
                String name = ((EntityWithAccessionedSequence) pe).getReferenceEntity().getDatabaseName().toLowerCase() + ":" +
                        ((EntityWithAccessionedSequence) pe).getReferenceEntity().getGeneName().get(0);
                names.add(name);
            }
        }
        // Set name from the reactome database
        String name_reactome = "reactome:" + pe.getName().get(0);
        names.add(name_reactome);
        return names;
    }

    private static Term getInteractorType(String schemaClass, String referenceType) {
        Term biologicalType = new Term();
        biologicalType.setDatabaseName("MI");
        switch (schemaClass) {
            case "EntityWithAccessionedSequence":
                switch (referenceType) {
                    case "ReferenceDNASequence":
                        biologicalType.setIdentifier("0250");
                        biologicalType.addName("gene");
                        return biologicalType;
                    case "ReferenceRNASequence":
                        biologicalType.setIdentifier("0320");
                        biologicalType.addName("ribonucleic acid");
                        return biologicalType;
                    case "ReferenceGeneProduct":
                    case "ReferenceIsoform":
                        biologicalType.setIdentifier("0326");
                        biologicalType.addName("protein");
                        return biologicalType;
                    case "ReferenceMolecule":
                        biologicalType.setIdentifier("0328");
                        biologicalType.addName("small molecule");
                        return biologicalType;
                }
            case "DefinedSet":
                biologicalType.setIdentifier("1307");
                biologicalType.addName("defined set");
                return biologicalType;
            case "CandidateSet":
                biologicalType.setIdentifier("1305");
                biologicalType.addName("candidate set");
                return biologicalType;
            case "Complex":
                biologicalType.setIdentifier("0314");
                biologicalType.addName("complex");
                return biologicalType;
            case "Polymer":
                biologicalType.setIdentifier("0383");
                biologicalType.addName("biopolymer");
                return biologicalType;
            case "SimpleEntity":
                biologicalType.setIdentifier("0328");
                biologicalType.addName("small molecule");
                return biologicalType;
            default:
                biologicalType.setIdentifier("MI:0329");
                biologicalType.addName("unknown participant");
                return biologicalType;
        }
    }

    private Term getReactomeAlternativeId(PhysicalEntity pe) {
        Term alternativeId = new Term();
        alternativeId.setDatabaseName("reactome");
        alternativeId.setIdentifier(pe.getStId());
        return alternativeId;
    }

    static List<Term> getCompartmentPE(PhysicalEntity pe) {
        List<Term> xrefCompartments = new ArrayList<>();
        for (Compartment cpt : pe.getCompartment()) {
            Term compartment = new Term();
            compartment.setIdentifier(cpt.getAccession());
            compartment.setDatabaseName(cpt.getDatabaseName().toLowerCase());
            compartment.addName(cpt.getName());
            xrefCompartments.add(compartment);
        }
        return xrefCompartments;
    }


    public void setEntity(PhysicalEntity pe) {
        this.setName(getNames(pe));

        //Set Compartment as Xrefs
        Term compartment = new Term();
        for (Compartment cpt : pe.getCompartment()) {
            compartment.setDatabaseName(cpt.getDatabaseName());
            compartment.setIdentifier(cpt.getAccession());
            compartment.addName(cpt.getName());
        }

        this.compartment = getCompartmentPE(pe);

        if (pe instanceof EntityWithAccessionedSequence) {
            //Set Identifier, Alternative ID
            this.setIdentifier(String.valueOf(((EntityWithAccessionedSequence) pe).getReferenceEntity().getIdentifier()));
            this.setDatabaseName(((EntityWithAccessionedSequence) pe).getReferenceEntity().getDatabaseName().toLowerCase());

            this.alternativeId.add(getReactomeAlternativeId(pe));

            //Set Organism with Taxonomy Identifier
            Term taxon = new Term();
            taxon.addName(((EntityWithAccessionedSequence) pe).getSpecies().getDisplayName());
            taxon.setIdentifier(((EntityWithAccessionedSequence) pe).getSpecies().getTaxId());
            this.addTaxon(taxon);


            //Set biological type
            Term type = getInteractorType(pe.getSchemaClass(), ((EntityWithAccessionedSequence) pe).getReferenceType());
            this.setBiologicalType(type);


            //Set biological modification
            if (((EntityWithAccessionedSequence) pe).getHasModifiedResidue().size() >= 1) {
                for (AbstractModifiedResidue modRes : ((EntityWithAccessionedSequence) pe).getHasModifiedResidue()) {

                    if (modRes instanceof ModifiedResidue) {
                        Term mod = new Term("MOD", ((ModifiedResidue) modRes).getPsiMod().getIdentifier(), ((ModifiedResidue) modRes).getPsiMod().getName().get(0));

                        Modification modification = new Modification(mod);
                        modification.setPosition(((ModifiedResidue) modRes).getCoordinate());
                        this.modifications.add(modification);
                    } else {
                        if (modRes instanceof GroupModifiedResidue) {

                            Term mod = new Term("MOD", ((GroupModifiedResidue) modRes).getPsiMod().getIdentifier(), ((GroupModifiedResidue) modRes).getPsiMod().getName().get(0));
                            Modification modification = new Modification(mod);
                            modification.setPosition(((GroupModifiedResidue) modRes).getCoordinate());
                        }
                    }
                }
            }
        } else { //Complex | CandidateSet | DefinedSet

            //Set Reactome id as Identifier
            this.setIdentifier(pe.getStId());
            this.setDatabaseName("reactome");

            //Set taxons
            this.setTaxon(getMultipleSpecies(pe));

            //Set biological type
            Term type = getInteractorType(pe.getSchemaClass(), null);
            this.setBiologicalType(type);

        }
    }

    private ArrayList<Term> getMultipleSpecies(PhysicalEntity pe) {
        ArrayList<Term> taxIds = new ArrayList<>();
        PhysicalEntityService ps = ReactomeGraphCore.getService(PhysicalEntityService.class);
        for (PhysicalEntity component : ps.getPhysicalEntitySubunitsNoStructures(pe)) {
            if (component instanceof EntityWithAccessionedSequence) {
                Term taxon = new Term();
                taxon.setDatabaseName("taxid");
                taxon.addName(((EntityWithAccessionedSequence) component).getSpecies().getName().get(0));
                taxon.setIdentifier(((EntityWithAccessionedSequence) component).getSpecies().getTaxId());
                if (!(taxIds.contains(taxon))) {
                    taxIds.add(taxon);
                }
            }
        }
        return taxIds;
    }
}


