/**
 * Server Messaging System
 * Provides Messaging services for RadAR.
 */

const common = require('../common')
const addMetas = common.addMetas
const isArray = common.isArray
const unique = common.unique

// helpful functions
const _ = require('lodash');

// logging framework
const winston = require('winston');

winston.level = 'debug';  // TODO use environment variable

const SVS = require('./SVS')
let svs;

// data models
let Group, Message, User;

const GroupsHelper = require('../helpers/groupsHelper');
let groupsHelper;
let getExistingIndividualChat;

var getLastMessages = (groups) => new Promise((resolve, reject) => {
  let groupsLastMessages = {};

  let promiseAll = groups.map(groupID => new Promise((resolve, reject) => {
    Message.findOne({groupID: groupID}).sort({time: -1}).exec()
    .then((message) => {
      if (message) {
        // TODO deprecate/move under GroupsDetails?
        groupsLastMessages[groupID] = {
          from: message.from,
          time: message.time,
          contentType: message.contentType,
          text: message.text
        }
        resolve(message);
      } else {
        resolve(null);
      }
    })
    .catch((err) => {
      reject(err);
    });

  }));

  Promise.all(promiseAll).then(lastMessages => {
    resolve(groupsLastMessages);
  }).catch(err => reject(err));
})

var getGroupsForUserImpl = (req, res, filterChatsOut) => {
  let userID = parseInt(req.params.userID)

  let groupsLastMessages = {};
  let groupsTmp, groups;
  let groupDetails = [];

  User.findOne({ userID: userID }).exec()

  .then(user => {
    groupsTmp = user.groups;

    if (filterChatsOut) {
      winston.debug("filter chats out");
      let promiseAll = user.groups.map(group => new Promise((resolve, reject) => {
        Group.findOne({groupID: group}).exec()
        .then(group => {
          resolve(group.isTrackingGroup);
        })
        .catch(err => reject(err));
      }));
      // true only if the group is a tracking group
      return Promise.all(promiseAll);
    } else {
      winston.debug("do not filter chats out")
      groups = user.groups;
      // "keep everything", false indices to be filtered off later
      return Promise.all(groups.map(group => true));
    }

  })

  .then(groupsAreTrackingGroups => {
    // console.log(groupsAreTrackingGroups);
    let groupsAndToKeep = _.zip(groupsTmp, groupsAreTrackingGroups);

    // filter off non-tracking groups
    let filteredGroup = groupsAndToKeep.filter(entry => entry[1]);
    let groupsFirst = filteredGroup.map(entry => entry[0]);
    groups = groupsFirst;

    return new Promise((resolve, reject) => resolve(groupsFirst));
  })

  .then(groupIDs => {
    let promiseAll = groupIDs.map(groupID => groupsHelper.getGroupInfo(groupID, userID));

    return Promise.all(promiseAll);
  })

  .then(groupDetails => {

    // sort groupDetails - TODO refactor to individual functions; test cases
    groupDetails.sort((group1, group2) => { // custom sort function
      let group1ID = group1.groupID;
      let group2ID = group2.groupID;

      // TODO group last updated => is active or inactive

      if (groupsLastMessages[group1ID] && groupsLastMessages[group2ID]) {
        let timeDifference = groupsLastMessages[group1ID].time - groupsLastMessages[group2ID].time;
        if (timeDifference < 0) {
          return 1;
        } else if (timeDifference == 0) {
          return 0;
        } else {
          return -1;
        }
        return timeDifference;

      } else if (groupsLastMessages[group1ID] && !groupsLastMessages[group2ID]) {
        return -1;
      } else if (!groupsLastMessages[group1ID] && groupsLastMessages[group2ID]) {
        return 1;
      } else {
        if (group1.name < group2.name) {
          winston.debug("group1.name < group2.name");
          return -1;
        } else if (group1.name === group2.name){
          winston.debug("group1.name = group2.name");
          return 0;
        } else {
          winston.debug("group1.name > group2.name");
          return 1;
        }
      }
    });

    groupDetails.map(group => {
      groupsLastMessages[group.groupID] = group.lastMessage;
    })

    let response = {
      success: true,
      errors: [],
      groups: groupDetails,
      groupsLastMessages: groupsLastMessages
    }
    res.json(addMetas(response, "/api/accounts/:userID/chats"))
  })

  .catch((err) => {
    winston.error(err)
    res.json({
      success: false,
      errors: []
    })
  })

}

