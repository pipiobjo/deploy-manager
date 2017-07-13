package de.svi.id.portal.deploymentManager.argumentParser

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import org.apache.commons.cli.*
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 *
 *
 **/
class ArgumentParser{

    private String[] args
    private HelpFormatter formatter = new HelpFormatter()
    private Options options = new Options()
    private CommandLine cmd;
    private Logger logger = LoggerFactory.getLogger(ArgumentParser.class);


    public ArgumentParser(args){
        this.args = args

        options = getGlobalOptions(options)
        options = getLiferayOptions(options)
        options = getSpringBootOptions(options)
    	options = getElasticSearchOptions(options)
        logger.info("Parsing parameters ...")
        cmd = parseArgs(options)
    }

    /**
     * 
     * @param options
     * @return
     */
    private Options getGlobalOptions(Options options){
        options.addOption(Option.builder("stageIdentifier").argName("sI").longOpt("stageIdentifier").desc("Defines which Stage Configuration should be used. For example: local/dev/ft/uat/prod").hasArg().required().build())
        options.addOption(Option.builder("s").argName("service").longOpt("service").desc("Service which should be handled. For examle liferay or springBoot").hasArg().required().build())
        options.addOption(Option.builder("rURL").argName("releaseURL").longOpt("releaseURL").desc("Path or URL to the release artifact").hasArg().build())
        options.addOption(Option.builder("rFilePath").argName("realeaseFilePath").longOpt("realeaseFilePath").desc("Uses a local release zip.").hasArg().build())
        options.addOption(Option.builder("temp").argName("tempDir4DM").longOpt("tempDir4DM").hasArg().desc("Temp directory for DownloadManager. By default using the temp dir from the operating system").build())
        options.addOption(Option.builder("fallbackConfigPath").argName("fallback").longOpt("fallbackConfigPath").hasArg().desc("Overwrites the service home directory, with all content inside the given path").build())

        options.addOption("h", "help", false, "Prints this help")

        return options
    }

    /**
     * 
     * @param options
     * @return
     */
    private Options getLiferayOptions(Options options){
        options.addOption("lH", "liferayHome", true, "Path to liferay home for example /opt/tomcat01/liferay")
        options.addOption("m", "liferayDeployMode", true, "Liferay deployment mode incremental or full deployment")
        options.addOption("shutdownTime", "liferayShutdownTime", true, "Seconds to shutdown liferay tomcat, before killing")

        return options
    }

    /**
     * 
     * @param options
     * @return
     */
    private Options getSpringBootOptions(Options options){
        options.addOption("sH", "springBootHome", true, "Path to springBoot home for example /opt/idsl/springBoot")
        return options
    }
    
    /**
     * 
     * @param options
     * @return
     */
    private Options getElasticSearchOptions(Options options){
        options.addOption("eH", "elasticSearchHome", true, "Path to springBoot home for example /opt/tomcat1/elasticSearch/elasticSearch")
        return options
    }

    /**
     * parse global args
     * @return
     */
    public GlobalArguments parseGlobalArguments(){
        def gA = GlobalArguments.getInstance()

        if( cmd.hasOption( "help" ) ) {
            formatter.printHelp(600, "deploymentManager", null,options, null, true);
            System.exit(0)
        }

        Path myTempDir
        if( cmd.hasOption( "tempDir4DM" ) ) {
            myTempDir = Paths.get(cmd.getOptionValue( "tempDir4DM" ))
            if(!myTempDir.toFile().exists()){
                logger.info("Temp dir doesn't exists, creating it: " + myTempDir.toString())
                myTempDir.toFile().mkdirs()
            }
        }else{
            myTempDir = Files.createTempDirectory("innendienst-deployManager");
        }
        gA.tempDir = myTempDir


        if( cmd.hasOption( "realeaseFilePath" ) && !cmd.hasOption( "releaseURL" ) ) {
            gA.skipDownloads =true
            def path2ReleaseZip = cmd.getOptionValue( "realeaseFilePath" )

            Path path2ReleaseZipPath = Paths.get(path2ReleaseZip)
            if(!path2ReleaseZipPath.toFile().exists()){
                logger.error("Expecting parameter \"--realeaseFilePath\" to be a valid file path: " + path2ReleaseZipPath + "... Stop execution")
                System.exit(-1)
            }
            gA.realeaseFilePath = path2ReleaseZipPath
        }else if( cmd.hasOption( "releaseURL" ) && !cmd.hasOption( "realeaseFilePath" )) {
            gA.releaseURL =cmd.getOptionValue( "releaseURL" )
        }else{
            logger.error("Expecting parameter \"--realeaseFilePath\" or \"--releaseURL\" ... Stop execution")
            System.exit(-1)
        }

        if( cmd.hasOption( "service" ) ) {
            def service = cmd.getOptionValue( "service" )

            if("liferay".equalsIgnoreCase( service ) || "springBoot".equalsIgnoreCase( service ) || "elasticSearch".equalsIgnoreCase( service )){
                gA.service =service.toLowerCase()
            }else{
                logger.error("Unsupported service=" + service + " expecting liferay, springBoot or elasticSearch")
                System.exit(-1)
            }
        }

        if( cmd.hasOption( "stageIdentifier" ) ) {
            gA.stageIdentifier =cmd.getOptionValue( "stageIdentifier" )
        }

        if(cmd.hasOption("fallbackConfigPath")){
            String pathStr = cmd.getOptionValue( "fallbackConfigPath" )
            Path fallbackPath = Paths.get(pathStr)
            if(!fallbackPath.toFile().exists()){
                logger.error("Expecting parameter \"--fallbackConfigPath\" to be valid path, but is: " + pathStr + " ... Stop execution")
                System.exit(-1)
            }
            gA.fallbackConfigPath = fallbackPath
        }


        return gA
    }



