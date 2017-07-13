package de.svi.id.portal.deploymentManager.liferay

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import de.svi.id.portal.deploymentManager.argumentParser.GlobalArguments;
import de.svi.id.portal.deploymentManager.argumentParser.LiferayArguments
import de.svi.id.portal.deploymentManager.liferay.commands.AbstractLiferayCommand

class LiferayDeploymentManager{
    Logger logger = LoggerFactory.getLogger(LiferayDeploymentManager.class);
    //commands are executed in their defined order
    // full deployment
    def fullCmds = [
        "de.svi.id.portal.deploymentManager.liferay.commands.CatalinaShutdownCmd",
        "de.svi.id.portal.deploymentManager.liferay.commands.CatalinaCleanUpCmd",
        "de.svi.id.portal.deploymentManager.liferay.commands.OSGICleanUp",
        "de.svi.id.portal.deploymentManager.liferay.commands.LiferayBaseSetupCmd",
        "de.svi.id.portal.deploymentManager.liferay.commands.LiferayDeploymentCmd",
        "de.svi.id.portal.deploymentManager.liferay.commands.LiferaySetupFallbackConfigCmd",
        "de.svi.id.portal.deploymentManager.liferay.commands.CatalinaStartupCmd"
    ] as LinkedList
    // incremental deployment
    def incrementalCmds = [
		//"de.svi.id.portal.deploymentManager.liferay.commands.CatalinaCleanUpCmd",
		//"de.svi.id.portal.deploymentManager.liferay.commands.OSGICleanUp",
        "de.svi.id.portal.deploymentManager.liferay.commands.LiferayDeploymentCmd"
    ] as LinkedList
    def cmds = [] as LinkedList
    def GlobalArguments glArgs
    def LiferayArguments lfrArgs


    public LiferayDeploymentManager(GlobalArguments glArgs, LiferayArguments lfrArgs){
        this.glArgs = glArgs
        this.lfrArgs = lfrArgs
        extendPrepareArgs(this.glArgs, this.lfrArgs)
        findAndInitCommands(this.glArgs, this.lfrArgs)
        executeSelectedCommands()
    }

    /**
     * Method to check if everthing is there as we expect it
     * <ul>
     *  <li>Searching for CatalinaHome</li>
     * </ul>
     * @param glArgs
     * @param lfrArgs
     * @return
     */
    private extendPrepareArgs(GlobalArguments glArgs, LiferayArguments lfrArgs){
        lfrArgs.getCatalinaHome()
    }




    /** 
     * This method handles the correct init behaviour for the defined deploy mode
     * @param glArgs
     * @param lfrArgs
     * @return
     */
    private findAndInitCommands(GlobalArguments glArgs, LiferayArguments lfrArgs){
        def initCmds
        if(LiferayArguments.DEPLOY_MODE_FULL.equals(lfrArgs.depMode)){
			logger.info("Full Deployment Cmds are called")
            initCmds = fullCmds
        }
        if(LiferayArguments.DEPLOY_MODE_INCREMENTAL.equals(lfrArgs.depMode)){
			logger.info("Incremental Deployment Cmds are called")
            initCmds = incrementalCmds
        }
        initCmds.each {
            AbstractLiferayCommand instance = this.getClass().classLoader.loadClass( it )?.newInstance()
            instance.init(glArgs, lfrArgs)
            cmds.addLast(instance)
        }
    }

    /**
     * Loop over all selected commands and call the execute method on all
     * @return
     */
    private executeSelectedCommands(){
        cmds.each { it.execute() }
    }

}