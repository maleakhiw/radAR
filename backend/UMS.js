/**
 * User Management System server-side component.
 * Handles user-related responsibilities and requests.
 * Also provides information/data on users.
 */

const common = require('./common')
const consts = require('./consts')
const svs = require('./SVS')

// Data models
const Metadata = require('./models/metadata')
const User = require('./models/user')
const Request = require('./models/request')
const LastRequestID = require('./models/lastRequestID')

// const mongoose = module.parent.exports.mongoose   // import from index.js
const ONLINE_THRESHOLD_SEC = consts.ONLINE_THRESHOLD_SEC

const getPublicUserInfo = common.getPublicUserInfo

// callback for '/UMS/isOnline' route
module.exports.isOnline = (req, res) => {
  callback = (req, res) => {
    let userID = req.body.userID
    let userIDsToCheck = req.body.userIDsToCheck

    let onlineUsers = null
    console.log(userIDsToCheck)
    // TODO: type checks for invalid type

    let errorKeys = []
    if (!userIDsToCheck) errorKeys.push('missingUserIDsToCheck')

    if (errorKeys.length) {
      response = {
        success: false,
        errors: common.errorObjectBuilder(errorKeys)
      }
    } else {
      // get friends first - don't let requester check online status of non-friends
      User.findOne({ userID: userID }).exec()

      .then((user) => {
        let friends = user.friends
        console.log(user)
        if (!friends) { // if undefined
          friends = []
        }

        console.log(friends)
        userIDsToCheck = userIDsToCheck.filter((userID) => friends.includes(userID))
        console.log(userIDsToCheck)
        return Metadata.find( { userID : { $in: userIDsToCheck } } )
      })

      .then((metadatas) => {
        // filter off the users who have not been online
        // console.log('metadatas', metadatas)
        // metadatas.map((metadata) => {
        //   console.log((Date.now() - metadata.lastSeen.getTime())/1000)
        // })
        metadatas = metadatas.filter((metadata) => (Date.now() - metadata.lastSeen.getTime())/1000 < ONLINE_THRESHOLD_SEC)

        onlineUsers = metadatas.map((metadata) => metadata.userID)

        console.log('metadatas_filtered', metadatas)

        let metadatasPromise = metadatas.map((metadata) => new Promise((resolve, reject) => {
          let firstName = null
          let lastName = null
          let profilePicture = null

          User.findOne({ userID: metadata.userID }).exec()
          .then((user) => resolve(getPublicUserInfo(user))) // Promise for the public user data
        }))

        return Promise.all(metadatasPromise) // this Promise is fulfilled when all the promises in the iterable (list) are fulfilled
      })

      .then((userInfos) => {
        console.log('userInfos', userInfos)
        onlineStatus = {}
        userIDsToCheck.map((userID) => {
          onlineStatus[userID] = (userID in onlineUsers)
        })

        response = {
          success: true,
          errors: [],
          onlineStatus: onlineStatus,
          userInfos: userInfos
        }
        res.json(response)
      })


      .catch((err) => {
        console.log(err)
      })

    }
  }

  svs.validateRequest(req, res, callback)
}

module.exports.addFriend = (req, res) => {
  callback = (req, res) => {
    let userID = req.body.userID
    let invitedUserID = req.body.invitedUserID

    let errorKeys = []
    function sendError() {  // assumption: variables are in closure
      let response = {
        success: false,
        errors: common.errorObjectBuilder(errorKeys)
      }
      res.json(response)
    }

    if (!invitedUserID) errorKeys.push('missingInvitedUserID')

    if (errorKeys.length) {
      response = {
        success: false,
        errors: common.errorObjectBuilder(errorKeys)
      }
    } else {
      let requestID = null

      Request.find({
        from: userID,
        to: invitedUserID,
        responded: false
      }).exec()
      .then((requests) => {
        // check if an existing request exists
        if (requests.length > 0) {
          return new Promise((resolve, reject) => {
            reject('Request already exists')
          })
        } else {
          return LastRequestID.findOneAndRemove({})
        }

      })

      .then((lastRequestID) => {
        console.log(lastRequestID)
        if (lastRequestID) {
          requestID = lastRequestID.requestID + 1
        } else {
          requestID = 1
        }
        return LastRequestID.create({ requestID: requestID })
      })

      .then((lastRequestID) => {
        console.log(lastRequestID)
        let request = {
          requestID: requestID,
          from: userID,
          to: invitedUserID,
          for: "friend",
          responded: false
        }
        console.log(request)

        // TODO: check if a request already exists for this!
        // TODO: add error message
        return Request.create(request)
      })

      .then((request) => {
        console.log('create', request)
        let response = {
          success: true,
          error: [],
          requestID: requestID
        }
        res.json(response)
      })

      .catch((err) => {
        console.log(err)
        if (err == 'Request already exists') {
          errorKeys.push('friendRequestAlreadyExists')
        } else {
          errorKeys.push('dbError')
        }
        sendError()
      })
    }

  }

  svs.validateRequest(req, res, callback)
}

