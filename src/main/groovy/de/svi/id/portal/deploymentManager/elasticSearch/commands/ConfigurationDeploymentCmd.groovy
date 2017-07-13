package de.svi.id.portal.deploymentManager.elasticSearch.commands

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.PosixFilePermission

import org.apache.commons.lang.SystemUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import de.svi.id.portal.deploymentManager.utils.CopyUtil
import de.svi.id.portal.deploymentManager.utils.Unzipper
import de.svi.id.portal.deploymentManager.utils.WildCardFileSearcher
/**
 * Base directory and configuration setup
 * <ul>
 * <li>Extracting the elasticSearch zip and set the paths to the ElasticSearchArgs, therefor we expect it to be the first task</li>
 * <li>Copy common config</li>
 * <li>Copy stage config</li>
 * <li>Copy fallback config from given local path --fallbackConfigPath</li>
 * </ul>
 * @author emir.rencber@sv-informatik.de
 *
 */
class ConfigurationDeploymentCmd extends AbstractElasticSearchCommand{

    Logger logger = LoggerFactory.getLogger(ConfigurationDeploymentCmd.class)
    def skipStageConfig = false;
    def skipCommonConfig = false;
    /**
     * Does the basic parameter extraction and checks if folders exists
     */
    public void init(){
        def Path elasticSearchArtifactPath = Paths.get(this.getGlblArgs().unzippedArtifactFolder.toFile().getAbsolutePath() + File.separatorChar + "elasticSearch")

        //search4zip
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

        //unzip elasticsearch*.zip
        logger.info("unzipping zip")
        Unzipper zipper = new Unzipper()
        this.getElasticSearchArgs().unzippedArtifact = zipper.extractFolder(this.getElasticSearchArgs().artifactZipped.toFile()).toPath()

        //envConfig path in zip BOOT-INF\classes\envConfig\
        this.getElasticSearchArgs().envConfigFolder = Paths.get(this.getElasticSearchArgs().unzippedArtifact.toFile().getAbsolutePath() + File.separatorChar + "Portal-ElasticSearch" + File.separatorChar + "elasticsearch-envConfig")

        //check if there is common folder        
        def String commonConfigFolderPath = this.getElasticSearchArgs().envConfigFolder.toFile().getAbsolutePath() + File.separatorChar + "common"
        def Path commonConfigFolder = Paths.get(commonConfigFolderPath)
        if(commonConfigFolder!=null && commonConfigFolder.toFile().exists()){
            this.getElasticSearchArgs().commonConfigFolder = commonConfigFolder
        }else{
            //logger.info("Expecting common configuration folder in ElasticSearch Config")
            //System.exit(-1)
            skipCommonConfig = true

        }
        
        //check if there is a stageId folder
        def String stageConfigFolderPath = this.getElasticSearchArgs().envConfigFolder.toFile().getAbsolutePath() + File.separatorChar + this.getGlblArgs().stageIdentifier
        def Path stageConfigFolder = Paths.get(stageConfigFolderPath)

        if(stageConfigFolder!=null && stageConfigFolder.toFile().exists()){
            this.getElasticSearchArgs().stageConfigFolder = stageConfigFolder
        }else{
            logger.info("No stage configuration folder=" + stageConfigFolderPath + " ... skipping")
            skipStageConfig = true
        }


    }

    /**
     * Executes the copy process
     */
    public void execute(){

        CopyUtil copyUtil = new CopyUtil()
        //copy common config    
		if(!skipCommonConfig){
			logger.info("copy common config from " + this.getElasticSearchArgs().commonConfigFolder)
			copyUtil.copyContentofFoldertoDirectory(this.getElasticSearchArgs().commonConfigFolder, Paths.get(this.getElasticSearchArgs().elasticSearchHome.toFile().getAbsolutePath() + File.separatorChar + "run"))
        }
        
        //copy stage config
        if(!skipStageConfig){
            logger.info("copy stage config from " + this.getElasticSearchArgs().stageConfigFolder)
            copyUtil.copyContentofFoldertoDirectory(this.getElasticSearchArgs().stageConfigFolder, Paths.get(this.getElasticSearchArgs().elasticSearchHome.toFile().getAbsolutePath() + File.separatorChar + "run") )
        }

        //copy local fallback config
        if(this.getGlblArgs().fallbackConfigPath!=null && this.getGlblArgs().fallbackConfigPath.toFile().exists()){
            logger.info("copy fallback config from " + this.getGlblArgs().fallbackConfigPath)
            copyUtil.copyContentofFoldertoDirectory(this.getGlblArgs().fallbackConfigPath, Paths.get(this.getElasticSearchArgs().elasticSearchHome.toFile().getAbsolutePath() + File.separatorChar + "run" + File.separatorChar + "config"))
        }

        def Path binFolder = Paths.get(this.elasticSearchArgs.elasticSearchHome.toFile().getAbsolutePath() + File.separatorChar + "run" + File.separatorChar + "bin")
        makeScriptExecutable(binFolder)

    }

    /**
     * Make all *.sh files in the given path readable, writeable, executable
     */
    private void makeScriptExecutable(def Path path2CMD){
        //nothing to do for windows
        if(SystemUtils.IS_OS_WINDOWS){
            return
        }
        WildCardFileSearcher searcher = new WildCardFileSearcher()
        String shellFilePattern = "*"
        List<Path> shellFiles=searcher.searchForFilesByWildCard(path2CMD, shellFilePattern)

        logger.info("Make "+shellFiles.size()+" files executable")

        shellFiles.forEach{

            File file = it.toFile()
            logger.info("file=" + file.getName())
            Set<PosixFilePermission> perms = new HashSet<>();
            perms.add(PosixFilePermission.OWNER_EXECUTE);
            perms.add(PosixFilePermission.OWNER_READ);
            perms.add(PosixFilePermission.OWNER_WRITE);

            Files.setPosixFilePermissions(file.toPath(), perms);
        }
    }
}