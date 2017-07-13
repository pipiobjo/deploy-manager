package de.svi.id.portal.deploymentManager.argumentParser

import java.nio.file.Path
import java.nio.file.Paths

/**
 * 
 * @author emir.rencber@sv-informatik.de
 *
 */
@Singleton
class ElasticSearchArguments{

    def Path elasticSearchHome, artifactZipped, unzippedArtifact, envConfigFolder, stageConfigFolder, commonConfigFolder

    def int secondsToWait4Container2Shutdown = 5
    def int secondsToWait4Container2Start = 15
}