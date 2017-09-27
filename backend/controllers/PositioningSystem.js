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

function isValidLatOrLon(val) {
  // latitude and longitude can only be +/- 90 degrees.
  return (val >= -90 && val <= 90);
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

  // check data types
  if (!isNumber(lat)) errorKeys.push('invalidLat');
  if (!isNumber(lon)) errorKeys.push('invalidLon');
  if (!isNumber(accuracy)) errorKeys.push('invalidAccuracy');
  if (!isNumber(heading)) errorKeys.push('invalidHeading');

  if (!isValidLatOrLon(lat)) errorKeys.push('invalidLat');
  if (!isValidLatOrLon(lon)) errorKeys.push('invalidLon');
  if (!isValidHeading(heading)) errorKeys.push('invalidHeading');

  return errorKeys;
}

/**
 * Validates the request object for getLocation(req)
 * Unlike the previous validator, this one uses the Promise style
 */
var isValidGetLocationReq = (req) => new Promise((resolve, reject) => {
  // let userID = req.query.userID;  // pre-validated by authentication middleware
  let queryUserID = req.params.queryUserID;
  if (!queryUserID) {
    reject('missingQueryUserID');
  }
  if (!isNumber(queryUserID)) {
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

  getLocation(req, res) {
    // GET {serverURL}/api/users/:queryUserID/location
    /* Request params (key: value)
      userID: userID used to authenticate the client along with the token

      Request params (in URI/URL)
      queryUserID: the userID of the user whose location we are getting
    */
    isValidGetLocationReq(req)
    .then(() => {

    })
    .catch((err) => {
      winston.debug(err);
      if (err == 'missingQueryUserID') {
        sendError(res, ['missingQueryUserID']);
      }
      if (err == 'invalidQueryUserID') {
        sendError(res, ['invalidQueryUserID']);
      }
      sendInternalError(res);
    })


  }

}
