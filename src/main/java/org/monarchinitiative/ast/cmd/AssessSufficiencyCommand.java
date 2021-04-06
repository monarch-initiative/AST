package org.monarchinitiative.ast.cmd;

import org.monarchinitiative.ast.exception.AstRuntimeException;
import org.monarchinitiative.ast.io.PhenopacketImporter;
import org.monarchinitiative.ast.resnik.Resnikator;
import org.monarchinitiative.ast.sufficiency.SimpleSufficiencyTool;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.io.File;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "assess", aliases = {"A"},
        mixinStandardHelpOptions = true,
        description = "Assess Annotation Sufficiency")
public class AssessSufficiencyCommand implements Callable<Integer> {
    private static final Logger logger = LoggerFactory.getLogger(AssessSufficiencyCommand.class);
    @CommandLine.Option(names={"-p","--phenopacket"}, description ="A version 1 GA4GH phenopacket (json format)" , required = true)
    private String phenopacketPath;
    /** Directory where various files are downloaded/created. */
    @CommandLine.Option(names={"-d","--data"}, description ="directory to download data (default: ${DEFAULT-VALUE})" )
    protected String datadir="data";

    @Override
    public Integer call() {
        final String hpoPath = String.format("%s%s%s","data", File.separator, "hp.obo");
        final String hpoaPath = String.format("%s%s%s","data", File.separator, "phenotype.hpoa");
        Resnikator resnikator = new Resnikator(hpoPath, hpoaPath);
        final Ontology hpo = resnikator.getHpo();
        PhenopacketImporter importer = PhenopacketImporter.fromJson(phenopacketPath, hpo);
        SimpleSufficiencyTool sufficiency = new SimpleSufficiencyTool(importer.getHpoTerms(), importer.getNegatedHpoTerms());


        return 0;
    }
}
