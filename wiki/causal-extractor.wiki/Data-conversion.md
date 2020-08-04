# Data conversion
We have defined recurring patterns of biological processes found in Reactome and translated them into causal interactions. 

## Transcription reaction pattern
Transcription is commonly represented in Reactome as the event where a transcription factor (TF), which can be a single protein or a complex of biological entities, binds to the gene TG and forms a complex. This complex can stimulate or inhibit the transcription of the gene into a nucleic acid or a protein.

In the following example, the TF is represented as a complex.


The causal interaction is assessed between the complex formed by the TF and the target gene (TG), and the gene product which can be a nucleic acid or a protein.

![Transcription](https://github.com/vtoure/causal-extractor/blob/development/wiki/images/transcription.svg)  
**Example of a positive regulation of the transcription of a gene product.** The TF-TG complex up-regulates the gene product. Depending on the active state of the gene product, the effect is either an up-regulation of the gene product's activity (1) or an up-regulation of the gene product's quantity by expression (2). The biological mechanism is defined as a "transcription regulation". An entity is defined as being active when it has been found, at least once, in the whole Reactome pathway database acting as a catalyst or regulator of a reaction.

> Note: The representation of a reaction where the gene is transcribed into a protein is a shortcut that does not explicitly represent the 'translation' event. 


## Translation reaction pattern
The translation reaction is commonly represented in Reactome as the event where a specific regulator (REG) binds to the RNA to form a complex. This complex can stimulate or inhibit the translation of the RNA into a protein.

![Translation](https://github.com/vtoure/causal-extractor/blob/development/wiki/images/translation.svg)  
**Example of a positive regulation of the translation of a protein.** The REG-RNA complex up-regulates the protein. Depending on the active state of the protein, the effect is either an up-regulation of the protein's activity (1) or an up-regulation of the protein's quantity by expression (2). The biological mechanism is defined as a "translation regulation".

## Catalysis reaction pattern


![Catalysis](https://github.com/vtoure/causal-extractor/blob/development/wiki/images/catalysis.svg)  
**Example of a positive regulation of the catalysis of a protein
