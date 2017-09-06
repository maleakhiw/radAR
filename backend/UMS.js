/**
 * User Management System server-side component.
 * Handles user-related responsibilities and requests.
 * Also provides information/data on users.
 */

const common = require('./common')
const svs = require('./SVS')

// callback for '/UMS/isOnline' route
module.exports.isOnline = function (req, res) {
  callback = function(req, res) {
    let userIDsToCheck = req.body.userIDsToCheck

    let errorKeys = []
    if (!userIDsToCheck) errorKeys.push('missingUserIDsToCheck')

    if (errorKeys.length) {
      response = {
        success: false,
        errors: common.errorObjectBuilder(errorKeys)
      }
    } else {
      response = {
        success: true,
        errors: [],
        onlineStatus: { // TODO: check with Metadata collection to see when user was last online
          1: true,
          2: false
        }
      }
    }

    res.json(response)
  }

  svs.validateRequest(req, res, callback)
}

module.exports.addFriend = function(req, res) {
  callback = function(req, res) {
    let userID = req.body.userID
    let invitedUserID = req.body.invitedUserID

    let errorKeys = []
    if (!invitedUserID) errorKeys.push('missingInvitedUserID')

    if (errorKeys.length) {
      response = {
        success: false,
        erros: common.errorObjectBuilder(errorKeys)
      }
    } else {
      response = {
        success: true,
        errors: [],
      }
    }

    res.json(response)
  }

  svs.validateRequest(req, res, callback)
}

module.exports.getInformation = function(req, res) {
  callback = function(req, res) {
    let userID = req.body.userID
    let username = req.body.username
    let queryUserID = req.body.queryUserID

    let errorKeys = []
    if (!queryUserID || !username) {
      errorKeys.push('missingQueryUserIDOrUsername')
    }

    if (errorKeys.length) {
      response = {
        success: false,
        erros: common.errorObjectBuilder(errorKeys)
      }
    } else {
      response = {  // TODO: replace with database calls
        success: true,
        errors: [],
        details: {
          firstName: "TestFirstName",
          lastName: "TestLastName",
          profilePictureResID: ""
        }
      }
    }

    res.json(response)
  }

  svs.validateRequest(req, res, callback)
}
