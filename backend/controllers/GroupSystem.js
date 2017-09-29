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
let Group, Message, User;

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

module.exports = class GroupSystem extends SMS{
  constructor(pGroup, pMessage, pUser) {
    super(pGroup, pMessage, pUser);
    Group = pGroup;
    Message = pMessage;
    User = pUser;
  }

  newGroup(req, res) {
    let callback = (groupID) => { // callback: only called if group creation is a success
      let userID = req.params.userID; // TODO make this consistent -> either by changing call params of callback
      promoteToTrackingGroupImpl(userID, groupID, req, res);
    }
    super.newGroupImpl(req, res, callback);
  }

  promoteToTrackingGroupImpl(userID, groupID, req, res) {
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


}
