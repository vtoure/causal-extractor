package eu.druglogics.server.tools.causalextractor.reactome.model;

import psidev.psi.mi.tab.model.Alias;
import psidev.psi.mi.tab.model.CrossReference;
import psidev.psi.mi.tab.model.Feature;

import java.util.Collections;
import java.util.List;

public class InteractorPSI {

    private String schemaClass;
    private String referenceType;

    private List<CrossReference> identifiers = Collections.emptyList();

    private List<Alias> aliases = Collections.emptyList();
    private List<CrossReference> species = Collections.emptyList();
    private List<Feature> features = Collections.emptyList();
    private List<CrossReference> crossReferences = Collections.emptyList();

}
