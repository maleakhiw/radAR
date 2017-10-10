/**
 * User Management System server-side component.
 * Handles user-related responsibilities and requests.
 * Also provides information/data on users.
 */

const common = require('../common')
const consts = require('../consts')

const SVS = require('./SVS')
let svs;

const isNumber = common.isNumber
const isArray = common.isArray

const isValidUser = common.isValidUser;
const sendUnauthorizedError = common.sendUnauthorizedError;
const sendInternalError = common.sendInternalError;

// logging framework
const winston = require('winston');

winston.level = 'debug';  // TODO use environment variable

// const mongoose = module.parent.exports.mongoose   // import from index.js
const ONLINE_THRESHOLD_SEC = consts.ONLINE_THRESHOLD_SEC
const getPublicUserInfo = common.getPublicUserInfo
const sendError = common.sendError

let User, Request, LastRequestID


/**
 * Validates a request to add a friend
 * @param req Express request object
 * @return errorKeys errors to be sent using sendError
 */
function validateAddFriend(req) {
  let userID = req.params.userID;
  let invitedUserID = req.body.invitedUserID;

  let errorKeys = [];
  if (!invitedUserID) errorKeys.push('missingInvitedUserID');

  if (userID && invitedUserID && userID == invitedUserID) {
    errorKeys.push('selfInviteError');
  }

  return errorKeys;

}

var checkIfAlreadyFriends = (userID, invitedUserID) => new Promise((resolve, reject) => {
  User.findOne({userID: userID}).then((user) => {
    if (user.friends.includes(invitedUserID)) {
      reject('invitedUserIDAlreadyAdded');
    } else {
      resolve();
    }
  })
})

var checkIfRequestAlreadySent = (userID, invitedUserID) => new Promise((resolve, reject) => {
  Request.findOne({$and: [
    {from: userID},
    {to: invitedUserID}
  ]}).then((request) => {
    if (request) {
      reject('friendRequestAlreadyExists');
    } else {
      resolve();
    }
  })
})

var getRequestIDForNewRequest = () => new Promise((resolve, reject) => {
  Request.findOne().sort({requestID: -1}).exec()
  .then((request) => {
    if (request) {
      resolve(request.requestID + 1);
    } else {
      resolve(1);
    }
  })
})

var validateDeleteRequest = (req) => new Promise((resolve, reject) => {
  let errorKeys = [];
  let userID = req.params.userID;
  let requestID = req.params.requestID;

  // userID assumed to be valid due to authentication middleware.

  // find a request
  Request.findOne({requestID: requestID}).exec()
  .then((request) => {
    if (request == null) {
      errorKeys.push('invalidRequestID');
    } else {
      if (request.from != userID) {
        errorKeys.push('invalidRequestID');
      }
    }

    resolve(errorKeys);
  })
  .catch(err => {
    reject(err);
  });

});

var deleteRequest = (requestID, res) => {
  Request.remove({requestID: requestID}).exec()
  .then((result) => {
    res.json({
      success: true,
      errors: []
    })
  })
  .catch((err) => {
    winston.error(err);
    common.sendInternalError(res);
  })
}

