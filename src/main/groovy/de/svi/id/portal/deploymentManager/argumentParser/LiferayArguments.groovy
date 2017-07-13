package de.svi.id.portal.deploymentManager.argumentParser

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes

class LiferayArguments{
    static DEPLOY_MODE_FULL = "full"
    static DEPLOY_MODE_INCREMENTAL = "incremental"

    def Path liferayHome
    def String depMode
    def String secondsToWait4Tomcat2Shutdown = 30

    //value is autodetected @see detectCatalinaHome
    def Path catalinaHome

    /**
     * for now just returning if a directory is found if not, in case of a initial setup return null
     * @return
     */
    public getCatalinaHome(){
        def files = detectCatalinaHome(liferayHome)
        if(files.size()==1){
            this.catalinaHome = files[0].toPath()
        }else{
            this.catalinaHome = null
        }
        return this.catalinaHome
    }

    /**
     * Detects a directory which starts with tomcat
     * If a symbolic link is found, we prefer it over the local directory
     * @param liferayHome
     * @return
     */
    public List<File> detectCatalinaHome(Path liferayHome){

        List<File> list = Arrays.asList(liferayHome.toFile().listFiles(new FilenameFilter(){

                    boolean foundSymbolicLink = false;
                    /**
             * Searching for tomcat directory. prefer symbolic links over local directory
             * @param dir
             * @param name
             * @return
             */
                    @Override
                    public boolean accept(File dir, String name) {
                        Path file = Paths.get(dir.getAbsolutePath() + File.separatorChar)
                        BasicFileAttributes attrs = Files.readAttributes(file, BasicFileAttributes.class);
                        if(attrs.isSymbolicLink()){
                            foundSymbolicLink = true;
                        }

                        if(foundSymbolicLink){
                            return false;
                        }

                        return name.startsWith("tomcat")
                    }
                }));

        return list
    }

}