package de.svi.id.portal.deploymentManager.springBoot
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import de.svi.id.portal.deploymentManager.springBoot.commands.AbstractSpringBootCommand

/**
 * Central instance to handle the springBoot deployment
 * <ul>
 *  <li>handles the init phase/order of all commands</li>
 *  <li>handles the execution phase/order of all commands</li>
 * </ul>
 * @author bjoern.pipiorke@sv-informatik.de
 *
 */
class SpringBootDeploymentManager{
    Logger logger = LoggerFactory.getLogger(SpringBootDeploymentManager.class);
    //commands are executed in their defined order
    def fullCmds = [
        "de.svi.id.portal.deploymentManager.springBoot.commands.InitFolderStructureCmd",
        "de.svi.id.portal.deploymentManager.springBoot.commands.StopServiceLayerCmd",
        "de.svi.id.portal.deploymentManager.springBoot.commands.ConfigurationDeploymentCmd",
        "de.svi.id.portal.deploymentManager.springBoot.commands.CleanUpCmd",
        "de.svi.id.portal.deploymentManager.springBoot.commands.ArtifactDeploymentCmd",
        "de.svi.id.portal.deploymentManager.springBoot.commands.StartServiceLayerCmd"
    ] as LinkedList
    def cmds = [] as LinkedList


    public SpringBootDeploymentManager(){
        findAndInitCommands()
        executeSelectedCommands()
    }


    /**
     * This method handles the correct init behaviour for the defined deploy mode
     * @param glArgs
     * @param lfrArgs
     * @return
     */
    private findAndInitCommands(){
        fullCmds.each {
            AbstractSpringBootCommand instance = this.getClass().classLoader.loadClass( it)?.newInstance()
            instance.init()
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