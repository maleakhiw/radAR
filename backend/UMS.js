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

// const mongoose = module.parent.exports.mongoose   // import from index.js
const ONLINE_THRESHOLD_SEC = consts.ONLINE_THRESHOLD_SEC

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
      User.find({ userID: userID }).exec()

      .then((user) => {
        let friends = user.friends
        if (!friends) { // if undefined
          friends = []
        }
        userIDsToCheck = userIDsToCheck.filter((userID) => userID in friends)
        userIDsToCheck = [1, 2] // TODO: remove - testing only
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

        metadatas = metadatas.map((metadata) => new Promise((resolve, reject) => {
          let firstName = null
          let lastName = null
          let profilePicture = null

          User.findOne({ userID: metadata.userID }).exec()
          .then((user) => {
            let retVal = {
              firstName: user.firstName,
              lastName: user.lastName,
              profilePicture: user.profilePicture
            }

            resolve(retVal)
          })
          .catch((err) => {
            console.log(err)
            reject("dbError")
          })

        }))

        return Promise.all(metadatas) // this Promise is fulfilled when all the promises in the iterable (list) are fulfilled
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
    if (!invitedUserID) errorKeys.push('missingInvitedUserID')

    if (errorKeys.length) {
      response = {
        success: false,
        erros: common.errorObjectBuilder(errorKeys)
      }
    } else {
      response = {
        success: true,
        errors: [],
      }
    }

    res.json(response)
  }

  svs.validateRequest(req, res, callback)
}

module.exports.getFriends = (req, res) => {
  callback = (req, res) => {
    let userID = req.body.userID

    // get user information, access list of friends
    // for friend in friends, access information (map)
      // only access public fields
      // build up return json, return
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
