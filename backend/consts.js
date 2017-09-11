/**
 * Common resources for radar-backend - error codes + reasons, ...
 */

module.exports.ONLINE_THRESHOLD_SEC = 60

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
    reason: 'Invalid request action. Valid actions: "accept" or "deny"'
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
