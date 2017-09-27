/**
 * Group Management System server-side component.
 * Provides groups' information and group management services.
 */

// logging framework
const winston = require('winston');

winston.level = 'debug';  // TODO use environment variable

var fs = require('fs')

var path = require('path')

const common = require('../common')

// TODO
const SVS = require('./SVS')
let svs;

let Resource;

module.exports = class ResMS {
  constructor(pResource, pUser) {
    Resource = pResource;
    svs = new SVS(pUser);
  }

  uploadResource(req, res) {
    // TODO: update API documentation to reflect fact that files are sent using multipart/form-data
    let userID = req.params.userID
    let file = req.file

    if (!file) {
      res.json({
        success: false,
        error: [common.errorObjectBuilder(['missingFile'])]
      })
      return;
    }

    let fileID = file.filename

    Resource.create({
      fileID: fileID,
      filename: file.originalname,
      mimetype: file.mimetype,
      owners: [userID],
      groupID: null,
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
      winston.error(err)
      res.json({
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

        // TODO: Groups and Groups
        if (file.owners.includes(userID)) {
          res.setHeader('Content-Type', file.mimetype)
          res.sendFile(fileID, { root: path.join(__dirname, '../uploads') })
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
