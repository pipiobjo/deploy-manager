package de.svi.id.portal.deploymentManager.elasticSearch.commands

import java.nio.file.Path
import java.nio.file.Paths

import org.slf4j.Logger
import org.slf4j.LoggerFactory
/**
 * Initalize directory structure
 * @author emir.rencber@sv-informatik.de
 *
 */
class InitFolderStructureCmd extends AbstractElasticSearchCommand{

    Logger logger = LoggerFactory.getLogger(InitFolderStructureCmd.class)

    public void init(){
    }

    public void execute(){
        def Path runFolder = Paths.get(this.elasticSearchArgs.elasticSearchHome.toFile().getAbsolutePath() + File.separatorChar + "run")


        checkIfFolderExitsAndCreate(runFolder)
    }

    private boolean checkIfFolderExitsAndCreate(Path dir) throws IOException {

        if(!dir.toFile().exists()){
            logger.info("Creating directory " + dir.toFile().getAbsolutePath())
            dir.toFile().mkdirs();
        }
    }
}