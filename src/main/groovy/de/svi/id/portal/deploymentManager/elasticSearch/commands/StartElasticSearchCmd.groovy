package de.svi.id.portal.deploymentManager.elasticSearch.commands

import java.nio.file.Path
import java.nio.file.Paths

import org.apache.commons.lang.SystemUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Calls start scripts for ElasticSearch
 * @author emir.rencber@sv-informatik.de
 *
 */
class StartElasticSearchCmd extends AbstractElasticSearchCommand{

    Logger logger = LoggerFactory.getLogger(StartElasticSearchCmd.class)

    public void init(){
        logger.info("--init--")
    }

    public void execute(){
        def Path binFolder = Paths.get(this.elasticSearchArgs.elasticSearchHome.toFile().getAbsolutePath() + File.separatorChar + "run" + File.separatorChar + "bin")
        // if bin folder not exists, assume we are on an initial setup, we dont have to shutdown
        if(binFolder == null || !binFolder.toFile().exists()){
            return;
        }

        def String cmd = "elasticsearchService.sh"

        if(SystemUtils.IS_OS_WINDOWS){
        	cmd = "elasticsearch.bat"
        } else {
        	cmd += " start"
        }


        def String executeCmd = "${binFolder.toFile().getAbsolutePath()}" + File.separatorChar + cmd
        logger.info("Starting ElasticSearch with cmd: " + executeCmd)

        def sout = new StringBuilder(), serr = new StringBuilder()
        def proc = executeCmd.execute()
        proc.consumeProcessOutput(sout, serr)
        proc.waitForOrKill((this.elasticSearchArgs.secondsToWait4Container2Start)*1000)

        logger.info(sout.toString())

    }



}