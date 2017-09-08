/**
 * Server Validation System server-side component.
 * Handles user authentication, validation, sign up and login services.
 */

const common = require('./common')
// const mongoose = module.parent.exports.mongoose   // import from index.js

// Mongoose models
const User = require('./models/user')
const Metadata = require('./models/metadata')
const LastUserID = require('./models/lastUserID')

// validate a request token
function validateToken(userID, token) {  // TODO: stub
  return true;
}

function generateToken(userID) {  // TODO: stub
  return "79"
}

// validates request tokens - if token is valid, call callback
// also checks if userID and token is present in the request.
const validateRequest = function(req, res, callback) {
  let errorKeys = []
  if (!('token' in req.body)) {
    errorKeys.push('missingToken')
  }
  if (!req.body.userID) {
    errorKeys.push('missingUserID')
  }
  if (!validateToken(req.body.userID, req.body.token)) {
    errorKeys.push('invalidToken')
  }

  if (errorKeys.length) {   // if there is an error
    res.json({
      success: false,
      errors: common.errorObjectBuilder(errorKeys)
    })
  } else {
    callback(req, res)
  }
}

module.exports.validateRequest = validateRequest

function getUserID(callback) {
  LastUserID.findOneAndRemove({}, callback)
}

// callback for '/SVS/signUp' route
module.exports.signUp = function(req, res) {
  let firstName = req.body.firstName
  let lastName = req.body.lastName
  let email = req.body.email
  let username = req.body.username
  let profileDesc = req.body.profileDesc
  let password = req.body.password
  let deviceID = req.body.deviceID

  let errorKeys = []
  // required fields
  if (!firstName) errorKeys.push('missingFirstName')
  if (!lastName) errorKeys.push('missingLastName')
  if (!email) errorKeys.push('missingEmail')
  if (!username) errorKeys.push('missingUsername')
  if (!password) errorKeys.push('missingPassword')
  if (!deviceID) errorKeys.push('missingDeviceID')

  function sendError() {  // assumption: variables are in closure
    let response = {
      success: false,
      errors: common.errorObjectBuilder(errorKeys)
    }
    res.json(response)
  }

  if (errorKeys.length) {
    sendError()
  } else {
    let userID = null
    let token = null

    LastUserID.findOneAndRemove({}).exec()
    .then((lastUserID) => {
      console.log(lastUserID)
      if (lastUserID) {
        userID = lastUserID.userID + 1
      } else {
        userID = 1
      }
      return LastUserID.create({ userID: userID })  // Promise
    })

    .then((lastUserID) => {
      console.log(lastUserID)
      let object = {
        userID: userID,
        firstName: firstName,
        lastName: lastName,
        email: email,
        profilePicture: null,
        profileDesc: profileDesc,
        friends: [],
        groups: [],
        chats: [],
        signUpDate: Date.now()
      }

      // TODO: add to Username/Password collection
      return User.create(object)  // Promise
    })

    .then((user) => { // User successfully created, create Metadata
      console.log(user)
      token = generateToken(userID)
      let object = {
        userID: userID,
        lastSeen: Date.now(),
        deviceIDs: [deviceID],
        activeTokens: [token]
      }
      Metadata.create(object) // Promise
    })

    .then((metadata) => {
      console.log(metadata)
      let response = {
        success: true,
        errors: [],
        token: token,
        userID: userID
      }
      res.json(response)
    })

    .catch((err) => { // one error handler for the chain of Promises
      console.log(err)
      errorKeys.push('dbError')
      sendError()
    })
  }
}

module.exports.login = function(req, res) {
  let userID = req.body.userID
  let username = req.body.username
  let password = req.body.password

  errorKeys = []
  if (!username) errorKeys.push('missingUsername')
  if (!password) errorKeys.push('missingPassword')

  function sendError() {  // uses variables in closure
    response = {
      success: false,
      errors: common.errorObjectBuilder(errorKeys)
    }
    res.json(response)
  }

  if (errorKeys.length) {
    sendError()
  } else {
    // TODO: validate username and password
    let token = generateToken(userID)
    Metadata.findOne({ userID: userID }).exec()

    .then( (metadata) => {
      if (!metadata) {  // cannot find metadata on the user
        sendError()
        throw Error('')
      } else {
        metadata.activeTokens.push(token)  // TODO: SIGN OUT ROUTE - REMOVES A TOKEN
        return metadata.save()
      }
    })

    .then((metadata) => {
      response = {
        success: true,
        errors: [],
        token: token  // TODO: stub
      }
      res.json(response)
    })

    .catch((err) => {
      console.log(err)
      errorKeys.push('dbError')
      sendError()
    })
  }

}
