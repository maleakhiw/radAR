 # RadAR - a COMP30022 project
 An Android application for location sharing and finding users through the help of augmented reality
 
 ## Technical Information
 
 This Android application is built to target Android Lollipop (5.0) and above. To operate to its greatest extent, the application requires permissions for fine location access as well as camera access.
 
 ## Build Instructions
 
 The current single option of accessing and testing the application is through building and compiling the application through an IDE configured with Android development tools, before being then tested in an emulator or through ADB.
 
 To perform so, open the folder /radAR2 on your IDE, initiate a Gradle build, and then pass it through ADB or use the emulator to run the application. 
 
 The AR functionality is best experienced via an actual Android device instead of through the emulator.
 
## Server Information
The server for this application is placed on a remote VPS running NodeJS. It uses RESTful design and its API root is accessible from http://radar.fadhilanshar.com/.

To start the server on a local machine, navigate to the /backend directory, and in a terminal instance, type 'npm install' to get all dependencies, and 'npm start' to intiate loading of the server after installation. To perform tests, type in 'npm test'.

This assumes Node and NPM has been installed on the testing machine. 
 
 ## Team Oxygen
 
 Full Name | GitHub Username | Student ID 
 ----------|--------------------|------------
 Edelin Onggo | edelinonggo | 784172
 Kenneth Aloysius | krusli |  772449
 Maleakhi Wijaya | maleakhiw | 784091
 Muhammad Fadhil Anshar | nightietime | 727214
 Ricky Tanudjaja | rtanudjaja | 773597
