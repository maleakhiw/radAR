/**
 * Server Messaging System
 * Provides Messaging services for RadAR.
 */

const common = require('./common')
const addMetas = common.addMetas
const isArray = common.isArray
const unique = common.unique

const svs = require('./SVS')

// data models
const Chat = require('./models/chat')
const Message = require('./models/message')
const User = require('./models/user')
const LastChatID = require('./models/lastChatID')

module.exports.newChat = (req, res) => {
  let callback = (req, res) => {
    let userID = req.body.userID
    let participantUserIDs = req.body.participantUserIDs
    let name = req.body.name

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
      name: name,
      chatID: lastChatID.chatID,
      members: filteredUserIDs,
      admins: [userID]
    }))

    .then((chat) => User.findOne({ userID: userID }))

    .then((user) => {
      user.chats.push(chatID)
      return user.save()
    })

    .then((user) => {
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

module.exports.getChatsForUser = (req, res) => {
  let callback = (req, res) => {
    let userID = req.params.userID

    User.findOne({ userID: userID }).exec()

    .then((user) => {
      response = {
        success: true,
        errors: [],
        chats: user.chats
      }
      res.json(addMetas(response, "/api/accounts/:userID/chats"))

    })

    .catch((err) => {
      console.log(err)
      res.status(400).json({
        success: false,
        errors: []
      })
    })
  }

  svs.validateRequest(req, res, callback)
}

module.exports.getChat = (req, res) => {
  let callback = (req, res) => {
    let userID = req.params.userID
    let chatID = req.params.chatID

    Chat.findOne({ chatID: chatID }).exec()

    .then((chat) => {
      chatObj = {
        name: chat.name,
        admins: chat.admins,
        members: chat.members
      }

      if (chat) {
        res.json({
          success: true,
          errors: [],
          chat: chatObj
        })
      } else {
        // chat does not exist
        res.status(404).json({
          success: false,
          error: common.errorObjectBuilder(['invalidChatID'])
        })
      }
    })
  }

  svs.validateRequest(req, res, callback)

}

module.exports.getMessages = (req, res) => {
  let callback = (req, res) => {
    let chatID = parseInt(req.query.chatID) || parseInt(req.params.chatID)
    let userID = parseInt(req.body.userID) || parseInt(req.params.userID)

    Chat.findOne({ chatID: chatID }).exec()

    .then((chat) => {
      if (!chat) {
        res.status(400).json({
          success: false,
          errors: common.errorObjectBuilder(['invalidChatID'])
        })
        return
      }

      if (!chat.members.includes(userID)) {
        res.status(401).json({
          success: false,
          errors: common.errorObjectBuilder(['unauthorisedChat'])
        })
        return
      }

      return Message.find({chatID: chatID})
    })

    .then((messages) => {
      let messagesRes = messages.map((message) => {
        return {
          from: message.from,
          time: message.time,
          contentType: message.contentType,
          text: message.text,
          contentResourceID: message.contentResourceID
        }
      })

      res.send({
        success: true,
        errors: [],
        messages: messagesRes
      })
    })

    .catch((err) => {
      console.log(err)
      res.status(400).send({
        success: false,
        errors: common.errorObjectBuilder(['internalError'])
      })
    })
    // check Chat if exists
      // if exists, check if member
        // if member, return messages (for now return everything.)
        // otherwise
  }

  svs.validateRequest(req, res, callback)
}

module.exports.sendMessage = (req, res) => {
  let callback = (req, res) => {
    console.log(req.body)
    let from = req.body.userID
    let chatID = parseInt(req.params.chatID)
    let message = req.body.message

    console.log(from, chatID, message)

    errorKeys = []
    if (!chatID) errorKeys.push('missingChatID')
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

    // check if chatID exists
    Chat.find({ chatID: chatID }).exec()

    .then((chats) => {
      if (!chats.length) {
        res.status(400).json({
          success: false,
          errors: common.errorObjectBuilder(['invalidChatID']),
          sentMessage: null
        })
        return
      } else if (!chats[0].members.includes(from)) {  // not a member of the chat
        res.status(400).json({
          success: false,
          errors: common.errorObjectBuilder(['unauthorisedChat']),
          sentMessage: null
        })
        return
      } else {
        sentMessage = {
          from: from,
          chatID: chatID,
          time: Date.now(),
          contentType: "text",
          text: message,
          contentResourceID: null
        }
        return Message.create(sentMessage)
      }
    })

    .then((message) => {
      res.json({
        success: true,
        errors: null,
        sentMessage: sentMessage
      })

    })

    .catch((err) => {
      console.log(err)
      res.json({
        success: false,
        errors: common.errorObjectBuilder(['internalError']),
        sentMessage: null
      })
    })
  }

  svs.validateRequest(req, res, callback)
}
