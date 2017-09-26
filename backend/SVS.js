/**
 * Server Validation System server-side component.
 * Handles user authentication, validation, sign up and login services.
 */

const common = require('./common')
const consts = require('./consts')
// const mongoose = module.parent.exports.mongoose   // import from index.js
const randomstring = require('randomstring')
const bcrypt = require('bcrypt')

let User, Metadata, LastUserID, PasswordHash

function generateToken(userID) {
  return randomstring.generate(64) // TODO MOVE TO consts
}

function hashSaltPassword(password) { // hashes and salts a plaintext password
  // TODO: defensive (check for empty passwords?) or assume already done externally?
  return bcrypt.hashSync(password, consts.SALT_ROUNDS)
}

const isString = common.isString
const isValidEmail = common.isValidEmail

function getUserID(callback) {
    LastUserID.findOneAndRemove({}, callback)
}

// functions to be unit tested
var isValidUser = (userID) => new Promise((resolve, reject) => {
  if (!userID) {  // if no userID specified
    resolve(false);
  }

  User.findOne({ userID: userID }).exec()

  .then((user) => {
    if (!user) {
      resolve(false);
    } else {
      resolve(true);
    }
  })
})

var validateToken = (token) => new Promise((resolve, reject) => {
  User.findOne({ userID: userID }).exec()

  .then((user) => {
    if (!user) {
      resolve(false);
    } else {
      if (!user.activeTokens.includes(authorizationToken)) {
        resolve(false);
      }
    }
    resolve(true);
  })
})

function sendUnauthorizedError(res, errorKeys) {
  res.status(401).json({
    success: false,
    errors: common.errorObjectBuilder(errorKeys)
  });
}

function sendInternalError(res) {
  res.status(500).json({
    success: false,
    errors: common.errorObjectBuilder(['internalError'])
  });
}

// helper functions for signUp

// validates the request for the signUp route
// returns an array of error keys if any
function validateSignUpRequest(obj) {
  let firstName = obj.firstName;
  let lastName = obj.lastName;
  let email = obj.email;
  let username = obj.username;
  let profileDesc = obj.profileDesc;
  let password = obj.password;
  let deviceID = obj.deviceID;

  let errorKeys = [];

  // required fields
  if (!firstName) errorKeys.push('missingFirstName');
  if (!lastName) errorKeys.push('missingLastName');
  if (!email) errorKeys.push('missingEmail');
  if (!username) errorKeys.push('missingUsername');
  if (!password) errorKeys.push('missingPassword');
  if (!deviceID) errorKeys.push('missingDeviceID');

  // check if entries are valid
  if (!isString(firstName)) errorKeys.push('invalidFirstName');
  if (!isString(lastName)) errorKeys.push('invalidLastName');
  if (!isString(email) || !isValidEmail(email)) errorKeys.push('invalidEmail');
  if (!isString(username)) errorKeys.push('invalidUsername');
  if (!isString(password)) errorKeys.push('invalidPassword'); // TODO enforce password lengths
  if (!isString(deviceID)) errorKeys.push('invalidDeviceID');

  return errorKeys;
}

function sendError(res, errorKeys) {
    let response = {
        success: false,
        errors: common.errorObjectBuilder(errorKeys)
    }
    res.json(response)
}

var isUsernameUnique = (username) => new Promise((resolve, reject) => {
  User.findOne({ username: username })
  .then((user) => {
    if (user) {
      resolve(false);
    } else {
      resolve(true);
    }
  })
})

var isEmailUnique = (email) => new Promise((resolve, reject) => {
  User.findOne({ email: email })
  .then((user) => {
    if (user) {
      resolve(false);
    } else {
      resolve(true);
    }
  })
})

var getUserIDForNewUser = () => new Promise((resolve, reject) => {
  User.findOne().sort({userID: -1}).exec()
  .then((user) => {
    if (user) {
      resolve(user.userID + 1);
    } else {
      resolve(1);
    }
  })
})

// helper functions for validating login
function validateFieldsForLogin(req) {
  let username = req.params.username
  let password = req.query.password

  let errorKeys = []
  if (!username) errorKeys.push('missingUsername')
  if (!password) errorKeys.push('missingPassword')
  return errorKeys;
}

