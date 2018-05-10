package subscriber;


import common.ServerCommandMessage;
import common.TopicMessage;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class Subscriber implements SubscriberUIController
{

    public static String commands = "Use following commands\n(a)\tType @topics to get list of all available topics\n" +
            "(b)\tType @subscribed to get list of topics already subscribed\n" +
            "(c)\tType @subscribe  <topic name> to subscribe to a topic\n" +
            "(d)\tType @unsubscribe  <topic name> to unsubscribe to a topic\n" +
            "(e)\tType @help to display command menu\n";

    private SubscriberUserInterface userInterface;
    private Socket cs;
    private DataInputStream dis;
    private DataOutputStream dos;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private String clientId;

    public Subscriber(String serverHost, int port, SubscriberUserInterface userInterface)
    {
        this.userInterface = userInterface;
        this.userInterface.setUIController(this);

        try
        {
            this.cs = new Socket(serverHost, port);
            userInterface.setServerStatus(ErrorMessage.SERVER_UP);

            this.userInterface.initiateLogin();
        } catch (Exception ne)
        {
            userInterface.setServerStatus(ErrorMessage.SERVER_DOWN);
            System.exit(-1);
        }
    }


    @Override
    public void getPublishedTopics()
    {
        try
        {
            ServerCommandMessage serverCommandMessage = new ServerCommandMessage(ServerCommandMessage.MessageType.GET_PUBLISHED_TOPICS_REQ, "");
            objectOutputStream.writeObject(serverCommandMessage);
            objectOutputStream.flush();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public void getSubscribedTopics()
    {
        try
        {
            ServerCommandMessage serverCommandMessage = new ServerCommandMessage(ServerCommandMessage.MessageType.GET_SUBSCRIBED_TOPICS_REQ, "");
            objectOutputStream.writeObject(serverCommandMessage);
            objectOutputStream.flush();

        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void subscribeTopic(String topic)
    {
        try
        {
            ServerCommandMessage serverCommandMessage = new ServerCommandMessage(ServerCommandMessage.MessageType.SUBSCRIBE_TOPIC_REQ, topic);
            objectOutputStream.writeObject(serverCommandMessage);
            objectOutputStream.flush();


        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void unsubscribeTopic(String topic)
    {
        try
        {
            ServerCommandMessage serverCommandMessage = new ServerCommandMessage(ServerCommandMessage.MessageType.UNSUBSCRIBE_TOPIC_REQ, topic);
            objectOutputStream.writeObject(serverCommandMessage);
            objectOutputStream.flush();

        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onReceivedTopicMessage(TopicMessage topicMessage)
    {

        userInterface.updateTextArea(topicMessage.toString());
    }


    //for cmd operation
    public void processCommand(String value)
    {
            //System.out.print("Enter command:");
            //String value = scanner.nextLine();
            if(value.equals("@topics"))
            {
                getPublishedTopics();
            }
            else if(value.equals("@subscribed"))
            {
                getSubscribedTopics();
            }
            else if(value.startsWith("@subscribe"))
            {
                String topic = value.substring("@subscribe".length()).trim();
                System.out.println("Selected topic is : "+ topic);

                subscribeTopic(topic);
            }
            else if(value.startsWith("@unsubscribe"))
            {
                String topic = value.substring("@unsubscribe".length()).trim();
                System.out.println("Selected topic is : "+ topic);

                unsubscribeTopic(topic);
            }
            else if(value.equals("@help"))
            {

                userInterface.updateTextArea(Subscriber.commands + "\n");
            }

    }

    private void processIncomingMessages()
    {

        while(true)
        {
            try
            {
                Object message = objectInputStream.readObject();
                if (message instanceof TopicMessage)
                {
                    onReceivedTopicMessage((TopicMessage) message);
                }
                else if(message instanceof ServerCommandMessage)
                {
                    ServerCommandMessage serverCommandMessage = (ServerCommandMessage) message;
                    switch (serverCommandMessage.getMsgType())
                    {
                        case GET_PUBLISHED_TOPICS_RESP:
                            List<String> publishedTopics = (List<String>) serverCommandMessage.getMessage();
                            Set<String> publishedTopicSet = new HashSet<String>();
                            publishedTopicSet.addAll(publishedTopics);
                            userInterface.setPublishedTopics(publishedTopicSet);
                            break;

                        case GET_SUBSCRIBED_TOPICS_RESP:
                            List<String> subscribedTopics = (List<String>) serverCommandMessage.getMessage();
                            Set<String> subscribedTopicsSet = new HashSet<String>();
                            subscribedTopicsSet.addAll(subscribedTopics);
                            userInterface.setSubscribedTopics(subscribedTopicsSet);
                            break;

                    }

                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

    }

    public void login(String subscriberId)
    {
        try
        {
            objectOutputStream = new ObjectOutputStream(cs.getOutputStream());

            ServerCommandMessage serverCommandMessage = new ServerCommandMessage(ServerCommandMessage.MessageType.SUBSCRIBER_LOGIN_REQ, subscriberId);
            objectOutputStream.writeObject(serverCommandMessage);
            objectOutputStream.flush();

            System.out.println("Subscriber connected to server " + cs.getRemoteSocketAddress().toString());

            objectInputStream = new ObjectInputStream(cs.getInputStream());
            serverCommandMessage = (ServerCommandMessage) objectInputStream.readObject();
            if (serverCommandMessage.getMsgType().equals(ServerCommandMessage.MessageType.SUBSCRIBER_LOGIN_RESP_ERR))
            {
                userInterface.setServerStatus(ErrorMessage.ALREADY_CONNECTED);
                System.exit(-1);
            }

            this.clientId = subscriberId;
            userInterface.startUser(clientId);

            communicate();

           // userInterface
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


    }

    //Continue the communication
    public void communicate() throws IOException, ClassNotFoundException
    {

        ExecutorService exService = Executors.newFixedThreadPool(1);

        exService.execute(new MessageTask());

    }

    public static void main(String[] args)
    {
        try
        {
            Subscriber subscriber = new Subscriber(args[0], Integer.parseInt(args[1]), new SubscriberUI());

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    class MessageTask implements Runnable
    {
        public void run()
        {
            processIncomingMessages();
        }
    }
}
