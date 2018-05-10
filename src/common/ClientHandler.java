package common;

import server.PublisherSocConnectionImpl;
import server.ServerConnectionManager;
import server.ServerController;
import subscriber.ErrorMessage;
import subscriber.SubscriberSocConnectionImpl;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable
{
    private ClientServerConnection clientServerConnection;

    private Socket clientSocket;

    private ServerController controller;

    private ServerConnectionManager connMgr;

    private ObjectInputStream objectInputStream;

    private ObjectOutputStream objectOutputStream;

    public ClientHandler(Socket clientSocket, ServerController controller, ServerConnectionManager connMgr)
    {
        this.clientSocket = clientSocket;
        this.controller = controller;
        this.connMgr = connMgr;
    }

    @Override
    public void run()
    {
        try
        {
            //Check the message from Client
            objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
            Object object = objectInputStream.readObject();

            if (object instanceof ServerCommandMessage)
            {
                ServerCommandMessage serverCommandMessage = (ServerCommandMessage) object;
                if (serverCommandMessage.getMsgType().equals(ServerCommandMessage.MessageType.PUBLISHER_LOGIN_REQ))
                {

                    System.out.println("Publisher client Identified....");

                    this.clientServerConnection = new PublisherSocConnectionImpl(controller.nextPublisherId(), clientSocket, controller, connMgr);
                    clientServerConnection.start();
                }
                else
                {
                    switch (serverCommandMessage.getMsgType())
                    {
                        case SUBSCRIBER_LOGIN_REQ:
                        {
                            String clientID = serverCommandMessage.getMessage().toString();
                            System.out.println("Subscriber client Identified....");
                            this.clientServerConnection = new SubscriberSocConnectionImpl(clientID, clientSocket, controller, connMgr, objectOutputStream, objectInputStream);
                            if (clientServerConnection.login(clientID))
                            {
                                clientServerConnection.start();
                            }
                            else
                            {
                                serverCommandMessage = new ServerCommandMessage(ServerCommandMessage.MessageType.SUBSCRIBER_LOGIN_RESP_ERR, "Already Connected");
                                objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                                objectOutputStream.writeObject(serverCommandMessage);
                                objectOutputStream.flush();

                            }
                        }
                        break;

                        default:
                            System.out.println(ErrorMessage.SERVER_INTERNAL_ERROR);
                    }
                }
            }
        }
        catch(Exception ex)
        {

        }
    }
}
