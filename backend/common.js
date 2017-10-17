const errorValues = require('./consts').errors
const metas = require('./consts').metas
const User = require('./models/user') // TODO refactor so User is plug and play
const Group = require('./models/group') // TODO refactor as above
const Resource = require('./models/resource');  // TODO as above

module.exports.getNumLength = number => (Math.abs(number) + "").length;


module.exports.updateGroupLastUpdated = (groupID, userID) => {
  // runs asynchronously - less "important" to validate if successfully completed
  Group.findOne({groupID: groupID}).exec()
  .then(group => {
    if (group.members.includes(userID)) {
      group.lastUpdated = Date.now();
      group.save();
    }
  })
}

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

module.exports.formatGroupInfo = (group) => {
  return {
    name: group.name,
    groupID: group.groupID,
    admins: group.admins,
    members: group.members,
    isTrackingGroup: group.isTrackingGroup,
    profilePicture: group.profilePicture,
    meetingPoint: group.meetingPoint
    // usersDetails: usersDetails,  // TODO pass it in
    // lastMessage: lastMessage // TODO pass it in
  }
}

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
  // winston.debug(obj, key)
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
    // winston.debug(errorKeys[i], errorValues[errorKeys[i]])
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
    email: user.email,
    firstName: user.firstName,
    lastName: user.lastName,
    profilePicture: user.profilePicture,
    profileDesc: user.profileDesc
  }
  return retVal
}

module.exports.getPublicUserInfoPromise = (userID, userIDToCheck) => new Promise((resolve, reject) => {
  let common = [];

  getCommonGroups(userID, userIDToCheck)
  .then(commonGroups => {
    common = commonGroups;
    return User.findOne({userID: userIDToCheck});
  })
  .then(user => {
    let userInfo = {
      userID: user.userID,
      username: user.username,
      email: user.email,
      firstName: user.firstName,
      lastName: user.lastName,
      profilePicture: user.profilePicture,
      profileDesc: user.profileDesc,
      commonGroups: commonGroups
    }
    resolve(userInfo);
  })
  .catch(err => reject(err));
})

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

module.exports.getUserDetail = (queryUserID, selfUserID) => module.exports.getUsersDetails([queryUserID], selfUserID);

module.exports.getUsersDetails = (members, userID) => new Promise((resolve, reject) => {
  let userDetails = {};
  let promiseAll = members.map((memberUserID) => new Promise((resolve, reject) => {
    User.findOne({userID: memberUserID}).exec()
    .then((user) => { // assumption: user is valid (since all other routes validated, this is only a GET route)
      if (user) {
        userDetails[memberUserID] = module.exports.getPublicUserInfo(user);
        userDetails[memberUserID].isFriend = user.friends.includes(parseInt(userID));
      }
      resolve();
    })
  }))

  // when all info loaded, resolve the promise
  Promise.all(promiseAll).then(() => {
    // already got userdetails, now get common groups if userID specified
    if (userID) {
      let promiseAll2 = members.map(memberUserID => new Promise((resolve, reject) => {
        module.exports.getCommonGroups(userID, memberUserID)
        .then(commonGroups => {
          userDetails[memberUserID].commonGroups = commonGroups;
          resolve();
        })
      }));

      Promise.all(promiseAll2).then(() => {
        resolve(userDetails);
      })
    } else {
      resolve(userDetails);
    }
  })
  .catch((err) => {
    reject(err);
  })
});

module.exports.getCommonGroups = (userID, userToCheckAgainst) => new Promise((resolve, reject) => {
  let user;
  let otherUser;

  let groups = {};

  User.findOne({userID: userID}).exec()
  .then(userRes => {
    user = userRes;
    return User.findOne({userID: userToCheckAgainst})
  })
  .then(userRes => {
    otherUser = userRes;
    if (!user || !otherUser) {
      reject('invalidUsers');
    }

    let users = [user, otherUser];
    // winston.debug(user, otherUser)
    // otherwise
    let users_dict = {};
    users.map(user => {
      users_dict[user.userID] = user;
    });

    let commonGroups = users_dict[userID].groups.filter(group => {
      // winston.debug(group, users_dict[userToCheckAgainst].groups);
      // winston.debug(users_dict[userToCheckAgainst].groups.includes(group));
      return users_dict[userToCheckAgainst].groups.includes(group);
    });


    let promiseAll = commonGroups.map(groupID => Group.findOne({groupID: groupID}).exec().then(
      group => {
        groups[groupID] = {
          name: group.name,
          profilePicture: group.profilePicture,
          // groupID: {type: Number, unique: true},
          createdOn: group.createdOn,
          members: group.members,
          admins: group.admins,
          meetingPoint: group.meetingPoint,
          isTrackingGroup: group.isTrackingGroup
        };
      }
    ));
    return Promise.all(promiseAll);
  })
  .then(() => {
    resolve(groups);
  })
  .catch(err => reject(err));
});
