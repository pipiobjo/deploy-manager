package de.svi.id.portal.deploymentManager.liferay.commands
import org.apache.commons.lang.SystemUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import de.svi.id.portal.deploymentManager.argumentParser.GlobalArguments
import de.svi.id.portal.deploymentManager.argumentParser.LiferayArguments



/**
 * 
 * @author bjoern.pipiorke@sv-informatik.de
 *
 */
class CatalinaShutdownCmd extends AbstractLiferayCommand{
    Logger logger = LoggerFactory.getLogger(CatalinaShutdownCmd.class)
    def GlobalArguments glArgs
    def LiferayArguments lfrArgs
    def int timeout

    public void init(GlobalArguments glArgs, LiferayArguments lfrArgs){
        this.glArgs = glArgs
        this.lfrArgs = lfrArgs

        try{
            timeout = Integer.parseInt(lfrArgs.secondsToWait4Tomcat2Shutdown)
        }catch(NumberFormatException e){
            logger.error("timeout parameter is not an Integer: " + lfrArgs.secondsToWait4Tomcat2Shutdown, e)
            System.exit(-1)
        }
    }
    public void execute(){
        // for init setup we dont need to stop
        if(lfrArgs.catalinaHome == null){
            logger.info("Empty catalinaHome, dont try to shutdown")
            return;
        }



        def String cmd = "shutdown.sh"

        if(SystemUtils.IS_OS_WINDOWS){
            cmd = "shutdown.bat"
        }
        // calling shutdown.sh 30 -force
        cmd = cmd + " " + lfrArgs.secondsToWait4Tomcat2Shutdown + " -force "
        def String executeCmd = "${lfrArgs.catalinaHome}" + File.separatorChar + "bin" + File.separatorChar + cmd
        logger.info("Stopping Tomcat with cmd: " + executeCmd)

        def sout = new StringBuilder(), serr = new StringBuilder()
        def proc = executeCmd.execute()
        proc.consumeProcessOutput(sout, serr)

        proc.waitForOrKill((timeout + 1)*1000)

        logger.info(sout.toString())

        // waiting 5 seconds, to be sure that tomcat is stopped and all filehandler are closed from the system
        sleep(5000)
    }

}