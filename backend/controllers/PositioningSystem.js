/**
 * Component containing Express controllers to manage users' location data.
 */
// logging framework
const winston = require('winston');
winston.add(winston.transports.File, { filename: '../logs/PositioningSystem.log' });
winston.level = 'debug';

const SVS = require('./controllers/SVS');
let svs;

const common = require('../common');

let LocationModel, User;

/**
 * Validates the request object for updateLocation(req, res)
 * @param req Express request object
 * @return errorKeys array of keys for the list of errors to be sent to the user
 */
function validateUpdateLocationReq(req) {


}


module.exports = class PositioningSystem {
  constructor(pLocation, pUser) {
    if (pLocation == null || pUser == null) {
      winston.log('debug', 'Null parameters in PositioningSystem constructor call');
    }

    LocationModel = pLocation;
    User = pUser;
  }

  updateLocation(req, res) {

  }

}
