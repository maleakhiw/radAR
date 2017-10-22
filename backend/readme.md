# Backend
## Running the backend
Make sure you have all dependencies installed by using `npm install` and run `npm start` to start the server. The server will listen in port 8080 (if the environment is set to `DEV` in `.env`), or 8443 (if the environment is set to `PRODUCTION` in `.env`).

An example `.env` file is provided in `.env.example`. Do not remove this file as it is needed by `dotenv`.

# API Documentation
<!-- TOC -->

- [Client-side components](#client-side-components)
  - [Authentication component](#authentication-component)
  - [Messaging/Chat component](#messagingchat-component)
  - [Friends/Users component](#friendsusers-component)
  - [Tracking Group Component](#tracking-group-component)
  - [Navigation Component](#navigation-component)
  - [Resources Component](#resources-component)
  - [Location Services Component](#location-services-component)
  - [Miscellaneous Services](#miscellaneous-services)
    - [Sensors Services Component](#sensors-services-component)
    - [Time Format Services](#time-format-services)
    - [Camera Service](#camera-service)
- [Server-side components: API Documentation](#server-side-components-api-documentation)
  - [Authentication Header](#authentication-header)
  - [Groups Management System](#groups-management-system)
    - [getGroup](#getgroup)
      - [Description](#description)
      - [Request format](#request-format)
      - [Response format](#response-format)
    - [updateGroupDetails](#updategroupdetails)
      - [Description](#description)
      - [Request format](#request-format)
      - [Response format](#response-format)
    - [newGroup](#newgroup)
      - [Description](#description)
      - [Request format](#request-format)
      - [Response format](#response-format)
    - [updateMeetingPoint](#updatemeetingpoint)
      - [Description](#description)
      - [Request format](#request-format)
      - [Response format](#response-format)
    - [addMembers](#addmembers)
      - [Description](#description)
      - [Request format](#request-format)
      - [Response format](#response-format)
    - [leaveGroup](#leavegroup)
      - [Description](#description)
      - [Request format](#request-format)
      - [Response format](#response-format)
    - [removeMember](#removemember)
      - [Description](#description)
      - [Request format](#request-format)
      - [Response format](#response-format)
    - [getLocations](#getlocations)
      - [Description](#description)
      - [Request format](#request-format)
      - [Response format](#response-format)
  - [Server User Positioning System](#server-user-positioning-system)
    - [updateLocation](#updatelocation)
      - [Description](#description)
      - [Request format](#request-format)
      - [Response format](#response-format)
    - [getLocation](#getlocation)
      - [Description](#description)
      - [Request format](#request-format)
      - [Response format](#response-format)
  - [Server Validation System](#server-validation-system)
    - [Authentication function](#authentication-function)
    - [signUp](#signup)
      - [Description](#description)
      - [Request format](#request-format)
      - [Response format](#response-format)
    - [login](#login)
      - [Description](#description)
      - [Request format](#request-format)
      - [Response format](#response-format)
  - [Server Messaging System](#server-messaging-system)
    - [newChat / newGroup](#newchat-newgroup)
      - [Description](#description)
      - [Request format](#request-format)
      - [Response format](#response-format)
    - [getMessages](#getmessages)
      - [Description](#description)
      - [Request format](#request-format)
      - [Response format](#response-format)
    - [sendMessage](#sendmessage)
      - [Description](#description)
      - [Request format](#request-format)
      - [Response format](#response-format)
    - [getOneToOneChat](#getonetoonechat)
      - [Description](#description)
      - [Request format](#request-format)
      - [Response format](#response-format)
    - [promoteToTrackingGroup](#promotetotrackinggroup)
      - [Description](#description)
      - [Request format](#request-format)
      - [Response format](#response-format)
  - [User Management System](#user-management-system)
    - [addFriend](#addfriend)
      - [Description](#description)
      - [Request format](#request-format)
      - [Response format](#response-format)
    - [getFriends](#getfriends)
      - [Description](#description)
      - [Request format](#request-format)
      - [Response format](#response-format)
    - [Search Users](#search-users)
      - [Description](#description)
      - [Request format](#request-format)
      - [Response format](#response-format)
    - [getInformation](#getinformation)
      - [Description](#description)
      - [Request format](#request-format)
      - [Response format](#response-format)
    - [getFriendRequests](#getfriendrequests)
      - [Description](#description)
      - [Request format](#request-format)
      - [Response format](#response-format)
    - [respondToRequest](#respondtorequest)
      - [Description](#description)
      - [Request format](#request-format)
      - [Response format](#response-format)
    - [cancelRequest](#cancelrequest)
      - [Description](#description)
      - [Request format](#request-format)
      - [Response format](#response-format)
    - [updateProfile](#updateprofile)
      - [Description](#description)
      - [Request format](#request-format)
      - [Response format](#response-format)
  - [Resource Management System](#resource-management-system)
    - [getResource](#getresource)
      - [Description](#description)
      - [Request format](#request-format)
      - [Response format](#response-format)
    - [uploadFile](#uploadfile)
      - [Description](#description)
      - [Request format](#request-format)
      - [Response format](#response-format)

<!-- /TOC -->

## Client-side components
### Authentication component
```Java
/**
 * API to handle authentication (sign-up, login) duties and access the
 * login and authentication services from a remote server. Also provides the
 * token needed for HTTP requests.
 */
interface AuthenticationService {
  /**
   * Returns the token needed for HTTP requests to server-side components.
   * @param context Android Context
   * @return Authentication token
   */
  String getToken(Context context);

  /**
     * Retrieves the userID from SharedPreferences. Returns 0 if unset.
     * @param context Android Context
     * @return userID
     */
  int getUserID(Context context);

  /**
     * Retrieves the username of a user. Returns 0 if unset.
     * @param context Android Context
     * @return userID
     */
  String getUsername(Context context)

  /**
  * Retrieves the first name of a user
  * @param context Android Context
  * @return user's first name
  */

  String getFirstName(Context context)
  
  /**
  * Retrieves the last name of a user
  * @param context Android Context
  * @return user's last name
  */
  String getLastName(Context context)

  /**
   * Retrieves the username from SharedPreferences. Returns 0 if unset.
   * @param context Android Context
   * @return userID
   */
  String getProfileDesc(Context context)


  /**
  * Retrieves the email of a user
  * @param context Android Context
  * @return user's email
  */
 String getEmail(Context context)


  /**
   * Creates a new user account for a user.
   * @param firstName first name for the account
   * @param lastName last name for the account
   * @param email e-mail address for the account
   * @param username username for the account
   * @param profileDesc a description for the user account's profile
   * @param password password for the account
   * @return an Observable to be subscribed by other functions for API responses 
   * TODO: Crosscheck with final interpretation
   * 
   */
  Status signUp(String firstName, String lastName, String email, String username);

  /**
   * Log in into the account
   * @param username username for the account
   * @param password password for the account
   * @return Observable to be subscribed to for the API response
   */
  String login(String username, String password);

  /**
   * Log out from the account
   * @param context Android context tor emove
   */
  signOut(Context context);
}
```

### Messaging/Chat component
```Java
/**
 * API to access services from RadAR's Server Messaging System.
 */
interface ChatService {
  /**
   * Create a new chat
   * @param newChatRequest class that contains chat name and participant user IDs
   * @return a list of new chats
   */
  Observable<NewChatResponse> newChat(NewChatRequest newChatRequest);

  /**
   * Retrieve messages from a chat.
   * @param chatID Unique identifier for a chat
   * @param pollingPeriod time in between requests in miliseconds
   * @return List of Message objects
   */
  Observable<MessagesResponse> getMessages(int chatID, int pollingPeriod);

  /**
   * Retrieve information about a chatroom
   * @param chatID unioque identifier for the chat
   * @return Status object
   */

  Observable<GetChatInfoResponse> getChatInfo(int chatID);

  /**
   * Send a message to a chat.
   * @param chatID Unique identifier for a chat
   * @param messageBody Message object to be sent, containing the actual text and other info such as time and media content
   * @return Array of message response objects
   */
  Observable<SendMessageResponse> sendMessages(int chatID, MessageBody messageBody);

  /**
   * Get all chats the user participates within.
   * @return Chat objects 
   */
  Observable<GetChatsResponse> getChats();

  /**
     * Leaves the group/chat
     * @param groupID group/chat to leave from
     * @return status of the request
     */
  public Observable<Status> leaveGroup(int groupID);

  /**
     * Adds users to the group/chat.
     * This method is found on the service as it is used for both user-to-user
     * and tracking group chats
     * @param groupID group in question
     * @param invitedUsers users to invite to the group
     * @return Status of the request
     */
  Observable<Status> addMembers(int groupID, ArrayList<Integer> invitedUsers);

```

### Friends/Users component
```Java
/**
 * API to handle friend management capabilities and access the corresponding
 * services on the RadAR server.
 * We use observables in an arraylist to get our return values.
 */
interface UsersService {

  /**
     * Sends an invitation (friend request) to a user.
     * @param invitedUserID user to send friend request to
     * @return response from API server - with a requestID
     */

  Observable<SearchUserResponse> getProfile(int queryUserID);
  /**
     * Sends an invitation (friend request) to a user.
     * @param invitedUserID user to send friend request to
     * @return response from API server that includes friend request status
     */
  Observable<AddFriendResponse> addFriend(int invitedUserID);

  /**
     * Gets the profile picture for a user
     * @param queryUserID user to get the profile picture for
     * @param resourcesService - a service for API, required to access profile picture
     * @param context Android Context, to load data from SharedPreferences and internal storage
     * @return
     */
  getProfilePicture(int queryUserID, ResourcesService resourcesService, Context context)
    

  /**
   * Retrieve the user's list of friends.
   * @return list of users (friends) objects
   */
  Observable<FriendsResponse> getFriends();

  /**
   * Search for RadAR users.
   * @param query text for the search
   * @param searchType what the search is for - name, email or username
   * @return list of Users
   */
   Observable<AddFriendResponse> searchForUsers(String query, int searchType);

  /**
     * Updates the user profile for the signed in user.
     * @param body fields to be changed from the profile, including 
     *        name, email, bio, and avatar file 
     * @return success or failure + reasons
     */
  Observable<Status> updateProfile(UpdateProfileBody body)

  /**
   * Retrieves the last known profile picture resource ID from SharedPreferences. Returns null if unset.
   * @param context Android Context
   * @return resource ID
   */
  String getProfilePictureResID(Context context)

   /**
    * Get friend requests
    * @return User object
    */
   Observable<FriendRequestsResponse> getFriendRequests();

   /**
    * Respond to a friend request
    * @param requestID request to respond to
    * @param requestAction enum that's either accept or decline
    * @return status object of the operation
    */
   Observable<Status> respondToFriendRequest(int requestID, REQUEST_ACTION requestAction);

}
```

### Tracking Group Component 
```Java
/**
 * API to access services to manage and get information on Tracking Groups
 * provided by our server-side Groups Management System.
 */
interface GroupsService {
  /**
   * Get information about a Tracking Group.
   * @param requestID request to respond to
   * @return the group and information associated with it
   */
  Observable<GroupsResponse> getGroup(int groupID);

  /**
   * Get all groups a user participates in
   * @return array of groups (represented internally as chat rooms) users participate in
   */
  Observable<GetChatsResponse> getGroups();

   /**
    * Get the current location of the members of the group
    * @param groupID unique identifier for the tracking group
    * @return list of user locations
    */
   ArrayList<UserLocation> getGroupLocations(int groupID);

  
   /**
     * Starts a new chat with a group. This passes on information of the group
     * into the  Messaging System's API to create a group with the same name
     * and association with a Tracking Group.
     * @param name - name of the group.
     * @param participantUserIDs all the partiicpants of of the chat
     * @return confirmation of response and new chat
     */
   Observable<NewChatResponse> newChat(String name, ArrayList<Integer> participantUserIDs);

  
   /**
     * Changes a group's Meeting Point
     * @param groupID group in question
     * @param meetingPoint an object that represents the new meeting point
     * @return status of the request
     */
  
   public Observable<Status> updateMeetingPoint(int groupID, MeetingPoint meetingPoint);


   /**
   * Updates group information
   * @param groupID group in question
   * @param UpdateGroupBody Class that defines group information, such as name and profile picture
   * @return status of the request
   */
   Observable<Status> updateGroup(int groupID, UpdateGroupBody body);

   /**
     * Deletes a group
     * @param groupID group in question
     * @return status of the request
     */
   Observable<Status> deleteGroup(int groupID);

   /**
     * Remove members from the group
     * @param groupID group in question
     * @param memberUserID user to be removed
     * @return status of the request
     */
   Observable<Status> removeMember(int groupID, int memberUserID);

   /**
     * Adds users to the group.
     * @param groupID group in question
     * @param invitedUsers users to invite to the group
     * @return status of the request
     */
   Observable<Status> addMembers(int groupID, ArrayList<Integer> invitedUsers);

   /**
    * Creates a new Tracking Group.
    * @param name Name of the tracking group
    * @param participantUserIDs list of users to invite to the tracking group
    * @param meetingPoint Meeting Point object with information regarding the meeting point
    * @return success or failure + reason
    */
    Observable<GroupsResponse> newGroup(String name, ArrayList<Integer> participantUserIDs);
    Observable<GroupsResponse> newGroup(String name, ArrayList<Integer> participantUserIDs,
                                        MeetingPoint meetingPoint);

   /**
    * Modifies the meeting point in a Tracking Group
    * @param groupID unique identifier of the Tracking Group
    * @param meetingPoint class that contains details of the meeting point 
    * @return operation status
    */
   Status Observable<Status> updateMeetingPoint(int groupID, MeetingPoint meetingPoint);

}
```

### Navigation Component
This uses a call to the Google Maps API, however, an abstract implementation of the requests looks as follows. 

```Java
interface NavigationService {
  /**
   * Retrieves directions in the form of waypoints.
   * @param lat Latitude of the destination
   * @param lon Longitude of the destination
   * @return List of waypoints
   */
  ArrayList<Waypoint> getDirections(float lat, float lon);

  /**
   * Searches for a nearby point of interest
   * @param query Keywords for the search
   * @return List of search results
   */
  ArrayList<POI> getPOIByKeyword(String query)
}
```

### Resources Component
```Java
interface ResourcesProviderService {
  /**
   * Uploads a file to the Resource Management System.
   * @param File to be uploaded
   * @return success or failure + reason
   * TODO complete this when implemented
   */
  Status uploadFile(File file);

  /**
   * Retrieves a file from the Resource Management System
   * @param resourceID Unique identifier for the file (resource)
   * @return The requested file
   */
  Observable<File> getResource(String resourceID);

  /**
   * Retrieves a file from the Resource Management System and cache it. Ideally used only for iamges.
   * @param resourceID Unique identifier for the file (resource)
   * @return The requested file
   */
  Observable<File> getResourceWithCache(String resourceID);
  
  /**
   * Saves files to the disk and a cache
   * This is adapted for RxJava2
   * @param response response from a file fetch from the server
   * @param fileID the unique file ID
   * @return The requested file
   */
  Observable<File> saveToDiskRxAndCache(Response<ResponseBody> response, String fileID);

  /**
   * Saves files to the disk
   * This is adapted for RxJava2
   * @param response response from a file fetch from the server
   * @return The requested file
   */
  Observable<File> saveToDiskRx(final Response<ResponseBody> response);
}
```

### Location Services Component
Provides an interface to gather location information from the device's GPS and geolocation systems to the application, following Android's location services API.

```Java
interface LocationService {
  /**
   * Retrieves last known location of the device
   * @return Location object, containing latitude, longitude, accuracy, and azimuth.
   */
  Observable<Location> getLastLocation();

  /**
     * Continuous stream of Location updates (current position of the device).
     * @param interval interval between requests in ms
     * @param fastestInterval fastestInterval between requests in ms
     * @param priority priority, defined in LocationRequest
     * @return Observable of Location objects
     */
  Observable<Location> getLocationUpdates(int interval, int fastestInterval, int priority);
  

  /**
    * Updates the location of a user to the radAR server
    * @param lat Latitude
    * @param lon Longitude
    * @param accuracy Relative reported GPS accuracy on device
    * @param heading Relative heading reported on device in degrees
    * @return response from the API server
    */

  Observable<UpdateLocationResponse> updateLocation(double lat, double lon, double accuracy, double heading);


  /**
     * Gets location of other users with location data on the server
     * @param queryUserID the user which location needs to be queried
     * @return response from the API server
     */

  Observable<GetLocationResponse> getLocation(int queryUserID);
  
  /**
     * Returns location info for a group.
     * @param groupID group for which location info is requested
     * @param interval time between requests in milliseconds
     * @return location information of everyone in the group
     */
  Observable<GroupLocationsInfo> getGroupLocationInfo(int groupID, int interval);

}
```

### Miscellaneous Services 
#### Sensors Services Component
An auxiliary service that gathers and calculates sensor readings from the device, to be used on other classes. Sensors include accelerometer and compass.

```Java
interface SensorService {
  /**
      * Reregisters the sensor event listeners.
      * To be used on onStart on the activity lifecycle for when the activity comes back into the
      * foreground.
      * Location tracking still can continue in the background via the LocationService.
      */
    void reregisterSensorEventListener();

    /**
       * Unregisters the sensor event listeners.
       * Location tracking still can continue in the background via the LocationService.
       */
    void unregisterSensorEventListener();
}

```

#### Time Format Services
An auxiliary helper service that allows time conversion. 

```Java
interface TimeFormatService {
  /**
      * Parses time input into string
      * @param timeString string which is generated by system or database using 
      * their internal representation of time 
      */
    String parseTimeString(String timeString);

}
```

#### Camera Service
An auxiliary helper service that captures the camera operations.

```Java
interface CameraService {
  /**
     * Sets up a camera preview using Camera2 API.
     * @return camera objects for a camera preview
     */
  Observable<CameraObjects> getCameraObjects(TextureView previewView);
  
  /**
     * Resumes the camera preview.
     * @return camera objects for a camera preview
     */
  Observable<CameraObjects> resumeCameraPreview();

  /**
     * Gets surface from the camera data stream
     */
  Observable<SurfaceAndSurfaceTexture> getSurfaceFromTextureView(TextureView previewView);

   /**
     * Gets raw camera data
     */
  Observable<CameraData> getCameraData(@NonNull CameraManager cameraManager);

   /**
     * Gets camera hardware
     */

  Observable<CameraDevice> getCameraDevice(@NonNull CameraManager cameraManager, @NonNull String cameraID);

   /**
     * Starts a camera capture/live data stream session 
     */

  Observable<CameraCaptureSession> createCaptureSession(@NonNull Surface previewSurface, @NonNull CameraDevice cameraDevice);
    

}
```


## Server-side components: API Documentation

### Authentication Header 

On every GET or POST request made to the server, the **authentication token is passed implicitly on the HTTP request header**, and hence are not outlined on every URI.

The JSON format of this authentication part of request headers are as follows:

```JSON
{
  "token": int
}
```

### Groups Management System
#### getGroup
`GET https://{serverURL}/api/accounts/{userID}/groups/{groupID}`
`GET https://{serverURL}/api/accounts/{userID}/chats/{groupID}`

##### Description
Get information about a Tracking Group

##### Request format
```JSON
{
    "userID": int,
    "groupID": int,
}
```

##### Response format
```JSON
{
    "success": bool,
    "errors": [
      {
        "reason": String,
        "errorCode": int
      },
      ...],
    "info": {
      "name": String,
      "groupID": int,
      createdOn: {
          type: Date, 
          default: Date.now // current date
        },
      "usersDetails": [
          {
            userID": int,
            "username": String,
            "email": String,
            "firstName": String,
            "lastName": String,
            "profilePicture": File,
            "profileDesc": String
          }
      ...],
      "isTrackingGroup": bool, 
      "usersDetails": String
    }
}
```

#### updateGroupDetails
`PUT https://{serverURL}/api/accounts/{userID}/groups/{groupID}`

##### Description
Update all the group details

##### Request format
```JSON
{
    "name": String,
    "userID": int,
    "groupID": int,
    "profilePicture": File
}
```

##### Response format
```JSON
{
    "success": bool,
    "errors": [
      {
        "reason": String,
        "errorCode": int
      },
      ...],
}
```


#### newGroup
`POST https://{serverURL}/api/groups`

##### Description
Creates a new Tracking Group

##### Request format
```JSON
{
    "userID": int,
    "name": String,
    "memberUserIDs": [int],
    "description": String,
    "token": String,
    "isTrackingGroup": true // use this flag to identify tracking groups from user chats
}
```

##### Response format
```JSON
{
    "success": bool,
    "errors": [
      {
        "reason": String,
        "errorCode": int
      },
      name: name,
      groupID: groupID,
      members: filteredUserIDs,
      admins: [userID],
      footprints: [], // currently not in use
      usersDetails: [User] // array of Users 
      meetingPoint: null,
      isTrackingGroup: true 
      ...],
}
```

#### updateMeetingPoint

`POST https://{serverURL}/api/accounts/{userID}/groups/{groupID}/meetingPoint`
`PUT https://{serverURL}/api/accounts/{userID}/groups/{groupID}/meetingPoint`

##### Description
Set the meeting point in the group

##### Request format
```JSON
{
    "userID": int,
    "lat": float,
    "lon": float,
    "name": String,
    "description": String
}
```

##### Response format
```JSON
{
    "success": bool,
    "meetingPoint": {
      "lat": float,
      "lon": float,
      "name": String,
      "description": String,
      "updatedBy": int
    },
    "errors": [
      {
        "reason": String,
        "errorCode": int
      },
      ...],
}
```

#### addMembers

`PUT https://{serverURL}/accounts/{userID}/groups/{groupID}/members/{newMemberUserID}`
`PUT https://{serverURL}/accounts/{userID}/chats/{groupID}/members/{newMemberUserID}`

##### Description
Adds a user to the Tracking Group.

##### Request format
```JSON
{
    "userID": int, // from newMemberUserID
    "groupID": int
}
```

##### Response format
```JSON
{
    "success": bool,
    "errors": [
      {
        "reason": String,
        "errorCode": int
      },
      ...],
}
```

#### leaveGroup
`DELETE https://{serverURL}/api/accounts/{groupID}/chats/{groupID}`
`DELETE https://{serverURL}/api/accounts/{groupID}/groups/{groupID}`

##### Description
Withdraws user from a group. 

##### Request format
```JSON
{
    "userID": int,
    "groupID": int
}
```

##### Response format
```JSON
{
    "success": bool,
    "errors": [
      {
        "reason": String,
        "errorCode": int
      },
      ...],
}
```

#### removeMember
`DELETE https://{serverURL}/accounts/{userID}/groups/{groupID}/members/{memberUserID}`

##### Description
Removes a user from the tracking group as another user. 

##### Request format
```JSON
{
    "userID": int,
    "groupID": int, 
    "memberUserID": int // user to remove from group
}
```

##### Response format
```JSON
{
    "success": bool,
    "errors": [
      {
        "reason": String,
        "errorCode": int
      },
      ...],
}
```

#### getLocations
`GET https://{serverURL}/accounts/{userID}/groups/{groupID}/location`

##### Description
Retrieves locations of every member of a group. 

##### Request format
```JSON
{
    "userID": int,
    "groupID": int, 
}
```

##### Response format
```JSON
{
    "success": bool,
    "errors": [
      {
        "reason": String,
        "errorCode": int
      },
      ...],

      "locationData" = [
        {
          userID: int,
          lat: float,
          lon: float,
          heading: float,
          accuracy: float,
          timeUpdated: float
        },
      ...]

}
```

### Server User Positioning System
#### updateLocation

`POST https://{serverURL}/api/accounts/{userID}/location`

##### Description
Updates the location of the user

##### Request format
```JSON
{
    "userID": int,
    "lat": float,
    "lon": float,
    "heading": float,
    "accuracy": float,
    "heading": float
}
```

##### Response format
```JSON
{
    "success": bool,
    "errors": [
      {
        "reason": String,
        "errorCode": int
      },
      ...],
}
```

#### getLocation

`GET https://{serverURL}/api/users/{userID}/location`

##### Description
Get the location of a user.

##### Request format
```JSON
{
    "userID": int,
    "groupID": int
}
```

##### Response format
```JSON
{
    "success": bool,
    "errors": [
      {
        "reason": String,
        "errorCode": int
      },
      ...],
    "locations": {
      int: {  
        "userID": int,
        "lat": float,
        "lon": float,
        "heading": float,
        "accuracy": float,
        "timeUpdated": Date
      },
    "userDetails": User, // user object schema in JSON - see Data Models at end of document. 
    "meetingPoint": MeetingPoint // meeting point object schema in JSON
    }
}
```


### Server Validation System
#### Authentication function
```JavaScipt
/**
 * Validates a token from a client's HTTP request.
 * @param token Authentication token for the request
 * @param userID userID from the request
 * @return true or false
 */

authenticate(token: String, userID: int)
```

#### signUp
`POST https://{serverURL}/api/auth`

##### Description
Creates a new account on the server.

##### Request format
```JSON
{
    "firstName": String,
    "lastName": String,
    "email": String,
    "username": String,
    "profileDesc": String,
    "password": String, // hashed
    "deviceID": String
}
```

##### Response format
```JSON
{
    "success": bool,
    "errors": [
      {
        "reason": String,
        "errorCode": int
      },
      ...],
    "token": String,
    "userInfo": {
      "userID": userID,
      "firstName": String,
      "lastName": String,
      "email": String,
      "username": String,
      "profileDesc": String,
      "profilePicture": File
    }
}
```

#### login
`GET https://{serverURL}/api/auth/{username}`

##### Description
Logs in into an existing account and retrieve a login token.

Username is part of the URI, but password is part of the query

##### Request format
```JSON
{
    "username": String,
    "password": String
}
```

##### Response format
```JSON
{
    "success": bool,
    "errors": [
      {
        "reason": String,
        "errorCode": int
      },
      ...],
    "userID": UserID,
    "token": String
}
```

### Server Messaging System
In the implementation of this API, the data models for both groups and chats are shared due to high similarity. 

#### newChat / newGroup
`POST https://{serverURL}/api/chats`
`POST https://{serverURL}/groups/chats`

##### Description
Create a new conversation (chat) on the Server Messaging System.

##### Request format
```JSON
{
    "userID": int,
    "name": String,
    "participantUserIDs": [int],
    "meetingPoint": MeetingPoint // MeetingPoint object
    "isTrackingGroup": bool // use this flag to identify tracking groups from user chats
}
```

##### Response format
```JSON
{
    "success": bool,
    "errors": [
      {
        "reason": String,
        "errorCode": int
      },
      name: name,
      groupID: groupID,
      members: filteredUserIDs,
      admins: [userID],
      footprints: [],
      meetingPoint: null,
      isTrackingGroup: bool 
      ...],
}
```

#### getMessages
`GET https://{serverURL}/api//accounts/{userID}/chats/{groupID}/messages`

##### Description
Get messages from a chat.

##### Request format
```JSON
{
    "userID": int,
    "groupID": int, // the chat ID 
}
```

##### Response format
```JSON
{
    "success": bool,
    "errors": [
      {
        "reason": String,
        "errorCode": int
      },
      ...],
    messages: [
      {
        "from": int,
        "time": Date,
        "contentType": String,
        "text": String,
        "contentResourceID": String
      }, ...
    ]
}
```

#### sendMessage
`POST https://{serverURL}/api/chats/{chatID}/messages`

##### Description
Sends a message to a chat.

##### Request format
```JSON
{
  "from": int, //userID of origin
  "groupID": chatID, // ID of chat
  "message": String
}
```

##### Response format
```JSON
{
    "success": bool,
    "errors": [
      {
        "reason": String,
        "errorCode": int
      },
      ...],
      messages: [
        {
          "from": int, //userID of origin
          "groupID":int,
          "time": Date,
          "contentType": String,
          "text": String,
          "contentResourceID": null 
        }

      ]
}
```

#### getOneToOneChat
`GET https://{serverURL}/api/accounts/{userID}/chats/with/{queryUserID}`

##### Description
Get user-to-user chat.

##### Request format
```JSON
{
    "userID": int,
    "queryUserID": int
}
```

##### Response format
```JSON
{
    "success": bool,
    "errors": [
      {
        "reason": String,
        "errorCode": int
      },
      ...],
    group: [group] // collection of Group objects
    ]
}
```

#### promoteToTrackingGroup

`POST https://{serverURL}/api/accounts/{userID}/chats/{groupID}`

##### Description
Changes the chat room to a tracking group, allowing it to be a larger group. 

##### Request format
```JSON
{
    "groupID": int,
    "isTrackingGroup": bool
}
```

##### Response format
```JSON
{
    "success": bool,
    "errors": [
      {
        "reason": String,
        "errorCode": int
      },
      ...],
    "group": {
      "name": String,
      "profilePicture": String, // file ID
      "groupID": int,
      "createdOn": Date,
      "admins": [int],
      "members": [int],
      "footprints": [],
      "meetingPoint": meetingPoint, // Meeting Point object 
      "isTrackingGroup": Boolean
    }
}
```

### User Management System
#### addFriend
`POST https://{serverURL}/api/accounts/{userID}/friends`

##### Description
Invites a user to be a friend.

##### Request format
```JSON
{
    "userID": int,
    "invitedUserID": int,
}
```

##### Response format
```JSON
{
    "success": bool,
    "errors": [
      {
        "reason": String,
        "errorCode": int
      },
      ...],
    "requestID": int
}
```

#### getFriends
`GET https://{serverURL}/api/accounts/{userID}/friends`

##### Description
Retrieves information about users in the user's friends list.

##### Request format
```JSON
{
  "userID": int
}
```

##### Response format
```JSON
{
  "success": bool,
  "errors": [
    {
      "reason": String,
      "errorCode": int
    },
    ...],
  "friends": [ // return User objects
    {
      "userID": int,
      "username": String,
      "firstName": String,
      "lastName": String,
      "profilePicture": int,  // fileID
      "profileDesc": String,
    }, ...
  ]
}
```

#### Search Users
`GET https://{serverURL}/api/users`

##### Description
Search for a user by a criteria.

##### Request format
```JSON
{
    "userID": int,
    "query": String,
    "searchType": String // username, email, name
}
```

##### Response format
```JSON
{
    "success": bool,
    "errors": [
      {
        "reason": String,
        "errorCode": int
      },
      ...],
    "results": [ // return User objects
    {
      "userID": int,
      "username": String,
      "firstName": String,
      "lastName": String,
      "profilePicture": int,  // fileID
      "profileDesc": String,
    }, ...
  ]
}
```

#### getInformation
`GET https://{serverURL}/api/users/{userID}`

##### Description
Get information for a user.

##### Request format
```JSON
{
    "username": String, // either queryUserID or username has to be filled
    "queryUserID": int,
    "requesterUserID": int
}
```

##### Response format
```JSON
{
    "success": bool,
    "errors": [
      {
        "reason": String,
        "errorCode": int
      },
      ...],
    "details": { // user object
      "userID": int,
      "username": String,
      "firstName": String,
      "lastName": String,
      "profilePicture": int,  // fileID
      "profileDesc": String,
    }
}
```

#### getFriendRequests
`GET https://{serverURL}/api/accounts/{userID}/friendRequests`

##### Description
Get pending friend requests (requests waiting for action).

##### Request format
```JSON
{
    "userID": int
}
```

##### Response format
```JSON
{
    "success": bool,
    "errors": [
      {
        "reason": String,
        "errorCode": int
      },
      ...],
    "requestsDetails": [
      {
        "requestID": int,
        "from": int,
        "firstName": String,
        "lastName": String,
        "profilePicture": String  // resourceID
      }, ...
    ]
}
```

#### respondToRequest
`POST https://{serverURL}/api/accounts/{userID}/friendRequests/{requestID}`

##### Description
Accept or decline a friend request.

##### Request format
```JSON
{
    "userID": int,
    "requestID": int,
    "action": String  // "accept" or "decline"
}
```

##### Response format
```JSON
{
    "success": bool,
    "errors": [
      {
        "reason": String,
        "errorCode": int
      },
      ...],
}
```

#### cancelRequest
`DELETE https://{serverURL}/api/accounts/{userID}/friendRequests/{requestID}`

##### Description
Cancel outgoing request. 

##### Request format
```JSON
{
    "userID": int,
    "requestID": int
}
```

##### Response format
```JSON
{
    "success": bool,
    "errors": [
      {
        "reason": String,
        "errorCode": int
      },
      ...],
    "requestID": int
}
```


#### updateProfile
`GET https://{serverURL}/api/accounts/{userID}`

##### Description
Get information for a user.

##### Request format
```JSON
{
    "username": String,  // validated
    "firstName": String,
    "lastName": String,
    "email": String, // validated
    "profilePicture": String,  // validated -> needs to point to a valid resource on the server (already uploaded)
    "profileDesc": String 
}
```

##### Response format
```JSON
{
    "success": bool,
    "errors": [
      {
        "reason": String,
        "errorCode": int
      },
      ...]
}
```

### Resource Management System
#### getResource
`GET https://{serverURL}/api/accounts/{userID}/resources`

##### Description
Get a resource (file) from the Resource Management System

##### Request format
```JSON
{
    "userID": int,
    "file": int // file ID 
}
```

##### Response format

The file itself is send over HTTPS on the body.

In case of the file not being available, it will return a 404 error. 

#### uploadFile
`POST https://{serverURL}/api/accounts/{userID}/resources`

##### Description
Uploads a resource (file) to the Resource Management System

##### Request format
```JSON
{
    "userID": int,
    "file": File
}
```

##### Response format
```JSON
{
    "success": bool,
    "errors": [
      {
        "reason": String,
        "errorCode": int
      },
      ...],
    "resourceID": int
}
```
