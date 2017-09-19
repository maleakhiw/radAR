/**
 * Common resources for radar-backend - error codes + reasons, ...
 */

module.exports.ONLINE_THRESHOLD_SEC = 60
module.exports.SALT_ROUNDS = 10 // rounds of salting for password

module.exports.secret = "topreisarubberdome"

module.exports.metas = {  // route -> metadata
  "/": {
    resources: [{URI: "/api", method: "GET"}]
  },
  "/api": {
    resources: [
      {URI: "/api/auth", methods: ["POST"], desc: "Create a new user account."},
      {URI: "/api/auth/{username}", methods: ["GET"], desc: "Log into a user account."},
      {URI: "/api/accounts/{userID}", methods: ["GET", "POST", "PUT"], desc: "Accounts (management)."},
      {URI: "/api/users", methods: ["GET"], desc: "Users on the system."},
      {URI: "/api/groups", methods: ["GET", "POST"], desc: "Tracking Groups."}
    ]
  },
  "/api/accounts/:userID": {
    resources: [
      {URI: "/api/accounts/{userID}/friends", methods: ["POST"], desc: "Add a new friend."},
      {URI: "/api/accounts/{userID}/friendRequests", methods: ["GET", "POST", "DELETE"], desc: "Manage friend requests."},
      {URI: "/api/accounts/{userID}/usersOnlineStatuses", methods: ["GET"], desc: "Check a list of users to see if they are online."},
      {URI: "/api/accounts/{userID}/resources", methods: ["GET", "POST"], desc: "Upload/download a file."}
    ]
  },
  "/api/accounts/:userID/resources": {
    resources: [
      {URI: "/api/accounts/{userID}/resources", methods: ["POST"], desc: "Upload a file."},
      {URI: "/api/accounts/{userID}/resources/{resourceID}", methods: ["GET"], desc: "Download a file."}
    ]
  },
  "/api/accounts/:userID/chats": {
    resources: [
      {URI: "/api/accounts/{userID}/chats", methods: ["POST"], desc: "Create a new chat."},
      {URI: "/api/accounts/{userID}/chats/{chatID}", methods: ["GET", "PUT", "DELETE"], desc: "Manage a chat."},
      {URI: "/api/accounts/{userID}/chats/{chatID}/messages", methods: ["POST"], desc: "Send a message/file."},
      {URI: "/api/accounts/{userID}/chats/{chatID}/messages/{messageID}", methods: ["GET"], desc: "Get a message (probably unused)."}
    ]
  },

  "/api/groups": {
    resources: [
      {URI: "/api/groups", methods: ["POST"], desc: "Create a new group."},
      {URI: "/api/groups/{groupID}", methods: ["GET", "PUT", "DELETE"], desc: "Manage a group."}
    ]
  },
  "/api/chats": {
    resources: [
      {URI: "/api/chats", methods: ["POST"], desc: "Create a new chat."},
      {URI: "/api/chats/{chatID}", methods: ["GET", "PUT", "DELETE"], desc: "Manage a chat."},
      {URI: "/api/chats/{chatID}/messages", methods: ["POST"], desc: "Send a message/file."},
      {URI: "/api/chats/{chatID}/messages/{messageID}", methods: ["GET"], desc: "Get a message (probably unused)."}
    ]
  }
}

module.exports.errors = {
  // TODO: serve up link to API documentation (on static route) in error string.
  invalidJSON: {
    code: 1,
    reason: "Invalid JSON format."
  },
  missingToken: {
    code: 2,
    reason: "Missing param: token."
  },
  invalidToken: {
    code: 3,
    reason: "Invalid request token."
  },
  missingFirstName: {
    code: 4,
    reason: "Missing param: firstName."
  },
  missingLastName: {
    code: 5,
    reason: "Missing param: lastName."
  },
  missingEmail: {
    code: 6,
    reason: "Missing param: email."
  },
  missingUsername: {
    code: 7,
    reason: "Missing param: username."
  },
  missingPassword: {
    code: 8,
    reason: "Missing param: password."
  },
  missingInvitedUserID: {
    code: 9,
    reason: "Missing param: invitedUserID."
  },
  missingUserIDOrUsername: {
    code: 10,
    reason: "Missing param: queryUserID or username."
  },
  internalError: {
    code: 11,
    reason: "Internal error."
  },
  missingDeviceID: {
    code: 12,
    reason: "Missing param: deviceID"
  },
  usernameTaken: {
    code: 13,
    reason: "Username is already taken"
  },
  invalidUsername: {
    code: 14,
    reason: "Invalid param: username"
  },
  invalidUserIDsToCheck: {
    code: 15,
    reason: "Invalid param: userIDsToCheck. userIDsToCheck is an array of userIDs."
  },
  invalidFirstName: {
    code: 16,
    reason: "Invalid param: firstName."
  },
  invalidLastName: {
    code: 17,
    reason: "Invalid param: lastName."
  },
  invalidEmail: {
    code: 18,
    reason: "Invalid param: email."
  },
  invalidPassword: {
    code: 20,
    reason: "Invalid param: password."
  },
  emailTaken: {
    code: 21,
    reason: "An account with this email has already been registered on the system."
  },
  invalidDeviceID: {
    code: 22,
    reason: "Invalid param: deviceID."
  },
  missingGroupName: {
    code: 23,
    reason: "Missing param: name."
  },
  missingMemberUserIDs: {
    code: 24,
    reason: "Missing param: memberUserIDs."
  },
  invalidMemberUserIDs: {
    code: 25,
    reason: "Invalid param: memberUserIDs."
  },
  missingFile: {
    code: 26,
    reason: "Missing form-data field: file."
  },
  invalidParticipantUserIDs: {
    code: 27,
    reason: "Invalid params: participantUserIDs."
  },
  invalidChatID: {
    code: 28,
    reason: "Invalid param: chatID."
  },
  unauthorisedChat: {
    code: 29,
    reason: "You do not have access to this chat."
  },
  missingChatID: {
    code: 30,
    reason: "Missing param: chatID."
  },
  missingMessage: {
    code: 31,
    reason: "Missing param: message."
  },
  missingGroupID: {
    code: 78,
    reason: "Missing param: groupID."
  },
  missingUserID: {
    code: 79,
    reason: "Missing param: userID."
  },
  missingUserIDsToCheck: {
    code: 80,
    reason: "Missing param: userIDsToCheck (for online status)."
  },
  friendRequestAlreadyExists: {
    code: 81,
    reason: "You have already sent a friend request to this User."
  },
  missingRequestID: {
    code: 82,
    reason: "Missing param: requestID"
  },
  invalidRequestID: {
    code: 83,
    reason: "Invalid requestID."
  },
  invalidAction: {
    code: 84,
    reason: 'Invalid request action. Valid actions: "accept" or "decline"'
  },
  missingQuery: {
    code: 85,
    reason: 'Missing param: query.'
  },
  missingSearchType: {
    code: 86,
    reason: 'Missing param: searchType.'
  },
  invalidSearchType: {
    code: 87,
    reason: 'Invalid search type. Valid search types: "username", "name", or "email"'
  },
  invalidUserID: {
    code: 88,
    reason: 'Invalid user ID'
  },
  selfInviteError: {
    code: 89,
    reason: 'userID and invitedUserID cannot be the same.'
  }
}
