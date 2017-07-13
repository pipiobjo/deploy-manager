package de.svi.id.portal.deploymentManager
import static java.nio.file.StandardCopyOption.*;

import java.nio.file.Files
import java.nio.file.Paths

import org.apache.commons.io.FileUtils
import org.apache.http.auth.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import de.svi.id.portal.deploymentManager.argumentParser.ArgumentParser
import de.svi.id.portal.deploymentManager.argumentParser.GlobalArguments
import de.svi.id.portal.deploymentManager.argumentParser.LiferayArguments
import de.svi.id.portal.deploymentManager.argumentParser.ElasticSearchArguments
import de.svi.id.portal.deploymentManager.argumentParser.SpringBootArguments
import de.svi.id.portal.deploymentManager.liferay.LiferayDeploymentManager
import de.svi.id.portal.deploymentManager.springBoot.SpringBootDeploymentManager
import de.svi.id.portal.deploymentManager.elasticSearch.ElasticSearchDeploymentManager
import de.svi.id.portal.deploymentManager.utils.Downloader
import de.svi.id.portal.deploymentManager.utils.Unzipper
import groovyx.net.http.*

class DeploymentManager{
    Logger logger = LoggerFactory.getLogger(DeploymentManager.class);
    File releaseZip
    Unzipper unzip = new Unzipper()

    public startDeploymentManager(String[] args){
        logger.info("Starting the DeploymentManager")
        ArgumentParser ap = new ArgumentParser(args)
        def globArg = ap.parseGlobalArguments()
        def lifArg = ap.parseLiferayArguments()
        def sbArg = ap.parseSpringBootArguments()
        def esArg = ap.parseElasticSearchArguments()

        releaseZip = new File(globArg.tempDir.toAbsolutePath().toString() + File.separatorChar + "release.zip")

        if(!globArg.skipDownloads){
            globArg.artifactFolder = getReleaseFromRemote(globArg, releaseZip)
        }else{
            globArg.artifactFolder = getReleaseFromLocal(globArg, releaseZip)
        }
        globArg.unzippedArtifactFolder = Paths.get(globArg.tempDir.toAbsolutePath().toString() + File.separatorChar + "release" + File.separatorChar + "ID-Portal-Release")


        if("liferay".equalsIgnoreCase(globArg.service)){
            startLiferayDeployment(globArg, lifArg)
        }else if("springBoot".equalsIgnoreCase(globArg.service)){
            startSpringBootDeployment(globArg,sbArg)
        }else if("elasticSearch".equalsIgnoreCase(globArg.service)){    
        	logger.info("elasticSearch");
            startElasticSearchDeployment(globArg,esArg)
        }else{
            logger.error("Unknown service type selected to deploy. Service=" + globArg.service)
        }

        //delete tempDir
        logger.info("Deleting DeploymentManager temp folder: " + globArg.tempDir.toFile())	
        FileUtils.deleteQuietly(globArg.tempDir.toFile());
    }

    /**
     * Copys the release from a filePath to the deploymentManager tempDir and extract it
     * @param globArgs
     * @param myFile
     * @return
     */
    private getReleaseFromLocal(GlobalArguments globArgs, File myFile){

        Files.copy(globArgs.realeaseFilePath, myFile.toPath(), REPLACE_EXISTING);
        unzip.extractFolder(myFile)
    }

    /**
     * Downloads the release from a specified URL to the deploymentManager tempDir and extract it
     * @param globArgs
     * @param myFile
     * @return
     */
    private getReleaseFromRemote(GlobalArguments globArgs, File myFile){

        URL url = new URL(globArgs.releaseURL)

        Downloader downloader = new Downloader()
        logger.info("Download releaseBundle.zip to " + myFile)
        logger.info("Download releaseBundle.zip from " + globArgs.releaseURL)

        downloader.download(url, myFile);

        unzip.extractFolder(myFile)
    }


    public startSpringBootDeployment(GlobalArguments globArgs, SpringBootArguments sbArgs){
        logger.info("Starting SpringBoot deployment ... ")
        SpringBootDeploymentManager sbDM = new SpringBootDeploymentManager()
        logger.info("SpringBoot deployment finished")
    }


    public startLiferayDeployment(GlobalArguments globArgs, LiferayArguments lfrArgs){
        this.logger.info("Starting Liferay deployment ... ")
        LiferayDeploymentManager lfrDM = new LiferayDeploymentManager(globArgs, lfrArgs)
        this.logger.info("Liferay deployment finished")
    }
    
    public startElasticSearchDeployment(GlobalArguments globArgs, ElasticSearchArguments esArgs){
        logger.info("Starting ElasticSearch deployment ... ")
        ElasticSearchDeploymentManager esDM = new ElasticSearchDeploymentManager()
        logger.info("ElasticSearch deployment finished")
    }

    static void main(String... args) {
        DeploymentManager dm = new DeploymentManager()
        dm.startDeploymentManager(args)
    }
}