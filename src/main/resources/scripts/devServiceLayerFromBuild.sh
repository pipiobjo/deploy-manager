#!/bin/bash
#set -x

DIR=$(pwd)

# build actual dist
## maybe git pull before? 
cd "/d/developerbase/workspaces/ID-Portal-Release"
./gradlew assembleDist -PassemblyVariant=FULL

cd $DIR



# build actual deploymentManager
echo "dir=$(pwd)"
./gradlew clean build


# set java home
JAVA_HOME="\"/c/Program Files/Java/jdk1.8.0_111/jre\""

# set path to deploymentManager.jar
DM_JAR_PATH="/d/developerbase/workspaces/ID-Portal-DeployManager/build/libs/portal-id-deploymentManager-*.jar"


# set params
OPTIONS="--service=springBoot"
OPTIONS="$OPTIONS --stageIdentifier=local"
OPTIONS="$OPTIONS --realeaseFilePath=/d/developerbase/workspaces/ID-Portal-Release/build/distributions/ID-Portal-Release.zip" 
OPTIONS="$OPTIONS --stageIdentifier=local"
OPTIONS="$OPTIONS --springBootHome=/d/developerbase/workspaces/ID-Portal-ServiceLayer/innendienst-service-layer/build/dm"



#Build command
CMD="$JAVA_HOME/bin/java -jar $DM_JAR_PATH $OPTIONS" 
echo "CMD=$CMD"

eval $CMD

