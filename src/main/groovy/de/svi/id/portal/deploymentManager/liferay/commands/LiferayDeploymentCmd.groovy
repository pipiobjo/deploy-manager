package de.svi.id.portal.deploymentManager.liferay.commands

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.PosixFilePermission

import org.apache.commons.lang.SystemUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import de.svi.id.portal.deploymentManager.argumentParser.GlobalArguments;
import de.svi.id.portal.deploymentManager.argumentParser.LiferayArguments;
import de.svi.id.portal.deploymentManager.utils.CopyUtil
import de.svi.id.portal.deploymentManager.utils.Unzipper
import de.svi.id.portal.deploymentManager.utils.WildCardFileSearcher

class LiferayDeploymentCmd extends AbstractLiferayCommand{
    Logger logger = LoggerFactory.getLogger(LiferayDeploymentCmd.class)
    def GlobalArguments glArgs
    def LiferayArguments lfrArgs
    def File liferayModuleZIP
    def Path distFolderUnzipped, stageConfigFolder, commonConfigFolder, libFolder
    WildCardFileSearcher searcher = new WildCardFileSearcher()
    CopyUtil copyUtil = new CopyUtil()

    public void init(GlobalArguments glArgs, LiferayArguments lfrArgs){
        this.glArgs = glArgs
        this.lfrArgs = lfrArgs



        Path liferayDistFolder = Paths.get(glArgs.unzippedArtifactFolder.toString() +  File.separatorChar + "liferayDist")
        String pattern = "liferay-assembly-*.zip"

        List<Path> files=searcher.searchForFilesByWildCard(liferayDistFolder, pattern)

        if(files.size() > 1 || files.size() == 0){
            logger.error("There are multiple or non liferayTomcatBundles references in the artifactDir=" + glArgs.unzippedArtifactFolder.toString() + " Following bundles are found: " + files.size())
            System.exit(-1)
        }

        liferayModuleZIP = files[0].toFile()

        Unzipper unzip = new Unzipper()
        unzip.extractFolder(liferayModuleZIP)

        // searching for the new unzipped distFolder
        File[] distFolders = liferayDistFolder.toFile().listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        return file.isDirectory();
                    }
                });
        distFolderUnzipped = distFolders[0].toPath()


        // get stage configuration folder
        String stageConfigPath = distFolderUnzipped.toString() +  File.separatorChar + "configs"+  File.separatorChar + glArgs.stageIdentifier
        stageConfigFolder = Paths.get(stageConfigPath)
        if(stageConfigFolder == null){
            logger.error("No liferay stage configuration found! Searching with stageIdentifier="+ glArgs.stageIdentifier + " in path="+ stageConfigPath)
            System.exit(-1)
        }

        // get common configuration folder
        String commonConfigPath = distFolderUnzipped.toString() +  File.separatorChar + "configs"+  File.separatorChar + "common"
        commonConfigFolder= Paths.get(commonConfigPath)
        if(commonConfigFolder == null){
            logger.error("No liferay common configuration found! Searching in path="+ commonConfigFolder)
            System.exit(-1)
        }

        // get library path and delete unwanted files javadoc and sources
        String libPath = distFolderUnzipped.toString() +  File.separatorChar + "lib"
        libFolder = Paths.get( libPath)
        if(libFolder == null){
            logger.error("No liferay modules directory found! Searching in path="+ libPath)
            System.exit(-1)
        }

        //delete javadoc jars
        String javaDocpattern = "*-javadoc.jar"
        List<Path> javaDocJars=searcher.searchForFilesByWildCard(libFolder, javaDocpattern)
        deleteAllFilesFromList(javaDocJars)

        //delete source jars
        String sourcesPattern = "*-sources.jar"
        List<Path> sourcesJars=searcher.searchForFilesByWildCard(libFolder, sourcesPattern)
        deleteAllFilesFromList(sourcesJars)


    }



    public void execute(){
		logger.info("LiferayDeploymentCmd execute is called, Deploy Mode: " + this.lfrArgs.depMode)
        // deploy all bundles to liferayHome/deploy
        Path liferayDeployFolder = Paths.get(lfrArgs.liferayHome.toString() +  File.separatorChar + "deploy")

        // copy all jars
        String jarPattern = "*.jar"
        List<Path> jars=searcher.searchForFilesByWildCard(libFolder, jarPattern)
        logger.info("Copy " + jars.size() + " jars to " + liferayDeployFolder.toString())
        copyUtil.copyListOfFilesToTargetDir(jars, liferayDeployFolder)

        String warPattern = "*.war"
        List<Path> wars=searcher.searchForFilesByWildCard(libFolder, warPattern)
        logger.info("Copy " + wars.size() + " wars to " + liferayDeployFolder.toString())
        copyUtil.copyListOfFilesToTargetDir(wars, liferayDeployFolder)
		
		if(LiferayArguments.DEPLOY_MODE_FULL.equals(this.lfrArgs.depMode)){
			//copy configs common
			logger.info("Copy common config")
			
			copyUtil.updateLiferayConfiguration(commonConfigFolder, lfrArgs.liferayHome, lfrArgs.catalinaHome)

			// copy stage config, to overwrite the default behaviour
			logger.info("Copy stage config")
			copyUtil.updateLiferayConfiguration(stageConfigFolder, lfrArgs.liferayHome, lfrArgs.catalinaHome)
		}
        makeCatalinaFilesExecutable()

    }
	

	

    private deleteAllFilesFromList(List<Path> files){
        files.forEach{
            it.toFile().delete()
        }
    }


    /**
     * For unix we need to make the unzipped files executable
     * @return
     */
    private makeCatalinaFilesExecutable(){
        //nothing to do for windows
        if(SystemUtils.IS_OS_WINDOWS){
            return
        }
        WildCardFileSearcher searcher = new WildCardFileSearcher()
        String shellFilePattern = "*.sh"
        def Path catalinaBin = Paths.get(this.lfrArgs.getCatalinaHome().toString()+  File.separatorChar + "bin")
        List<Path> shellFiles=searcher.searchForFilesByWildCard(catalinaBin, shellFilePattern)

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