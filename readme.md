Dokumentation für den DeploymentManager

# Bauen vom Projekt
gradlew clean build 

# Execution 

## Load Release.zip from Remote - deploy liferay
-s=liferay --stageIdentifier=local --liferayDeployMode=full --liferayHome="D:\developerbase\workspaces\ID-Portal-Workspace\bundles" --releaseURL="http://lxbld01e.gavi-intra.de:8081/nexus/service/local/artifact/maven/redirect?r=portalSnapshots&g=de.svi.liferay.id-portal&a=id-portal-full-assembly&v=LATEST&p=zip"

-Dhttp.proxyHost=proxy1-s.gavi-intra.de
-Dhttp.proxyPort=8080
-Dhttp.proxyUser=hproxyuser
-Dhttp.proxyPassword=rp8400
-Dhttp.nonProxyHosts='*.s-v.loc,*.gavi-intra.de,localhost,127.0.0.1,localhost,lxbld01e,services.ew.svi.loc,services.ft.s-v.loc'
-Dhttps.proxyHost=proxy1-s.gavi-intra.de
-Dhttps.proxyPort=8080
-Dhttps.proxyUser=hproxyuser



## Load Release.zip from local - deploy liferay
-s=liferay --stageIdentifier=local --liferayDeployMode=incremental --liferayHome="D:\developerbase\workspaces\ID-Portal-Workspace2\bundles" --realeaseFilePath="D:\developerbase\workspaces\ID-Portal-Release\build\distributions\ID-Portal-Release.zip"


## Load Release.zip from local - deploy springBoot
--service=springBoot"
--stageIdentifier=local"
--realeaseFilePath=/d/developerbase/workspaces/ID-Portal-Release/build/distributions/ID-Portal-Release.zip"
--stageIdentifier=local"
--springBootHome=/d/developerbase/workspaces/ID-Portal-ServiceLayer/innendienst-service-layer/build/dm
--fallbackConfigPath="D:\developerbase\workspaces\ID-Portal-Workspace\myLiferayConfig"

Weitere Beispiel Dateien im Artefakt unter scripts


# Vorbereitungen für Lifereay Deployment - nur Windows

## shutdown.bat
Um folgende Zeilen nach den Kommentaren erweitern 
```
SET mypath=%~dp0
echo "myPath=%mypath%"
set "CATALINA_HOME=%mypath:\bin\=%"
```

Kontrolle: 
Das Kommand muss von Überall auf dem System ausgeführt werden können, dann ist es richtig konfiguriert
```
D:\developerbase\workspaces\ID-Portal-Workspace\bundles\tomcat-8.0.32\bin\shutdown.bat 
```

Damit die Datei nicht nach jedem Full Deployment

# TODOs
Fehlerhandling, ParameterHandling
handle loglevel by verbose parameter


# Known Problems
Incrementelles Deployment führt kein Neustart des Portals durch, dies hat folgende Konsequenzen
* Properties werden nicht neu eingelesen
* JSPs werden nicht neu compiliert
** Workaround möglich über development mode in web.xml 





# Release 

Für die Entwicklung gibt es momentan nur den Develop- und den Masterbranch. Es gibt kein gesonderten Releasebranch. 
Zum markieren einer Release Version wird ein Tag erstellt, welches den ausgelieferten Zustand im Develop branch markiert. 

Die dafür notwendigen Gradle Tasks kurz im Überblick: 

## Start Release 
```
./gradlew createRelease -Prelease.disableSnapshotsCheck -PnexusUploadEnabled=false
```
* createRelease startet die nach GitFlow Definition - Start Release Phase 
* "-Prelease.disableSnapshotsCheck" keine Snapshot Checks Version ist noch nicht fix. Nach git flow -> Release Start, deshalb kein Problem
* "-PnexusUploadEnabled=false" kein Nexus Upload

## End Release
```
./gradlew pushRelease -Prelease.versionIncrementer=${versionIncrementer} -PnexusUploadEnabled=false
```

* createRelease startet die nach GitFlow Definition - Start Release Phase 
* -Prelease.versionIncrementer=${versionIncrementer} welcher Teil der Versionsnummer soll hochgezählt werden? <br>
    * incrementPatch - increment patch number
    * incrementMinor - increment minor (middle) number 
    * incrementMajor - increment major number
    * incrementMinorIfNotOnRelease - increment patch number if on release branch. Increment minor otherwise 
	* incrementPrerelease - increment pre-release suffix if possible (-rc1 to -rc2). Increment patch otherwise 

* "-PnexusUploadEnabled=false" kein Nexus Upload