module.exports.getFriendRequests = (req, res) => {
  callback = (req, res) => {
    let userID = req.body.userID
    let errorKeys = []
    function sendError() {  // assumption: variables are in closure
      let response = {
        success: false,
        errors: common.errorObjectBuilder(errorKeys)
      }
      res.json(response)
    }

    Request.find({ to: userID }).exec()
    .then((requests) => {
      console.log(requests)
      let requestsPromise = requests.map((request) => new Promise((resolve, reject) => {
        User.findOne({ userID: request.from }).exec()
        .then((user) => {
          let resolved = {
            requestID: request.requestID,
            from: request.from,
            userInfo: getPublicUserInfo(user)
          }
          resolve(resolved)
        }) // Promise for the public user data
        .catch((err) => console.log(err)) // TODO: send fail
      }))
      return Promise.all(requestsPromise)
    })

    .then((friendDetails) => {
      console.log(friendDetails)
      let response = {
        success: true,
        errors: [],
        friendDetails: friendDetails
      }
      res.json(response)
    })

    .catch((err) => {
      console.log(err)
      errorKeys.push('dbError')
      sendError()
    })
  }

  svs.validateRequest(req, res, callback)
}

module.exports.getFriends = (req, res) => {
  callback = (req, res) => {
    let userID = req.body.userID
    let friends = []
    let errorKeys = []
    function sendError() {  // assumption: variables are in closure
      let response = {
        success: false,
        errors: common.errorObjectBuilder(errorKeys)
      }
      res.json(response)
    }

    User.findOne({ userID: userID }).exec()
    .then((user) => {
      console.log(user)
      friends = user.friends
      if (!friends) friends = []
      return User.find({ userID: { $in: friends } })
    })

    .then((users) => {  // friends
      console.log('users', users)
      friends = users.map((user) => getPublicUserInfo(user))
      console.log('friends', friends)

      let response = {
        success: true,
        errors: [],
        friends: friends
      }
      res.json(response)
    })

    .catch((err) => {
      console.log(err)
      errorKeys.push('dbError')
      sendError()
    })
  }

  svs.validateRequest(req, res, callback)
}

module.exports.search = (req, res) => {
  callback = (req, res) => {
    let userID = req.body.userID
    let query = req.body.query
    let searchType = req.body.searchType

    // get user information, access list of friends
    // for friend in friends, access information (map)
      // only access public fields
      // build up return json, return
  }

  svs.validateRequest(req, res, callback)
}

module.exports.removeFriend = (req, res) => {
  callback = (req, res) => {
    let userID = req.body.userID
    let query = req.body.query
    let searchType = req.body.searchType

    // get user information, access list of friends
    // for friend in friends, access information (map)
      // only access public fields
      // build up return json, return
  }

  svs.validateRequest(req, res, callback)
}

module.exports.getInformation = (req, res) => {
  callback = (req, res) => {
    let userID = req.body.userID
    let username = req.body.username
    let queryUserID = req.body.queryUserID

    let errorKeys = []
    if (!queryUserID || !username) {
      errorKeys.push('missingQueryUserIDOrUsername')
    }

    if (errorKeys.length) {
      response = {
        success: false,
        erros: common.errorObjectBuilder(errorKeys)
      }
    } else {
      response = {  // TODO: replace with database calls
        success: true,
        errors: [],
        details: {
          firstName: "TestFirstName",
          lastName: "TestLastName",
          profilePictureResID: ""
        }
      }
    }

    res.json(response)
  }

  svs.validateRequest(req, res, callback)
}
