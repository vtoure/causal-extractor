package eu.druglogics.server.tools.causalextractor.export;

import java.io.IOException;

/**
 * Export to PSI-MITAB2.8
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

