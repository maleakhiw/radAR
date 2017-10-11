const errorValues = require('./consts').errors
const metas = require('./consts').metas
const User = require('./models/user') // TODO refactor so User is plug and play
const Resource = require('./models/resource');  // TODO as above

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

module.exports.isUsernameUnique = (username) => new Promise((resolve, reject) => {
  User.findOne({ username: username })
  .then((user) => {
    if (user) {
      resolve(false);
    } else {
      resolve(true);
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

module.exports.getAuthUserInfo = function(user) {
  let retVal = {
    email: user.email,
    userID: user.userID,
    username: user.username,
    firstName: user.firstName,
    lastName: user.lastName,
    profilePicture: user.profilePicture,
    profileDesc: user.profileDesc
  }
  return retVal
}

module.exports.isValidPicture = (resourceID) => new Promise((resolve, reject) => {
  Resource.findOne({fileID: resourceID}).exec()
  .then((resource) => {
    if (!resource) {
      reject('invalidResourceID');
    } else {
      if (resource.mimetype.includes('image')) {
        resolve();
      } else {
        reject('invalidMimetype');
      }
    }
  })
});

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

module.exports.getUsersDetails = (members) => new Promise((resolve, reject) => {
  let userDetails = {};
  let promiseAll = members.map((memberUserID) => new Promise((resolve, reject) => {
    User.findOne({userID: memberUserID}).exec()
    .then((user) => { // assumption: user is valid (since all other routes validated, this is only a GET route)
      if (user) {
        userDetails[memberUserID] = module.exports.getPublicUserInfo(user);
      }
      resolve();
    })
  }))

  // when all info loaded, resolve the promise
  Promise.all(promiseAll).then(() => {
    resolve(userDetails);
  })
  .catch((err) => {
    reject(err);
  })
});
