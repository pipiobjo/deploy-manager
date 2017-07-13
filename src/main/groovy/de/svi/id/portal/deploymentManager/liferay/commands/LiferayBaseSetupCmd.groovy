package de.svi.id.portal.deploymentManager.liferay.commands

import java.nio.file.DirectoryStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.PosixFilePermission

import org.apache.commons.io.FileUtils
import org.apache.commons.lang.SystemUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import de.svi.id.portal.deploymentManager.argumentParser.GlobalArguments;
import de.svi.id.portal.deploymentManager.argumentParser.LiferayArguments;
import de.svi.id.portal.deploymentManager.utils.Unzipper
import de.svi.id.portal.deploymentManager.utils.WildCardFileSearcher
import groovy.io.FileType

class LiferayBaseSetupCmd extends AbstractLiferayCommand{
    Logger logger = LoggerFactory.getLogger(LiferayBaseSetupCmd.class)
    def GlobalArguments glArgs
    def LiferayArguments lfrArgs
    def File liferayBundleZIP

    public void init(GlobalArguments glArgs, LiferayArguments lfrArgs){
        this.glArgs = glArgs
        this.lfrArgs = lfrArgs



        Path tomcatBundleDir = Paths.get(glArgs.unzippedArtifactFolder.toString() +  File.separatorChar + "liferayTomcatBundle")

        List<Path> files=new ArrayList<>();
        DirectoryStream dirStream = Files.newDirectoryStream(tomcatBundleDir, "*.zip")
        dirStream.forEach{
            files.add(it);
        }

        if(files.size() > 1 || files.size() == 0){
            logger.error("There are multiple or non liferayTomcatBundles references in the artifactDir=" + glArgs.unzippedArtifactFolder + " Following bundles are found: " + files)
            System.exit(-1)
        }

        liferayBundleZIP = files[0].toFile()
    }

    public void execute(){
        File liferayHome = lfrArgs.liferayHome.toFile()
        if(liferayHome.exists()){
            try{
                cleanLiferayHome()
                cleanCatalinaHome()
            }catch(java.io.FileNotFoundException e){
                logger.error("Error while cleaning LiferayHome" + liferayHome, e)
            }
        }else{
            liferayHome.mkdir()
        }

        Unzipper unzip = new Unzipper()
        logger.info("Unzip liferayBundle to " + liferayHome)
        unzip.unZipAll(liferayBundleZIP, liferayHome, false, true)
    }

    private void cleanLiferayHome(){
        cleanDirectory(lfrArgs.liferayHome, ["log", "tomcat"])
    }

    private void cleanCatalinaHome(){
        cleanDirectory(lfrArgs.liferayHome, ["log"])
    }

    /**
     * Cleans a directory except the files and folders matching one of the elements in the list
     */
    private void cleanDirectory(def Path dir, List<String> except){

        //        def dir = new File("path_to_parent_dir")
        dir.toFile().eachFile (FileType.ANY) { file ->
            def fileName = file.getName()
            def isBlacklisted = false
            except.each {
                if(fileName.contains("${it}")){
                    isBlacklisted = true
                }

            }

            if(file.exists() && !isBlacklisted){
                logger.info("Deleting file: " + file.getAbsolutePath())
                FileUtils.forceDelete(file);
            }

        }

    }
   
}