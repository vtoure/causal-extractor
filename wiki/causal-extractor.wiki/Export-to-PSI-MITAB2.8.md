# Export to PSI-MITAB2.8 of a causal statement
The following table presents the type of information exported from Reactome into the different columns of [PSI-MITAB2.8](https://psicquic.github.io/MITAB28Format.html) when a causal statement is defined.

|    | PSI-MITAB2.8                                       | Reactome data |
|----|----------------------------------------------------|---------------|
| 1  | Unique identifier for interactor A                 |               |
| 2  | Unique identifier for interactor B                 |               |
| 3  | Alternative identifier for interactor A            |               |
| 4  | Alternative identifier for interactor B            |               |
| 5  | Aliases for A                                      |               |
| 6  | Aliases for B                                      |               |
| 7  | Interaction detection methods                      | psi-mi:"MI:0063"(interaction prediction) |
| 8  | First author                                       |  -            |
| 9  | Identifier of the publication                      | pubmed IDs listed in the reaction where causal statement is extracted from              |
| 10 | NCBI Taxonomy identifier for interactor A          | Taxon ID annotated on the source entity              |
| 11 | NCBI Taxonomy identifier for interactor B          | Taxon ID annotated on the target entity              |
| 12 | Interaction types                                  | psi-mi:"MI:2286"(functional association) for transcription and translation causal statements |
| 13 | Source databases                                   | psi-mi:"MI:0467"(reactome) |
| 14 | Interaction identifier(s)                          | Reactome reaction identifier |
| 15 | Confidence score                                   | -              |
| 16 | Complex expansion                                  | -              |
| 17 | Biological role A                                  | psi-mi:"MI:2274"(regulator) |
| 18 | Biological role B                                  | psi-mi:"MI:2275"(regulator target) |
| 19 | Experimental role A                                | psi-mi:"MI:0499"(unspecified role) |
| 20 | Experimental role B                                | psi-mi:"MI:0499"(unspecified role) |
| 21 | Interactor type A                                  | Defined based on the schemaClass and referenceType of the entity |
| 22 | Interactor type B                                  | Defined based on the schemaClass and referenceType of the entity |
| 23 | Xref for interactor A                              | -             |
| 24 | Xref for interactor B                              | -             |
| 25 | Xref for the interaction                           | -             |
| 26 | Annotations for interactor A                       | compartment   |
| 27 | Annotations for interactor B                       | compartment   |
| 28 | Annotations for the interaction                    | -             |
| 29 | NCBI Taxonomy identifier for the host organism     | -             |
| 30 | Parameters of the interaction                      | -             |
| 31 | Creation date                                      | Defined by the date the code has been run  |
| 32 | Update date                                        | -             |
| 33 | Checksum for interactor A                          | -             |
| 34 | Checksum for interactor B                          | -             |
| 35 | Checksum for interaction                           | -             |
| 36 | negative                                           | -             |
| 37 | Feature(s) for interactor A                        | _in progress_ |
| 38 | Feature(s) for interactor B                        | _in progress_ |
| 39 | Stoichiometry for interactor A                     | stoichiometry of source Reactome entity   |
| 40 | Stoichiometry for interactor B                     | stoichiometry of target Reactome entity              |
| 41 | Participant identification method for interactor A | -              
| 42 | Participant identification method for interactor B | -             |
| 43 | Biological effect of interactor A                  | _in progress_ |
| 44 | Biological effect of interactor B                  | _in progress_ |
| 45 | Causal regulatory mechanism                        |psi-mi:"MI:2247"(transcriptional regulation) for causal statement from transcription reaction <br> psi-mi:"MI:2248"(translation regulation) for causal statement from translation reaction |
| 46 | Causal Statement                                   | inferred from Reactome regulation, use of MI term branch 'causal statement'|