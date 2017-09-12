const errorValues = require('./consts').errors
const metas = require('./consts').metas

module.exports.addMetas = (obj, key) => {
  console.log(obj, key)
  obj.resources = metas[key].resources
  return obj
}

module.exports.errorObjectBuilder = function(errorKeys) {
  errors = []
  // TODO: error handling for keys that don't exist
  for (let i=0; i<errorKeys.length; i++) {
    // console.log(errorKeys[i], errorValues[errorKeys[i]])
    errors.push({
      reason: errorValues[errorKeys[i]].reason,
      errorCode: errorValues[errorKeys[i]].code
    })
  }
  return errors
}

module.exports.getPublicUserInfo = function(user) {
  // TODO: check settings - privacy, visibility (Iteration 3)
  let retVal = {
    userID: user.userID,
    firstName: user.firstName,
    lastName: user.lastName,
    profilePicture: user.profilePicture,
    profileDesc: user.profileDesc
  }
  return retVal
}

module.exports.isString = (object) => {
  return (typeof object === 'string' || object instanceof String)
}

module.exports.isNumber = (object) => {
  return (typeof object === 'number')
}

module.exports.isValidEmail = (email) => {
  // https://stackoverflow.com/questions/46155/how-to-validate-email-address-in-javascript
    var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(email);
}
