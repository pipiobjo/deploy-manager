#!/bin/bash
echo "Starting DeploymentManager - Elastic Search Deployment"


# set java home
JAVA_HOME="/usr/local/java"

# set path to deploymentManager.jar
DM_JAR_PATH="/opt/tomcat01/deploymentManager/run/portal-id-deploymentManager-*.jar"


JAVA_OPTS=""


#Getting Release from temp path
#RELEASE_PATH=/tmp/innendienstRelease
RELEASE_PATH=/opt/tomcat01/deploymentManager/releases


# set params
OPTIONS="--service=elasticSearch"
OPTIONS="$OPTIONS --stageIdentifier=rt1"
OPTIONS="$OPTIONS --realeaseFilePath=$RELEASE_PATH/ID-Portal-Release.zip"
OPTIONS="$OPTIONS --elasticSearchHome=/opt/tomcat01/elasticsearch"
OPTIONS="$OPTIONS --fallbackConfigPath=/opt/tomcat01/elasticsearchFallbackConfig"
OPTIONS="$OPTIONS --temp=/opt/tomcat01/deploymentManager/tmp"



#Build command
CMD="$JAVA_HOME/bin/java -jar $JAVA_OPTS $DM_JAR_PATH $OPTIONS"
echo "CMD=$CMD"

eval $CMD

echo "End DeploymentManager - Elastic Search Deployment"
