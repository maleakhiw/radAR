/**
 * Server Messaging System
 * Provides Messaging services for RadAR.
 */

const common = require('./common')
const isArray = common.isArray
const unique = common.unique

const svs = require('./SVS')

// data models
const Chat = require('./models/chat')
const Message = require('./models/message')
const User = require('./models/user')
const LastChatID = require('./models/lastChatID')

module.exports.newChat = (req, res) => {
  console.log('newChat')
  let callback = (req, res) => {
    let userID = req.body.userID
    let participantUserIDs = req.body.participantUserIDs

    errorKeys = []
    // TODO: get rid of code duplication, move sendError() to common.js
    function sendError() { // assumption: variables are in closure
        let response = {
            success: false,
            errors: common.errorObjectBuilder(errorKeys)
        }
        res.status(401).json(response)
    }


    if (!isArray(participantUserIDs)) {
      errorKeys.push('invalidParticipantUserIDs')
      sendError()
      return
    }

    participantUserIDs = participantUserIDs.map((entry) => parseInt(entry))
    participantUserIDs.push(userID) // add the requester to participants
    participantUserIDs = unique(participantUserIDs) // filter to only unique userIDs

    let filteredUserIDs
    let chatID

    User.find( { userID: { $in: participantUserIDs } } ).exec()

    .then((users) => {  // userIDs that are actually on the system
      filteredUserIDs = users.map((user) => user.userID)

      if (filteredUserIDs.length == 0) {
        throw new Error('invalidParticipantUserIDs')
      }

      return LastChatID.findOneAndRemove({})
    })

    .then((lastChatID) => {
      if (lastChatID) {
        chatID = lastChatID.chatID + 1
      } else {
        chatID = 1
      }

      return LastChatID.create({ // update the value in the system
        chatID: chatID
      })
    })

    .then((lastChatID) => Chat.create({
      chatID: lastChatID.chatID,
      members: filteredUserIDs,
      admins: [userID]
    }))

    .then((chat) => {
      res.json({
        success: true,
        errors: [],
        chatID: chatID
      })
    })

    .catch((err) => {
      console.log(err)
      if (err == 'Error: invalidParticipantUserIDs') {
        errorKeys.push('invalidParticipantUserIDs')
        sendError()
        return
      }

      errorKeys.push('internalError')
      sendError()
      return
    })


    // Group.create({
    //   name: name,
    //   groupID: 1,  // TODO: use number from LastGroupID collection as with UserID
    //   members: [],
    //   admins: [userID],
    //   footprints: [],
    //   meetingPoint: null
    // })
    //
    // .then((group) => {
    //   res.json({
    //     success: true,
    //     errors: []
    //   })
    // })
    //
    // .catch((err) => {
    //   errorKeys.push('internalError')
    //   sendError()
    // })
  }

  svs.validateRequest(req, res, callback)
}
