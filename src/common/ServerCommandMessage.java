package common;


import java.io.Serializable;

public class ServerCommandMessage implements Serializable{

    public enum MessageType {
        SUBSCRIBER_LOGIN_REQ,
        SUBSCRIBER_LOGIN_RESP_OK,
        SUBSCRIBER_LOGIN_RESP_ERR,
        GET_PUBLISHED_TOPICS_REQ,
        SUBSCRIBE_TOPIC_REQ,
        UNSUBSCRIBE_TOPIC_REQ,
        UNSUBSCRIBE_TOPIC_RESP,
        SUBSCRIBE_TOPIC_RESP,
        GET_PUBLISHED_TOPICS_RESP,
        GET_SUBSCRIBED_TOPICS_REQ,
        GET_SUBSCRIBED_TOPICS_RESP,
        PUBLISHER_LOGIN_REQ,
        SERVER_INTERNAL_ERR;
    }

    private final MessageType msgType;
    private final Object message;

    public ServerCommandMessage(MessageType msgType, Object message) {
        this.msgType = msgType;
        this.message = message;
    }

    public MessageType getMsgType(){
        return  msgType;
    }

    public Object getMessage()
    {
        return message;
    }
}
