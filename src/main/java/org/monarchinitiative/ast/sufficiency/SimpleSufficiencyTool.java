package org.monarchinitiative.ast.sufficiency;

import org.monarchinitiative.ast.resnik.SimpleScoreCalculator;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public class SimpleSufficiencyTool {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleSufficiencyTool.class);
    /**
     * List of HPO terms observed in the subject of the investigation.
     */
    private final List<TermId> hpoIdList;
    /**
     * List of excluded HPO terms in the subject.
     */
    private final List<TermId> negatedHpoIdList;

    private final String phenopacket;
    private final SimpleScoreCalculator resnikator;

    public SimpleSufficiencyTool(List<TermId> hpoIdList, List<TermId> excludedHpoIdList, SimpleScoreCalculator resnikator, String phenopacketPath) {
        this.hpoIdList = hpoIdList;
        this.negatedHpoIdList = excludedHpoIdList;
        LOGGER.trace("HPO Terms: {}", this.hpoIdList.size());
        LOGGER.trace("Excluded HPO Terms: {}", this.negatedHpoIdList.size());
        this.resnikator = resnikator;
        File f = new File(phenopacketPath);
        this.phenopacket = f.getName();
    }

    public String analyze() {
        double score = resnikator.calculateSimpleScore(this.hpoIdList, this.negatedHpoIdList);
        return String.format("%s\t%.1f%%",this.phenopacket, 100*score);
    }
}
