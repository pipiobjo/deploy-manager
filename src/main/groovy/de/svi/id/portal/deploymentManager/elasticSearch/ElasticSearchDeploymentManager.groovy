package de.svi.id.portal.deploymentManager.elasticSearch
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import de.svi.id.portal.deploymentManager.elasticSearch.commands.AbstractElasticSearchCommand
/**
 * Central instance to handle the Elastic Search deployment
 * <ul>
 *  <li>handles the init phase/order of all commands</li>
 *  <li>handles the execution phase/order of all commands</li>
 * </ul>
 * @author emir.rencber@sv-informatik.de
 *
 */
class ElasticSearchDeploymentManager{
    Logger logger = LoggerFactory.getLogger(ElasticSearchDeploymentManager.class);
    //commands are executed in their defined order
    def fullCmds = [
        "de.svi.id.portal.deploymentManager.elasticSearch.commands.InitFolderStructureCmd",
        "de.svi.id.portal.deploymentManager.elasticSearch.commands.StopElasticSearchCmd",
        "de.svi.id.portal.deploymentManager.elasticSearch.commands.CleanUpCmd",
        "de.svi.id.portal.deploymentManager.elasticSearch.commands.ArtifactDeploymentCmd",
        "de.svi.id.portal.deploymentManager.elasticSearch.commands.ConfigurationDeploymentCmd",
        "de.svi.id.portal.deploymentManager.elasticSearch.commands.StartElasticSearchCmd"

    ] as LinkedList
    		
    def cmds = [] as LinkedList


    public ElasticSearchDeploymentManager(){
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
            AbstractElasticSearchCommand instance = this.getClass().classLoader.loadClass( it)?.newInstance()
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