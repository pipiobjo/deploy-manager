package de.svi.id.portal.deploymentManager.elasticSearch.commands

import java.nio.file.Path
import java.nio.file.Paths

import org.apache.commons.lang.SystemUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import de.svi.id.portal.deploymentManager.utils.WildCardFileSearcher
/**
 * Stops running ElasticSearch instances
 * @author emir.rencber@sv-informatik.de
 *
 */
class StopElasticSearchCmd extends AbstractElasticSearchCommand{

    Logger logger = LoggerFactory.getLogger(StopElasticSearchCmd.class)

    public void init(){
    }

    public void execute(){
        def Path binFolder = Paths.get(this.elasticSearchArgs.elasticSearchHome.toFile().getAbsolutePath() + File.separatorChar + "run" + File.separatorChar + "bin")
        def Path runFolder = Paths.get(this.elasticSearchArgs.elasticSearchHome.toFile().getAbsolutePath() + File.separatorChar + "run")

        def boolean runFolderIsEmpty =isDirEmpty(runFolder)
        // if run folder does not exists, assume we are on an initial setup, we dont have to shutdown
        if(binFolder == null || !binFolder.toFile().exists() || runFolderIsEmpty){
            return;
        }
        def String cmd = "elasticsearchService.sh"

        if(SystemUtils.IS_OS_WINDOWS){
            cmd = "elasticSearch.bat"
        }
        cmd += " stop"
        def String executeCmd = "${binFolder.toFile().getAbsolutePath()}" + File.separatorChar + cmd
        logger.info("Stopping ElasticSearch with cmd: " + executeCmd)

        def sout = new StringBuilder(), serr = new StringBuilder()
        def proc = executeCmd.execute()
        proc.consumeProcessOutput(sout, serr)
        proc.waitForOrKill((this.elasticSearchArgs.secondsToWait4Container2Shutdown)*1000)

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