# causal-extractor
Extraction of causal interaction from pathway databases

## Introduction
causal-extractor is a JAVA framework developed to extract causal molecular interactions from the Reactome database. Based on defined biological templates, biological processes depicted in Reactome are translated into causal statements. The causal statements extracted follow the [MI2CAST](https://github.com/MI2CAST/MI2CAST) guidelines. The output format is a PSI-MITAB 2.8.

## Documentation
For more information about the extraction process, please read the documentation available at [https://github.com/vtoure/causal-extractor/wiki](https://github.com/vtoure/causal-extractor/wiki).

## Installation

## Data
The causal interactions are available in the [results](https://github.com/vtoure/causal-extractor/tree/master/results) in the following formats: 
* SIF
* PSI-MITAB2.8

Formats planned to be supported:
* BEL 

## Notes
* 18/07/2020: Current export files contain causal interactions exported for human reactions. Patterns considered: transcription, translation and catalysis.

## Contact
vasundra.toure@ntnu.no
