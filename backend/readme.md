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
  - [AR Component](#ar-component)
  - [Location Services Component](#location-services-component)
  - [Sensors Services Component](#sensors-services-component)
- [Server-side components](#server-side-components)
  - [Push Notification System](#push-notification-system)
  - [Groups Management System](#groups-management-system)
    - [getGroup](#getgroup)
      - [Description](#description)
      - [Request format](#request-format)
      - [Response format](#response-format)
    - [respondToRequest](#respondtorequest)
      - [Description](#description)
      - [Request format](#request-format)
      - [Response format](#response-format)
    - [getLocations](#getlocations)
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
    - [addMemberToGroup](#addmembertogroup)
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
    - [sendFile](#sendfile)
      - [Description](#description)
      - [Request format](#request-format)
      - [Response format](#response-format)
    - [promoteToTrackingGroup](#promotetotrackinggroup)
      - [Description](#description)
      - [Request format](#request-format)
      - [Response format](#response-format)
  - [User Management System](#user-management-system)
    - [isOnline](#isonline)
      - [Description](#description)
      - [Request format](#request-format)
      - [Response format](#response-format)
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
   * @return token
   */
  String getToken();

  /**
     * Retrieves the userID from SharedPreferences. Returns 0 if unset.
     * @param context Android Context
     * @return userID
     */
  int getUserID(Context context);

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
   backwards
   * @return List of Message objects
   */
  Observable<MessagesResponse> getMessages(int chatID);

  /**
   * Retrieve information about a chatroom
   * @param chatID unioque identifier for the chat
   * @return Status object
   */

  Observable<GetChatInfoResponse> getChatInfo(int chatID)

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
  Observable<GetChatsResponse> getChats()

```

### Friends/Users component
```Java
/**
 * API to handle friend management capabilities and access the corresponding
 * services on the RadAR server.
 */
interface UsersService {

  /**
     * Sends an invitation (friend request) to a user.
     * @param invitedUserID user to send friend request to
     * @return response from API server - with a requestID
     */
    Observable<AddFriendResponse> addFriend(int invitedUserID)

  /**
   * Retrieve the user's list of friends.
   * @return list of users (friends) objects
   */
  ArrayList<User> getFriends();

  /**
   * Search for RadAR users.
   * @param query text for the search
   * @param searchType what the search is for - name, email or username
   * @return list of Users
   */
   ArrayList<User> search(String query, int searchType);

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
interface TrackingGroupService {
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
    * Creates a new Tracking Group.
    * @param name Name of the tracking group
    * @param participantUserIDs list of users to invite to the tracking group
    * @return success or failure + reason
    */
    Observable<GroupsResponse> newGroup(String name, ArrayList<Integer> participantUserIDs);

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
  Observable<File> getResource(String resourceID)

  /**
   * Saves files to the disk
   * @param response response from a file fetch from the server
   * @return The requested file
   */
  Observable<File> saveToDiskRx(final Response<ResponseBody> response)
}
```

### AR Component
```Java
interface ARHelperService {
  /**
   * Calculates information for drawing on-screen AR overlays.
   * @param heading Compass direction in degrees
   * @param lat Current latitude of the device
   * @param locations Current locations of other users to be drawn.
   * @return lon Information for drawing on-screen overlays.
   */
  OverlayObjects calculateOnScreenPositions(float heading, float lat,
    float lon, ArrayList<UserLocations> locations);
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
    public Observable<Location> getLocationUpdates(int interval, int fastestInterval, int priority)

  /**
    * Updates the location of a user to the radAR server
    * @param lat Latitude
    * @param lon Longitude
    * @param accuracy Relative reported GPS accuracy on device
    * @param heading Relative heading reported on device in degrees
    * @return response from the API server
    */

  public Observable<UpdateLocationResponse> updateLocation(float lat, float lon, float accuracy, float heading);


  /**
     * Gets location of other users with location data on the server
     * @param queryUserID the user which location needs to be queried
     * @return response from the API server
     */

  public Observable<GetLocationResponse> getLocation(int queryUserID);

  /**
     * Returns location info for a group.
     * @param groupID group for which location info is requested
     * @param interval time between requests in milliseconds
     * @return location info
     */
  public Observable<GroupLocationsInfo> getGroupLocationInfo(int groupID, int interval);

}
```
### Sensors Services Component
An auxiliary service that gathers and calculates sensor readings from the device, to be used on other classes. Sensors include accelerometer and compass.

```Java
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

```


## Server-side components

On every GET or POST request made to the server, the **authentication token is passed implicitly on the HTTP request header**, and hence are not outlined on every URI.

### Push Notification System
```JavaScript
/**
 * Sends a notification to a device.
 * Abstraction layer for Google Cloud Messaging API or other notifications API.
 * @param requestID Unique identifier for the request
 * @param deviceUniqueID Unique identifier for the destination device (UDID, etc.)
 * @param text Text to be displayed in the push notification.
 * TODO: Update when fully implemented.
 */
function sendNotification(requestID: int, deviceUniqueID: String, text: String) {}
```

### Groups Management System
#### getGroup
`GET https://{serverURL}/api/accounts/{userID}/groups/{groupID}`

##### Description
Get information about a Tracking Group

##### Request format
```JSON
{
    "userID": int,
    "groupID": int,
    "token": String
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
      "members": [int],
      "isTrackingGroup": bool,
      "usersDetails": String
    }
}
```

#### respondToRequest
`POST https://{serverURL}/api/accounts/:userID/friendRequests/:requestID`

##### Description
Respond to a group invite

##### Request format
```JSON
{
    "userID": int,
    "requestID": int,
    "accept": bool,
    "token": String
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
TODO: Modify when implemented

`GET https://{serverURL}/accounts/{userID}/groups/{groupID}/locations`

##### Description
Get the location of the members in the tracking group.

##### Request format
```JSON
{
    "userID": int,
    "groupID": int,
    "token": String
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
      }
    }
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
      footprints: [],
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
    "description": String,
    "updatedBy": UserID,
    "token": String
}
```

##### Response format
```JSON
{
    "success": bool,
    "lat": float,
    "lon": float,
    "name": String,
    "description": String,
    "updatedBy": UserID,
    "errors": [
      {
        "reason": String,
        "errorCode": int
      },
      ...],
}
```

#### addMemberToGroup
TODO: Modify URI

`POST https://{serverURL}/GMS/addMemberToGroup`

##### Description
Invites a member to the Tracking Group.

##### Request format
```JSON
{
    "userID": int,
    "invitedUserID": int,
    "token": String
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

### Server User Positioning System
#### updateLocation

`POST https://{serverURL}/api/accounts/:userID/location`

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
    "timeUpdated": Date,
    "token": String
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

##### Description
Takes the location of users

#### Request format

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
    "lat": float,
    "lon": float,
    "heading": float,
    "accuracy": float,
    "timeUpdated": Date

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
    "password": String,
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
    "userID": userID,
    "firstName": String,
    "lastName": String,
    "email": String,
    "username": String,
    "profileDesc": String,
    "profilePicture": File
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
`POST https://{serverURL}/api/groups`

##### Description
Create a new conversation (chat) on the Server Messaging System.

##### Request format
```JSON
{
    "userID": int,
    "name": String,
    "memberUserIDs": [int],
    "description": String,
    "token": String,
    "isTrackingGroup": false // use this flag to identify tracking groups from user chats
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
      isTrackingGroup: false
      ...],
}
```

#### getMessages
`GET https://{serverURL}/api/chats/{chatID}/messages`

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
  "groupID": chatID // ID of chat
  "time": Date,
  "contentType": String,
  "text": String,
  "contentResourceID": int
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
          "time": Date,
          "contentType": String,
          "text": String,
          "contentResourceID": int
        }

      ]
}
```

#### sendFile

TODO: Modify URI
TODO: Not done yet
`POST https://{serverURL}/SMS/sendFile`

##### Description
Sends a file to a chat room.

##### Request format
```JSON
{
    "userID": int,
    "chatID": int,
    "file": file,
    "caption": String,
    "token": String
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

#### promoteToTrackingGroup

`POST https://{serverURL}/api/accounts/{userID}/chats/{groupID}`

##### Description
Changes the chat room to a tracking group, allowing it to be a larger group.

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

### User Management System
#### isOnline
TODO: Ensure correct

`GET https://{serverURL}/api/accounts/:userID/usersOnlineStatuses`

##### Description
Checks if user(s) are online on the system.

##### Request format

TODO: UserInfo implementation

```JSON
{
    "userID": int,
    "userIDsToCheck": [int],
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
    "onlineStatus": {
      int: bool,  // userID: online or not
      ...
    },
    "userInfos": {
      "UserID": [int]
    }
}
```

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
    "details": { // user object
      "userID": int,
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
        requestID: int,
        from: int,
        firstName: String,
        lastName: String,
        profilePicture: String  // resourceID
      }, ...
    ]
}
```

#### respondToRequest
`POST https://{serverURL}/api/accounts/:userID/friendRequests/:requestID`

##### Description
Accept or decline a friend request.

##### Request format
```JSON
{
    "userID": int,
    "requestID": int,
    "token": String,
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

### Resource Management System
#### getResource
`GET https://{serverURL}/api/accounts/{userID}/resources`

##### Description
Get a resource (file) from the Resource Management System

##### Request format
```JSON
{
    "userID": int,
    "fileID": int
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
    "resource": File
}
```

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
