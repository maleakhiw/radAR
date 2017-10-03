const errorValues = require('./consts').errors
const metas = require('./consts').metas
const User = require('./models/user') // TODO refactor so User is plug and play

module.exports.isValidUser = (userID) => new Promise((resolve, reject) => {
  if (!userID) {  // if no userID specified
    reject('missingUserID');
  }

  User.findOne({ userID: userID }).exec()

  .then((user) => {
    if (!user) {
      reject('invalidUserID');
    } else {
      resolve();
    }
  })
})

module.exports.isValidLat = (val) => {
  // latitude can only be +/- 90 degrees.
  // longitude can be +/- 180 deg.
  return (val >= -90 && val <= 90);
}

module.exports.isValidLon = (val) => {
  return (val >= -180 && val <= 180);
}

module.exports.isValidHeading = (val) => {
  return (val >= 0 && val <= 360);
}


module.exports.sendUnauthorizedError = (res, errorKeys) => {
  res.status(401).json({
    success: false,
    errors: module.exports.errorObjectBuilder(errorKeys)
  });
}

module.exports.sendInternalError = (res) => {
  res.status(500).json({
    success: false,
    errors: module.exports.errorObjectBuilder(['internalError'])
  });
}

module.exports.addMetas = (obj, key) => {
  // console.log(obj, key)
  obj.resources = metas[key].resources
  return obj
}

module.exports.sendError = (res, errorKeys) => {
    let response = {
        success: false,
        errors: module.exports.errorObjectBuilder(errorKeys)
    }
    res.json(response)
}

// filter duplicate entries
module.exports.unique = (a) => {
    var seen = {};
    return a.filter(function(item) {
        return seen.hasOwnProperty(item) ? false : (seen[item] = true);
    });
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
    username: user.username,
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

module.exports.isArray = (object) => {
  return (object instanceof Array)
}

module.exports.isValidEmail = (email) => {
  // https://stackoverflow.com/questions/46155/how-to-validate-email-address-in-javascript
    var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(email);
}
