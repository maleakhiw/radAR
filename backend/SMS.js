/**
 * Server Messaging System
 * Provides Messaging services for RadAR.
 */

const common = require('./common')
const addMetas = common.addMetas
const isArray = common.isArray
const unique = common.unique

const SVS = require('./SVS')
let svs;

// data models
let Chat
let Message
let User
let LastChatID

module.exports = class SMS {
  constructor(pChat, pMessage, pUser, pLastRequestID, pLastChatID, pMetadata, pLastUserID, pPasswordHash) {
      Chat = pChat
      Message = pMessage
      User = pUser
      LastChatID = pLastChatID
      svs = new SVS(pUser, pMetadata, pLastUserID, pPasswordHash)
  }

  newChat(req, res) {
      let userID = req.params.userID
      let participantUserIDs = req.body.participantUserIDs
      let name = req.body.name

      let errorKeys = []
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
      let chat

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

      .then((lastChatID) => { // TODO remove lastChatID collection
        chat = {
          name: name,
          chatID: lastChatID.chatID,
          members: filteredUserIDs,
          admins: [userID]
        }
        Chat.create(chat)
      })

      .then((chat) => User.findOne({ userID: userID }))

      .then((user) => { // add the user to the chat
        user.chats.push(chatID)
        return user.save()
      })

      .then((user) => {
        res.json({
          success: true,
          errors: [],
          chat: chat
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


  }

  getChatsForUser(req, res) {
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
        res.json({
          success: false,
          errors: []
        })
      })

  }

  getChat(req, res) {
      let userID = req.params.userID
      let chatID = req.params.chatID

      Chat.findOne({ chatID: chatID }).exec()

      .then((chat) => {
        chatObj = {
          name: chat.name,
          chatID: chatID,
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

  getMessages(req, res) {
      let chatID = parseInt(req.query.chatID) || parseInt(req.params.chatID)
      let userID = parseInt(req.body.userID) || parseInt(req.params.userID)

      Chat.findOne({ chatID: chatID }).exec()

      .then((chat) => {
        if (!chat) {
          res.json({
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
        res.send({
          success: false,
          errors: common.errorObjectBuilder(['internalError'])
        })
      })
      // check Chat if exists
        // if exists, check if member
          // if member, return messages (for now return everything.)
          // otherwise

  }

  sendMessage(req, res) {
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
          res.json({
            success: false,
            errors: common.errorObjectBuilder(['invalidChatID']),
            sentMessage: null
          })
          return
        } else if (!chats[0].members.includes(from)) {  // not a member of the chat
          res.json({
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

}
