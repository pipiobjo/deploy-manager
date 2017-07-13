package de.svi.id.portal.deploymentManager.liferay.commands

import java.nio.file.Path
import java.nio.file.Paths

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import de.svi.id.portal.deploymentManager.argumentParser.GlobalArguments;
import de.svi.id.portal.deploymentManager.argumentParser.LiferayArguments;
import de.svi.id.portal.deploymentManager.utils.CopyUtil
import de.svi.id.portal.deploymentManager.utils.WildCardFileSearcher

/**
 * 
 * @author bjoern.pipiorke@sv-informatik.de
 *
 */
class LiferaySetupFallbackConfigCmd extends AbstractLiferayCommand{
    Logger logger = LoggerFactory.getLogger(LiferaySetupFallbackConfigCmd.class)
    def GlobalArguments glArgs
    def LiferayArguments lfrArgs
    def Path distFolderUnzipped, sampleFallbackFolder
    def WildCardFileSearcher searcher = new WildCardFileSearcher()



    public void init(GlobalArguments glArgs, LiferayArguments lfrArgs){
        this.glArgs = glArgs
        this.lfrArgs = lfrArgs


        Path liferayDistFolder = Paths.get(glArgs.unzippedArtifactFolder.toString() +  File.separatorChar + "liferayDist")

        // searching for the new unzipped distFolder
        File[] distFolders = liferayDistFolder.toFile().listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        return file.isDirectory();
                    }
                });

        //expecting that the job befores do all the unzipping
        if(distFolders.length != 1){
            throw new Exception("Expecting a liferayDist directory in " + glArgs.unzippedArtifactFolder.toString())
        }
        distFolderUnzipped = distFolders[0].toPath()


        // get stage configuration folder
        String sampleFallbackConfigPath = distFolderUnzipped.toString() +  File.separatorChar + "configs"+  File.separatorChar + "sampleConfig"
        sampleFallbackFolder = Paths.get(sampleFallbackConfigPath)




    }




    public void execute(){
        //no execution if parameter is not set
        if(glArgs.fallbackConfigPath == null){
            logger.info("No fallbackConfiguration found, skip execution")
            return
        }

        CopyUtil copyUtil = new CopyUtil()


        //copy sample files to fallbackConfigPath
        if(this.sampleFallbackFolder != null && this.sampleFallbackFolder.toFile().exists()){
            logger.info("Copy sample configuration")
            logger.info("\t from=" + this.sampleFallbackFolder.toString())
            logger.info("\t to=" + glArgs.fallbackConfigPath.toString())
            copyUtil.copyContentofFoldertoDirectory(this.sampleFallbackFolder, glArgs.fallbackConfigPath)

        }

        logger.info("Copy fallback configuration")
        logger.info("\t from=" + glArgs.fallbackConfigPath.toString())
        logger.info("\t to=" + lfrArgs.liferayHome.toString())
        //copy fallback configs to liferay home
		copyUtil.updateLiferayConfiguration(glArgs.fallbackConfigPath, lfrArgs.liferayHome, lfrArgs.catalinaHome)

    }


}