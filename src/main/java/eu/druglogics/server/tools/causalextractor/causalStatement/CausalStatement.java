package eu.druglogics.server.tools.causalextractor.causalStatement;

import java.util.ArrayList;

public class CausalStatement {

    Term source;
    Term effect;
    Term target;

    ArrayList<Term> reference;
    ArrayList<Term> evidence;
    ArrayList<Term> sourceExpSetup;
    ArrayList<Term> targetExpSetup;

    Term sourceActivity;
    Term targetActivity;
    Term sourceType;
    Term targetType;
    Term sourceTaxon;
    Term targetTaxon;
    Modification sourceModif;
    Modification targetModif;
    Term sourceCompartment;
    Term targetCompartment;

    Term effectCompartment;
    Term mechanism;
    Modification effectModif;
    Term taxon;
    Term tissue;
    Term cell;

    public CausalStatement(Term source, Term effect, Term target, ArrayList<Term> reference, ArrayList<Term> evidence) {
        this.source = source;
        this.effect = effect;
        this.target = target;
        this.reference = reference;
        this.evidence = evidence;
    }

    public CausalStatement(Term source, Term effect, Term target) {
        this.source = source;
        this.effect = effect;
        this.target = target;
    }

    public Term getSource() {
        return source;
    }

    public void setSource(Term source) {
        this.source = source;
    }

    public Term getEffect() {
        return effect;
    }

    public void setEffect(Term effect) {
        this.effect = effect;
    }

    public Term getTarget() {
        return target;
    }

    public void setTarget(Term target) {
        this.target = target;
    }

    public ArrayList<Term> getReference() {
        return reference;
    }

    public void setReference(ArrayList<Term> reference) {
        this.reference = reference;
    }

    public ArrayList<Term> getEvidence() {
        return evidence;
    }

    public void setEvidence(ArrayList<Term> evidence) {
        this.evidence = evidence;
    }

    public ArrayList<Term> getSourceExpSetup() {
        return sourceExpSetup;
    }

    public void setSourceExpSetup(ArrayList<Term> sourceExpSetup) {
        this.sourceExpSetup = sourceExpSetup;
    }

    public ArrayList<Term> getTargetExpSetup() {
        return targetExpSetup;
    }

    public void setTargetExpSetup(ArrayList<Term> targetExpSetup) {
        this.targetExpSetup = targetExpSetup;
    }

    public Term getSourceActivity() {
        return sourceActivity;
    }

    public void setSourceActivity(Term sourceActivity) {
        this.sourceActivity = sourceActivity;
    }

    public Term getTargetActivity() {
        return targetActivity;
    }

    public void setTargetActivity(Term targetActivity) {
        this.targetActivity = targetActivity;
    }

    public Term getSourceType() {
        return sourceType;
    }

    public void setSourceType(Term sourceType) {
        this.sourceType = sourceType;
    }

    public Term getTargetType() {
        return targetType;
    }

    public void setTargetType(Term targetType) {
        this.targetType = targetType;
    }

    public Term getSourceTaxon() {
        return sourceTaxon;
    }

    public void setSourceTaxon(Term sourceTaxon) {
        this.sourceTaxon = sourceTaxon;
    }

    public Term getTargetTaxon() {
        return targetTaxon;
    }

    public void setTargetTaxon(Term targetTaxon) {
        this.targetTaxon = targetTaxon;
    }

    public Modification getSourceModif() {
        return sourceModif;
    }

    public void setSourceModif(Modification sourceModif) {
        this.sourceModif = sourceModif;
    }

    public Modification getTargetModif() {
        return targetModif;
    }

    public void setTargetModif(Modification targetModif) {
        this.targetModif = targetModif;
    }

    public Term getSourceCompartment() {
        return sourceCompartment;
    }

    public void setSourceCompartment(Term sourceCompartment) {
        this.sourceCompartment = sourceCompartment;
    }

    public Term getTargetCompartment() {
        return targetCompartment;
    }

    public void setTargetCompartment(Term targetCompartment) {
        this.targetCompartment = targetCompartment;
    }

    public Term getEffectCompartment() {
        return effectCompartment;
    }

    public void setEffectCompartment(Term effectCompartment) {
        this.effectCompartment = effectCompartment;
    }

    public Term getMechanism() {
        return mechanism;
    }

    public void setMechanism(Term mechanism) {
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

    public Term getTissue() {
        return tissue;
    }

    public void setTissue(Term tissue) {
        this.tissue = tissue;
    }

    public Term getCell() {
        return cell;
    }

    public void setCell(Term cell) {
        this.cell = cell;
    }
}

