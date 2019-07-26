package org.opentripplanner.ext.examples.updater;

import com.fasterxml.jackson.databind.JsonNode;
import org.opentripplanner.routing.graph.Graph;
import org.opentripplanner.updater.GraphUpdaterManager;
import org.opentripplanner.updater.GraphWriterRunnable;
import org.opentripplanner.updater.PollingGraphUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class shows an example of how to implement a polling graph updater. Besides implementing the
 * of the interface {@link PollingGraphUpdater}, the updater also needs to be registered in
 * 'router-config.json'.
 * <p/>
 * See the configuration documentation.
 * <p/>
 * This example is suited for polling updaters. For streaming updaters (aka push updaters) it is
 * better to use the GraphUpdater interface directly for this purpose. The class ExampleGraphUpdater
 * shows an example of how to implement this.
 * <p/>
 * Usage example in the file 'router-config.json':
 *
 * <pre>
 * {
 *    "type": "example-polling-updater",
 *    "frequencySec": 60,
 *    "url": "https://api.updater.com/example-polling-updater"
 * }
 * </pre>
 *
 * @see ExampleGraphUpdater
 */
public class ExamplePollingGraphUpdater extends PollingGraphUpdater {

    private static Logger LOG = LoggerFactory.getLogger(ExamplePollingGraphUpdater.class);

    private GraphUpdaterManager updaterManager;

    private String url;

    // Here the updater can be configured using the properties in the file 'Graph.properties'.
    // The property frequencySec is already read and used by the abstract base class.
    @Override
    protected void configurePolling(Graph graph, JsonNode config) throws Exception {
        url = config.path("url").asText();
        LOG.info("Configured example polling updater: frequencySec={} and url={}", pollingPeriodSeconds, url);
    }

    // Here the updater gets to know its parent manager to execute GraphWriterRunnables.
    @Override
    public void setGraphUpdaterManager(GraphUpdaterManager updaterManager) {
        LOG.info("Example polling updater: updater manager is set");
        this.updaterManager = updaterManager;
    }

    // Here the updater can be initialized.
    @Override
    public void setup(Graph graph) {
        LOG.info("Setup example polling updater");
    }

    // This is where the updater thread receives updates and applies them to the graph.
    // This method will be called every frequencySec seconds.
    @Override
    protected void runPolling() {
        LOG.info("Run example polling updater with hashcode: {}", this.hashCode());
        // Execute example graph writer
        updaterManager.execute(new ExampleGraphWriter());
    }

    // Here the updater can cleanup after itself.
    @Override
    public void teardown() {
        LOG.info("Teardown example polling updater");
    }
    
    // This is a private GraphWriterRunnable that can be executed to modify the graph
    private class ExampleGraphWriter implements GraphWriterRunnable {
        @Override
        public void run(Graph graph) {
            LOG.info("ExampleGraphWriter {} runnable is run on the "
                            + "graph writer scheduler.", this.hashCode());
            // Do some writing to the graph here
        }
    }
}
