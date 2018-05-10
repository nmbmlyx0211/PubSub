package subscriber;



import common.TopicMessage;

import java.util.List;
import java.util.Set;

public interface SubscriberUserInterface {

    /**
     * Initiate the subscriber login
     */
    void initiateLogin();

    /**
     * Set controller to invoke for user inputs
     * @param controller
     */
    void setUIController(SubscriberUIController controller);

    /**
     * Display server status in UI
     * @param errMsg
     */
    void setServerStatus(ErrorMessage errMsg);

    /**
     * Get subscriber id input from UI
     * @return
     */
    String getSubscriberId();

    /**
     * Display all topics publsihed
     * @param topics
     */
    void setPublishedTopics(Set<String> topics);

    /**
     * Display topics subscribed by the client
     * @param topics
     */
    void setSubscribedTopics(Set<String> topics);

    /**
     * Start user interface
     * @param user
     */
    void startUser(String user);

    /**
     * Set published messages received from the server
     * @param messages
     */
    void setPublishedMessages(List<TopicMessage> messages);

    void updateTextArea(String text);

}
