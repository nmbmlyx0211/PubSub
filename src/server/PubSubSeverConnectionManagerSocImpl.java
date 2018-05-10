package server;

import common.ClientHandler;
import common.ClientServerConnection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class PubSubSeverConnectionManagerSocImpl implements ServerConnectionManager
{
    private ServerController serverController;

    private ServerSocket serverSocket;

    private int clientNumber = 0;

    public PubSubSeverConnectionManagerSocImpl(String hostName, int port) throws Exception
    {
        this.serverSocket = new ServerSocket(port);
    }

    @Override
    public void start() throws IOException
    {

        // client request
        while (true)
        {
            Socket socket = null;

            try
            {
                // socket object to receive incoming client requests
                socket = serverSocket.accept();

                System.out.println("A new client is connected : " + socket);
                Thread clientThread = new Thread(new ClientHandler(socket,serverController, this));
                clientThread.start();

            }
            catch (Exception e)
            {
                try
                {
                    socket.close();
                } catch (Exception e1)
                {
                    e1.printStackTrace();
                }

                e.printStackTrace();
            }
        }


    }


    @Override
    public void setServerController(ServerController controller)
    {
        this.serverController = controller;
    }

    @Override
    public ClientServerConnection createSubscriberConnection(Object connHandle)
    {
        return null;
    }

    @Override
    public ClientServerConnection createPublisherConnection(Object connHandle)
    {
        return null;
    }

    @Override
    public void removeConnection(int connectionId)
    {

    }

    @Override
    public void stop()
    {

    }
}
