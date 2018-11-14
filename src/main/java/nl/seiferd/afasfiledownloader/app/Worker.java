package nl.seiferd.afasfiledownloader.app;

import nl.seiferd.afasfiledownloader.client.CustomConnectorClient;
import nl.seiferd.afasfiledownloader.client.FileConnectorClient;
import nl.seiferd.afasfiledownloader.file.FileWriter;
import nl.seiferd.afasfiledownloader.model.AfasFile;
import nl.seiferd.afasfiledownloader.model.Batch;
import nl.seiferd.afasfiledownloader.model.Config;
import nl.seiferd.afasfiledownloader.model.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;

class Worker {

    private static final Logger LOG = LoggerFactory.getLogger(Worker.class);

    private static final int THROTTLE = 1;
    private static final int TIMEOUT = 5;

    private final Config config;

    Worker(Config config) {
        this.config = config;
    }

    void start(){
        long skip = config.getStartingPoint();
        while (skip < config.getNrOfItems()) {
            LOG.info("Going to prepare batch {} - {}", skip, (skip + config.getItemsToTake()));
            doWork(config, skip += config.getItemsToTake());
        }
    }

    private void doWork(final Config config, long skip) {
        try {
            final FileWriter fileWriter = fileWriter(config);
            final CustomConnectorClient connectorClient = connectorClient(config);
            final Batch result = connectorClient.getBatch(skip, config.getItemsToTake());

            getFilesForBatch(config, result.getRows())
                    .doOnNext(fileWriter::writeToFile)
                    .blockLast(Duration.ofMinutes(TIMEOUT));
        } catch (final Exception error) {
            LOG.error("Unrecoverable error occurred, skipping to next batch", error);
        }
    }

    private Flux<AfasFile> getFilesForBatch(final Config config, final List<Row> rows) {
        return Flux.fromStream(rows.stream())
                .delayElements(Duration.ofSeconds(THROTTLE))
                .flatMap(row -> fileConnectorClient(config).downloadFile(row));
    }

    private FileWriter fileWriter(final Config config) {
        return new FileWriter(config.getOutputDirectory());
    }

    private CustomConnectorClient connectorClient(final Config config) {
        return new CustomConnectorClient(config.getAbboId(), config.getTokenBase64(), config.getConnectorName());
    }

    private FileConnectorClient fileConnectorClient(final Config config) {
        return new FileConnectorClient(config.getAbboId(), config.getTokenBase64());
    }
}
