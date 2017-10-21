let Group, Message, User;
const common = require('../common');
const winston = require('winston');

var getExistingIndividualChat = (userID, queryUserID) => {
  return getExistingIndividualChatImpl(userID, queryUserID, getGroupInfo);
}

var isGroupMember = (userID, group) => {
  return group.members.includes(userID);
}

var getExistingIndividualChatImpl = (userID, queryUserID, getGroupInfo) => {
  return new Promise((resolve, reject) => {
    winston.debug('getExistingIndividualChat');

    Group.findOne({members: [userID, queryUserID]}).exec()
    .then(group => {
      if (group) {
        // console.log(group);
        resolve(getGroupInfo(group.groupID, userID));
      } else {
        resolve(null);
      }
    })
  });
}

var formatGroupInfo = (group) => {
  if (group) {
    return {
      name: group.name,
      groupID: group.groupID,
      admins: group.admins,
      members: group.members,
      isTrackingGroup: group.isTrackingGroup,
      profilePicture: group.profilePicture,
      meetingPoint: common.getMeetingPointInfo(group.meetingPoint)
    }
  } else {
    return null;
  }

}

var getUserDetail = (queryUserID, selfUserID) => {
  return getUsersDetails([queryUserID], selfUserID);
}

var getUsersDetails = (members, userID) => {
  return new Promise((resolve, reject) => {
    let userDetails = {};
    let promiseAll = members.map((memberUserID) => new Promise((resolve, reject) => {
      User.findOne({userID: memberUserID}).exec()
      .then((user) => { // assumption: user is valid (since all other routes validated, module.exports is only a GET route)
        if (user) {
          userDetails[memberUserID] = common.getPublicUserInfo(user);
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
          common.getCommonGroups(userID, memberUserID)
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
}

var getGroupInfo = (groupID, userID) => {
  return new Promise((resolve, reject) => {
    let lastMessage;

    Message.findOne({groupID: groupID}).exec()
    .then(message => {
      if (message) {
        lastMessage = {
          from: message.from,
          time: message.time,
          contentType: message.contentType,
          text: message.text
        }
      }
      return Group.findOne({groupID: groupID});
    })
    .then(group => {
      if (group) {
        let groupInfo = formatGroupInfo(group);
        groupInfo.lastMessage = lastMessage;  // needed for our existing client implementation

        getUsersDetails(group.members, userID)
        .then(usersDetails => {
          groupInfo["usersDetails"] = usersDetails;
          resolve(groupInfo);
        })
      } else {
        resolve();
      }
    })
    .catch(err => reject(err))
  });
}

class GroupsHelper {
  constructor(pGroup, pMessage, pUser) {
    Group = pGroup;
    Message = pMessage;
    User = pUser;
  }

  getExistingIndividualChat(userID, queryUserID) {
    return getExistingIndividualChat(userID, queryUserID)
  }

  // above with custom getGroupInfo function - for testing
  getExistingIndividualChatImpl(userID, queryUserID, getGroupInfo) {
    return getExistingIndividualChatImpl(userID, queryUserID, getGroupInfo);
  }

  formatGroupInfo(group) {
    return formatGroupInfo(group)
  }

  getGroupInfo(groupID, userID) {
    return getGroupInfo(groupID, userID)
  }

  getUserDetail(queryUserID, selfUserID) {
    return getUserDetail(queryUserID, selfUserID)
  }

  getUsersDetails(members, userID) {
    return getUsersDetails(members, userID)
  }

  isGroupMember(userID, group) {
    return isGroupMember(userID, group);
  }
}

module.exports = GroupsHelper;
