package de.svi.id.portal.deploymentManager.liferay.commands

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Path
import java.nio.file.Paths
import java.io.File
import de.svi.id.portal.deploymentManager.argumentParser.GlobalArguments;
import de.svi.id.portal.deploymentManager.argumentParser.LiferayArguments;
import de.svi.id.portal.deploymentManager.liferay.commands.AbstractLiferayCommand
import org.apache.commons.io.FileUtils

class OSGICleanUp extends AbstractLiferayCommand{
    
    Logger logger = LoggerFactory.getLogger(OSGICleanUp.class)
    def GlobalArguments glArgs
    def LiferayArguments lfrArgs
    
    public void init(GlobalArguments glArgs, LiferayArguments lfrArgs){
        this.glArgs = glArgs
        this.lfrArgs = lfrArgs
    }
    
    public void execute(){
		logger.info("OSGICleanUp execute command is called")

        // Cleaning old modules
        File osgiModules = Paths.get(lfrArgs.liferayHome.toString() +  File.separatorChar + "osgi"+  File.separatorChar + "modules").toFile()
        if(osgiModules.exists()){
            logger.info("Deleting all OSGI Modules: " + osgiModules.getAbsoluteFile())
            FileUtils.forceDelete(osgiModules)
            osgiModules.mkdir()
        }
        // Cleaning old precompiled jsps
        File liferayWork = Paths.get(lfrArgs.liferayHome.toString() +  File.separatorChar + "work").toFile()
        if(liferayWork.exists()){
            logger.info("Deleting LiferayWork: " + liferayWork.getAbsoluteFile())
            FileUtils.forceDelete(liferayWork)
            liferayWork.mkdir()
        }
        
        // Cleaning osgi/state folder in full deployment, needs tomcat restart
        if(LiferayArguments.DEPLOY_MODE_FULL.equals(lfrArgs.depMode)){
            File osgiState = Paths.get(lfrArgs.liferayHome.toString() +  File.separatorChar + "osgi"+  File.separatorChar + "state").toFile()
            if(osgiState.exists()){
                logger.info("Deleting OSGI State: " + osgiState.getAbsoluteFile())
                FileUtils.forceDelete(osgiState)
                osgiState.mkdir()
            }
        }
        
    }
       
}