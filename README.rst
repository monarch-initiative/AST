###########################
Annotation Sufficiency Tool
###########################

The Annotation Sufficiency Tool (AST) is designed to provide feedback as to whether a collection of
`Human Phenotype Ontology (HPO) <https://hpo.jax.org/app/>`_ terms used to annotate a patient are
sufficient with respect to quantity and specificity. The current version of AST uses a heuristic to
provide feedback on a one to five star scale. Future versions will implement a more sophisticated algorithm.

Building AST
############

To build AST from source, clone it from GitHub and use the maven package command. Check the result
by running the tool without arguments.

.. code-block:: bash

    git clone https://github.com/monarch-initiative/AST.git
    cd AST
    mvn package
    java -jar target/AnnotationSufficiencyTool.jar
    Usage: java -jar AnnotationSufficiencyTool.jar [-hV] [COMMAND]
    Annotation Sufficiency Tool
        -h, --help      Show this help message and exit.
        -V, --version   Print version information and exit.
    Commands:
        download, D  Download files for AnnotationSufficiencyTool



