/**
 * User Management System server-side component.
 * Handles user-related responsibilities and requests.
 * Also provides information/data on users.
 */

const common = require('./common')
const consts = require('./consts')

const SVS = require('./SVS')
let svs;

const isNumber = common.isNumber
const isArray = common.isArray

// const mongoose = module.parent.exports.mongoose   // import from index.js
const ONLINE_THRESHOLD_SEC = consts.ONLINE_THRESHOLD_SEC
const getPublicUserInfo = common.getPublicUserInfo

let Metadata, User, Request, LastRequestID

module.exports = class UMS {
  constructor(pMetadata, pUser, pRequest, pLastRequestID, pLastUserID) {
    Metadata = pMetadata
    User = pUser
    Request = pRequest
    LastRequestID = pLastRequestID
    svs = new SVS(User, Metadata, pLastUserID)
  }

  isOnline(req, res) {
    let callback = (req, res) => {
      let userID = req.body.userID
      let userIDsToCheck = req.query.userIDsToCheck

      let onlineUsers = null
      console.log(userIDsToCheck)

      if (userIDsToCheck instanceof Array) {
        userIDsToCheck = userIDsToCheck.map((entry) => parseInt(entry))
      } else {
        userIDsToCheck = [parseInt(userIDsToCheck)]
      }

      console.log(userIDsToCheck)
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
          // console.log(user)
          if (!friends) { // if undefined
            friends = []
          }

          // console.log(friends)
          userIDsToCheck = userIDsToCheck.filter((userID) => friends.includes(userID))
          // console.log(userIDsToCheck)
          return Metadata.find( { userID : { $in: userIDsToCheck } } )
        })

        .then((metadatas) => {
          // filter off the users who have not been online
          console.log('metadatas', metadatas)
          metadatas.map((metadata) => {
            console.log((Date.now() - metadata.lastSeen.getTime())/1000)
          })
          metadatas = metadatas.filter((metadata) => (Date.now() - metadata.lastSeen.getTime())/1000 < ONLINE_THRESHOLD_SEC)

          onlineUsers = metadatas.map((metadata) => metadata.userID)

          // console.log('metadatas_filtered', metadatas)

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
          // console.log('userInfos', userInfos)
          onlineStatus = {}
          // console.log('onlineUsers', onlineUsers)
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
          console.log(err)
        })

      }
    }

    svs.validateRequest(req, res, callback)
  }

  addFriend(req, res) {
    let callback = (req, res) => {
      let userID = req.body.userID
      let invitedUserID = req.body.invitedUserID

      let errorKeys = []
      function sendError() {  // assumption: variables are in closure
        let response = {
          success: false,
          errors: common.errorObjectBuilder(errorKeys)
        }
        res.status(400).json(response)
      }

      if (!invitedUserID) errorKeys.push('missingInvitedUserID')

      if (userID && invitedUserID && userID == invitedUserID) {
        errorKeys.push('selfInviteError')
      }

      // TODO: should not be able to send request if already friends

      if (errorKeys.length) {
        sendError()
      } else {
        let requestID = null

        User.find({
          userID: invitedUserID
        }).exec()
        .then((users) => {
          if (!users.length) {
            throw new Error('invalidUserID')
          } else {
            return Request.find({
              from: userID,
              to: invitedUserID,
              responded: false
            })
          }
        })

        .then((requests) => {
          if (requests.length) {
            return new Promise((resolve, reject) => {
              reject('Request already exists')
            })
          } else {
            return LastRequestID.findOneAndRemove({})
          }

        })

        .then((lastRequestID) => {
          // console.log(lastRequestID)
          if (lastRequestID) {
            requestID = lastRequestID.requestID + 1
          } else {
            requestID = 1
          }
          return LastRequestID.create({ requestID: requestID })
        })

        .then((lastRequestID) => {
          // console.log(lastRequestID)
          let request = {
            requestID: requestID,
            from: userID,
            to: invitedUserID,
            for: "friend",
            responded: false
          }
          // console.log(request)

          // TODO: check if a request already exists for this!
          // TODO: add error message
          return Request.create(request)
        })

        .then((request) => {
          // console.log('create', request)
          let response = {
            success: true,
            error: [],
            requestID: requestID
          }
          res.json(response)
        })

        .catch((err) => {
          // console.log(err)
          if (err == 'Request already exists') {
            errorKeys.push('friendRequestAlreadyExists')
          } else if (err == 'Error: invalidUserID') {
            errorKeys.push('invalidUserID')
          } else {
            errorKeys.push('internalError')
          }
          sendError()
        })
      }

    }

    svs.validateRequest(req, res, callback)
  }

  getFriendRequests(req, res) {
    let callback = (req, res) => {
      let userID = req.body.userID
      let errorKeys = []
      function sendError() {  // assumption: variables are in closure
        let response = {
          success: false,
          errors: common.errorObjectBuilder(errorKeys)
        }
        res.status(400).json(response)
      }

      Request.find({ to: userID, responded: false }).exec()
      .then((requests) => {
        // console.log(requests)
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
          .catch((err) => console.log(err)) // TODO: send fail
        }))
        return Promise.all(requestsPromise)
      })

      .then((requestsDetails) => {
        // console.log(requestsDetails)
        let response = {
          success: true,
          errors: [],
          requestDetails: requestsDetails
        }
        res.json(response)
      })

      .catch((err) => {
        console.log(err)
        errorKeys.push('internalError')
        sendError()
      })
    }

    svs.validateRequest(req, res, callback)
  }

  getInformation(req, res) {
    let callback = (req, res) => {
      let queryUserID = req.body.queryUserID
      let username = req.body.username

      let errorKeys = []
      function sendError() {  // assumption: variables are in closure
        let response = {
          success: false,
          errors: common.errorObjectBuilder(errorKeys)
        }
        res.status(400).json(response)
      }

      console.log(req.body)

      if (!username && !queryUserID) {
        errorKeys.push('missingUserIDOrUsername')
        sendError()
      } else {
        if (username) {
          User.findOne( { username: username } ).exec()
          .then((user) => {
            // console.log(user)
            let userInfo = getPublicUserInfo(user)
            let response = {
              success: true,
              errors: [],
              details: userInfo
            }
            res.json(response)
          })
          .catch((err) => {
            errorKeys.push('internalError')
            sendError()
          })
        } else {
          User.findOne( { userID: queryUserID } ).exec()
          .then((user) => {
            // console.log(user)
            let userInfo = getPublicUserInfo(user)
            let response = {
              success: true,
              errors: [],
              details: userInfo
            }
            res.json(response)
          })
          .catch((err) => {
            errorKeys.push('internalError')
            sendError()
          })
        }
      }

    }

    svs.validateRequest(req, res, callback)
  }

  respondToRequest(req, res) {
    let callback = (req, res) => {
      let userID = req.body.userID
      // let requestID = req.body.requestID
      let requestID = req.params.requestID
      let action = req.body.action
      let errorKeys = []

      function sendError() {  // assumption: variables are in closure
        let response = {
          success: false,
          errors: common.errorObjectBuilder(errorKeys)
        }
        res.status(400).json(response)
      }

      if (!requestID) {
        errorKeys.push('missingRequestID')
        sendError()
      } else {
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
                    user.save()

                    let response = {
                      success: true,
                      error: []
                    }
                    res.json(response)

                  })
                })

              } else {
                let response = {
                  success: true,
                  error: []
                }
                res.json(response)
              }
            }
            else {
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
            console.log(err)
            errorKeys.push('internalError')
          }
          sendError()
        })
      }

    }


    svs.validateRequest(req, res, callback)
  }

  getFriends(req, res) {
    let callback = (req, res) => {
      let userID = req.body.userID
      let friends = []
      let errorKeys = []
      function sendError() {  // assumption: variables are in closure
        let response = {
          success: false,
          errors: common.errorObjectBuilder(errorKeys)
        }
        res.status(400).json(response)
      }

      User.findOne({ userID: userID }).exec()
      .then((user) => {
        // console.log(user)
        friends = user.friends
        if (!friends) friends = []
        return User.find({ userID: { $in: friends } })
      })

      .then((users) => {  // friends
        // console.log('users', users)
        friends = users.map((user) => getPublicUserInfo(user))
        // console.log('friends', friends)

        let response = {
          success: true,
          errors: [],
          friends: friends
        }
        res.json(response)
      })

      .catch((err) => {
        console.log(err)
        errorKeys.push('internalError')
        sendError()
      })
    }

    svs.validateRequest(req, res, callback)
  }

  search(req, res) {
    let callback = (req, res) => {
      let userID = req.query.userID
      let query = req.query.query
      let searchType = req.query.searchType

      let errorKeys = []
      function sendError() {  // assumption: variables are in closure
        let response = {
          success: false,
          errors: common.errorObjectBuilder(errorKeys)
        }
        res.status(400).json(response)
      }

      if (!query) errorKeys.push('missingQuery')
      if (!searchType) errorKeys.push('missingSearchType')

      if (errorKeys.length) {
        response = {
          success: false,
          errors: common.errorObjectBuilder(errorKeys)
        }
        res.json(response)
      } else {
        if (searchType == 'name') {
          let regexQuery = new RegExp(query, "i") // case-insensitive matching
          // console.log(regexQuery)
          User.find({ $or:
            [
              { firstName: regexQuery },
              { lastName: regexQuery }
            ]
          }).exec()

          .then((users) => {
            // console.log(users)
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
            console.log(err)
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
            console.log(err)
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
            console.log(err)
            errorKeys.push('dbError')
            sendError()
          })
        } else {
          errorKeys.push('invalidSearchType')
          sendError()
        }

      }

    }

    svs.validateRequest(req, res, callback)
  }



}
