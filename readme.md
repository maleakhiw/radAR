# RadAR - a COMP30022 project

## Product Description

The deliverable for this project will be an augmented reality based app for smartphone. The application will be targeted at Android users with phone running Android Lollipop or laterwith working GPS, gyroscope and compass hardware.

The flagship functionality of this application utilizes geopositioning technologies to aid visually locate other users, through the proxy of their devices, running the applicationand providing access to others of their position. In addition, any preselected points of destination/rendezvous, current position and other user location should also be displayed.Using the information, user can track other users’ positions and move toward the designated location.

At the very general scale, the main functionalities will be implemented to allow user to track, share, and communicate their location with other users. Firstly, users can add otherusers as friends. Secondly, users can share their location to other users and vice versa. In doing so, users can also see the location of other users as specified, in real time.Third, users can create groups consisting of 2 or more users and select a meeting/rendezvous point, while similarly being able to track the location of each user of the group in realtime.
The tracking feature relies on phones of users in the group pushing location data between one another, and is displayed graphically either on a map display, or in an augmentedreality display where representations of other users are indicated as rendered icon overlays.

In addition to location sharing in map-based and AR-based fashion, the application will also implement a messaging system, allowing a medium for users to communicate with each otherin a more familiar way, enhancing effectiveness of the application in completing the users’ goal of locating each other within a given area. This helps users in sharing additionalinformation in form of text, and maximize the application’s proficiency to work as a one-stop-shop to facilitate physical rendezvouses between users.

## Technical Information
This Android application is built to target Android Lollipop (5.0) and above. To operate to its greatest extent, the application requires permissions for fine location access as wellas camera access.

The backend runs on top of Node.js and requires a MongoDB server to be running on the same machine as the server. To point it towards another MongoDB server, simply change the line containing `mongoose.connect()`

## Dependencies
### Backend
The backend assumes that Node and NPM has been installed on the computer/server running it.

### Android Application
The source code is implemented using Android Studio Beta 3.0, [available here](https://developer.android.com/studio/preview/install-preview.html). While this should not cause any issues with the stable version of Android Studio, this is currently not tested. Binaries of the Android application would be attached to the latest releases, but any rebuilding should be done through Android Studio.

The source code uses build tools 26.0.2. If a build fails and the IDE requests you to install it, please do so and build the source code again. To build the source code, please ensure all of the dependencies requested by Gradle are installed and/or granted. 

This application is best experienced through a real device, as there are some issues with the AR component which may overload and crash the Android Emulator.

## How to run

### Backend
- Open a terminal and point it towards the `backend` directory.
- Run `npm install` to install the required Node.js dependencies.

#### Running the server
If you do not have HTTPS certificates set up, change the flag `HTTPS_MODE` to false (TODO: environment variables). Make sure that the MongoDB server is up, and start the server by running `npm start`

### Android application
Open the folder /radAR2 on Android Studio 3.0 or above, build the project and install the resulting application through ADB or to a connected Android device with USB debugging turned on. Alternatively, generate a signed APK (Android package file) using Android Studio and install the APK on the Android phone.

The AR functionality is best experienced via an actual Android device instead of through the emulator.

## Testing
### Backend
Ensure that `mocha`, the test driver is installed by running `npm install -g mocha`. Also run `npm install`, which would also install the dependencies required for running the unit tests.

Run `npm run-script unit` to run the unit tests. To run all tests, run `npm test`.

### Application
To run a test, ensure that you have the project opened in Android Studio. Find the `radar.radar (test)` folder. Right click on that folder and select Run Tests in "radar". 

When all of the dependencies are installed properly by Gradle, tests should work fine. If the tests fail to compile or execute, attempt a full rebuild of the project. If it doesn't work, opt to invalidate caches and restart the IDE. If it still doesn't work, please open an issue.

## Server Information
The server for this application is placed on a remote VPS running NodeJS. It uses RESTful design and its API root is accessible from https://radar.fadhilanshar.com/.

This assumes Node and NPM has been installed on the testing machine.

## Team Oxygen

Full Name | GitHub Username | Student ID
---------|--------------------|------------
Edelin Onggo | edelinonggo | 784172
Kenneth Aloysius | krusli |  772449
Maleakhi Wijaya | maleakhiw | 784091
Muhammad Fadhil Anshar | nightietime | 727214
Ricky Tanudjaja | rtanudjaja | 773597
