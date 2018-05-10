package server;


import common.ClientServerConnection;

import java.io.IOException;

public interface ServerConnectionManager {

    /**
     * Start
     */
    void start() throws IOException;

    /**
     * Set controller for the server connection manager
     * @param controller
     */
    void setServerController(ServerController controller);

    /**
     * Create a subscriber client connection
     * @param connHandle connection implementation object
     * @return
     */
    ClientServerConnection createSubscriberConnection(Object connHandle);

    /**
     * Create a publisher client connection
     * @param connHandle connection implementation object
     * @return
     */
    ClientServerConnection createPublisherConnection(Object connHandle);


    /**
     * Clean up resources associated with a connection
     * @param connectionId
     */
    void removeConnection(int connectionId);

    /**
     * Stop
     */
    void stop();
}
