#!/bin/bash
set -x

# set java home
JAVA_HOME=/c/Program\ Files/Java/jdk1.8.0_111

IDPortalReleaseDIR="/d/developerbase/workspaces/ID-Portal-Release"
IDPortalDeployManagerDIR="/d/developerbase/workspaces/ID-Portal-DeployManager"


## if script file exisgts call it to set env variables 
#~/./deploymentManager-setenv.sh
USER_DM_CONFIG_FILE=~/deploymentManager-setenv.sh
if [ -f "$USER_DM_CONFIG_FILE" ]; then
   . ./$USER_DM_CONFIG_FILE
fi
JAVA_CMD="$JAVA_HOME"/bin/java

DIR=${pwd}

# build actual dist
## maybe git pull before? 
echo "building release ..."
cd $IDPortalReleaseDIR
./gradlew assembleDist -PassemblyVariant=FULL


echo "building deploymentManager"
#cd "/d/developerbase/workspaces/ID-Portal-DeployManager"
#./gradlew build 
cd $IDPortalDeployManagerDIR
./gradlew build

cd $DIR


echo "starting deploymentManager"
# set path to deploymentManager.jar
## TODO unzip deploymentManager.jar from zip and set JAR Path


DM_JAR_PATH="$IDPortalDeployManagerDIR/build/libs/portal-id-deploymentManager-*.jar"


# set params
OPTIONS="--service=liferay"
OPTIONS="$OPTIONS --stageIdentifier=local"
OPTIONS="$OPTIONS --liferayDeployMode=incremental"
OPTIONS="$OPTIONS --realeaseFilePath=/d/developerbase/workspaces/ID-Portal-Release/build/distributions/ID-Portal-Release.zip" 
OPTIONS="$OPTIONS --liferayHome=/d/developerbase/workspaces/ID-Portal-Workspace/bundles"
OPTIONS="$OPTIONS --fallbackConfigPath=/d/developerbase/workspaces/ID-Portal-Workspace/myLiferayConfig" 


#"$JAVA_HOME"/bin/java -version

#Build command
CMD='"$JAVA_CMD" -jar $DM_JAR_PATH $OPTIONS'
#echo "CMD=$CMD"

eval $CMD

