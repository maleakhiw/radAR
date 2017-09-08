const errorValues = require('./consts').errors

module.exports.errorObjectBuilder = function(errorKeys) {
  errors = []
  // TODO: error handling for keys that don't exist
  for (let i=0; i<errorKeys.length; i++) {
    errors.push({
      reason: errorValues[errorKeys[i]].reason,
      errorCode: errorValues[errorKeys[i]].code
    })
  }
  return errors
}

module.exports.getPublicUserInfo = function(user) {
  let retVal = {
    firstName: user.firstName,
    lastName: user.lastName,
    profilePicture: user.profilePicture,
    profileDesc: user.profileDesc
  }
  return retVal
}
