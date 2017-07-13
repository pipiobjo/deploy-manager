package de.svi.id.portal.deploymentManager.liferay.commands

import de.svi.id.portal.deploymentManager.argumentParser.GlobalArguments
import de.svi.id.portal.deploymentManager.argumentParser.LiferayArguments

/**
 * Abstrac class for commands
 * @author bjoern.pipiorke@sv-informatik.de
 *
 */
abstract class AbstractLiferayCommand {
    def glblArgs, lfrArgs

    /**
     * Method to prepare execution
     * <ul>
     * <li>Parametervalidation</li>
     * <li>Unzipping</li>
     * <li>...</li>
     * </ul>
     */
    abstract def void init(GlobalArguments glArgs, LiferayArguments lfrArgs)

    /**
     * Method which is called for the command execution
     */
    abstract def void execute()
}