package de.svi.id.portal.deploymentManager.springBoot.commands

import java.nio.file.Path
import java.nio.file.Paths

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class InitFolderStructureCmd extends AbstractSpringBootCommand{

    Logger logger = LoggerFactory.getLogger(InitFolderStructureCmd.class)

    public void init(){
    }

    public void execute(){
        def Path binFolder = Paths.get(this.springBootArgs.springBootHome.toFile().getAbsolutePath() + File.separatorChar + "bin")
        def Path runFolder = Paths.get(this.springBootArgs.springBootHome.toFile().getAbsolutePath() + File.separatorChar + "run")
        def Path configFolder = Paths.get(this.springBootArgs.springBootHome.toFile().getAbsolutePath() + File.separatorChar + "config")
        def Path logFolder = Paths.get(this.springBootArgs.springBootHome.toFile().getAbsolutePath() + File.separatorChar + "log")

        checkIfFolderExitsAndCreate(binFolder)
        checkIfFolderExitsAndCreate(runFolder)
        checkIfFolderExitsAndCreate(configFolder)
        checkIfFolderExitsAndCreate(logFolder)
    }

    private boolean checkIfFolderExitsAndCreate(Path dir) throws IOException {

        if(!dir.toFile().exists()){
            logger.info("Creating directory " + dir.toFile().getAbsolutePath())
            dir.toFile().mkdirs();
        }
    }
}