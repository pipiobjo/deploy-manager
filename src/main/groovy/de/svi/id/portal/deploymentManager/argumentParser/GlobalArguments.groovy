package de.svi.id.portal.deploymentManager.argumentParser

import java.nio.file.Path

@Singleton
public class GlobalArguments{

    def String service,releaseURL,stageIdentifier
    def Boolean skipDownloads;
    def Path tempDir, realeaseFilePath, unzippedArtifactFolder, fallbackConfigPath
    def File artifactFolder;
}