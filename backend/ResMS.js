/**
 * Group Management System server-side component.
 * Provides groups' information and group management services.
 */
var fs = require('fs')

const mongoose = require('mongoose')
const common = require('./common')

// TODO
const SVS = require('./SVS')
let svs;

let Resource

// const connection = module.parent.exports.connection

module.exports = class ResMS {
  constructor(pResource, pUser, pMetadata, pLastUserID, pPasswordHash) {
    Resource = pResource
    svs = new SVS(pUser, pMetadata, pLastUserID, pPasswordHash)
  }

  uploadResource(req, res) {
    // TODO: update API documentation to reflect fact that files are sent using multipart/form-data
    let userID = req.params.userID
    let file = req.file

    if (!file) {
      res.status(400).json({
        success: false,
        error: [common.errorObjectBuilder(['missingFile'])]
      })
      return
    }

    let fileID = file.filename

    Resource.create({
      fileID: fileID,
      filename: file.originalname,
      mimetype: file.mimetype,
      owners: [userID],
      chatID: null,
      groupID: null
    })
    .then((resource) => {
      res.json({
        success: true,
        errors: [],
        resourceID: fileID
      })
    })
    .catch((err) => {
      console.log(err)
      res.status(400).json({
        success: false,
        error: [common.errorObjectBuilder(['internalError'])]
      })
    })

  }


  getResource(req, res) {
    let fileID = req.params.resourceID
    let userID = parseInt(req.params.userID) || parseInt(req.body.userID)  // TODO: check

    Resource.find({ fileID: fileID }).exec()
    .then((resource) => {
      if (resource.length) {
        let file = resource[0]
        // do checks - if unauthorised, send 401 unauthorised

        // TODO: Chats and Groups
        if (file.owners.includes(userID)) {
          res.setHeader('Content-Type', file.mimetype)
          res.sendFile('uploads/' + fileID, { root: __dirname })
        } else {
          res.sendStatus(401)
        }

      } else {
        res.sendStatus(404) // resource not found
      }
    })


  }

  // NOTE:
  /*
  Deleting file: check with either listdir or our collection.
  Don't allow dots. (can delete server files, and even above.). Files uploaded to the server won't have dots.
  */

}
