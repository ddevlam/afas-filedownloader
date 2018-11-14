package nl.seiferd.afasfiledownloader.app;

import nl.seiferd.afasfiledownloader.file.ConfigExtractor;
import nl.seiferd.afasfiledownloader.model.Config;

import java.io.IOException;
import java.nio.file.Paths;

public class AFASFileDownloaderApp {

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            throw new IllegalArgumentException("The one and only argument should be the file location of the config json file.");
        }

        final Config config = new ConfigExtractor().extractConfig(Paths.get(args[0]));
        new Worker(config).start();
    }
}
