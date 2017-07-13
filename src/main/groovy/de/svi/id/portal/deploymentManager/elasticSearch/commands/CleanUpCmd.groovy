package de.svi.id.portal.deploymentManager.elasticSearch.commands

import static java.nio.file.StandardCopyOption.*

import java.io.File;
import java.nio.file.Path
import java.nio.file.Paths

import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.DirectoryFileFilter; 
import org.apache.commons.io.filefilter.TrueFileFilter; 
import org.slf4j.Logger
import org.slf4j.LoggerFactory
/**
 * Clean up the old artifacts
 * @author emir.rencber@sv-informatik.de
 *
 */
class CleanUpCmd extends AbstractElasticSearchCommand{

    Logger logger = LoggerFactory.getLogger(CleanUpCmd.class)

    public void init(){
        logger.info("--init--")
    }

    public void execute(){
        def Path runFolder = Paths.get(this.elasticSearchArgs.elasticSearchHome.toFile().getAbsolutePath() + File.separatorChar + "run")

        if(runFolder==null && !runFolder.toFile().exists()){
            logger.error("Expecting elasticSearch folder=" + runFolder)
            System.exit(-1)
        }
        logger.info("Cleanup files and folders except data & logs in path:" + runFolder.toString())
        
        // remove files and folders except data & logs
		for(File file: runFolder.toFile().listFiles())  {
		    if (!"logs".equalsIgnoreCase(file.getName()) && !"data".equalsIgnoreCase(file.getName()) && !"run".equalsIgnoreCase(file.getName())) {
		    	FileUtils.forceDelete(file);
		    }
		}
        
        runFolder.toFile().mkdirs();
    }
}