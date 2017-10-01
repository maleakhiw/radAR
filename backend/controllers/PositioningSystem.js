/**
 * Component containing Express controllers to manage users' location data.
 */
// logging framework
const winston = require('winston');

winston.level = 'debug';  // TODO use environment variable

const SVS = require('./SVS');
let svs;

const common = require('../common');
const sendError = common.sendError;
const sendInternalError = common.sendInternalError;
const isNumber = common.isNumber;

let LocationModel, User;

function isValidLat(val) {
  // latitude can only be +/- 90 degrees.
  // longitude can be +/- 180 deg.
  return (val >= -90 && val <= 90);
}

function isValidLon(val) {
  return (val >= -180 && val <= 180);
}

function isValidHeading(val) {
  return (val >= 0 && val <= 360);
}

/**
 * Validates the request object for updateLocation(req, res)
 * @param req Express request object
 * @return errorKeys array of keys for the list of errors to be sent to the user
 */
function validateUpdateLocationReq(req) {
  // userID is pre-validated by authentication middleware
  let lat      = req.body.lat;
  let lon      = req.body.lon;
  let accuracy = req.body.accuracy;
  let heading  = req.body.heading;

  let errorKeys = [];

  // check if keys exist
  if (lat == null) errorKeys.push('missingLat');
  if (lon == null) errorKeys.push('missingLon');
  if (accuracy == null) errorKeys.push('missingAccuracy');
  if (heading == null) errorKeys.push('missingHeading');

  if (errorKeys.length) {
    return errorKeys;
  }

  // check data types
  if (!isNumber(lat)) errorKeys.push('invalidLat');
  if (!isNumber(lon)) errorKeys.push('invalidLon');
  if (!isNumber(accuracy)) errorKeys.push('invalidAccuracy');
  if (!isNumber(heading)) errorKeys.push('invalidHeading');

  if (errorKeys.length) {
    return errorKeys;
  }

  if (!isValidLat(lat)) errorKeys.push('invalidLat');
  if (!isValidLon(lon)) errorKeys.push('invalidLon');
  if (!isValidHeading(heading)) errorKeys.push('invalidHeading');

  return errorKeys;
}

/**
 * Validates the request object for getLocation(req)
 * Unlike the previous validator, this one uses the Promise style
 */
var isValidGetLocationReq = (req) => new Promise((resolve, reject) => {
  // let userID = req.query.userID;  // pre-validated by authentication middleware
  let queryUserID = parseInt(req.params.queryUserID);
  if (!queryUserID) {
    reject('missingQueryUserID');
  }
  if (!isNumber(queryUserID)) {
    winston.debug('Invalid query user ID - not a number');
    reject('invalidQueryUserID');
  }

  // check if user is in the system
  User.findOne({ userID: queryUserID }).exec().then((user) => {
    if (!user) {
      reject('invalidQueryUserID');
    } else {
      resolve();  // TODO check if user is in the same Group (Tracking) as the requester
    }
  })
  .catch((err) => {
    winston.error(err);
  })

});

module.exports = class PositioningSystem {
  constructor(pLocation, pUser) {
    if (pLocation == null || pUser == null) {
      winston.log('debug', 'Null parameters in PositioningSystem constructor call');
    }

    LocationModel = pLocation;
    User = pUser;
  }

  updateLocation(req, res) {
    // POST {serverURL}/api/accounts/:userID/location
    /* Request body:
       {
         lat: Number,  // latitude
         lon: Number,  // longitude
         accuracy: Number, // in metres
         heading: Number   // compass facing direction, in degrees
       }
    */

    let userID   = req.params.userID;
    let lat      = req.body.lat;
    let lon      = req.body.lon;
    let accuracy = req.body.accuracy;
    let heading  = req.body.heading;

    let errorKeys = validateUpdateLocationReq(req);
    if (errorKeys.length) {
      sendError(res, errorKeys);
      return;
    }

    LocationModel.create({
      userID: userID,
      lat: lat,
      lon: lon,
      heading: heading,
      accuracy: accuracy
    })
    .then((location) => {
      res.json({
        success: true,
        errors: []
      })
    })

  }

  // TODO: make route that can return location of multiple users
  // TODO (global): make sure all routes can handle accidental arrays in GET requests
  getLocation(req, res) {
    // GET {serverURL}/api/users/:queryUserID/location
    /* Request params (key: value)
      userID: userID used to authenticate the client along with the token

      Request params (in URI/URL)
      queryUserID: the userID of the user whose location we are getting
    */
    let queryUserID = parseInt(req.params.queryUserID);

    isValidGetLocationReq(req)
    .then(() => {
      // get latest location. TODO refactor to individual function for testability
      // (SRP - single responsibility principle)
      return LocationModel.findOne({ userID: queryUserID }).sort({ timeUpdated: -1 });
    })
    .then((location) => {
      if (!location) {
        sendError(res, ['locationUnavailable']);
      } else {
        res.json({
          success: true,
          errors: [],
          lat: location.lat,
          lon: location.lon,
          heading: location.heading,
          accuracy: location.accuracy,
          timeUpdated: location.timeUpdated
        });
      }
    })
    .catch((err) => {
      winston.debug(err);
      if (err == 'missingQueryUserID') {
        sendError(res, ['missingQueryUserID']);
      } else if (err == 'invalidQueryUserID') {
        sendError(res, ['invalidQueryUserID']);
      } else {
        sendInternalError(res);
      }
    })


  }

}
