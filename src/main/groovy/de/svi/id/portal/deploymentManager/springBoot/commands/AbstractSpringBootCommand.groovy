package de.svi.id.portal.deploymentManager.springBoot.commands

import de.svi.id.portal.deploymentManager.argumentParser.GlobalArguments
import de.svi.id.portal.deploymentManager.argumentParser.SpringBootArguments

/**
 * Abstrac class for commands
 * @author bjoern.pipiorke@sv-informatik.de
 *
 */
abstract class AbstractSpringBootCommand {
    def GlobalArguments glblArgs = GlobalArguments.getInstance()
    def SpringBootArguments springBootArgs = SpringBootArguments.getInstance()

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