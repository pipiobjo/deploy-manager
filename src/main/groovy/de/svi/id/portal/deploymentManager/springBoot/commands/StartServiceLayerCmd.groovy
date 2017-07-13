package de.svi.id.portal.deploymentManager.springBoot.commands

import java.nio.file.Path
import java.nio.file.Paths

import org.apache.commons.lang.SystemUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class StartServiceLayerCmd extends AbstractSpringBootCommand{

    Logger logger = LoggerFactory.getLogger(StartServiceLayerCmd.class)

    public void init(){
        logger.info("--init--")
    }

    public void execute(){
        def Path binFolder = Paths.get(this.springBootArgs.springBootHome.toFile().getAbsolutePath() + File.separatorChar + "bin")
        // if bin folder not exists, assume we are on an initial setup, we dont have to shutdown
        if(binFolder == null || !binFolder.toFile().exists()){
            return;
        }

        def String cmd = "serviceWrapper.sh"

        if(SystemUtils.IS_OS_WINDOWS){
            cmd = "serviceWrapper.bat"
        }

        cmd += " start"
        def String executeCmd = "${binFolder.toFile().getAbsolutePath()}" + File.separatorChar + cmd
        logger.info("Starting SpringBoot with cmd: " + executeCmd)

        def sout = new StringBuilder(), serr = new StringBuilder()
        def proc = executeCmd.execute()
        proc.consumeProcessOutput(sout, serr)
        proc.waitForOrKill((this.springBootArgs.secondsToWait4Container2Start)*1000)

        logger.info(sout.toString())

    }



}