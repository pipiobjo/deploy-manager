package de.svi.id.portal.deploymentManager.elasticSearch.commands

import de.svi.id.portal.deploymentManager.argumentParser.GlobalArguments
import de.svi.id.portal.deploymentManager.argumentParser.ElasticSearchArguments

/**
 * Abstract class for commands
 * @author emir.rencber@sv-informatik.de
 *
 */
abstract class AbstractElasticSearchCommand {
    def GlobalArguments glblArgs = GlobalArguments.getInstance()
    def ElasticSearchArguments elasticSearchArgs = ElasticSearchArguments.getInstance()

    /**
     * Method to prepare execution
     * <ul>
     * <li>Parametervalidation</li>
     * <li>Unzipping</li>
     * <li>...</li>
     * </ul>
     */
    abstract def void init()

    /**
     * Method which is called for the command execution
     */
    abstract def void execute()
}