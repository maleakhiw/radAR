/**
 * Common resources for radar-backend - error codes + reasons, ...
 */

module.exports.errors = {
  // TODO: serve up link to API documentation (on static route) in error string.
  invalidJSON: {
    code: 1,
    reason: "Invalid JSON format."
  },
  missingToken: {
    code: 2,
    reason: "Missing param: request token."
  },
  invalidToken: {
    code: 3,
    reason: "Invalid param: request token."
  },
  missingFirstName: {
    code: 4,
    reason: "Missing param: first name."
  },
  missingLastName: {
    code: 5,
    reason: "Missing param: last name."
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
    reason: "Missing param: invited userID."
  },
  missingUserIDOrUsername: {
    code: 10,
    reason: "Missing param: queryUserID or username."
  },
  dbError: {
    code: 11,
    reason: "Internal error."
  },
  missingDeviceID: {
    code: 12,
    reason: "Missing param: deviceID"
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
    reason: "Missing param: users to check (for online status)."
  }
}
