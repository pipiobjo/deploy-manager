package de.svi.id.portal.deploymentManager.springBoot.commands

import java.nio.file.Path
import java.nio.file.Paths

import org.apache.commons.lang.SystemUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import de.svi.id.portal.deploymentManager.utils.WildCardFileSearcher

class StopServiceLayerCmd extends AbstractSpringBootCommand{

    Logger logger = LoggerFactory.getLogger(StopServiceLayerCmd.class)

    public void init(){
    }

    public void execute(){
        def Path binFolder = Paths.get(this.springBootArgs.springBootHome.toFile().getAbsolutePath() + File.separatorChar + "bin")
        def Path runFolder = Paths.get(this.springBootArgs.springBootHome.toFile().getAbsolutePath() + File.separatorChar + "run")

        def boolean runFolderIsEmpty =isDirEmpty(runFolder)
        // if bin folder not exists, assume we are on an initial setup, we dont have to shutdown
        if(binFolder == null || !binFolder.toFile().exists() || runFolderIsEmpty){
            return;
        }
        def String cmd = "serviceWrapper.sh"

        if(SystemUtils.IS_OS_WINDOWS){
            cmd = "serviceWrapper.bat"
        }
        cmd += " stop"
        def String executeCmd = "${binFolder.toFile().getAbsolutePath()}" + File.separatorChar + cmd
        logger.info("Stopping SpringBoot with cmd: " + executeCmd)

        def sout = new StringBuilder(), serr = new StringBuilder()
        def proc = executeCmd.execute()
        proc.consumeProcessOutput(sout, serr)
        proc.waitForOrKill((this.springBootArgs.secondsToWait4Container2Shutdown)*1000)

        logger.info(sout.toString())

    }

    private boolean isDirEmpty(Path directory) throws IOException {
        WildCardFileSearcher wcfs = new WildCardFileSearcher()
        List<Path> result = wcfs.searchForFilesByWildCard(directory, "*.jar")

        if (result == null || result.empty) {
            return true;
        }else{
            return false;
        }


    }
}