/**
 * @param req Express request object
 * @param res Express response object
 * @param callback callback function. If defined, callback should handle response
 */
function newGroupImpl(req, res, callback, newOneToOneChat) {
  let userID = parseInt(req.params.userID); // TODO validate
  let participantUserIDs = req.body.participantUserIDs
  let name = req.body.name
  let meetingPoint = req.body.meetingPoint;

  if (newOneToOneChat == null) {  // when called from getOneToOneChat
    newOneToOneChat = false;
  }

  let errorKeys = []
  // TODO: get rid of code duplication, move sendError() to common.js
  function sendError() { // assumption: variables are in closure
    let response = {
      success: false,
      errors: common.errorObjectBuilder(errorKeys)
    }
    res.status(401).json(response)
  }

  if (!participantUserIDs) {
    errorKeys.push('missingParticipantUserIDs');
  }
  if (!name && !newOneToOneChat) {
    errorKeys.push('missingGroupName');
  }
  if (errorKeys.length) {
    sendError();
    return;
  }

  let isMeetingPointValid = false;
  if (meetingPoint) {
    let lat = meetingPoint.lat;
    let lon = meetingPoint.lon;
    let name = meetingPoint.name;
    let updatedBy = userID;
    if (common.isValidLat(lat) && common.isValidLon(lon) && name) {
      meetingPoint.updatedBy = userID;
      isMeetingPointValid = true;
    }

  }

  if (!isArray(participantUserIDs)) {
    errorKeys.push('invalidParticipantUserIDs');
    sendError();
    return
  }

  participantUserIDs = participantUserIDs.map((entry) => parseInt(entry))
  participantUserIDs.push(userID) // add the requester to participants
  participantUserIDs = unique(participantUserIDs) // filter to only unique userIDs

  let filteredUserIDs, groupID, group;
  let usersDetails = {};

  User.find( { userID: { $in: participantUserIDs } } ).exec()

  .then((users) => {  // userIDs that are actually on the system
    filteredUserIDs = users.map((user) => user.userID)

    if (filteredUserIDs.length == 0) {
      throw new Error('invalidParticipantUserIDs')
    }
    return Group.findOne().sort({groupID: -1})
  })

  .then((pGroup) => {
    if (pGroup) {
      groupID = pGroup.groupID + 1;
    } else {
      groupID = 1;
    }

    group = {
      name: name,
      groupID: groupID,
      members: filteredUserIDs,
      admins: [userID],
      footprints: [],
      meetingPoint: null,
      isTrackingGroup: false
    }

    if (isMeetingPointValid) {
      group.meetingPoint = meetingPoint;
    }

    return Group.create(group);
  })

  .then((pGroup) => {

    // add to everyone's group lists
    let promiseAll = participantUserIDs.map(
      (participantUserID) => new Promise((resolve, reject) => {
        User.findOne({userID: participantUserID}).exec()
        .then((user) => {
          // console.log(participantUserID)
          usersDetails[participantUserID] = (common.getPublicUserInfo(user));

          user.groups.push(groupID)
          user.save().then(() => resolve());
        })
        .catch((err) => winston.error(err));  // TODO send fail
    }));

    return Promise.all(promiseAll);
  })
  .then(() => {
    if (callback) {
      callback(groupID, usersDetails);
    } else {
      group['usersDetails'] = usersDetails;

      res.json({
        success: true,
        errors: [],
        group: group
      })
    }
  })

  .catch((err) => {
    winston.error(err)
    if (err == 'Error: invalidParticipantUserIDs') {
      errorKeys.push('invalidParticipantUserIDs')
      sendError()
      return
    }

    errorKeys.push('internalError')
    sendError()
    return
  })
}

