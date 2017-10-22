# RadAR - a COMP30022 project

## Product Description
RadAR - an app for meeting up.

RadAR is an Android application built to help you find your friends and meet up with the help of augmented reality (AR). Create a group, set your meeting point and just *start!*. RadAR will then show you the location of you, your friends and your destinations either on a map or as an augmented reality overlay for the camera.

With Chats, you can communicate with your friends: plan your next meetup, ask them for directions, or just have a chat with them!

## Android application
Android client/front-end for RadAR.

### Device Requirements
- Android Lollipop (5.0) or above
- Camera2 API support
- Compass, gyroscope and GPS
- Other dependencies listed in `build.gradle` and `app/build.gradle` in radAR2. Android Studio will automatically download the dependencies on compile/build time.

## Backend
The server for this application is placed on a remote VPS and runs our backend, which runs our RESTful API, accessible at https://radar.fadhilanshar.com/.

The server requires Node.js (v8 or above), npm and a MongoDB server running on `localhost:27017`. Unit tests for the backend can be run without an active MongoDB instance, however, the backend components still expect the dependencies to be present at runtime.

### Server requirements
- Node.js version 8 or above
- MongoDB v3.4.7 or above, running on the same server (`localhost:27017`, can be changed in `backend/server.js`)
- Dependencies from `package.json`. Install the dependies by `cd`ing into the `backend` directory and running `npm install`.

## Directories
- `backend` contains the source code for the Node.js backend application, which runs on the server.
- `radAR2` containis the source code for the Android application, as described below.

## How to use
See [here](tutorial.md) for a guide to the application.

## Deploy/install instructions
### Backend
Make sure Node.js and MongoDB is installed, and make sure MongoDB is running. Install dependencies by running `npm install`. To start the server, simply run `npm start`. The server will listen on port 8080 by default (`DEV` environment). In `PRODUCTION`, the server listens on port 8443 (forwarded from 443 using `iptables`) using HTTPS certificates for the [API endpoint](https://radar.fadhilanshar.com) (not provided).

### Android Application
Android Studio 3.0 is required to open the project and build the application, [available here](https://developer.android.com/studio/preview/install-preview.html). Previous versions of Android Studio are untested as the app relies on features available from the newer version of Build Tools supported only by version 3.0 and above.

The source code uses Build Tools 26.0.2. If a build fails and the IDE requests you to install or upgrade Build Tools, please do so and build the source code again. To build the source code, please ensure all of the dependencies requested by Gradle are installed and/or granted.

To get the .apk (application installer file), simply select either the `Build APK` or `Generate Signed APK` options in Android Studio, the latter requiring application signing keys. This .apk file can be installed on Android devices meeting the above system requirements.

To install the application on a device with USB debugging enabled (using ADB - Android Debug Bridge: connect the device. If prompted, grant USB debugging permissions on the device. The device should show up on the Run menu (for `app`). Select the device and click on Run to deploy and run the application on the connected device.

## Tests: how to run
### Backend
Run `npm install` (if not already), which will also install the dependencies required for running the unit tests. To install `mocha` (the test driver) globally, run `npm install -g mocha`. You can then run individual test cases by running `mocha name`, where name is the name to be pattern matched against.

Run `npm run-script unit` to run all backend unit tests. To run all tests (requires a MongoDB server to be up on `localhost:27017`), run `npm test`.

### Application
To run a test, ensure that you have the project opened in Android Studio. Find the `radar.radar (test)` folder. Right click on that folder and select *Run Tests* in "radar".

When all of the dependencies are installed properly by Gradle, tests should work fine. If the tests fail to compile or execute, attempt a full rebuild of the project. If it doesn't work, opt to invalidate caches and restart the IDE. If it still doesn't work, please open an issue or get back to us.



## The Team - Team Oxygen
Full Name | GitHub Username | Student ID
---------|--------------------|------------
Edelin Onggo | edelinonggo | 784172
Kenneth Aloysius | krusli |  772449
Maleakhi Wijaya | maleakhiw | 784091
Muhammad Fadhil Anshar | nightietime | 727214
Ricky Tanudjaja | rtanudjaja | 773597
