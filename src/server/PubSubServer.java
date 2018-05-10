package server;



import common.ClientServerConnection;
import common.TopicMessage;
import subscriber.Subscriber;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PubSubServer implements ServerController {

    private final ServerConnectionManager connManager;

    private ConcurrentHashMap<String, Set<String>> subscriptions = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String, ClientServerConnection> clientServerConnectionMap = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String, List<TopicMessage>> publishedTopicMessages = new ConcurrentHashMap<>();

    private int publisherId = 0;

    // keep subscriber id-connection bindings, data structures for subscriptions, messages etc

    public PubSubServer(ServerConnectionManager connManager) {
        this.connManager = connManager;
        this.connManager.setServerController(this);
    }

    public synchronized int nextPublisherId()
    {
        return ++publisherId;
    }

    @Override
    public boolean addConnection(String subscriberId, ClientServerConnection connection) {
        if(clientServerConnectionMap.get(subscriberId) == null)
        {
            clientServerConnectionMap.put(subscriberId, connection);
            return true;
        }

        return false;
    }

    @Override
    public boolean removeConnection(String subsriberId) {
    	return clientServerConnectionMap.remove(subsriberId) != null;
    }

    @Override
    public ClientServerConnection getSubscriberConnection(String subscriberId) {
        return clientServerConnectionMap.get(subscriberId);
    }

    @Override
    public Set<String> getAllPublishedTopics()
    {
        return publishedTopicMessages.keySet();
    }

    @Override
    public Set<String> getSubscribedTopics(String subscriberId) {
        Set<String> subscribedTopics = new HashSet<String>();

        Iterator<Map.Entry<String, Set<String>>> subscriptionIterator = subscriptions.entrySet().iterator();
        while(subscriptionIterator.hasNext())
        {
            Map.Entry<String, Set<String>> entry = subscriptionIterator.next();
            if(entry.getValue().contains(subscriberId))
            {
                subscribedTopics.add(entry.getKey());
            }
        }

        return subscribedTopics;
    }

    @Override
    public boolean addTopic(String subscriberId, String topic)
    {
        Set<String> subscriberList = subscriptions.get(topic);
        if(subscriberList == null)
        {
            subscriberList = new HashSet<String>();
        }


        boolean added = subscriberList.add(subscriberId);
        subscriptions.put(topic, subscriberList);

        return added;
    }

    @Override
    public boolean removeTopic(String subscriberId, String topic)
    {
        Set<String> subscriberList = subscriptions.get(topic);
        if(subscriberList == null)
        {
            return false;
        }
        else
        {
            subscriberList.remove(subscriberId);
            subscriptions.put(topic, subscriberList);

            return true;
        }

    }

    @Override
    public void onReceivedTopicMessage(TopicMessage message)
    {
        List<TopicMessage> messageList = publishedTopicMessages.get(message.getTopic());
        if (messageList == null)
        {
            messageList = new ArrayList<TopicMessage>();
        }

        messageList.add(message);
        publishedTopicMessages.put(message.getTopic(), messageList);

        System.out.println("Received topic message:" + message.toString());


        Set<String> subscriberIds = subscriptions.get(message.getTopic());

        if (subscriberIds != null)
        {
            List<TopicMessage> messagesToBeSent = new ArrayList<TopicMessage>();
        messagesToBeSent.add(message);

        for (String id : subscriberIds)
        {
            ClientServerConnection clientServerConnection = getSubscriberConnection(id);
            clientServerConnection.sendTopicMessages(messagesToBeSent);
        }
    }

    }

    public static void main(String[] args)
    {
        ServerConnectionManager connMgr = null;
        try 
        {
            connMgr = new PubSubSeverConnectionManagerSocImpl(args[0], Integer.parseInt(args[1]));
            PubSubServer server = new PubSubServer(connMgr);
            connMgr.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (connMgr != null)
            {
                connMgr.stop();
            }
        }

    }

}
