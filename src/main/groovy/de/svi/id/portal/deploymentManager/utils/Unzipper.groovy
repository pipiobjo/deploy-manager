package de.svi.id.portal.deploymentManager.utils
import java.util.zip.*

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Unzipper{
    Logger logger = LoggerFactory.getLogger(Unzipper.class)
    int BUFFER = 2048;

    public File extractFolder(File myZipFile) throws ZipException, IOException {


        String filePath = myZipFile.getAbsoluteFile()
        String newPath = filePath.substring(0, filePath.length() - 4);

        File destination = new File(newPath)

        unZipAll(myZipFile, destination, false, false)

        return destination;
    }


    public void unZipAll(File source, File destination, boolean ignoreFirstFolderInZip, boolean ignoreZipName) throws IOException {
        int BUFFER = 2048;
        String firstFolderName = "";
        int zipNameLength = source.getName().length()
        String zipName = source.getName().substring(0, zipNameLength - 4);
        ZipFile zip = new ZipFile(source);
        try{
            destination.getParentFile().mkdirs();
            Enumeration zipFileEntries = zip.entries();

            // Process each entry
            while (zipFileEntries.hasMoreElements())
            {
                // grab a zip file entry
                ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
                String currentEntry = entry.getName();

                if("".equals(firstFolderName) &&ignoreFirstFolderInZip){
                    firstFolderName= currentEntry
                }


                if(ignoreFirstFolderInZip){
                    currentEntry = currentEntry.replace(firstFolderName, "")
                }

                if(ignoreZipName && currentEntry.contains(zipName)){
                    currentEntry = currentEntry.replace(zipName, "")
                }
                File destFile = new File(destination, currentEntry);
                File destinationParent = destFile.getParentFile();


                // create the parent directory structure if needed
                destinationParent.mkdirs();

                if (!entry.isDirectory())
                {
                    BufferedInputStream is = null;
                    FileOutputStream fos = null;
                    BufferedOutputStream dest = null;
                    try{
                        is = new BufferedInputStream(zip.getInputStream(entry));
                        int currentByte;
                        // establish buffer for writing file
                        byte[] data = new byte[BUFFER];

                        // write the current file to disk
                        fos = new FileOutputStream(destFile);
                        dest = new BufferedOutputStream(fos, BUFFER);

                        // read and write until last byte is encountered
                        while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
                            dest.write(data, 0, currentByte);
                        }
                    } catch (Exception e){
                        logger.error("unable to extract entry:" + entry.getName(), e);
                        throw e;
                    } finally{
                        if (dest != null){
                            dest.close();
                        }
                        if (fos != null){
                            fos.close();
                        }
                        if (is != null){
                            is.close();
                        }
                    }
                }else{
                    //Create directory
                    if(!destFile.getName().equals(firstFolderName)){
                        destFile.mkdirs();
                    }else{
                        firstFolderName = destFile.getName();
                    }
                }

                if (currentEntry.endsWith(".zip")){
                    // no recursion for now
                    continue
                    // found a zip file, try to extract
                    unZipAll(destFile, destinationParent, false);
                    if(!destFile.delete()){
                        logger.error("Could not delete zip");
                    }
                }
            }
        } catch(Exception e){
            logger.error("Failed to successfully unzip:" + source.getName(), e);
        } finally {
            zip.close();
        }
        logger.info("Done Unzipping:" + source.getName());


    }

}