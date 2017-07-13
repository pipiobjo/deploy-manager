package de.svi.id.portal.deploymentManager.springBoot.commands

import static java.nio.file.StandardCopyOption.*

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import org.slf4j.Logger
import org.slf4j.LoggerFactory
/**
 * Copys the new serviceLayer jar to the run folder
 * @author bjoern.pipiorke@sv-informatik.de
 *
 */
class ArtifactDeploymentCmd extends AbstractSpringBootCommand{

    Logger logger = LoggerFactory.getLogger(ArtifactDeploymentCmd.class)

    public void init(){
        logger.info("--init--")
    }

    public void execute(){
        def Path runFolder = Paths.get(this.springBootArgs.springBootHome.toFile().getAbsolutePath() + File.separatorChar + "run")

        if(runFolder==null && !runFolder.toFile().exists()){
            logger.info("Folder does not exists creating it" + runFolder)
            runFolder.toFile().mkdirs()
        }
        def String fileName = this.springBootArgs.jarZipped.getFileName()
        def Path targetFile = Paths.get(runFolder.toFile().getAbsolutePath() + File.separatorChar + fileName)
        logger.info("copy jar from " + this.springBootArgs.jarZipped +" to " + targetFile.toFile().getAbsolutePath())
        Files.copy(this.springBootArgs.jarZipped, targetFile, REPLACE_EXISTING);
    }
}