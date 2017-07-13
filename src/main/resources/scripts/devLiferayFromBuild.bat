@echo off

echo ">>> Starting script execution <<<"

:: #########################################################################################
:: ################ BUILD ACTUAL DIST                     ##################################
:: #########################################################################################

:: #################################################
:: ######## SET CONFIGURATION          #############
:: #################################################

:: use the config file, if exist
:: if not exist setenv.bat (call :displayHint
::			goto end)
::call setenv.bat

:: ********************** Set the environment configuration  --- START ---

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
set OPTIONS=%OPTIONS% --realeaseFilePath="d:\\developerbase\\workspaces\\ID-Portal-Release\\build\\distributions\\ID-Portal-Release.zip"
set OPTIONS=%OPTIONS% --liferayHome="d:\\developerbase\\workspaces\\ID-Portal-Workspace\\bundles_dm"
set OPTIONS=%OPTIONS% --fallbackConfigPath="d:\\developerbase\\workspaces\\ID-Portal-Workspace\\myLiferayConfig"
set OPTIONS=%OPTIONS% --tempDir4DM="d:\\developerbase\\workspaces\\ID-Portal-Workspace\\tempDir4DM"

if %PRINT_ENVIRONMENT_CONFIG%==Y call :printEnvironmentConfig
goto :executionStart
echo ">>> Setting the environment END ... <<<"

:printEnvironmentConfig
echo "-------------------Printing the configuration -- START --"
echo Laufwerk = %LAUFWERK%
echo Workspace-Pfad = %WS_PATH%
echo Workspace-Pfad Release = %WS_PATH_RELEASE%
echo Workspace-Pfad Portal = %WS_PATH_PORTAL%
echo Workspace-Pfad DeployManager = %WS_PATH_DM%
echo Options =  %OPTIONS%
echo "-------------------Printing the configuration -- END --"

EXIT /B

:: ********************** Set the environment configuration  --- END ---

:: #################################################
:: ######## Build the actual dist      #############
:: #################################################


:: Create the release archive (gradlew assembleDist)

:executionStart
%LAUFWERK%
cd %WS_PATH_RELEASE%

gradlew assembleDist -PassemblyVariant=FULL |pause

:: Create the deployment manager jar file
%LAUFWERK%
cd %WS_PATH_DM%
gradlew build |pause
set DM_JAR_PATH=%WS_PATH_DM%\build\libs
call :findJarFileName


call %JAVA_HOME%\bin\java -jar %DM_JAR_PATH%\%DM_JAR_NAME% %OPTIONS%

EXIT /B
	echo ">>> End of script execution <<<"
EXIT /B

:: Find the name of the Deployment Manager jar File
:: Function: findJarFileName
:: As the name of the jar file depends/differs on version, the name of the actual file is read.
:: If more than one file named "innendienst-service-layer-*-SNAPSHOT.jar" is found, the last one wins. 
:findJarFileName
	echo Searching for the jar file of the deployment manager ...
	For /F %%i in ('dir /B %DM_JAR_PATH%\portal-id-deploymentManager-*-SNAPSHOT.jar') do set DM_JAR_NAME=%%i
	echo The full name of the jar file is %DM_JAR_PATH% %DM_JAR_NAME%
EXIT /B


:: Function: displayHint
:: Display that setup file is missing 
:displayHint
	echo setenv.sh Script missing in the actual folder, please add ... 
EXIT /B