    /**
     * parsing liferay args
     * @return
     */
    public LiferayArguments parseLiferayArguments(){
        def lA = new LiferayArguments()

        if( cmd.hasOption( "liferayHome" ) ) {
            def liferayHome = cmd.getOptionValue( "liferayHome" )

            // using groovy truth, empty or null -> false
            if (!liferayHome?.trim()) {
                logger.error("Expecting parameter \"--liferayHome\" but is not set ... Stop execution")
                System.exit(-1)
            }

            Path liferayHomePath = Paths.get(liferayHome)
            if(!liferayHomePath.toFile().exists()){
                logger.info("liferayHome does not exists, creating path=" + liferayHomePath.toFile().getAbsolutePath())
                liferayHomePath.toFile().mkdirs()
            }

            lA.liferayHome =liferayHomePath
        }


        if( cmd.hasOption( "shutdownTime" ) ) {
            def time =cmd.getOptionValue( "shutdownTime" )
            lA.secondsToWait4Tomcat2Shutdown = time
        }




        if( cmd.hasOption( "liferayDeployMode" ) ) {

            def depMode =cmd.getOptionValue( "liferayDeployMode" )

            if(LiferayArguments.DEPLOY_MODE_FULL.equalsIgnoreCase( depMode ) || LiferayArguments.DEPLOY_MODE_INCREMENTAL.equalsIgnoreCase( depMode )){
                lA.depMode = depMode
            }else{
                logger.error("Expecting parameter \"--liferayDeployMode\" to be full or incremental, but is: " + depMode + " ... Stop execution")
                System.exit(-1)
            }
        }


        return lA

    }



    /**
     * parsing springBootargs
     * @return
     */
    public SpringBootArguments parseSpringBootArguments(){
        SpringBootArguments sP = SpringBootArguments.getInstance()
        if(cmd.hasOption("springBootHome")){
            String pathStr = cmd.getOptionValue( "springBootHome" )
            Path springBootHomePath = Paths.get(pathStr)

            if(!springBootHomePath.toFile().exists()){
                logger.info("springBootHome does not exists, creating path=" + springBootHomePath.toFile().getAbsolutePath())
                springBootHomePath.toFile().mkdirs()
            }
            sP.springBootHome = springBootHomePath
        }
        return sP
    }
    
    /**
     * parsing elasticSearchArgs
     * @return
     */
    public ElasticSearchArguments parseElasticSearchArguments(){
        ElasticSearchArguments eS = ElasticSearchArguments.getInstance()
        if(cmd.hasOption("elasticSearchHome")){
            String pathStr = cmd.getOptionValue( "elasticSearchHome" )
            Path elasticSearchHomePath = Paths.get(pathStr)

            if(!elasticSearchHomePath.toFile().exists()){
                logger.info("elasticSearchHome does not exists, creating path=" + elasticSearchHomePath.toFile().getAbsolutePath())
                elasticSearchHomePath.toFile().mkdirs()
            }
            eS.elasticSearchHome = elasticSearchHomePath
        }
        return eS
    }

    /**
     *  init the commandline reader
     */
    private CommandLine parseArgs(Options options){
        // create the parser
        CommandLineParser parser = new DefaultParser();
        try {
            // parse the command line arguments
            return parser.parse( options, this.args );
        }
        catch( ParseException exp ) {
            // oops, something went wrong
            logger.error( "Parsing failed.  Reason: " + exp.getMessage() );
            formatter.printHelp(600, "deploymentManager", null,options, null, true);
            System.exit(-1)
        }

    }

}