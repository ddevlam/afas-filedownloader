package nl.seiferd.afasfiledownloader.file;

import nl.seiferd.afasfiledownloader.model.AfasFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;

import java.io.FileOutputStream;
import java.io.OutputStream;

public class FileWriter {

    private static final Logger LOG = LoggerFactory.getLogger(FileWriter.class);

    private final String directory;

    public FileWriter(final String directory) {
        this.directory = directory;
    }

    public void writeToFile(final AfasFile afasFile) {
        if (StringUtils.isEmpty(afasFile.getFiledata())) {
            LOG.warn("No content found for {}, likely not found", afasFile.getFilename());
            return;
        }

        try (OutputStream stream = new FileOutputStream(getPath(afasFile))) {
            stream.write(Base64Utils.decodeFromString(afasFile.getFiledata()));
        } catch (Exception e) {
            LOG.error("Unable to write: {}", e.getMessage());
        } finally {
            LOG.info("Done processing {}", afasFile.getFilename());
        }
    }

    private String getPath(final AfasFile afasFile) {
        final String preFixedFileName = String.format("%d_%s", afasFile.getDossieritem(), afasFile.getFilename());
        return String.format("%s/%s", directory, preFixedFileName);
    }
}
