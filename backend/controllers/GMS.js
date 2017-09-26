/**
 * Group Management System server-side component.
 * Provides groups' information and group management services.
 */

const common = require('../common')
const svs = require('./controllers/SVS')

// data models
const Group = require('../models/group')

// NOTE: for iteration 2
module.exports.getGroupInfo = function(req, res) {
  callback = function(req, res) {
    let groupID = req.body.groupID
    errorKeys = []
    if (!groupID) errorKeys.push('missingGroupID')

    if (errorKeys.length) {
      response = {
        success: false,
        errors: common.errorObjectBuilder(errorKeys)
      }
    } else {
      response = {
        success: true,
        errors: [],
        info: {
          name: "TestGroup",
          members: [1, 3, 4, 5],
          description: "Test group (stub). Replace with actual group returned from data store",
          chatID: 1
        }
      }
    }

    res.json(response)
  }

  SVS.validateRequest(req, res, callback)
}

module.exports.newGroup = (req, res) => {
  let callback = (req, res) => {
    let userID = req.body.userID
    let name = req.body.name
    let description = req.body.description

    errorKeys = []
    // TODO: get rid of code duplication, move sendError() to common.js
    function sendError() { // assumption: variables are in closure
        let response = {
            success: false,
            errors: common.errorObjectBuilder(errorKeys)
        }
        res.status(401).json(response)
    }

    if (!name) errorKeys.push('missingGroupName')
    // description can be empty
    if (errorKeys.length) {
      console.log('early error')
      sendError()
      return
    }

    Group.create({
      name: name,
      groupID: 1,  // TODO: use number from LastGroupID collection as with UserID
      members: [],
      admins: [userID],
      footprints: [],
      meetingPoint: null
    })

    .then((group) => {
      res.json({
        success: true,
        errors: []
      })
    })

    .catch((err) => {
      errorKeys.push('internalError')
      sendError()
    })
  }

  svs.validateRequest(req, res, callback)
}
