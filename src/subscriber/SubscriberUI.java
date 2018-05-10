package subscriber;

import common.TopicMessage;
import jdk.nashorn.internal.scripts.JO;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Utilities;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Set;

public class SubscriberUI implements SubscriberUserInterface
{
    private static String DEFAULT_ITEM = "<Select>";

    private JFrame jframe;
    private JPanel mainContainerPanel;
    private JLabel lblSubcriberID;
    private JPanel commandAreaPanel;
    private JLabel lblSubscriberIDValue;
    private JTextArea txtCommandArea;
    private JLabel lblAvailableTopics;
    private JComboBox cmbPublishedTopics;
    private JLabel lblSubscribedTopics;
    private JComboBox cmbSubscribedTopics;
    private JButton btnSubscribe;
    private JButton btnUnsubscribe;
    private JButton btnReload;

    private SubscriberUIController subscriberUIController;

    public SubscriberUI()
    {
        jframe = new JFrame();
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setContentPane(mainContainerPanel);
        jframe.setSize(new Dimension(400, 300));
        jframe.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                //subscriberUIController.
                super.windowClosing(e);
            }
        });

        btnReload.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                subscriberUIController.getPublishedTopics();
                subscriberUIController.getSubscribedTopics();
            }
        });

        btnSubscribe.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                subscribe(cmbPublishedTopics.getSelectedItem().toString(), true);
            }
        });

        btnUnsubscribe.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                subscribe(cmbSubscribedTopics.getSelectedItem().toString(), false);
            }
        });

        cmbSubscribedTopics.addItemListener(new ItemListener()
        {
            @Override
            public void itemStateChanged(ItemEvent e)
            {
                if(cmbSubscribedTopics.getItemCount() > 0 && cmbSubscribedTopics.getSelectedItem().equals(DEFAULT_ITEM))
                {
                    btnUnsubscribe.setEnabled(false);
                }
                else
                {
                    btnUnsubscribe.setEnabled(true);
                }
            }
        });

        cmbPublishedTopics.addItemListener(new ItemListener()
        {
            @Override
            public void itemStateChanged(ItemEvent e)
            {
                if(cmbPublishedTopics.getItemCount() > 0 && cmbPublishedTopics.getSelectedItem().equals(DEFAULT_ITEM))
                {
                    btnSubscribe.setEnabled(false);
                }
                else
                {
                    btnSubscribe.setEnabled(true);
                }
            }
        });

        jframe.setVisible(true);

        initComponents();
    }


    private void initComponents()
    {
        btnUnsubscribe.setEnabled(cmbSubscribedTopics.getItemCount() > 0);
        btnSubscribe.setEnabled(cmbPublishedTopics.getItemCount() > 0);
    }

    @Override
    public void initiateLogin()
    {
        String subscriberId = JOptionPane.showInputDialog(jframe, "What's subscriber ID?");

        this.subscriberUIController.login(subscriberId);
    }

    @Override
    public void setUIController(SubscriberUIController controller)
    {
        this.subscriberUIController = controller;
    }

    @Override
    public void setServerStatus(ErrorMessage errMsg)
    {
        System.out.println(errMsg.toString());
        if(errMsg.getMessageType().equals(ErrorMessage.SERVER_DOWN))
        {
            JOptionPane.showConfirmDialog(this.jframe, errMsg.toString(),"Error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public String getSubscriberId()
    {
        return null;
    }

    @Override
    public void setPublishedTopics(Set<String> topics)
    {
        cmbPublishedTopics.removeAllItems();

        cmbPublishedTopics.addItem(DEFAULT_ITEM);
        for(String topic : topics)
        {
            cmbPublishedTopics.addItem(topic);
        }
    }

    @Override
    public void setSubscribedTopics(Set<String> topics)
    {
        cmbSubscribedTopics.removeAllItems();

        cmbSubscribedTopics.addItem(DEFAULT_ITEM);
        for(String topic : topics)
        {
            cmbSubscribedTopics.addItem(topic);
        }
    }

    @Override
    public void startUser(String user)
    {
        this.lblSubscriberIDValue.setText(user);
    }

    @Override
    public void setPublishedMessages(List<TopicMessage> messages)
    {
        for(TopicMessage message : messages)
        {
            this.txtCommandArea.append(message.toString());
            this.txtCommandArea.append("\n");
        }
    }

    @Override
    public void updateTextArea(String text)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                txtCommandArea.append("\n");
                txtCommandArea.append(text);
                txtCommandArea.append("\n");
            }
        });

    }

    /**
     *  Subscribe to the given topic or Unsubscribe from the topic.
     * @param topic
     * @param subscribe if it is true subscribe to that topic, otherwise unsubscribe from that topic.
     * @return
     */
    private void subscribe(String topic, boolean subscribe)
    {
        if(subscribe)
        {
            subscriberUIController.subscribeTopic(topic);
        }
        else
        {
            subscriberUIController.unsubscribeTopic(topic);
        }

        subscriberUIController.getSubscribedTopics();

    }
}
