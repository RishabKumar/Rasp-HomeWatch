# Rasp-HomeWatch
HomeWatch is a project to monitor your home remotely from anywhere that has internet access.

All you will need is to setup your home with cam server and some hardwares.Follow the guide to make your own home made monitoring system.

Things you will need:

A. For setting up the webcam server.
  1. A webcam/ or a Pi Cam
  2. Raspberry Pi (Recommended) /PC/Laptop.
  3. Internet Access. 
  4. Router with UPNP protocol support. (Don't worry if you have no idea about that. Just try the steps.)
Note: You don't need to have static IP or even you don't have to forward the port from your router configuration. The documentation will help to accomplish this via mapping the routing table programmatically.

B. A Client application.
  1. Client application includes: An android phone (currently only android client is available), Web-browser.
  Android client supports Live video streaming. Live picture can be captured with Web-browser any time.

Steps to setup server:
1. Pull homeWatchServer.
2. Set a convenient port number greater than 1000.
3. Connect a configured webcam to the system. 
4. Start project by executing the class "HomeWatchServer.java".

  I have used sarxos for image/video capturing and bitlet upnp library for mapping ports.

5. Now that the server has been started, it's time to test the application on your local  



Documentation is incomplete at the moment.