module.exports = class SMS {
  constructor(pGroup, pMessage, pUser) {
    Group = pGroup
    Message = pMessage
    User = pUser
    svs = new SVS(pUser)

    groupsHelper = new GroupsHelper(Group, Message, User);
    getExistingIndividualChat = groupsHelper.getExistingIndividualChat;
  }

  getOneToOneChat(req, res) {
    // api/accounts/:userID/chats/with/:queryUserID
    let userID = parseInt(req.params.userID);
    let queryUserID = parseInt(req.params.queryUserID);

    winston.debug('getOneToOneChat');

    common.userExists(queryUserID).then(exists => {
      winston.debug('userExists.then');
      winston.debug(exists);
      if (!exists) {
        throw 'invalidUserID';
      }
      return getExistingIndividualChat(userID, queryUserID)
    })

    .then(group => {
      // winston.debug(group);
      if (group) {
        winston.debug('got group');
        res.json({
          success: true,
          errors: [],
          group: group
        })
      } else {
        winston.debug('new group');
        req.body.participantUserIDs = [userID, queryUserID];
        req.body.name = null;

        newGroupImpl(req, res, null, true);
      }
    })
    .catch(err => {
      if (err == 'invalidUserID') {
        common.sendError(res, ['invalidUserID']);
      } else {
        winston.error(err);
        common.sendInternalError(res);
      }
    })
  }

  newGroup(req, res) {
    newGroupImpl(req, res, null);
  }

  getGroupsForUser(req, res) {
    getGroupsForUserImpl(req, res, false);
  }

  getGroup(req, res) {
    let userID = req.params.userID
    let groupID = req.params.groupID

    common.groupExists(groupID)
    .then(exists => {
      if (exists) {
        return groupsHelper.getGroupInfo(groupID, userID);
      } else {
        throw 'invalidGroupID';
      }
    })

    .then(group => {
      res.json({
        success: true,
        errors: [],
        group: group
      });
    })

    .catch(err => {
      if (err == 'invalidGroupID') {
        res.status(404).json({
          success: false,
          error: common.errorObjectBuilder(['invalidGroupID'])
        });
      } else {
        common.sendInternalError(res)
      }
    });

  }

  getMessages(req, res) {
    let groupID = parseInt(req.query.groupID) || parseInt(req.params.groupID)
    let userID = parseInt(req.body.userID) || parseInt(req.params.userID)

    let usersDetails;


    Group.findOne({ groupID: groupID }).exec()

    .then((group) => {
      if (!group) {
        res.json({
          success: false,
          errors: common.errorObjectBuilder(['invalidGroupID'])
        })
        return
      }

      if (!group.members.includes(userID)) {
        res.status(401).json({
          success: false,
          errors: common.errorObjectBuilder(['unauthorisedGroup'])
        })
        return
      }

      return groupsHelper.getUsersDetails(group.members);
    })

    .then((pUsersDetails) => {
      usersDetails = pUsersDetails;
      return Message.find({groupID: groupID});
    })

    .then((messages) => {
      let messagesRes = messages.map((message) => {
        return {
          from: message.from,
          time: message.time,
          contentType: message.contentType,
          text: message.text,
          contentResourceID: message.contentResourceID,
        }
      })

      res.send({
        success: true,
        errors: [],
        messages: messagesRes,
        usersDetails: usersDetails
      })
    })

    .catch((err) => {
      winston.error(err)
      res.send({
        success: false,
        errors: common.errorObjectBuilder(['internalError'])
      })
    })

  }

  sendMessage(req, res) { // TODO refactor - still unhandled promise rejections
      // winston.debug(req.body)
      let from = parseInt(req.params.userID);
      let groupID = parseInt(req.params.groupID)
      let message = req.body.message

      winston.debug(from, groupID, message)

      let errorKeys = []
      if (!groupID) errorKeys.push('missingGroupID')
      if (!message) errorKeys.push('missingMessage')
      if (errorKeys.length) {
        res.json({
          success: false,
          errors: common.errorObjectBuilder(errorKeys),
          sentMessage: null
        })
        return
      }

      let sentMessage

      // check if groupID exists
      Group.findOne({ groupID: groupID }).exec()

      .then((group) => {
        if (!group) {
          res.json({
            success: false,
            errors: common.errorObjectBuilder(['invalidGroupID']),
            sentMessage: null
          })
          return
        } else if (!group.members.includes(from)) {  // not a member of the group
          res.json({
            success: false,
            errors: common.errorObjectBuilder(['unauthorisedGroup']),
            sentMessage: null
          })
          return
        } else {
          sentMessage = {
            from: from,
            groupID: groupID,
            time: Date.now(),
            contentType: "text",
            text: message,
            contentResourceID: null
          }
          return Message.create(sentMessage)
        }
      })

      .then((message) => {
        sentMessage.time = message.time;

        res.json({
          success: true,
          errors: null,
          sentMessage: sentMessage
        })

      })

      .catch((err) => {
        winston.error(err)
        res.json({
          success: false,
          errors: common.errorObjectBuilder(['internalError']),
          sentMessage: null
        })
      })

  }

}

// exports, after the class
module.exports.newGroupImpl = newGroupImpl;
module.exports.getGroupsForUserImpl = getGroupsForUserImpl;
