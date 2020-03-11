package eu.druglogics.server.tools.causalextractor.mitab;


import psidev.psi.mi.tab.model.*;



public class InteractionBinary {
    Interactor a;
    Interactor b;
    InteractionBinary interaction;

    public InteractionBinary(Interactor a, Interactor b, InteractionBinary interaction){
        this.a = a;
        this.b = b;
        this.interaction = interaction;
    }





}
