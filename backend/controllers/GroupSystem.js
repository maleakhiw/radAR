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
const unique = common.unique;

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

function promoteToTrackingGroupImpl2(userID, groupID, req, res) {
  // group must exist
  groupExists(groupID).then(() => upgradeToTrackingGroup(groupID))
  .then(() => Group.findOne({groupID: groupID}))
  .then((group) => {
    if (group) {
      let groupRes = {
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

module.exports = class GroupSystem extends SMS{

  constructor(pGroup, pMessage, pUser, pLocation) {
    super(pGroup, pMessage, pUser);
    Group = pGroup;
    Message = pMessage;
    User = pUser;
    UserLocation = pLocation;
  }

  newGroup(req, res) {
    let callback = (groupID) => { // callback: only called if group creation is a success
      winston.debug('callback ' + groupID);
      let userID = req.params.userID; // TODO make this consistent -> either by changing call params of callback
      promoteToTrackingGroupImpl2(userID, groupID, req, res);
    }
    SMS.newGroupImpl(req, res, callback);
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

    let members;

    groupExists(groupID).then(() => Group.findOne({groupID: groupID}))
    .then((group) => {
      console.log(group);
      members = group.members;
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
    })
    .then(() => {
      console.log(locations);
      console.log(userDetails);
      res.json({
        success: true,
        errors: [],
        locations: locations,
        userDetails: userDetails
      })
    })

    .catch((err) => {
      if (err == 'groupDoesNotExist') {
        common.sendError(res, ['invalidGroupID']);
      }
    })

  }


}
