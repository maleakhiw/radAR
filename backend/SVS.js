/**
 * Server Validation System server-side component.
 * Handles user authentication, validation, sign up and login services.
 */

const common = require('./common')

// validate a request token
function validateToken(userID, token) {  // TODO: stub
  return true;
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

// callback for '/SVS/signUp' route
module.exports.signUp = function(req, res) {
  let firstName = req.body.firstName
  let lastName = req.body.lastName
  let email = req.body.email
  let username = req.body.username
  let profileDesc = req.body.profileDesc
  let password = req.body.password

  let errorKeys = []
  // required fields
  if (!firstName) errorKeys.push('missingFirstName')
  if (!lastName) errorKeys.push('missingLastName')
  if (!email) errorKeys.push('missingEmail')
  if (!username) errorKeys.push('missingUsername')
  if (!password) errorKeys.push('missingPassword')

  if (errorKeys.length) {
    response = {
      success: false,
      errors: common.errorObjectBuilder(errorKeys)
    }
  } else {
    response = {
      success: true,
      errors: [],
      token: "79" // TODO: stub
    }
  }
  res.json(response)
}

module.exports.login = function(req, res) {
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
  } else {
    // TODO: validate username and password
    response = {
      success: true,
      errors: [],
      token: "79" // TODO: stub
    }
  }
  res.json(response)
}
