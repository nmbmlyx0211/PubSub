# Spring 2018 CS602 Final Project
Group Members: Jianchao Wang, Yinxia Li


- Technical
   - ThreadPoolExecutor
   - ConcurrentHashMap
   - Synchronized Method
   
- Functions
   - Login/Logout
   - Multiple topic subscription
   - Multiple Subscriber/Publisher
   - Data Endurance
   - Real time message delivery

- Critique
   - Exception Handling insufficient
   - Rudimentary UI
   - Scalability? 


Implement a simple publish-subscribe system using TCP sockets.  A publisher client connect to the server and publish messages related to different topics. Each published message has a time stamp associated with it. A subscriber client will connect to the server with a subscriber id and then subscribe to one or more topics at different points in time. The server needs to send the messages published on a topic to all clients that subscribed to that topic in the order they were published. A simple Swing GUI is implemented.