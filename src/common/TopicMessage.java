package common;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class TopicMessage implements Serializable
{

    private static final ThreadLocal<SimpleDateFormat> df = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
              return new SimpleDateFormat("yyyy-MM-dd:HH:mm:ss");
        }
    };
    private final String topic;
    private final String message;
    private final Date timeStamp;

    public TopicMessage(String topic, String message, Date timeStamp) {
        this.topic = topic;
        this.message = message;
        this.timeStamp = timeStamp;
    }

    public String getTopic() {
        return topic;
    }

    public String getMessage() {
        return message;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public static TopicMessage get(DataInputStream dis) throws IOException, ParseException {
        String topic = dis.readUTF();
        Date ts = df.get().parse(dis.readUTF());
        String msg = dis.readUTF();
        return new TopicMessage(topic,msg,ts);
    }

    //Serialize and write to socket
    public void put(DataOutputStream dos) throws IOException {
        dos.writeUTF(this.topic);
        dos.writeUTF(df.get().format(this.timeStamp));
        dos.writeUTF(this.message);
    }

    public String toString() {
        return String.format("topic:%s,time:%s,msg:%s", topic, timeStamp.toString(), message);
    }

}
