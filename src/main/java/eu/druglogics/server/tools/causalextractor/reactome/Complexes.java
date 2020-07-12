package eu.druglogics.server.tools.causalextractor.reactome;

import org.reactome.server.graph.domain.model.PhysicalEntity;

import java.util.ArrayList;

/**
 * Singleton class of the list of complexes parsed.
 *
 * @author Vasundra Touré
 */
public class Complexes {

    private ArrayList<PhysicalEntity> complexes;
    private static Complexes instance;

    private Complexes(){
        complexes = new ArrayList<>();

    }
    public static Complexes getInstance(){
        if(instance == null){
            instance = new Complexes();
        }
        return instance;
    }

    public void addComplex(PhysicalEntity complex){
        this.complexes.add(complex);
    }


    public ArrayList<PhysicalEntity> getList(){
        return this.complexes;
    }


}
