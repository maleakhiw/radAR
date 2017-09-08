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

  if (errorKeys.length) {
    let response = {
      success: false,
      errors: common.errorObjectBuilder(errorKeys)
    }
    res.json(response)
  } else {
    let userID = null
    let callback = (error, doc) => {
      if (doc) {
        userID = doc.userID + 1
      } else {
        userID = 1
      }

      LastUserID.create({
        userID: userID
      }, (err, lastUserID) => {
        if (err) {
          // TODO: move everything below the create call to this callback
          // console.log(err)
        } else {
          // console.log(lastUserID)
        }
      })

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

      // got all the required information, create a User
      User.create(object, (err, user) => {
        if (err) {  // TODO: refactor common code
          console.log(err)
          errorKeys.push('dbError')
          let response = {
            success: false,
            errors: common.errorObjectBuilder(errorKeys)
          }
          res.json(response)
        }

        else {
          let token = generateToken(userID)
          let object = {
            userID: userID,
            lastSeen: Date.now(),
            deviceIDs: [deviceID],
            activeTokens: [token]
          }
          Metadata.create(object, (err, metadata) => {
            if (err) {
              console.log(err)
              errorKeys.push('dbError')
              let response = {
                success: false,
                errors: common.errorObjectBuilder(errorKeys)
              }
              res.json(response)
            } else {
              let response = {
                success: true,
                errors: [],
                token: token,
                userID: userID
              }
              res.json(response)
            }
          })
        }
      })
    }

    getUserID(callback)
  }
}

module.exports.login = function(req, res) {
  let userID = req.body.userID
  let username = req.body.username
  let password = req.body.password

  errorKeys = []
  if (!username) errorKeys.push('missingUsername')
  if (!password) errorKeys.push('missingPassword')

  if (errorKeys.length) {
    response = {
      success: false,
      errors: common.errorObjectBuilder(errorKeys)
    }
    res.json(response)


  } else {
    // TODO: validate username and password
    let token = generateToken(userID)
    Metadata.findOneAndRemove({ userID: userID }, (err, doc) => {
      if (err) {
        // TODO: send error
      }

      else {
        // TODO: update metadata with updated object
        doc.tokens = doc.tokens.push(token)

        Metadata.create(doc, (err, metadata) => {
          if (err) {
            // TODO: send err
          }
          else {
            response = {
              success: true,
              errors: [],
              token: token  // TODO: stub
            }
            res.json(response)
          }
        })

      }
    })

    }
  }
