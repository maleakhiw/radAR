# API Documentation
***PLEASE NOTE: The most updated revisions of this document will be reflected in the team Google Drive folders.*** This README is a carbon copy provisioned for quicker, neater reference in-directory.

<!-- TOC -->

- [API Documentation](#api-documentation)
  - [Client-side components](#client-side-components)
    - [Authentication component](#authentication-component)
    - [Messaging component](#messaging-component)
    - [Friends component](#friends-component)
    - [Tracking Group Component](#tracking-group-component)
    - [Navigation Component](#navigation-component)
    - [Resources Component](#resources-component)
    - [AR Component](#ar-component)
  - [Server-side components](#server-side-components)
    - [Push Notification System](#push-notification-system)
    - [Groups Management System](#groups-management-system)
      - [getGroupInfo](#getgroupinfo)
        - [Description](#description)
        - [Request format](#request-format)
        - [Response format](#response-format)
      - [respondToRequest](#respondtorequest)
        - [Description](#description-1)
        - [Request format](#request-format-1)
        - [Response format](#response-format-1)
      - [getGroupLocations](#getgrouplocations)
        - [Description](#description-2)
        - [Request format](#request-format-2)
        - [Response format](#response-format-2)
      - [newGroup](#newgroup)
        - [Description](#description-3)
        - [Request format](#request-format-3)
        - [Response format](#response-format-3)
      - [setMeetingPoint](#setmeetingpoint)
        - [Description](#description-4)
        - [Request format](#request-format-4)
        - [Response format](#response-format-4)
      - [addMemberToGroup](#addmembertogroup)
        - [Description](#description-5)
        - [Request format](#request-format-5)
        - [Response format](#response-format-5)
    - [Server User Positioning System](#server-user-positioning-system)
      - [updateLocation](#updatelocation)
        - [Description](#description-6)
        - [Request format](#request-format-6)
        - [Response format](#response-format-6)
    - [Server Validation System](#server-validation-system)
      - [signUp](#signup)
        - [Description](#description-7)
        - [Request format](#request-format-7)
        - [Response format](#response-format-7)
      - [login](#login)
        - [Description](#description-8)
        - [Request format](#request-format-8)
        - [Response format](#response-format-8)
    - [Server Messaging System](#server-messaging-system)
      - [newChat](#newchat)
        - [Description](#description-9)
        - [Request format](#request-format-9)
        - [Response format](#response-format-9)
      - [getMessages](#getmessages)
        - [Description](#description-10)
        - [Request format](#request-format-10)
        - [Response format](#response-format-10)
      - [sendMessage](#sendmessage)
        - [Description](#description-11)
        - [Request format](#request-format-11)
        - [Response format](#response-format-11)
      - [sendMessage](#sendmessage-1)
        - [Description](#description-12)
        - [Request format](#request-format-12)
        - [Response format](#response-format-12)
      - [sendFile](#sendfile)
        - [Description](#description-13)
        - [Request format](#request-format-13)
        - [Response format](#response-format-13)
      - [addToChat](#addtochat)
        - [Description](#description-14)
        - [Request format](#request-format-14)
        - [Response format](#response-format-14)
    - [User Management System](#user-management-system)
      - [isOnline](#isonline)
        - [Description](#description-15)
        - [Request format](#request-format-15)
        - [Response format](#response-format-15)
      - [addFriend](#addfriend)
        - [Description](#description-16)
        - [Request format](#request-format-16)
        - [Response format](#response-format-16)
      - [getFriends](#getfriends)
        - [Description](#description-17)
        - [Request format](#request-format-17)
        - [Response format](#response-format-17)
      - [Search](#search)
        - [Description](#description-18)
        - [Request format](#request-format-18)
        - [Response format](#response-format-18)
      - [removeFriend](#removefriend)
        - [Description](#description-19)
        - [Request format](#request-format-19)
        - [Response format](#response-format-19)
      - [getInformation](#getinformation)
        - [Description](#description-20)
        - [Request format](#request-format-20)
        - [Response format](#response-format-20)
      - [getFriendRequests](#getfriendrequests)
        - [Description](#description-21)
        - [Request format](#request-format-21)
        - [Response format](#response-format-21)
      - [respondToRequest](#respondtorequest-1)
        - [Description](#description-22)
        - [Request format](#request-format-22)
        - [Response format](#response-format-22)
    - [Resource Management System](#resource-management-system)
      - [getResource](#getresource)
        - [Description](#description-23)
        - [Request format](#request-format-23)
        - [Response format](#response-format-23)
      - [uploadFile](#uploadfile)
        - [Description](#description-24)
        - [Request format](#request-format-24)
        - [Response format](#response-format-24)

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
  String retrieveToken();

  /**
   * Creates a new user account for a user.
   * @param firstName first name for the account
   * @param lastName last name for the account
   * @param email e-mail address for the account
   * @param username username for the account
   * @param profileDesc a description for the user account's profile
   * @param password password for the account
   * @return success or failure + reason
   * TODO: Put back profile picture when implemented
   * 
   */
  Status signUp(String firstName, String lastName, String email, String username
                      , String profileDesc, String password, String deviceID);

  /**
   * Log in into the account
   * @param username username for the account
   * @param password password for the account
   * @return authentication token for future requests
   */
  String login(String username, String password);
}
```

### Messaging component
```Java
/**
 * API to access services from RadAR's Server Messaging System.
 */
interface MessagingService {
  /**
   * Create a new chat
   * @param participantUserIDs participants for the chat
   * @param name name for the chatroom
   * @return success and chatID, or failure and reason
   */
  NewChatStatus newChat(ArrayList<int> participantUserIDs, String name);

  /**
   * Retrieve messages from a chat.
   * @param chatID Unique identifier for a chat
   * @param endIndex Index of the latest message to be fetched
   * @param noOfMessages Number of messages to fetch, starting from endIndex
   backwards
   * @return List of Message objects
   * TODO: Update implementation accordingly if the third parameter is not applied
   */
  ArrayList<Message> getMessages(int chatID, int endIndex, int noOfMessages);

  /**
   * Send a message to a chat.
   * @param chatID Unique identifier for a chat
   * @param message Message text to be sent
   * @return Status object
   */
  Status sendMessage(int chatID, String message);

  /**
   * Send an image to a chat.
   * @param chatID Unique identifier for a chat
   * @param imageFile Image file to be sent to the chat
   * @param caption Caption for the file
   * @return Status object
   */
  Status sendImage(int chatID, FileInputStream imageFile, String caption);

  /**
   * Send a file to a chat.
   * @param chatID Unique identifier for a chat
   * @param file File to be sent to the chat
   * @param caption Caption for the file
   * @return Status object
   */
  Status sendFile(int chatID, FileInputStream file, String caption);

  /**
   * Add a new participant to the chat.
   * @param chatID Unique identifier for a chat
   * @param userID User to be added to the chat
   * @return Status object
   */
  Status addToChat(int chatID, int userID);
}
```

### Friends component
```Java
/**
 * API to handle friend management capabilities and access the corresponding
 * services on the RadAR server.
 */
interface FriendsService {
  /**
   * Check if a user is online.
   * @return whether the user is online
   */
  bool isOnline(int userID);

  /**
   * Invites a user to be a friend.
   * @param invitedUserID User being added
   * @return Status object
   */
  Status addFriend(int invitedUserID);

  /**
   * Retrieve the user's list of friends.
   * @return list of users (friends)
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
   ArrayList<FriendRequest> getFriendRequests();

   /**
    * Respond to a friend request
    * @param requestID request to respond to
    * @param action accept (true) or decline (false) the request
    */
   Status respondToRequest(int requestID, bool action);

   /**
    * Get information about a user.
    * @param username username to lookup
    * @return information about the user
    */
    User getInformation(String username);
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
   * @param accept accept (true) or decline (false)
   */
  Group getGroupInfo(int groupID);

  /**
   * Respond to a friend request
   * @param requestID request to respond to
   * @param accept accept (true) or decline (false)
   */
   Status respondToRequest(int requestID, bool accept);

   /**
    * Get the current location of the members of the group
    * @param groupID unique identifier for the tracking group
    * @return list of user locations
    */
   ArrayList<UserLocation> getGroupLocations(int groupID);

   /**
    * Creates a new Tracking Group.
    * @param name Name of the tracking group
    * @param userIDs users to invite to the tracking group
    * @param description Description of the tracking group
    * @return success or failure + reason
    */
   Status newGroup(String name, ArrayList<Integer> userIDs,
     String description);

   /**
    * Sets the meeting point in a Tracking Group
    * @param groupID unique identifier of the Tracking Group
    * @param lat latitude of the meeting point
    * @param lon longitude of the meeting point
    * @param description description for the meeting point
    * @return success or failure + reason
    */
   Status setMeetingPoint(int groupID, float lat, float lon,
     String description);

   /**
    * Invite a member to a Tracking Group
    * @param groupID unique identifier of the tracking group
    * @param userID unique ID for the user to be invited
    * @return success or failure + reason
    */
   Status addMemberToGroup(int groupID, int userID);

   /**
    * Sends the current location of the user to RadAR.
    * @return success or failure + reason
    */
   Status updateLocation();
}
```

### Navigation Component
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
   */
  Status uploadFile(File file);

  /**
   * Retrieves a file from the Resource Management System
   * @param fileID Unique identifier for the file (resource)
   * @return The requested file
   */
  File getResource(int fileID)
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

## Server-side components

On every GET or POST request made to the server, the authentication token is passed implicitly on the HTTP request header, and hence are not outlined on every URI.

### Push Notification System
```JavaScript
/**
 * Sends a notification to a device.
 * Abstraction layer for Google Cloud Messaging API or other notifications API.
 * @param requestID Unique identifier for the request
 * @param deviceUniqueID Unique identifier for the destination device (UDID, etc.)
 * @param text Text to be displayed in the push notification.
 */
function sendNotification(requestID: int, deviceUniqueID: String, text: String) {}
```

### Groups Management System
#### getGroupInfo
`GET https://{serverURL}/api/groups/:groupID`

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
      "members": [int],
      "description": String,
      "chatID": int
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

#### getGroupLocations
TODO: Modify when implemented

`POST https://{serverURL}/GMS/getGroupLocations`

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
      int: {  // userID: Location
        "lat": float,
        "lon": float,
        "accuracy": float,
        "validUntil": Date
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

#### setMeetingPoint
TODO: Modify URI

`POST https://{serverURL}/GMS/setMeetingPoint`

##### Description
Set the meeting point in the group

##### Request format
```JSON
{
    "userID": int,
    "lat": float,
    "lon": float,
    "description": String
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
TODO: Modify URI

`POST https://{serverURL}/UPS/updateLocation`

##### Description
Updates the location of the user

##### Request format
```JSON
{
    "userID": int,
    "lat": float,
    "lon": float,
    "accuracy": float
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

### Server Validation System
```JavaScipt
/**
 * Validates a token from a client's HTTP request.
 * @param token Authentication token for the request
 * @param userID userID from the request
 * @return true or false
 */
function validateToken(token: String, userID: int) {}
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
    "token": String
}
```

#### login
`GET https://{serverURL}/api/auth/:username`

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
    "token": String
}
```

### Server Messaging System
#### newChat
`POST https://{serverURL}/api/accounts/:UserID/chats`

##### Description
Create a new conversation (chat) on the Server Messaging System.

##### Request format
```JSON
{
    "userID": int,
    "participantsUserIDs": [int],
    "name": String,
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
    "chatID": int
}
```

#### getMessages
`GET https://{serverURL}/api/accounts/:userID/chats/:chatID/messages`

##### Description
Get messages from a chat.

##### Request format
```JSON
{
    "userID": int,
    "chatID": int,
    "endIndex": int,
    "noOfMessages": int,
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
    messages: [
      {
        "from": int,
        "chatID": int,
        "time": Date,
        "contentType": String,
        "text": String,
        "contentResourceID": String
      }, ...
    ]
}
```

#### sendMessage
`POST https://{serverURL}/api/chats/:chatID/messages`

##### Description
Sends a message to a chat.

##### Request format
```JSON
{
    "userID": int,
    "chatID": int,
    "message": String,
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

#### sendMessage
`POST https://{serverURL}/api/chats/:chatID/messages`

##### Description
Sends an image to a chat.

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

#### sendFile

TODO: Modify URI
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

#### addToChat

TODO: Modify URI
`POST https://{serverURL}/SMS/addToChat`

##### Description
Add a new participant to the chat.

##### Request format
```JSON
{
    "userID": int,
    "chatID": int,
    "participantUserID": int,
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

### User Management System
#### isOnline
`GET https://{serverURL}/api/accounts/:userID/usersOnlineStatuses`

##### Description
Checks if user(s) are online on the system.

##### Request format

TODO: UserInfo implementation

```JSON
{
    "userID": int,
    "userIDsToCheck": [int],
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
    "onlineStatus": {
      int: bool,  // userID: online or not
      ...
    },
    "userInfos": {
      int: 
    }
}
```

#### addFriend
`POST https://{serverURL}/api/accounts/:userID/friends`

##### Description
Invites a user to be a friend.

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
    "requestID": int
}
```

#### getFriends
`GET https://{serverURL}/api/accounts/:userID/friends`

##### Description
Retrieves information about users in the user's friends list.

##### Request format
```JSON
{
  "userID": int,
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

#### Search
`GET https://{serverURL}/api/users`

##### Description
Search for a user by a criteria.

##### Request format
```JSON
{
    "userID": int,
    "query": String,
    "searchType": String, // username, email, name
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

#### removeFriend
TODO: Modify URI
`POST https://{serverURL}/UMS/removeFriend`

##### Description
Removes a person from the user's friends list.

##### Request format
```JSON
{
  "userID": int,
  "token": String,
  "userIDToRemove": int
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

#### getInformation
`GET https://{serverURL}/api/users/:userID`

##### Description
Get information for a user.

##### Request format
```JSON
{
    "username": String, // either queryUserID or username has to be filled
    "queryUserID": int,
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
`GET https://{serverURL}/api/accounts/:userID/friendRequests`

##### Description
Get pending friend requests (requests waiting for action).

##### Request format
```JSON
{
    "userID": int,
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
`GET https://{serverURL}/api/accounts/:userID/resources/:resourceID`

##### Description
Get a resource (file) from the Resource Management System

##### Request format
```JSON
{
    "userID": int,
    "fileID": String,
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
    "resource": File
}
```

#### uploadFile
`POST https://{serverURL}/api/accounts/:userID/resources`

##### Description
Uploads a resource (file) to the Resource Management System

##### Request format
```JSON
{
    "userID": int,
    "file": File,
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
    "resourceID": String
}
```