var validateCredentials = (username, password) => new Promise((resolve, reject) => {
  let token;
  let userID;

  console.log("@validateCredentials")

  User.findOne({ username: username }).exec()
  .then((user) => {
    if (!user) {
      reject('invalidUsername');
    } else {
      // check if password is valid
      let passwordHash = user.passwordHash;
      let success = bcrypt.compareSync(password, passwordHash);

      if (success) {
        userID = user.userID;
        token = generateToken(userID);
        user.activeTokens.push(token);
        return user.save();
      } else {
        reject('invalidPassword');
      }
    }

  })
  .then((user) => {
    // updated list of active tokens saved
    resolve({token: token, userID: userID});
  })


})

module.exports = class SVS {

  constructor(pUser) {
    User = pUser
  }

  /**
   * Authentication Express middleware.
   * The middleware can easily be swapped out for a Passport.js authentication
   * middleware down the line.
   */
  authenticate(req, res, next) {
    let userID = req.query.userID || req.params.userID || req.body.userID;
    let authorizationToken = req.get('token')

    if (!authorizationToken) {
      sendUnauthorizedError(res, ['missingToken'])
    }

    isValidUser(userID).then((isValid) => {
      if (isValid) {
        validateToken(authorizationToken).then((isValid) => {
          if (isValid) {
            next();
          } else {
            sendUnauthorizedError(res, ['invalidToken']);
          }
        });
      } else {  // invalid userID, do not pass request to route handlers
        sendUnauthorizedError(res, ['invalidUserID']);
      }
    })
    .catch((err) => sendInternalError(res))
  }

  /**
   * Creates a new user account.
   */
  signUp(req, res) {
    let errorKeys = validateSignUpRequest(req.body)
    if (errorKeys.length) {
        sendError(res, errorKeys);
        return;
    }

    // retrieve the fields
    let obj = req.body;
    let firstName = obj.firstName;
    let lastName = obj.lastName;
    let email = obj.email;
    let username = obj.username;
    let profileDesc = obj.profileDesc;
    let password = obj.password;
    let deviceID = obj.deviceID;

    let token;
    let userID;

    // check if username and email not taken
    isUsernameUnique(username).then((isUnique) => {
      if (!isUnique) {
        sendError(res, ['usernameTaken']);
      } else {
        return isEmailUnique(email);
      }
    })

    .then((isUnique) => {
      if (!isUnique) {
        sendError(res, ['emailTaken']);
      } else {
        return getUserIDForNewUser()
      }
    })

    // get the userID for the new user then create the new user
    .then((userIDRes) => {
      userID = userIDRes;
      token = generateToken(userID);
      return User.create({
        userID: userID,
        username: username,
        firstName: firstName,
        lastName: lastName,
        email: email,
        profileDesc: profileDesc,
        signUpDate: Date.now(),
        lastSeen: Date.now(),
        deviceIDs: [deviceID],
        activeTokens: [token],
        passwordHash: hashSaltPassword(password)
      })
    })

    .then((user) => {
      // user has been created successfully
      res.json({
        success: true,
        errors: [],
        token: token,
        userID: userID
      })

    })

    .catch((err) => {
      console.log(err);
      sendInternalError(res);
    })


    // create the user
  }

  login(req, res) {
    // let username = req.body.username
    let username = req.params.username
    let password = req.query.password

    let errorKeys = validateFieldsForLogin(req);

    if (errorKeys.length) {
        sendError(res, errorKeys);
        return;
    }

    validateCredentials(username, password)
    .then((obj) => {
      console.log('@validateCredentials')
      let token = obj.token;
      let userID = obj.userID;

      console.log(token, userID);


      res.json({
        success: true,
        errors: [],
        userID: userID,
        token: token
      });
    })
    .catch((error) => {
      if (error == 'invalidUsername') {
        sendError(res, ['invalidUsername']);
      } else if (error == 'invalidPassword') {
        sendError(res, ['invalidPassword']);
      } else {
        sendInternalError(res);
      }
    })

  }


}
