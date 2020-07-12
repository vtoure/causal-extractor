package eu.druglogics.server.tools.causalextractor.reactome;

import org.reactome.server.graph.domain.model.*;
import psidev.psi.mi.tab.model.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class AnnotationUtils {


    // Databases:
    public static final String PSI_MI = "psi-mi";
    public static final String REACTOME = "reactome";
    public static final String PUBMED = "pubmed";
    public static final String TAX_ID = "taxid";

    public static final List<CrossReference> SPOKE_EXPANSION = new ArrayList<>(
            Collections.singleton(new CrossReferenceImpl(PSI_MI, "MI:1060", "spoke expansion")));

    public static final List<CrossReference> REACTOME_DB = new ArrayList<>(
            Collections.singleton(new CrossReferenceImpl(PSI_MI, "MI:0467", "reactome")));

    public static final List<CrossReference> INTERACTION_PREDICTION = new ArrayList<>(
            Collections.singleton(new CrossReferenceImpl(PSI_MI, "MI:0063", "interaction prediction")));

    //Biological roles
    public static final List<CrossReference> TARGET = new ArrayList<>(
            Collections.singleton(new CrossReferenceImpl(PSI_MI, "MI:2275", "regulator target")));
    public static final List<CrossReference> REGULATOR = new ArrayList<>(
            Collections.singleton(new CrossReferenceImpl(PSI_MI, "MI:2274", "regulator")));
    public static final List<CrossReference> UNSPECIFIED_ROLE = new ArrayList<>(
            Collections.singleton(new CrossReferenceImpl(PSI_MI, "MI:0499", "unspecified role")));

    //Interactor Reactome types:
    public static final List<CrossReference> COMPLEX = new ArrayList<>(
            Collections.singleton(new CrossReferenceImpl(PSI_MI, "MI:0314", "complex")));
    public static final List<CrossReference> GENE = new ArrayList<>(
            Collections.singleton(new CrossReferenceImpl(PSI_MI, "MI:0250", "gene")));
    public static final List<CrossReference> RNA = new ArrayList<>(
            Collections.singleton(new CrossReferenceImpl(PSI_MI, "MI:0320", "ribonucleic acid")));
    public static final List<CrossReference> PROTEIN = new ArrayList<>(
            Collections.singleton(new CrossReferenceImpl(PSI_MI, "MI:0326", "protein")));
    public static final List<CrossReference> CHEMICAL = new ArrayList<>(
            Collections.singleton(new CrossReferenceImpl(PSI_MI, "MI:0328", "small molecule")));
    public static final List<CrossReference> BIOPOLYMER = new ArrayList<>(
            Collections.singleton(new CrossReferenceImpl(PSI_MI, "MI:0383", "biopolymer")));
    public static final List<CrossReference> DEFINED_SET = new ArrayList<>(
            Collections.singleton(new CrossReferenceImpl(PSI_MI, "MI:1307", "defined set")));
    public static final List<CrossReference> CANDIDATE_SET = new ArrayList<>(
            Collections.singleton(new CrossReferenceImpl(PSI_MI, "MI:1305", "candidate set")));
    public static final List<CrossReference> UNKNOWN_PARTICIPANT = new ArrayList<>(
            Collections.singleton(new CrossReferenceImpl(PSI_MI, "MI:0329", "unknown participant")));

    public static final List<CrossReference> FUNCTIONAL_ASSOCIATION = new ArrayList<>(
            Collections.singleton(new CrossReferenceImpl(PSI_MI, "MI:2286", "functional association")));

    public static final List<CrossReference> PHYSICAL_ASSOCIATION = new ArrayList<>(
            Collections.singleton(new CrossReferenceImpl(PSI_MI, "MI:0915", "physical association")));

    //Causal Interactions PSI-MI
    public static final List<CrossReference> DOWN_REGULATES = new ArrayList<>(
            Collections.singleton(new CrossReferenceImpl(PSI_MI, "MI:2240", "down-regulates")));

    public static final List<CrossReference> DOWN_REGULATES_ACTIVITY = new ArrayList<>(
            Collections.singleton(new CrossReferenceImpl(PSI_MI, "MI:2241", "down-regulates activity")));

    public static final List<CrossReference> DOWN_REGULATES_QUANTITY = new ArrayList<>(
            Collections.singleton(new CrossReferenceImpl(PSI_MI, "MI:2242", "down-regulates quantity")));

    public static final List<CrossReference> DOWN_REGULATES_REPRESSION = new ArrayList<>(
            Collections.singleton(new CrossReferenceImpl(PSI_MI, "MI:2243", "down-regulates quantity by repression")));

    public static final List<CrossReference> UP_REGULATES_EXPRESSION = new ArrayList<>(
            Collections.singleton(new CrossReferenceImpl(PSI_MI, "MI:2238", "up-regulates quantity by expression")));

    public static final List<CrossReference> UP_REGULATES = new ArrayList<>(
            Collections.singleton(new CrossReferenceImpl(PSI_MI, "MI:2235", "up-regulates")));

    public static final List<CrossReference> UP_REGULATES_ACTIVITY = new ArrayList<>(
            Collections.singleton(new CrossReferenceImpl(PSI_MI, "MI:2236", "up-regulates activity")));

    public static final List<CrossReference> UP_REGULATES_QUANTITY = new ArrayList<>(
            Collections.singleton(new CrossReferenceImpl(PSI_MI, "MI:2237", "up-regulates quantity")));

    public static final List<CrossReference> TRANSCRIPTIONAL_REG = new ArrayList<>(
            Collections.singleton(new CrossReferenceImpl(PSI_MI, "MI:2247", "transcriptional regulation")));

    public static final List<CrossReference> TRANSLATION_REG = new ArrayList<>(
            Collections.singleton(new CrossReferenceImpl(PSI_MI, "MI:2248", "translation regulation")));

    public static final List<CrossReference> UNKNOWN_ROLE = new ArrayList<>(
            Collections.singletonList(new CrossReferenceImpl(PSI_MI, "MI:0499", "unspecified role")));

    /**
     * A list of default parameters to be set about an interaction
     *
     * @param bi  BinaryInteraction - Causal interaction
     * @param rle ReactionLikeEvent - Reaction from which the causal interaction is extracted
     */
    static void setDefaultInteraction(BinaryInteraction bi, ReactionLikeEvent rle, Collection<Publication> publications) {
        //Interaction detection method(s)
        bi.setDetectionMethods(AnnotationUtils.INTERACTION_PREDICTION);

        //Publication identifier(s)
        List<CrossReference> publication = new ArrayList<>();
        if (publications != null)
            for (Publication pb : publications) {
                CrossReferenceImpl ref = new CrossReferenceImpl();
                if (pb instanceof LiteratureReference) {
                    ref.setIdentifier(((LiteratureReference) pb).getPubMedIdentifier().toString());
                    ref.setDatabase(AnnotationUtils.PUBMED);
                    publication.add(ref);
                }
            }
        if (!(publication.isEmpty()))
            bi.setPublications(publication);

        //Source database(s): Reactome
        bi.setSourceDatabases(AnnotationUtils.REACTOME_DB);

        //Interaction identifier(s)
        bi.setInteractionAcs(new ArrayList<>(Collections.singletonList(new CrossReferenceImpl(AnnotationUtils.REACTOME, rle.getStId()))));

        //Interaction Xref(s): compartments of the RLE
        bi.setXrefs(getCompartmentRLE(rle));

        //TODO: Interaction annotation(s) - add 'predicted' to the interaction

        //Creation date
        bi.setCreationDate(getDate());
    }

    /**
     * Get the date of creation of the binary interaction
     *
     * @return date of creation
     */
    public static List<Date> getDate() {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        List<Date> l_date = new ArrayList<>();
        try {
            l_date.add(dateFormat.parse(dateFormat.format(date)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return l_date;
    }

    /**
     * Get the list of compartments associated to a PhysicalEntity
     *
     * @param pe PhysicalEntity
     * @return CrossReference list of compartments
     */
    public static List<CrossReference> getCompartmentPE(PhysicalEntity pe) {
        List<CrossReference> xrefCompartments = new ArrayList<>();
        for (Compartment cpt : pe.getCompartment()) {
            CrossReference xref = new CrossReferenceImpl();
            xref.setIdentifier(cpt.getDatabaseName() + ":" + cpt.getAccession());
            xref.setDatabase(cpt.getDatabaseName().toLowerCase());
            xref.setText(cpt.getName());
            xrefCompartments.add(xref);
        }
        return xrefCompartments;
    }

    /**
     * Get the list of compartments associated to a ReactionLikeEvent
     *
     * @param rle ReactionLikeEvent
     * @return CrossReference list of compartments
     */
    private static List<CrossReference> getCompartmentRLE(ReactionLikeEvent rle) {
        List<CrossReference> xrefCompartments = new ArrayList<>();
        for (Compartment cpt : rle.getCompartment()) {
            CrossReference xref = new CrossReferenceImpl();
            xref.setDatabase(cpt.getDatabaseName());
            xref.setIdentifier(cpt.getDatabaseName() + ":" + cpt.getAccession());
            xref.setText(cpt.getName());
            xrefCompartments.add(xref);
        }
        return xrefCompartments;
    }
}
