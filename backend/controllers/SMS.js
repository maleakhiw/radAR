/**
 * Server Messaging System
 * Provides Messaging services for RadAR.
 */

const common = require('../common')
const addMetas = common.addMetas
const isArray = common.isArray
const unique = common.unique

const SVS = require('./SVS')
let svs;

// data models
let Group
let Message
let User
let LastGroupID

module.exports = class SMS {
  constructor(pGroup, pMessage, pUser) {
      Group = pGroup
      Message = pMessage
      User = pUser
      svs = new SVS(pUser)
  }

  newGroup(req, res) {
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
      let groupID
      let group

      User.find( { userID: { $in: participantUserIDs } } ).exec()

      .then((users) => {  // userIDs that are actually on the system
        filteredUserIDs = users.map((user) => user.userID)

        if (filteredUserIDs.length == 0) {
          throw new Error('invalidParticipantUserIDs')
        }

        return LastGroupID.findOneAndRemove({})
      })

      .then((lastGroupID) => {
        if (lastGroupID) {
          groupID = lastGroupID.groupID + 1
        } else {
          groupID = 1
        }

        return LastGroupID.create({ // update the value in the system
          groupID: groupID
        })
      })

      .then((lastGroupID) => { // TODO remove lastGroupID collection
        group = {
          name: name,
          groupID: lastGroupID.groupID,
          members: filteredUserIDs,
          admins: [userID]
        }
        Group.create(group)
      })

      .then((group) => User.findOne({ userID: userID }))

      .then((user) => { // add the user to the group
        user.groups.push(groupID)
        return user.save()
      })

      .then((user) => {
        res.json({
          success: true,
          errors: [],
          group: group
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

  getGroupsForUser(req, res) {
      let userID = req.params.userID

      User.findOne({ userID: userID }).exec()

      .then((user) => {
        response = {
          success: true,
          errors: [],
          groups: user.groups
        }
        res.json(addMetas(response, "/api/accounts/:userID/groups"))

      })

      .catch((err) => {
        console.log(err)
        res.json({
          success: false,
          errors: []
        })
      })

  }

  getGroup(req, res) {
      let userID = req.params.userID
      let groupID = req.params.groupID

      Group.findOne({ groupID: groupID }).exec()

      .then((group) => {
        groupObj = {
          name: group.name,
          groupID: groupID,
          admins: group.admins,
          members: group.members
        }

        if (group) {
          res.json({
            success: true,
            errors: [],
            group: groupObj
          })
        } else {
          // group does not exist
          res.status(404).json({
            success: false,
            error: common.errorObjectBuilder(['invalidGroupID'])
          })
        }
      })


  }

  getMessages(req, res) {
      let groupID = parseInt(req.query.groupID) || parseInt(req.params.groupID)
      let userID = parseInt(req.body.userID) || parseInt(req.params.userID)

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

        return Message.find({groupID: groupID})
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
      // check Group if exists
        // if exists, check if member
          // if member, return messages (for now return everything.)
          // otherwise

  }

  sendMessage(req, res) {
      console.log(req.body)
      let from = req.body.userID
      let groupID = parseInt(req.params.groupID)
      let message = req.body.message

      console.log(from, groupID, message)

      errorKeys = []
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
      Group.find({ groupID: groupID }).exec()

      .then((groups) => {
        if (!groups.length) {
          res.json({
            success: false,
            errors: common.errorObjectBuilder(['invalidGroupID']),
            sentMessage: null
          })
          return
        } else if (!groups[0].members.includes(from)) {  // not a member of the group
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
