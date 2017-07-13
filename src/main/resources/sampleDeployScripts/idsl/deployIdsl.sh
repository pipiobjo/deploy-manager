#!/bin/bash
echo "Starting DeploymentManager - ServiceLayer Deployment"


# set java home
JAVA_HOME="/usr/local/java"

# set path to deploymentManager.jar
DM_JAR_PATH="/opt/idsl/deploymentManager/run/portal-id-deploymentManager-*.jar"


JAVA_OPTS=""


#Getting Release from temp path
#RELEASE_PATH=/tmp/innendienstRelease
RELEASE_PATH=/opt/tomcat01/deploymentManager/releases


# set params
OPTIONS="--service=springBoot"
OPTIONS="$OPTIONS --stageIdentifier=rt1"
OPTIONS="$OPTIONS --realeaseFilePath=$RELEASE_PATH/ID-Portal-Release.zip"
OPTIONS="$OPTIONS --springBootHome=/opt/idsl/idsl/"
OPTIONS="$OPTIONS --fallbackConfigPath=/opt/idsl/idslFallbackConfig/"
OPTIONS="$OPTIONS --temp=/opt/idsl/deploymentManager/tmp"



#Build command
CMD="$JAVA_HOME/bin/java -jar $JAVA_OPTS $DM_JAR_PATH $OPTIONS"
echo "CMD=$CMD"

eval $CMD
