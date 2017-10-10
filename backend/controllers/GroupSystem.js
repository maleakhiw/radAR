/**
 * Group Management System server-side component.
 * Provides groups' information and group management services.
 */

// logging framework
const winston = require('winston');

winston.level = 'debug';  // TODO use environment variable

const common = require('../common');
const isNumber = common.isNumber;
const isArray = common.isArray;
const isString = common.isString;
const unique = common.unique;
const isValidLat = common.isValidLat;
const isValidLon = common.isValidLon;
const isValidHeading = common.isValidHeading;

const SMS = require('./SMS');

// data models
let Group, Message, User, UserLocation;

var groupExists = (groupID) => new Promise((resolve, reject) => {
  Group.findOne({groupID: groupID}).exec()
  .then((group) => {
    if (!group) {
      reject('groupDoesNotExist');
    } else {
      resolve();
    }
  })
});

var upgradeToTrackingGroup = (groupID) => new Promise((resolve, reject) => {
  Group.findOne({groupID: groupID}).exec()
  .then((group) => {
    if (!group) {
      reject('groupDoesNotExist');
    } else {
      group.isTrackingGroup = true;
      group.save().then((group) => {
        resolve();
      })
    }
  })
  .catch((err) => winston.error(err));
})

// TODO test
var isAdmin = (userID, groupID) => new Promise((resolve, reject) => {
  Group.findOne({groupID: groupID}).exec()
  .then((group) => {
    if (!group) {
      reject('groupDoesNotExist');
    } else {
      if (!group.admins.includes(userID)) {
        resolve(false);
      } else {
        resolve(true);
      }
    }
  })
})

function promoteToTrackingGroupImpl(userID, groupID, req, res) {
  // group must exist
  groupExists(groupID).then(() => upgradeToTrackingGroup(groupID))
  .then(() => {
    res.json({
      success: true,
      errors: []
    })
  })
  .catch((err) => {
    if (err == 'groupDoesNotExist') {
      common.sendError(res, ['invalidGroupID']);
    }
  })
}

function promoteToTrackingGroupImpl2(userID, groupID, userDetails, req, res) {
  // group must exist
  groupExists(groupID).then(() => upgradeToTrackingGroup(groupID))
  .then(() => Group.findOne({groupID: groupID}))
  .then((group) => {
    if (group) {
      let groupRes = {
        usersDetails: userDetails,
        name: group.name,
        groupID: group.groupID,
        members: group.members,
        admins: group.admins,
        footprints: group.footprints,
        meetingPoint: group.meetingPoint,
        isTrackingGroup: group.isTrackingGroup
      }
      res.json({
        success: true,
        errors: [],
        group: groupRes
      })
    }

  })
  .catch((err) => {
    if (err == 'groupDoesNotExist') {
      common.sendError(res, ['invalidGroupID']);
    }
  })
}

function validateMeetingPoint(req) {
  let errorKeys = [];
  let lat = req.body.lat;
  let lon = req.body.lon;
  let name = req.body.name;
  let description = req.body.description;

  if (lat == null) {
    errorKeys.push('missingLat');
  }
  if (lon == null) {
    errorKeys.push('missingLon');
  }
  if (name == null) {
    errorKeys.push('missingMeetingPointName');
  }
  if (errorKeys.length) {
    return errorKeys;
  }

  if (!isString(name)) {
    errorKeys.push('invalidMeetingPointName');
  }
  if (!isValidLat(lat)) {
    errorKeys.push('invalidLat');
  }
  if (!isValidLon(lon)) {
    errorKeys.push('invalidLon');
  }

  return errorKeys;
}

function deleteGroupImpl(req, res) {
  console.log('deleteGroupImpl()');

  let groupID = parseInt(req.params.groupID);
  let userID = parseInt(req.params.userID);

  let members = [];

  Group.findOne({groupID: groupID}).exec()
  .then((group) => {
    if (group.admins.includes(userID)) {
      members = group.members;
      return group.remove();
    } else {
      throw 'Unauthorized'
    }
  })
  .then(() => {
    let promiseAll = members.map(member => {
      return User.findOne({userID: member}).exec()
      .then(user => {
        user.groups = user.groups.filter((group) => group != groupID);
      })
    })
    return Promise.all(promiseAll);
  })
  .then(() => {
    res.json({
      success: true,
      errors: []
    });
  })
  .catch((err) => {
    if (err == 'Unauthorized') {
      // console.log('Sending unauthorized');
      common.sendUnauthorizedError(res, ['notGroupAdmin']);
    } else {
      common.sendInternalError(res);
    }
  });
}

