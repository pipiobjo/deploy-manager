package de.svi.id.portal.deploymentManager.liferay.commands

import org.apache.commons.lang.SystemUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import de.svi.id.portal.deploymentManager.argumentParser.GlobalArguments;
import de.svi.id.portal.deploymentManager.argumentParser.LiferayArguments;

class CatalinaStartupCmd extends AbstractLiferayCommand{
    Logger logger = LoggerFactory.getLogger(CatalinaStartupCmd.class)
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

        def String cmd = "startup.sh"

        if(SystemUtils.IS_OS_WINDOWS){
            cmd = "startup.bat"
        }
        def String executeCmd = "${lfrArgs.getCatalinaHome()}" + File.separatorChar + "bin" + File.separatorChar + cmd
        logger.info("Starting Tomcat with cmd: " + executeCmd)

        def sout = new StringBuilder(), serr = new StringBuilder()
        def proc = executeCmd.execute()
        proc.consumeProcessOutput(sout, serr)
        proc.waitForOrKill((timeout + 1)*1000)
    }
}