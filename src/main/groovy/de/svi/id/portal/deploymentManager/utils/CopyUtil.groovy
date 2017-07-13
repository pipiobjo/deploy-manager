package de.svi.id.portal.deploymentManager.utils

import java.nio.file.Path
import java.nio.file.Paths

import org.apache.commons.io.FileUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
/**
 * Class to encapsualte copy logic
 * @author bjoern.pipiorke@sv-informatik.de
 *
 */
class CopyUtil{

    Logger logger = LoggerFactory.getLogger(CopyUtil.class)
	
	
/**
 * Update Liferay configuration with tomcat version handling
 * configuration src folder ignores tomcat version just /common/tomcat
 * to trg/tomcat-${version} -> catalinaHome ->
 **/
	public void updateLiferayConfiguration(Path src, Path liferayHome, Path catalinaHome){
		new AntBuilder().copy(todir: liferayHome) {
			fileset(dir : src) {
				exclude(name:"**/tomcat*/**")
			}
		}
		//copy tomcat configuration to tomcat-${version} directory
		String tomcatConfigPath = src.toString() +  File.separatorChar + "tomcat"
		def Path tomcatConfigFolder= Paths.get(tomcatConfigPath)
		this.copyContentofFoldertoDirectory(tomcatConfigFolder, catalinaHome)
	}
	
	
	
    /**
     * Method to copy the all content of a folder to an other folder, without the parentFolder 
     */
    public copyContentofFoldertoDirectory(Path source, Path target){
        //copy root files
        File[] rootFiles = source.toFile().listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        return file.isFile();
                    }
                });
        if(rootFiles != null){
            logger.info("\t Copy " + rootFiles.length + " files to " + target.toString())
            copyAllFilesToTargetDir(rootFiles, target)
        }

        copyAllSubFoldersToDirectory(source, target)
    }

    /**
     * Copys a list of files to the given target folder
     * @param files
     * @param target
     * @return
     */
    public copyListOfFilesToTargetDir(List<Path> files, Path target){
        files.forEach{
            File f = it.toFile()
            FileUtils.copyFileToDirectory(f, target.toFile(), true)

        }
    }



    /**
     * Helper method to copy files in the direct folder
     * @param files
     * @param target
     * @return
     */
    private copyAllFilesToTargetDir(File[] files, Path target){
        files.each{
            FileUtils.copyFileToDirectory(it, target.toFile(), true)

        }
    }
    /**
     * Helper method to copy all subfolders to the given target folder
     * @param sourceFolder
     * @param targetFolder
     * @return
     */
    private copyAllSubFoldersToDirectory(Path sourceFolder, Path targetFolder){
        //copy folders
        File[] subFolders = sourceFolder.toFile().listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        return file.isDirectory();
                    }
                });

        if(subFolders != null){
            logger.info("\t Copy " + subFolders.length + " folders to " + targetFolder.toString())
            subFolders.each{
                FileUtils.copyDirectoryToDirectory(it, targetFolder.toFile())
            }
        }

        File[] subFiles = sourceFolder.toFile().listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        return file.isFile();
                    }
                });

        subFiles.each{
            FileUtils.copyFileToDirectory(it, targetFolder.toFile())
        }

    }
}

