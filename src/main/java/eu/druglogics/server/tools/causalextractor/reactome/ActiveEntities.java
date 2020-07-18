package eu.druglogics.server.tools.causalextractor.reactome;

import java.util.ArrayList;
/**
 * Singleton class of the list of active entities.
 *
 * @author Vasundra Touré
 */

public class ActiveEntities {

        private ArrayList<String> activeEntities;
        private static ActiveEntities instance;

        private ActiveEntities(){
            activeEntities = new ArrayList<String>(DataFactory.getActiveEntities());

        }
        public static ActiveEntities getInstance(){
            if(instance == null){
                instance = new ActiveEntities();
            }
            return instance;
        }

        public ArrayList<String> getList(){
            return this.activeEntities;
        }


}
