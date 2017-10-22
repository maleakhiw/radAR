# RadAR - a COMP30022 project

## Product Description
RadAR - an app for meeting up.

RadAR is an Android application built to help you find your friends and meet up with the help of augmented reality (AR). Create a group, set your meeting point and just *start!*. RadAR will then show you the location of you, your friends and your destinations either on a map or as an augmented reality overlay for the camera.

With Chats, you can communicate with your friends: plan your next meetup, ask them for directions, or just have a chat with them!

### Device Requirements
- Android Lollipop (5.0) or above
- Camera2 API support
- Compass, gyroscope and GPS

## Directories
- `backend` contains the source code for the Node.js backend application, which runs on the server.
- `radAR2` containis the source code for the Android application, as described below.

## Technical Information
This Android application is built to target Android Lollipop (5.0) and above. For full functionality, grant the application access to camera and fine location permissions.

The backend runs on top of Node.js and requires a MongoDB server to be running on the same machine as the server. To point it towards another MongoDB server, simply change the line containing `mongoose.connect()` in `server.js`

## How to use
See [here](tutorial.md) for a guide to the application.

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
