package org.monarchinitiative.ast.sufficiency;

import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SimpleSufficiencyTool {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleSufficiencyTool.class);
    /**
     * List of HPO terms observed in the subject of the investigation.
     */
    private List<TermId> hpoIdList;
    /**
     * List of excluded HPO terms in the subject.
     */
    private List<TermId> negatedHpoIdList;
    public SimpleSufficiencyTool(List<TermId> hpoIdList, List<TermId> excludedHpoIdList) {
        this.hpoIdList = hpoIdList;
        this.negatedHpoIdList = excludedHpoIdList;
        LOGGER.trace("HPO Terms: {}", this.hpoIdList.size());
        LOGGER.trace("Excluded HPO Terms: {}", this.negatedHpoIdList.size());
    }
}
