package eu.druglogics.server.tools.causalextractor.causalStatement;

public class Modification {

    Term modif;
    Term residue;
    Integer position;


    public Modification(Term modif){
        this.modif = modif;
    }

    public Modification(Term modif, Term residue){
        this.modif = modif;
        this.residue = residue;
    }

    public Modification(Term modif, Term residue, Integer position){
        this.modif = modif;
        this.residue = residue;
        this.position = position;
    }

    public Term getModif() {
        return modif;
    }

    public void setModif(Term modif) {
        this.modif = modif;
    }

    public Term getResidue() {
        return residue;
    }

    public void setResidue(Term residue) {
        this.residue = residue;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }
}

