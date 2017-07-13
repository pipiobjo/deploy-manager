package de.svi.id.portal.deploymentManager.utils;

import java.nio.file.DirectoryStream
import java.nio.file.Files
import java.nio.file.Path
/**
 * 
 * @author bjoern.pipiorke@sv-informatik.de
 *
 */
class WildCardFileSearcher{

    public List<Path> searchForFilesByWildCard(Path baseDir, String pattern){
        List<Path> files=new ArrayList<>();
        DirectoryStream dirStream = Files.newDirectoryStream(baseDir, pattern)
        dirStream.forEach{
            files.add(it);
        }
        dirStream.close()
        return files
    }
}
