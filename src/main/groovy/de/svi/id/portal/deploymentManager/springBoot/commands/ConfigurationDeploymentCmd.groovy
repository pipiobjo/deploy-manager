package de.svi.id.portal.deploymentManager.springBoot.commands

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
 * <li>Extracting the serviceLayer jar and set the paths to the SpringBootArgs, therefor we expect it to be the first task</li>
 * <li>Copy common config</li>
 * <li>Copy stage config</li>
 * <li>Copy fallback config from local path</li>
 * </ul>
 * @author bjoern.pipiorke@sv-informatik.de
 *
 */
class ConfigurationDeploymentCmd extends AbstractSpringBootCommand{

    Logger logger = LoggerFactory.getLogger(ConfigurationDeploymentCmd.class)
    def skipStageConfig = false;
    /**
     * Does the basic parameter extraction and checks if folders exists
     */
    public void init(){
        def Path serviceLayerArtifactPath = Paths.get(this.getGlblArgs().unzippedArtifactFolder.toFile().getAbsolutePath() + File.separatorChar + "serviceLayer")

        //search4jar
        WildCardFileSearcher wcFileSearcher = new WildCardFileSearcher()
        def List<Path> serviceLayerJarSearch = wcFileSearcher.searchForFilesByWildCard(serviceLayerArtifactPath, "innendienst-service-layer*.jar")


        if(serviceLayerJarSearch.size() < 1){
            logger.error("Can not find innendienst-service-layer-*.jar in path: " + serviceLayerArtifactPath)
            System.exit(-1)
        }else if(serviceLayerJarSearch.size() > 1){
            logger.error("or found multiple innendienst-service-layer-*.jar in path: " + serviceLayerArtifactPath)
            System.exit(-1)

        }else{

            this.getSpringBootArgs().jarZipped = serviceLayerJarSearch[0]
        }

        //unzip innendienst-layer.jar
        logger.info("unzipping jar")
        Unzipper zipper = new Unzipper()
        this.getSpringBootArgs().unzippedJar = zipper.extractFolder(this.getSpringBootArgs().jarZipped.toFile()).toPath()

        //envConfig path in jar BOOT-INF\classes\envConfig\
        this.getSpringBootArgs().envConfigFolder = Paths.get(this.getSpringBootArgs().unzippedJar.toFile().getAbsolutePath() + File.separatorChar + "BOOT-INF" + File.separatorChar + "classes"+ File.separatorChar + "envConfig")

        //check if there is common folder
        def String commonConfigFolderPath = this.getSpringBootArgs().envConfigFolder.toFile().getAbsolutePath() + File.separatorChar + "common"
        def Path commonConfigFolder = Paths.get(commonConfigFolderPath)
        if(commonConfigFolder!=null && commonConfigFolder.toFile().exists()){
            this.getSpringBootArgs().commonConfigFolder = commonConfigFolder
        }else{
            logger.error("Expecting common configuration folder=" + commonConfigFolderPath)
            System.exit(-1)
        }
        //check if there is a stageId folder
        def String stageConfigFolderPath = this.getSpringBootArgs().envConfigFolder.toFile().getAbsolutePath() + File.separatorChar + this.getGlblArgs().stageIdentifier
        def Path stageConfigFolder = Paths.get(stageConfigFolderPath)

        if(stageConfigFolder!=null && stageConfigFolder.toFile().exists()){
            this.getSpringBootArgs().stageConfigFolder = stageConfigFolder
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
        logger.info("copy common config from " + this.getSpringBootArgs().commonConfigFolder)
        copyUtil.copyContentofFoldertoDirectory(this.getSpringBootArgs().commonConfigFolder, this.getSpringBootArgs().springBootHome)

        //copy stage config
        if(!skipStageConfig){
            logger.info("copy stage config from " + this.getSpringBootArgs().stageConfigFolder)
            copyUtil.copyContentofFoldertoDirectory(this.getSpringBootArgs().stageConfigFolder, this.getSpringBootArgs().springBootHome)
        }

        //copy local fallback config ... do we need to init sample configuration
        if(this.getGlblArgs().fallbackConfigPath!=null && this.getGlblArgs().fallbackConfigPath.toFile().exists()){
            logger.info("copy fallback config from " + this.getGlblArgs().fallbackConfigPath)
            copyUtil.copyContentofFoldertoDirectory(this.getGlblArgs().fallbackConfigPath, this.getSpringBootArgs().springBootHome)
        }

        def Path binFolder = Paths.get(this.springBootArgs.springBootHome.toFile().getAbsolutePath() + File.separatorChar + "bin")
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
        String shellFilePattern = "*.sh"
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