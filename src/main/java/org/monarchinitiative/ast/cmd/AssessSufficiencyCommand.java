package org.monarchinitiative.ast.cmd;

import org.monarchinitiative.ast.io.PhenopacketImporter;
import org.monarchinitiative.ast.resnik.SimpleScoreCalculator;
import org.monarchinitiative.ast.sufficiency.SimpleSufficiencyTool;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "assess", aliases = {"A"},
        mixinStandardHelpOptions = true,
        description = "Assess Annotation Sufficiency")
public class AssessSufficiencyCommand implements Callable<Integer> {
    private static final Logger logger = LoggerFactory.getLogger(AssessSufficiencyCommand.class);
    @CommandLine.Option(names={"-p","--phenopacket"}, description ="A version 1 GA4GH phenopacket (json format) or directory" , required = true)
    private String phenopacketPath;
    /** Directory where various files are downloaded/created. */
    @CommandLine.Option(names={"-d","--data"}, description ="directory to download data (default: ${DEFAULT-VALUE})" )
    protected String datadir="data";
    @CommandLine.Option(names={"-o","--out"}, description ="outfile name (default: ${DEFAULT-VALUE})" )
    protected String outname="AST.tsv";

    @Override
    public Integer call() {
        final String hpoPath = String.format("%s%s%s","data", File.separator, "hp.obo");
        final String hpoaPath = String.format("%s%s%s","data", File.separator, "phenotype.hpoa");
        SimpleScoreCalculator resnikator = new SimpleScoreCalculator(hpoPath, hpoaPath);
        final Ontology hpo = resnikator.getHpo();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outname))) {
            File f = new File(phenopacketPath);
            if (f.isDirectory()) {
                for (File file : f.listFiles()) {
                    if (file.getAbsolutePath().contains(".json")){
                        PhenopacketImporter importer = PhenopacketImporter.fromJson(file.getAbsolutePath(), hpo);
                        SimpleSufficiencyTool sufficiency = new SimpleSufficiencyTool(importer.getHpoTerms(), importer.getNegatedHpoTerms(), resnikator, file.getAbsolutePath());
                        String result = sufficiency.analyze();
                        System.out.println(result);
                        bw.write(result + "\n");
                    }
                }
            } else {
                PhenopacketImporter importer = PhenopacketImporter.fromJson(f.getAbsolutePath(), hpo);
                SimpleSufficiencyTool sufficiency = new SimpleSufficiencyTool(importer.getHpoTerms(), importer.getNegatedHpoTerms(), resnikator, f.getAbsolutePath());
                String result = sufficiency.analyze();
                System.out.println(result);
                bw.write(result + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }




}
