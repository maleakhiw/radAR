/**
 * Group Management System server-side component.
 * Provides groups' information and group management services.
 */

const common = require('./common')
const SVS = require('./SVS')

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
