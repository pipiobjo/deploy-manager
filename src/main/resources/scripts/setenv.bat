@echo off


echo ">>> Setting the environment START ... <<<"

:: #########################################################################################
:: ################ CONFIGURE ENVIRONMENT                 ##################################
:: #########################################################################################

:: Activate/Deaktivate printing of variables here (default=deactivated, set to Y to activate)
set PRINT_ENVIRONMENT_CONFIG=Y

set JAVA_HOME=d:\developerbase\java\jdk1.8.0_121\jre



set LAUFWERK=d:
set WS_PATH=%LAUFWERK%\developerbase\workspaces
set WS_PATH_RELEASE=%WS_PATH%\ID-Portal-Release
set WS_PATH_PORTAL=%WS_PATH%\ID-Portal-Workspace
set WS_PATH_DM=%WS_PATH%\ID-Portal-DeployManager

set DM_JAR_PATH=%WS_PATH_DM%\build\libs

set OPTIONS=--service=liferay
set OPTIONS=%OPTIONS% --stageIdentifier=local
set OPTIONS=%OPTIONS% --liferayDeployMode=full
set OPTIONS=%OPTIONS% --realeaseFilePath=\developerbase\workspaces\ID-Portal-Release\build\distributions\ID-Portal-Release.zip
set OPTIONS=%OPTIONS% --liferayHome=\developerbase\workspaces\ID-Portal-Workspace\bundles_dm
set OPTIONS=%OPTIONS% --fallbackConfigPath=\developerbase\workspaces\ID-Portal-Workspace\myLiferayConfig
set OPTIONS=%OPTIONS% --tempDir4DM=\developerbase\workspaces\ID-Portal-Workspace\tempDir4DM

if %PRINT_ENVIRONMENT_CONFIG%==Y call :printEnvironmentConfig
echo ">>> Setting the environment END ... <<<"
exit /B


:printEnvironmentConfig
echo "-------------------Printing the configuration -- START --"
echo Laufwerk = %LAUFWERK%
echo Workspace-Pfad = %WS_PATH%
echo Workspace-Pfad Release = %WS_PATH_RELEASE%
echo Workspace-Pfad Portal = %WS_PATH_PORTAL%
echo Workspace-Pfad DeployManager = %WS_PATH_DM%
echo Options =  %OPTIONS%
echo "-------------------Printing the configuration -- END --"
exit /B
::echo hier ~dpn0


