package de.svi.id.portal.deploymentManager.liferay.commands

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Path
import java.nio.file.Paths
import de.svi.id.portal.deploymentManager.argumentParser.GlobalArguments;
import de.svi.id.portal.deploymentManager.argumentParser.LiferayArguments;
import de.svi.id.portal.deploymentManager.liferay.commands.AbstractLiferayCommand
import org.apache.commons.io.FileUtils
import java.io.File

class CatalinaCleanUpCmd extends AbstractLiferayCommand{
    
    Logger logger = LoggerFactory.getLogger(CatalinaCleanUpCmd.class)
    def GlobalArguments glArgs
    def LiferayArguments lfrArgs
    
    public void init(GlobalArguments glArgs, LiferayArguments lfrArgs){
        this.glArgs = glArgs
        this.lfrArgs = lfrArgs
    }
    
    public void execute(){
        // for init setup we dont need to clean
        if(lfrArgs.catalinaHome == null){
            return;
        }
        File catalinaTemp = Paths.get(lfrArgs.catalinaHome.toString() +  File.separatorChar + "temp").toFile()
        if(catalinaTemp.exists()){
            logger.info("Deleting CatalinaTemp: " + catalinaTemp.getAbsoluteFile())
            FileUtils.forceDelete(catalinaTemp);
        }
        File catalinaWork = Paths.get(lfrArgs.catalinaHome.toString() +  File.separatorChar + "work").toFile()
        if(catalinaWork.exists()){
            logger.info("Deleting CatalinaWork: " + catalinaWork.getAbsoluteFile())
            FileUtils.forceDelete(catalinaWork);
        }
    }
       
}