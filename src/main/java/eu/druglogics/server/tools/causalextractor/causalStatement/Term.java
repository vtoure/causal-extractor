package eu.druglogics.server.tools.causalextractor.causalStatement;

public class Term {
    String name ;
    String identifier;

    public Term(String name){
        this.name = name;
    }

    public Term(String identifier, String name){
        this.identifier = identifier;
        this.name = name;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}
