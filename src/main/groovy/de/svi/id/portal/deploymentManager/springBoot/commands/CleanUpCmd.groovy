package de.svi.id.portal.deploymentManager.springBoot.commands

import static java.nio.file.StandardCopyOption.*

import java.nio.file.Path
import java.nio.file.Paths

import org.apache.commons.io.FileUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
/**
 * Clean up the old artifacts
 * @author bjoern.pipiorke@sv-informatik.de
 *
 */
class CleanUpCmd extends AbstractSpringBootCommand{

    Logger logger = LoggerFactory.getLogger(CleanUpCmd.class)

    public void init(){
        logger.info("--init--")
    }

    public void execute(){
        def Path runFolder = Paths.get(this.springBootArgs.springBootHome.toFile().getAbsolutePath() + File.separatorChar + "run")

        if(runFolder==null && !runFolder.toFile().exists()){
            logger.error("Expecting serviceLayer folder=" + runFolder)
            System.exit(-1)
        }
        logger.info("Cleanup content of executable binaries in path:" + runFolder.toString())
        FileUtils.forceDelete(runFolder.toFile())
        runFolder.toFile().mkdirs();
    }
}