module.exports = class GroupSystem extends SMS {

  constructor(pGroup, pMessage, pUser, pLocation) {
    super(pGroup, pMessage, pUser);
    Group = pGroup;
    Message = pMessage;
    User = pUser;
    UserLocation = pLocation;
  }

  updateGroupDetails(req, res) {
    /*
      HTTP PUT {serverURL}/api/accounts/:userID/groups/:groupID

      Body:
      {
        name: String (optional),  // validated
      }

      Headers:
      token: (token issued by the server)
    */

    let name = req.body.name;
    let userID = parseInt(req.params.userID);
    let groupID = req.params.groupID;

    if (name) {
      Group.findOneAndUpdate({groupID: groupID}, {
        "$set": {name: name}
      }).exec((err, group) => {
        if (err) {
          common.sendInternalError(res);
        } else if (!group) {
          common.sendError(res, ['invalidGroupID']);
        } else if (!group.members.includes(userID)) {
          common.sendUnauthorizedError(res, ['unauthorisedGroup']);
        } else {
          res.json({
            success: true,
            errors: []
          });
        }

      });

    }

  }

  newGroup(req, res) {
    let callback = (groupID, userDetails) => { // callback: only called if group creation is a success
      winston.debug('callback ' + groupID);
      let userID = req.params.userID; // TODO make this consistent -> either by changing call params of callback
      promoteToTrackingGroupImpl2(userID, groupID, userDetails, req, res);
    }
    SMS.newGroupImpl(req, res, callback);
  }

  deleteGroup(req, res) {
    deleteGroupImpl(req, res);
  }

  promoteToTrackingGroup_validateParams(groupID, isTrackingGroup) {
    let errorKeys = [];
    if (groupID == null) {
      errorKeys.push('missingGroupID');
    }
    if (isTrackingGroup == null) {
      errorKeys.push('missingIsTrackingGroup');
    }
    if (errorKeys.length) {
      return errorKeys;
    }

    if (!isNumber(groupID)) {
      errorKeys.push('invalidGroupID');
    }
    if (typeof(isTrackingGroup) !== 'boolean') {
      errorKeys.push('invalidIsTrackingGroup');
    }
    return errorKeys;
  }

  promoteToTrackingGroup(req, res) {
    // PUT {serverURL} /api/accounts/:userID/chats/:groupID
    /* Request body
       {
         groupID: Number,
         isTrackingGroup: Boolean
       }
    */
    let groupID = req.body.groupID;
    let isTrackingGroup = req.body.isTrackingGroup;

    // param validation
    // TODO HTTP status code for bad request
    let errorKeys = promoteToTrackingGroup_validateParams(groupID, isTrackingGroup);
    if (errorKeys.length) {
      common.sendError(res, errorKeys);
      return;
    }

    promoteToTrackingGroupImpl(userID, groupID, req, res);
  }

  getLocations(req, res) {
    // GET {serverURL}/api/accounts/:userID/groups/:groupID/locations
    let userID = req.params.userID;
    let groupID = req.params.groupID;

    let locations = [];
    let userDetails = {};

    console.log(userID, groupID);

    let members, meetingPoint;

    groupExists(groupID).then(() => Group.findOne({groupID: groupID}))
    .then((group) => {
      // TODO refactor to function isAuthorized()

      console.log(group);
      members = group.members;
      meetingPoint = group.meetingPoint;

      // TODO: implement
      // if (!members.includes(userID)) {
      //   throw 'unauthorizedGroup';
      // }

      // don't include self
      members = members.filter((memberUserID) => memberUserID != userID);

      let promiseAll = members.map((memberUserID) => new Promise((resolve, reject) => {
        console.log(memberUserID);
        UserLocation.findOne({userID: memberUserID}).exec()
        .then((location) => {
          console.log(location);
          if (location) {
            let locationData = {
              userID: location.userID,
              lat: location.lat,
              lon: location.lon,
              heading: location.heading,
              accuracy: location.accuracy,
              timeUpdated: location.timeUpdated
            };
            locations.push(locationData);
            resolve(locationData);
          } else {
            resolve();
          }
        });
      }));

      return Promise.all(promiseAll);
    })
    .then(() => {
      let promiseAll = members.map((memberUserID) => new Promise((resolve, reject) => {
        User.findOne({userID: memberUserID}).exec()
        .then((user) => { // assumption: user is valid (since all other routes validated, this is only a GET route)
          if (user) {
            userDetails[memberUserID] = common.getPublicUserInfo(user);
          }
          resolve();
        })
      }))
      return Promise.all(promiseAll);
    })
    .then(() => {
      console.log(locations);
      console.log(userDetails);
      res.json({
        success: true,
        errors: [],
        locations: locations,
        userDetails: userDetails,
        meetingPoint: meetingPoint
      })
    })

    .catch((err) => {
      if (err == 'groupDoesNotExist') {
        common.sendError(res, ['invalidGroupID']);
      }
    })

  }

  updateMeetingPoint(req, res) {

    let groupID = req.params.groupID;
    let userID = req.params.userID;

    let lat = req.body.lat;
    let lon = req.body.lon;
    let name = req.body.name;
    let description = req.body.description;
    let errorKeys = validateMeetingPoint(req);
    if (errorKeys.length) {
      common.sendError(res, errorKeys);
      return;
    }

    groupExists(groupID).then(() => {
      Group.findOne({groupID: groupID}).exec()
      .then((group) => {
        group.meetingPoint = {
          lat: lat,
          lon: lon,
          name: name,
          description: description,
          updatedBy: userID
        }
        group.save();
      })
    })
    .then((group) => {
      res.json({
        success: true,
        errors: [],
        meetingPoint: {
          lat: lat,
          lon: lon,
          name: name,
          description: description,
          updatedBy: userID
        }
      })
    })
    .catch((err) => {
      if (err == 'groupDoesNotExist') {
        common.sendError(res, ['invalidGroupID']);
      }
    });
  }

}
