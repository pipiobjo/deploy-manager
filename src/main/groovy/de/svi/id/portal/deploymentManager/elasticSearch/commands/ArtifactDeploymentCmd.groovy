package de.svi.id.portal.deploymentManager.elasticSearch.commands

import static java.nio.file.StandardCopyOption.*

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import de.svi.id.portal.deploymentManager.utils.CopyUtil
import de.svi.id.portal.deploymentManager.utils.Unzipper
import de.svi.id.portal.deploymentManager.utils.WildCardFileSearcher
/**
 * Copys the new ElasticSearch Artifact to the run folder
 * @author emir.rencber@sv-informatik.de
 *
 */
class ArtifactDeploymentCmd extends AbstractElasticSearchCommand{

    Logger logger = LoggerFactory.getLogger(ArtifactDeploymentCmd.class)

    public void init(){
        logger.info("--init-- elasticSearch ArtifactDeploymentCmd")
    }

    public void execute(){
        def Path runFolder = Paths.get(this.elasticSearchArgs.elasticSearchHome.toFile().getAbsolutePath() + File.separatorChar + "run")

        if(runFolder==null && !runFolder.toFile().exists()){
            logger.info("Folder does not exists creating it" + runFolder)
            runFolder.toFile().mkdirs()
        }
        CopyUtil copyUtil = new CopyUtil()
		def Path elasticSearchArtifactPath = Paths.get(this.getGlblArgs().unzippedArtifactFolder.toFile().getAbsolutePath() + File.separatorChar + "elasticSearch")
		
		// search elasticsearch zip file 
        WildCardFileSearcher wcFileSearcher = new WildCardFileSearcher()
        def List<Path> elasticSearchZipSearch = wcFileSearcher.searchForFilesByWildCard(elasticSearchArtifactPath, "elasticsearch*.zip")


        if(elasticSearchZipSearch.size() < 1){
            logger.error("Can not find elasticsearch*.zip in path: " + elasticSearchArtifactPath)
            System.exit(-1)
        }else if(elasticSearchZipSearch.size() > 1){
            logger.error("or found multiple elasticsearch*.zip in path: " + elasticSearchArtifactPath)
            System.exit(-1)

        }else{

            this.getElasticSearchArgs().artifactZipped = elasticSearchZipSearch[0]
        }

        // unzip elasticsearch*.zip
        logger.info("unzipping zip")
        Unzipper zipper = new Unzipper()
        this.getElasticSearchArgs().unzippedArtifact = zipper.extractFolder(this.getElasticSearchArgs().artifactZipped.toFile()).toPath()

        // get artifact path 
        def Path artifactBinFolder = Paths.get(this.getElasticSearchArgs().unzippedArtifact.toFile().getAbsolutePath() + File.separatorChar + "Portal-ElasticSearch" + File.separatorChar + "elasticsearch-bin")
        
        // copy files and folders to elastic search's run directory
		logger.info("copy artifact from " + artifactBinFolder +" to " + this.elasticSearchArgs.elasticSearchHome.toFile().getAbsolutePath() + File.separatorChar + "run")
        copyUtil.copyContentofFoldertoDirectory(artifactBinFolder, Paths.get(this.elasticSearchArgs.elasticSearchHome.toFile().getAbsolutePath() + File.separatorChar + "run"));
    }
}