/**
 * Group Management System server-side component.
 * Provides groups' information and group management services.
 */
var fs = require('fs')

const mongoose = require('mongoose')

const common = require('./common')
const svs = require('./SVS')

const Resource = require('./models/resource')

const connection = module.parent.exports.connection

module.exports.uploadResource = (req, res) => {
  // TODO: update API documentation to reflect fact that files are sent using multipart/form-data
  let callback = (req, res) => {
    let userID = req.body.userID
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

  svs.validateRequest(req, res, callback)
}

module.exports.getResource = (req, res) => {
  let callback = (req, res) => {
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
        res.sendStatus(404)
      }
    })


  }

  svs.validateRequest(req, res, callback)
}

// NOTE:
/*
Deleting file: check with either listdir or our collection.
Don't allow dots. (can delete server files, and even above.). Files uploaded to the server won't have dots.
*/