package nl.seiferd.afasfiledownloader.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.seiferd.afasfiledownloader.model.Config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigExtractor {

    public Config extractConfig(final Path path) throws IOException {
        if(!Files.exists(path) || !isJsonFile(path)){
            throw new IllegalArgumentException("Path should be pointing to valid .json file");
        }

        return new ObjectMapper().readerFor(Config.class).readValue(Files.newBufferedReader(path));
    }

    private boolean isJsonFile(final Path path){
        return path.toString().contains(".json");
    }
}
