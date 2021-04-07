package org.monarchinitiative.ast.resnik;

import org.monarchinitiative.ast.cmd.AssessSufficiencyCommand;
import org.monarchinitiative.ast.exception.AstRuntimeException;
import org.monarchinitiative.phenol.annotations.assoc.HpoAssociationParser;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoDisease;
import org.monarchinitiative.phenol.annotations.obo.hpo.HpoDiseaseAnnotationParser;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.data.TermIds;
import org.monarchinitiative.phenol.ontology.similarity.HpoResnikSimilarity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class Resnikator {
    private static final Logger LOGGER = LoggerFactory.getLogger(AssessSufficiencyCommand.class);
    private final Ontology hpo;
    private final Map<TermId, HpoDisease> diseaseMap;
    private HpoResnikSimilarity resnikSimilarity;
    private final Map<TermId, Double> termToIc;
    private final Map<TermId, Collection<TermId>> diseaseIdToTermIds;


    public Resnikator(String hpoPath, String hpoaPath) {
        Instant t1 = Instant.now();
        File hpoFile = new File(hpoPath);
        if (! hpoFile.isFile()) {
            throw new AstRuntimeException("Could not find hp.obo file at \"" + hpoFile.getAbsolutePath() +"\". Did you run the download command?");
        }
        this.hpo = OntologyLoader.loadOntology(hpoFile);
        Instant t2 = Instant.now();
        LOGGER.trace(String.format("Loaded hp.obo in %.3f seconds.\n", Duration.between(t1,t2).toMillis()/1000d));
        t1 = Instant.now();
        List<String> databases = List.of("OMIM"); // restrict ourselves to OMIM entries
        this.diseaseMap = HpoDiseaseAnnotationParser.loadDiseaseMap(hpoaPath, hpo,databases);
        t2 = Instant.now();
        LOGGER.trace(String.format("Loaded phenotype.hpoa in %.3f seconds.\n",Duration.between(t1,t2).toMillis()/1000d));
        // Compute list of annotations and mapping from OMIM ID to term IDs.
        t1 = Instant.now();
        this.diseaseIdToTermIds = new HashMap<>();
        final Map<TermId, Collection<TermId>> termIdToDiseaseIds = new HashMap<>();
        for (TermId diseaseId : diseaseMap.keySet()) {
            HpoDisease disease = diseaseMap.get(diseaseId);
            List<TermId> hpoTerms = disease.getPhenotypicAbnormalityTermIdList();
            diseaseIdToTermIds.putIfAbsent(diseaseId, new HashSet<>());
            // add term anscestors
            final Set<TermId> inclAncestorTermIds = TermIds.augmentWithAncestors(hpo, new HashSet(hpoTerms), true);

            for (TermId tid : inclAncestorTermIds) {
                termIdToDiseaseIds.putIfAbsent(tid, new HashSet<>());
                termIdToDiseaseIds.get(tid).add(diseaseId);
                diseaseIdToTermIds.get(diseaseId).add(tid);
            }
        }
        t2 = Instant.now();
        LOGGER.trace(String.format("Calculated gene-disease links in %.3f seconds.\n", Duration.between(t1,t2).toMillis()/1000d));
        t1 = Instant.now();
        TermId ROOT_HPO = TermId.of("HP:0000118");//Phenotypic abnormality
        int totalPopulationHpoTerms = termIdToDiseaseIds.get(ROOT_HPO).size();
        t1 = Instant.now();
        termToIc = new HashMap<>();
        for (TermId tid : termIdToDiseaseIds.keySet()) {
            int annotatedCount = termIdToDiseaseIds.get(tid).size();
            double ic = -1*Math.log((double)annotatedCount/totalPopulationHpoTerms);
            termToIc.put(tid, ic);
        }
        t2 = Instant.now();
        System.out.printf("[INFO] Calculated information content in %.3f seconds.\n",Duration.between(t1,t2).toMillis()/1000d);
        t1 = Instant.now();
        this.resnikSimilarity = new HpoResnikSimilarity(this.hpo, this.termToIc);
        t2 = Instant.now();
        System.out.printf("[INFO] Calculated pairwise Resnik similarity in %.3f seconds.\n",Duration.between(t1,t2).toMillis()/1000d);
    }


    public Ontology getHpo() {
        return hpo;
    }

    public HpoResnikSimilarity getResnikSimilarity() {
        return resnikSimilarity;
    }
}
