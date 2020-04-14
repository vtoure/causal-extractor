package eu.druglogics.server.tools.causalextractor.export;

import psidev.psi.mi.tab.PsimiTabWriter;
import psidev.psi.mi.tab.model.BinaryInteraction;
import psidev.psi.mi.tab.model.builder.PsimiTabVersion;

import java.io.File;
import java.io.IOException;

public class PSIWriter {

    private String fileName;


    public PSIWriter(String fileName){
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void initiateFile() throws IOException {
        File outputFile = new File(this.fileName);

        if (outputFile.exists()) {
            outputFile.delete();
        }

        PsimiTabWriter psimiTabWriter = new PsimiTabWriter(PsimiTabVersion.v2_8);
        psimiTabWriter.writeMitabHeader(outputFile);
    }

    public void writeNewFile(BinaryInteraction bi) throws IOException {
        // Create MITAB2.8 file for transcription
        File outputFile = new File(this.fileName);

        if (outputFile.exists()) {
            outputFile.delete();
        }

        PsimiTabWriter psimiTabWriter = new PsimiTabWriter(PsimiTabVersion.v2_8);
        psimiTabWriter.writeMitabHeader(outputFile);

        psimiTabWriter.write(bi, outputFile);

    }

    public void appendInFile(BinaryInteraction bi) throws IOException {
        File outputFile = new File(this.fileName);

        PsimiTabWriter psimiTabWriter = new PsimiTabWriter(PsimiTabVersion.v2_8);
        psimiTabWriter.write(bi, outputFile);
    }
}
