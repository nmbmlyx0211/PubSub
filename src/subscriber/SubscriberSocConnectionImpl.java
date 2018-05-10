package subscriber;

import common.ClientServerConnection;
import common.ServerCommandMessage;
import common.TopicMessage;
import server.ServerConnectionManager;
import server.ServerController;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SubscriberSocConnectionImpl implements ClientServerConnection
{
    private final Socket connSock;
    private final ServerController controller;
    private final ServerConnectionManager connMgr;
    private final String subscriberId;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    public SubscriberSocConnectionImpl(String subscriberId, Socket clientSock, ServerController controller, ServerConnectionManager connMgr, ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream) throws IOException
    {
        this.connSock = clientSock;
        this.subscriberId = subscriberId;
        this.controller = controller;
        this.connMgr = connMgr;
        this.objectInputStream = objectInputStream;
        this.objectOutputStream = objectOutputStream;
    }

    @Override
    public void start()
    {
        try
        {
            if(objectOutputStream == null)
            {
                objectOutputStream = new ObjectOutputStream(connSock.getOutputStream());
            }

            ServerCommandMessage serverCommandMessage = new ServerCommandMessage(ServerCommandMessage.MessageType.SUBSCRIBER_LOGIN_RESP_OK, "");
            objectOutputStream.writeObject(serverCommandMessage);
            objectOutputStream.flush();

            try
            {

                while (true)
                {

                    serverCommandMessage = (ServerCommandMessage)objectInputStream.readObject();
                    System.out.println(serverCommandMessage.getMessage());

                    switch(serverCommandMessage.getMsgType())
                    {
                        case GET_PUBLISHED_TOPICS_REQ:
                            Set<String> publishedTopic = controller.getAllPublishedTopics();
                            serverCommandMessage = new ServerCommandMessage(ServerCommandMessage.MessageType.GET_PUBLISHED_TOPICS_RESP, new ArrayList<String>(publishedTopic));
                            objectOutputStream.writeObject(serverCommandMessage);
                            objectOutputStream.flush();

                            break;

                        case SUBSCRIBE_TOPIC_REQ:
                            boolean subscribed = controller.addTopic(this.subscriberId, serverCommandMessage.getMessage().toString());
                            serverCommandMessage = new ServerCommandMessage(ServerCommandMessage.MessageType.SUBSCRIBE_TOPIC_RESP, String.valueOf(subscribed));
                            objectOutputStream.writeObject(serverCommandMessage);
                            objectOutputStream.flush();

                            break;

                        case GET_SUBSCRIBED_TOPICS_REQ:
                            Set<String> subscribedTopics = controller.getSubscribedTopics(this.subscriberId);

                            serverCommandMessage = new ServerCommandMessage(ServerCommandMessage.MessageType.GET_SUBSCRIBED_TOPICS_RESP, new ArrayList<String>(subscribedTopics));
                            objectOutputStream.writeObject(serverCommandMessage);
                            objectOutputStream.flush();

                            break;

                        case UNSUBSCRIBE_TOPIC_REQ:
                            boolean unsubscribed = controller.removeTopic(this.subscriberId, serverCommandMessage.getMessage().toString());
                            serverCommandMessage = new ServerCommandMessage(ServerCommandMessage.MessageType.UNSUBSCRIBE_TOPIC_RESP, String.valueOf(unsubscribed));
                            objectOutputStream.writeObject(serverCommandMessage);
                            objectOutputStream.flush();

                            break;
                    }
                }
            }
            catch (Exception ex)
            {
                System.out.println("Exception thrown: " + ex.getMessage() + ", stopping current connection");
                this.stop();
            }
            finally
            {
                if(objectInputStream != null)
                {
                    //objectInputStream.close();
                }

                if(objectOutputStream != null)
                {
                    //objectOutputStream.close();
                }
            }
        }
        catch(Exception exs)
        {
            exs.printStackTrace();
            this.stop();
        }
    }

    @Override
    public boolean isAlive()
    {
        return connSock != null && connSock.isConnected();
    }

    @Override
    public boolean login(String clientId)
    {
        return controller.addConnection(clientId, this);
    }

    @Override
    public Set<String> getPublishedTopics()
    {
        return controller.getAllPublishedTopics();
    }

    @Override
    public Set<String> getSubscribedTopics()
    {
        return controller.getSubscribedTopics(String.valueOf(this.subscriberId));
    }

    @Override
    public boolean subscribe(String topic)
    {
        return controller.addTopic(String.valueOf(this.subscriberId), topic);
    }

    @Override
    public boolean unsubscribe(String topic)
    {
        return controller.removeTopic(String.valueOf(this.subscriberId), topic);
    }

    @Override
    public void sendTopicMessages(List<TopicMessage> topicMessages)
    {
        for(TopicMessage message : topicMessages)
        {
            try
            {
                objectOutputStream.writeObject(message);
                objectOutputStream.flush();
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }

        }
    }

    @Override
    public void stop()
    {
    	controller.removeConnection(subscriberId);
    }

}
