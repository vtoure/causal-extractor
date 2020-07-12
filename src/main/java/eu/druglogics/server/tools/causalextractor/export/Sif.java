package eu.druglogics.server.tools.causalextractor.export;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Generation of a SIF file
 *
 * @author Vasundra Touré
 */


public class Sif {

    public Sif(String fileName) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.close();
    }


    public void appendInteraction(String fileName, String source, String effect, String target)
            throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
        writer.append(source + "\t" + effect + "\t" + target + "\n");

        writer.close();
    }
}