module.exports = class UMS {
  constructor(pUser, pRequest) {
    User = pUser
    Request = pRequest
    svs = new SVS(User)
  }

  updateProfile(req, res) {
    /*
      HTTP PUT {serverURL}/api/accounts/:userID

      Body:
      {
        username: String (optional),  // validated
        firstName: String (optional),
        lastName: String (optional),
        email: String (optional), // validated
        profilePicture: String (optional),  // validated -> needs to point to a valid resource on the server
        profileDesc: String (optional)
      }

      Headers:
      token: (token issued by the server)
    */

    let userID = req.params.userID;

    let toUpdate = {};
    let errorKeys = [];

    let username = req.body.username;
    let firstName = req.body.firstName;
    let lastName = req.body.lastName;
    let email = req.body.email;
    let profilePicture = req.body.profilePicture;
    let profileDesc = req.body.profileDesc;

    // validation
    /*
    1. Valid username -> not taken.
    2. Valid email -> not taken + correct format
    */

    function updateProfile(errorKeys, toUpdate) {
      if (errorKeys.length) {
        common.sendError(res, errorKeys);
        return;
      }

      if (firstName) toUpdate['firstName'] = firstName;
      if (lastName) toUpdate['lastName'] = lastName;
      if (profileDesc) toUpdate['profileDesc'] = profileDesc;
      // NOTE to empty profileDesc, send in a string containing a space character.
      if (new String(profileDesc).valueOf() == new String(" ".valueOf())) {
        profileDesc = "";
      }

      User.findOneAndUpdate({userID: userID}, {
        "$set": toUpdate
      }).exec((err, user) => {
        if (err) {
          common.sendInternalError(res);
        } else {
          res.json({
            success: true,
            errors: []
          });
        }
      });
    }

    if (username) {
      common.isUsernameUnique(username).then((isUnique) => {
        if (isUnique) {
          toUpdate['username'] = username;
        } else {
          errorKeys.push('invalidUsername');
        }
      });
    }
    if (email) {
      if (common.isValidEmail(email)) {
        toUpdate['email'] = email;
      } else {
        errorKeys.push('invalidEmail');
      }
    }

    if (profilePicture) {
      common.isValidPicture(profilePicture)
      .then(() => {
        toUpdate['profilePicture'] = profilePicture;
        updateProfile(errorKeys, toUpdate);
      })
      .catch((err) => {
        if (err == 'invalidResourceID') {
          errorKeys.push('invalidResourceID');
        }
        if (err == 'invalidMimetype') {
          errorKeys.push('invalidResourceID');
        }
        updateProfile(errorKeys, toUpdate);
      })
    } else {
      updateProfile(errorKeys, toUpdate);
    }



  }

  // TODO refactor, write unit tests for isOnline
  isOnline(req, res) {
    let userID = req.params.userID
    let userIDsToCheck = req.query.userIDsToCheck

    let onlineUsers = null

    if (userIDsToCheck instanceof Array) {
      userIDsToCheck = userIDsToCheck.map((entry) => parseInt(entry))
    } else {
      userIDsToCheck = [parseInt(userIDsToCheck)]
    }

    // TODO: type checks for invalid type

    let errorKeys = []
    if (!(req.query.hasOwnProperty('userIDsToCheck'))) {
      errorKeys.push('missingUserIDsToCheck')
    }

    if (!userIDsToCheck instanceof Array) {
      errorKeys.push('invalidUserIDsToCheck')
    }

    if (errorKeys.length) {
      let response = {
        success: false,
        errors: common.errorObjectBuilder(errorKeys)
      }
      res.json(response)
    } else {
      // get friends first - don't let requester check online status of non-friends
      User.findOne({ userID: userID }).exec()

      .then((user) => {
        let friends = user.friends
        // winston.debug(user)
        if (!friends) { // if undefined
          friends = []
        }

        // winston.debug(friends)
        userIDsToCheck = userIDsToCheck.filter((userID) => friends.includes(userID))
        // winston.debug(userIDsToCheck)
        return User.find( { userID : { $in: userIDsToCheck } } )
      })

      .then((users) => {
        // filter off the users who have not been online
        users.map((user) => {
          winston.debug((Date.now() - user.lastSeen.getTime())/1000)
        })
        users = users.filter((user) => (Date.now() - user.lastSeen.getTime())/1000 < ONLINE_THRESHOLD_SEC)

        onlineUsers = users.map((user) => user.userID)

        // winston.debug('users_filtered', users)

        let usersPromise = users.map((user) => new Promise((resolve, reject) => {
          let firstName = null
          let lastName = null
          let profilePicture = null

          User.findOne({ userID: user.userID }).exec()
          .then((user) => resolve(getPublicUserInfo(user))) // Promise for the public user data
        }))

        return Promise.all(usersPromise) // this Promise is fulfilled when all the promises in the iterable (list) are fulfilled
      })

      .then((userInfos) => {
        // winston.debug('userInfos', userInfos)
        let onlineStatus = {}
        // winston.debug('onlineUsers', onlineUsers)
        userIDsToCheck.map((userID) => {
          onlineStatus[userID] = onlineUsers.includes(userID)
        })

        let response = {
          success: true,
          errors: [],
          onlineStatus: onlineStatus,
          userInfos: userInfos
        }
        res.json(response)
      })


      .catch((err) => {
        winston.error(err)
      })

    }

  }

  cancelRequest(req, res) {
    let userID = req.params.userID;
    let requestID = req.params.requestID;

    validateDeleteRequest(req)
    .then((errorKeys) => {
      // validation: requestID exists, user sent the request
      if (errorKeys.length) {
        sendError(res, errorKeys);
        return;
      }

      deleteRequest(requestID, res);
    })
    .catch((err) => {
      common.sendInternalError(res);
    })

  }

  addFriend(req, res) {
    let userID = req.params.userID  // TODO check for missing userID
    let invitedUserID = req.body.invitedUserID

    let errorKeys = validateAddFriend(req);

    // TODO: should not be able to send request if already friends

    if (errorKeys.length) {
      sendError(res, errorKeys);
      return;
    }

    /*
    - find if invited userID exists -> if not, throw 'invalidUserID'
    - find if a user already has the invited user in his/her friends list
    - find if an existing friend request already exists
    - create a new request
    */
    let requestID;

    isValidUser(invitedUserID)
    .then(() => {
      return checkIfAlreadyFriends(userID, invitedUserID);
    })
    .then(() => {
      return checkIfRequestAlreadySent(userID, invitedUserID);
    })
    .then(() => {
      return getRequestIDForNewRequest();
    })
    .then((requestIDRes) => {
      requestID = requestIDRes;
      return Request.create({
        requestID: requestID,
        from: userID,
        to: invitedUserID,
        for: "friend",
        responded: false
      })
    })
    .then((request) => {
      // request successfully created
      let response = {
        success: true,
        error: [],
        requestID: requestID
      }
      res.json(response)
    })
    .catch((err) => {
      if (err == 'invalidUserID') {
        sendError(res, ['invalidUserID']);
      } else if (err == 'invitedUserIDAlreadyAdded') {
        sendError(res, ['invitedUserIDAlreadyAdded']);
      } else if (err == 'friendRequestAlreadyExists') {
        sendError(res, ['friendRequestAlreadyExists']);
      } else {
        winston.error(err);
        sendInternalError(res);
      }
    })
  }

  getFriendRequests(req, res) {
    let userID = req.params.userID  // TODO check for missing userID
    let errorKeys = []
    function sendError() {  // assumption: variables are in closure
      let response = {
        success: false,
        errors: common.errorObjectBuilder(errorKeys)
      }
      res.json(response)
    }

    Request.find({ to: userID, responded: false }).exec()
    .then((requests) => {

      let requestsPromise = requests.map((request) => new Promise((resolve, reject) => {
        User.findOne({ userID: request.from }).exec()
        .then((user) => {
          let publicUserInfo = getPublicUserInfo(user)
          let resolved = {
            requestID: request.requestID,
            from: request.from,
            firstName: publicUserInfo.firstName,
            lastName: publicUserInfo.lastName,
            profilePicture: publicUserInfo.profilePicture
          }
          resolve(resolved)
        }) // Promise for the public user data
        .catch((err) => winston.error(err)) // TODO: send fail
      }))

      return Promise.all(requestsPromise)
    })

    .then((requestsDetails) => {
      let response = {
        success: true,
        errors: [],
        requestDetails: requestsDetails
      }
      res.json(response)
    })

    .catch((err) => {
      winston.error(err)
      errorKeys.push('internalError')
      sendError()
    })

  }

  getInformation(req, res) {
    let queryUserID = req.params.userID
    let username = req.body.username

    let errorKeys = []
    function sendError() {  // assumption: variables are in closure
      let response = {
        success: false,
        errors: common.errorObjectBuilder(errorKeys)
      }
      res.json(response)
    }

    winston.debug(req.body)

    if (!username && !queryUserID) {
      errorKeys.push('missingUserIDOrUsername')
      sendError()
    } else {
      if (username) {
        User.findOne( { username: username } ).exec()
        .then((user) => {
          // winston.debug(user)
          let userInfo = getPublicUserInfo(user)
          let response = {
            success: true,
            errors: [],
            details: userInfo
          }
          res.json(response)
        })
        .catch((err) => {
          winston.error(err);
          errorKeys.push('internalError')
          sendError()
        })
      } else {
        User.findOne( { userID: queryUserID } ).exec()
        .then((user) => {
          // winston.debug(user)
          let userInfo = getPublicUserInfo(user)
          let response = {
            success: true,
            errors: [],
            details: userInfo
          }
          res.json(response)
        })
        .catch((err) => {
          winston.error(err);
          errorKeys.push('internalError')
          sendError()
        })
      }
    }

  }


  respondToRequest(req, res) {
    let userID = req.params.userID
    let requestID = req.params.requestID
    let action = req.body.action
    let errorKeys = []

    function sendError() {  // assumption: variables are in closure
      let response = {
        success: false,
        errors: common.errorObjectBuilder(errorKeys)
      }
      res.json(response)
    }

    if (!requestID) {
      errorKeys.push('missingRequestID')
      sendError()
      return;
    }

    Request.findOne({
      requestID: requestID,
      to: userID,
      responded: false
    }).exec()

    .then((request) => {
      if (!request) {
        throw new Error('invalidRequestID')
      } else {
        if (action == 'accept' || action == 'decline') {
          // update request
          request.responded = true
          request.save()

          if (action == 'accept') {
            let from = request.from
            let to = request.to
            let usersToUpdate = [from, to]

            User.find({ userID: { $in: usersToUpdate } }).exec()
            .then((users) => {
              users.map((user) => {
                if (user.userID == from) {
                  user.friends.push(to)
                } else {
                  user.friends.push(from)
                }
                user.save() // TODO use Promise.all to validate
              })

              let response = {
                success: true,
                error: []
              }
              res.json(response)
            })

          }

          // decline friend request
          else {
            request.responded = true
            request.save()

            let response = {
              success: true,
              error: []
            }

            res.json(response)
          }

        } else {
          throw new Error('invalidAction')
        }
      }
    })

    .catch((err) => {
      if (err == 'Error: invalidAction') {
        errorKeys.push('invalidAction')
      } else if (err == 'Error: invalidRequestID') {
        errorKeys.push('invalidRequestID')
      } else {
        winston.error(err)
        errorKeys.push('internalError')
      }
      sendError()
    })


  }



  getFriends(req, res) {
    let userID = req.params.userID
    let friends = []
    let errorKeys = []
    function sendError() {  // assumption: variables are in closure
      let response = {
        success: false,
        errors: common.errorObjectBuilder(errorKeys)
      }
      res.json(response)
    }

    if (!userID) {
      errorKeys.push('missingUserID')
      sendError()
      return
    }

    User.findOne({ userID: userID }).exec()
    .then((user) => {
      // winston.debug(user)
      if (!user) {
        throw new Error('invalidUserID') // should not happen
      }
      friends = user.friends
      if (!friends) friends = []
      return User.find({ userID: { $in: friends } })
    })

    .then((users) => {  // friends
      // winston.debug('users', users)
      friends = users.map((user) => getPublicUserInfo(user))
      // winston.debug('friends', friends)

      let response = {
        success: true,
        errors: [],
        friends: friends
      }
      res.json(response)
    })

    .catch((err) => {
      if (err == 'Error: invalidUserID') {
        errorKeys.push('invalidUserID')
        sendError()
        return
      }
      winston.error(err)
      errorKeys.push('internalError')
      sendError()
    })
  }

  search(req, res) {
    let userID = req.query.userID
    let query = req.query.query
    let searchType = req.query.searchType

    let errorKeys = []
    function sendError() {  // assumption: variables are in closure
      let response = {
        success: false,
        errors: common.errorObjectBuilder(errorKeys)
      }
      res.json(response)
    }

    if (!query) errorKeys.push('missingQuery')
    if (!searchType) errorKeys.push('missingSearchType')

    if (errorKeys.length) {

      let response = {
        success: false,
        errors: common.errorObjectBuilder(errorKeys)
      }
      res.json(response)
    } else {
      if (searchType == 'name') {
        let regexQuery = new RegExp(query, "i") // case-insensitive matching
        // winston.debug(regexQuery)
        User.find({ $or:
          [
            { firstName: regexQuery },
            { lastName: regexQuery }
          ]
        }).exec()

        .then((users) => {
          // winston.debug(users)
          if (users.length) {
            users = users.map(getPublicUserInfo)
          }
          let response = {
            success: true,
            errors: [],
            results: users
          }
          res.json(response)
        })

        .catch((err) => {
          winston.error(err)
          errorKeys.push('dbError')
          sendError()
        })
      }
      else if (searchType == 'username') {
        User.find({ username: query }).exec()

        .then((users) => {
          if (users.length) {
            users = users.map(getPublicUserInfo)
          }

          let response = {
            success: true,
            errors: [],
            results: users
          }
          res.json(response)
        })

        .catch((err) => {
          winston.error(err)
          errorKeys.push('dbError')
          sendError()
        })

      } else if (searchType == 'email') {
        User.find({ email: query }).exec()
        .then((users) => {
          users = users.map(getPublicUserInfo)
          let response = {
            success: true,
            errors: [],
            results: users
          }
          res.json(response)
        })
        .catch((err) => {
          winston.error(err)
          errorKeys.push('dbError')
          sendError()
        })
      } else {
        errorKeys.push('invalidSearchType')
        sendError()
      }

    }

  }



}
