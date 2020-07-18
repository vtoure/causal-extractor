package eu.druglogics.server.tools.causalextractor.causalStatement;

import java.util.ArrayList;

public class Term {

    String databaseName;
    ArrayList<String> name;
    String identifier;


    public Term() {
        this.name = new ArrayList<String>();
    }

    public Term(String databaseName, String identifier, String name){
        this.databaseName = databaseName;
        this.identifier = identifier;
        this.name = new ArrayList<String>();
        this.name.add(name);
    }


    public ArrayList<String> getName() {
        return name;
    }

    public void setName(ArrayList<String> name) {
        this.name = name;
    }

    public void addName(String name) {
        this.name.add(name);
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }
}
