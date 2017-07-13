package de.svi.id.portal.deploymentManager.argumentParser

import java.nio.file.Path
/**
 * 
 * @author bjoern.pipiorke@sv-informatik.de
 *
 */
@Singleton
class SpringBootArguments{

    def Path springBootHome, jarZipped, unzippedJar, envConfigFolder, commonConfigFolder, stageConfigFolder
    def int secondsToWait4Container2Shutdown = 5
    def int secondsToWait4Container2Start = 15
}