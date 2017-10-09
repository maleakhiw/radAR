 # RadAR - a COMP30022 project
 
 ## Product Description
 
 The deliverable for this project will be an augmented reality based app for smartphone. The application will be targeted at Android users with phone running Android Lollipop or later. This application relies on the assumption that the phone used to run the application also has working access to location services, and hardware that supports positioning and compass direction sensors.
The flagship functionality of this application utilizes geopositioning technologies to aid visually locate other users, through the proxy of their devices, running the application and providing access to others of their position. In addition, any preselected points of destination/rendezvous, current position and other user location should also be displayed. Using the information, user can track other users’ positions and move toward the designated location. 
At the very general scale, the main functionalities will be implemented to allow user to track, share, and communicate their location with other users. Firstly, users can add other users as friends. Secondly, users can share their location to other users and vice versa. In doing so, users can also see the location of other users as specified, in real time. Third, users can create groups consisting of 2 or more users and select a meeting/rendezvous point, while similarly being able to track the location of each user of the group in real time. The tracking feature relies on phones of users in the group pushing location data between one another, and is displayed graphically either on a map display, or in an augmented reality display where representations of other users are indicated as rendered icon overlays. 
In addition to location sharing in map-based and AR-based fashion, the application will also implement a messaging system, allowing a medium for users to communicate with each other in a more familiar way, enhancing effectiveness of the application in completing the users’ goal of locating each other within a given area. This helps users in sharing additional information in form of text, and maximize the application’s proficiency to work as a one-stop-shop to facilitate physical rendezvouses between users. 

 
 ## Technical Information
 
 This Android application is built to target Android Lollipop (5.0) and above. To operate to its greatest extent, the application requires permissions for fine location access as well as camera access.
 
 ## Build Instructions
 
 The current single option of accessing and testing the application is through building and compiling the application through an IDE configured with Android development tools, before being then tested in an emulator or through ADB.
 
 To perform so, open the folder /radAR2 on your IDE, initiate a Gradle build, and then pass it through ADB or use the emulator to run the application. 
 
 The AR functionality is best experienced via an actual Android device instead of through the emulator.
 
 ## Server Information
The server for this application is placed on a remote VPS running NodeJS. It uses RESTful design and its API root is accessible from http://radar.fadhilanshar.com/.
a

This assumes Node and NPM has been installed on the testing machine. 
 
 ## Team Oxygen
 
 Full Name | GitHub Username | Student ID 
 ---------|--------------------|------------
 Edelin Onggo | edelinonggo | 784172
 Kenneth Aloysius | krusli |  772449
 Maleakhi Wijaya | maleakhiw | 784091
 Muhammad Fadhil Anshar | nightietime | 727214
 Ricky Tanudjaja | rtanudjaja | 773597
