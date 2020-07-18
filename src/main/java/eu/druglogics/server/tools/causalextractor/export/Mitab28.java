package eu.druglogics.server.tools.causalextractor.export;

import java.io.IOException;

/**
 * Writer for writing and getting the PSI-MITAB2.8 format
 *
 * @author Vasundra Touré
 */

public class Mitab28 {

    PSIWriter psiWriter;

    public Mitab28(String outputFile) {
        this.psiWriter = new PSIWriter(outputFile);
        try {
            this.psiWriter.initiateFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PSIWriter getPsiWriter() {
        return this.psiWriter;
    }

    public void setPsiWriter(PSIWriter psiWriter) {
        this.psiWriter = psiWriter;
    }
}

