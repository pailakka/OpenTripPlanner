package org.opentripplanner.standalone;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.opentripplanner.routing.error.GraphNotFoundException;
import org.opentripplanner.routing.services.GraphService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * This replaces a Spring application context, which OTP originally used.
 * It contains a field referencing each top-level component of an OTP server. This means that supplying a single
 * instance of this object allows accessing any of the other OTP components.
 */
public class OTPServer {

    private static final Logger LOG = LoggerFactory.getLogger(OTPServer.class);

    public final CommandLineParameters params;

    // Core OTP modules
    private final GraphService graphService;

    public OTPServer (CommandLineParameters params, GraphService gs) {
        LOG.info("Wiring up and configuring server.");

        this.params = params;

        // Core OTP modules
        this.graphService = gs;
    }

    /**
     * @return The GraphService. Please use it only when the GraphService itself is necessary. To
     *         get Graph instances, use getRouter().
     */
    public GraphService getGraphService() {
        return graphService;
    }

    /**
     * @return A list of all router IDs currently available.
     */
    public Collection<String> getRouterIds() {
        return graphService.getRouterIds();
    }

    public Router getRouter(String routerId) throws GraphNotFoundException {
        return graphService.getRouter(routerId);
    }

    /**
     * Return an HK2 Binder that injects this specific OTPServer instance into Jersey web resources.
     * This should be registered in the ResourceConfig (Jersey) or Application (JAX-RS) as a singleton.
     * Jersey forces us to use injection to get application context into HTTP method handlers, but in OTP we always
     * just inject this OTPServer instance and grab anything else we need (routers, graphs, application components)
     * from this single object.
     *
     * More on custom injection in Jersey 2:
     * http://jersey.576304.n2.nabble.com/Custom-providers-in-Jersey-2-tp7580699p7580715.html
     */
    AbstractBinder makeBinder() {
        return new AbstractBinder() {
            @Override
            protected void configure() {
                bind(OTPServer.this).to(OTPServer.class);
            }
        };
    }
}
