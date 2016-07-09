# Rasp-HomeWatch
HomeWatch is a project to monitor your home remotely from anywhere that has internet access.

All you will need is to setup your home with cam server and some hardwares.Follow the guide to make your own home made monitoring system.

Things you will need:

A. Setting up the webcam server.
  1. A webcam/ or a Pi Cam
  2. Raspberry Pi (Recommended) /PC/Laptop.
  3. Internet Access. 
  4. Router with UPNP protocol support. (Don't worry if you have no idea about that. Just try the steps.)

Note: You don't need to have static IP or even you don't have to forward the port from your router configuration. The documentation will help to accomplish this via mapping the routing table programmatically.

B. Client application.
  1. Client application includes: 
      (i) An android phone (currently only android client is available), 
      (ii) Web-browser.
  
Note: Android client supports Live video streaming whereas Live clicks can be taken with Web-browser any time.

#   Steps to setup server and test on client (browser/android app):
  1. Pull homeWatchServer.
  2. Set a convenient port number greater than 1000 in "HomeWatchServer" file, like 8085.
  3. Connect a configured webcam to the system. 
  4. Start project by executing the "HomeWatchServer.java".

  I have used sarxos for image/video capturing and bitlet upnp library for mapping the port.

  5. Now that the server has been started, it's time to test the application on your local: Start with opening the browser, input your public ip address:port in the address bar, something like this [public ip:server port]. If everything works fine, an image will get downloaded. The image is actually a click taken by the webcam.
  6. If you have downloaded the android app then enter the public ip address (and port number) of the internet connection your server is working on. You will get a live streaming of the webcam.
  
  7. You are free to modify and add your own implementation for a remote webcam server. 
  
 I am yet to publish the android app however the server can still work with browser as client to capture image.

Documentation is incomplete at the moment.
