#!/bin/bash
echo "Starting DeploymentManager - Liferay Deployment - Full"


# set java home
JAVA_HOME="/usr/local/java"

# set path to deploymentManager.jar
DM_JAR_PATH="/opt/tomcat01/deploymentManager/run/portal-id-deploymentManager-*.jar"


JAVA_OPTS=""


#Getting Release from temp path
#RELEASE_PATH=/tmp/innendienstRelease
RELEASE_PATH=/opt/tomcat01/deploymentManager/releases


# set params
OPTIONS="--service=liferay"
OPTIONS="$OPTIONS --stageIdentifier=rt1"
OPTIONS="$OPTIONS --liferayDeployMode=incremental"
OPTIONS="$OPTIONS --realeaseFilePath=$RELEASE_PATH/ID-Portal-Release.zip" 
OPTIONS="$OPTIONS --liferayHome=/opt/tomcat01/liferay"
OPTIONS="$OPTIONS --fallbackConfigPath=/opt/tomcat01/liferayFallbackConfig"
OPTIONS="$OPTIONS --temp=/opt/tomcat01/deploymentManager/tmp"
OPTIONS="$OPTIONS --liferayShutdownTime=50" 



#Build command
CMD="$JAVA_HOME/bin/java -jar $JAVA_OPTS $DM_JAR_PATH $OPTIONS" 
echo "CMD=$CMD"

eval $CMD
