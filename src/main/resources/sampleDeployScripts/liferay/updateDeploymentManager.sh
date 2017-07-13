#!/bin/bash
echo "Updating Deployment Manager"
DM_HOME=/opt/tomcat01/deploymentManager
EXE_DIR=$DM_HOME/run
mkdir -p $EXE_DIR

if find "$EXE_DIR" -mindepth 1 -print -quit | grep -q .; then
    echo "Clear old manager"
    rm -rf $EXE_DIR/*
fi
#Getting Release from temp path
RELEASE_PATH=/opt/tomcat01/deploymentManager/releases

# -j ignore path in zip, during extraction
unzip -j $RELEASE_PATH/ID-Portal-Release*.zip 'ID-Portal-Release/deploymentManager/*' -d $DM_HOME/run 
