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

function hashSaltPasssword(password) { // hashes and salts a plaintext password
  // TODO: defensive (check for empty passwords?) or assume already done externally?
  return bcrypt.hashSync(password, consts.SALT_ROUNDS)
}

const isString = common.isString
const isValidEmail = common.isValidEmail

function getUserID(callback) {
    LastUserID.findOneAndRemove({}, callback)
}

function signUpImpl(req, res) {
  this.busy_signUp = true;
  let firstName = req.body.firstName
  let lastName = req.body.lastName
  let email = req.body.email
  let username = req.body.username
  let profileDesc = req.body.profileDesc
  let password = req.body.password
  let deviceID = req.body.deviceID

  let errorKeys = []

  // required fields
  if (!firstName) errorKeys.push('missingFirstName')
  if (!lastName) errorKeys.push('missingLastName')
  if (!email) errorKeys.push('missingEmail')
  if (!username) errorKeys.push('missingUsername')
  if (!password) errorKeys.push('missingPassword')
  if (!deviceID) errorKeys.push('missingDeviceID')

  // check if valid
  // TODO: new error codes
  if (!isString(firstName)) errorKeys.push('invalidFirstName')
  if (!isString(lastName)) errorKeys.push('invalidLastName')
  if (!isString(email) || !isValidEmail(email)) errorKeys.push('invalidEmail')
  if (!isString(username)) errorKeys.push('invalidUsername')
  if (!isString(password)) errorKeys.push('invalidPassword')
  if (!isString(deviceID)) errorKeys.push('invalidDeviceID')


  function sendError() { // assumption: variables are in closure
      let response = {
          success: false,
          errors: common.errorObjectBuilder(errorKeys)
      }
      res.json(response)
      this.busy_signUp = false;
  }

  if (errorKeys.length) {
      sendError()
  } else {
    let userID = null
    let token = null

    User.find({ username: username }).exec()
    .then((users) => {
      if (users.length) {
        throw Error('usernameTaken')
      } else {
        return User.find({ email: email }).exec()
      }
    })

    .then((users) => {
      if (users.length) {
        throw Error('emailTaken')
      }
      return LastUserID.findOneAndRemove({})
    })


    .then((lastUserID) => {
      // console.log(lastUserID)
      if (lastUserID) {
          userID = lastUserID.userID + 1
      } else {
          userID = 1
      }
      return LastUserID.create({ userID: userID }) // Promise
    })


    .then((lastUserID) =>  {
      let hashed = hashSaltPasssword(password)
      // console.log(hashed)
      return PasswordHash.create({
        userID: userID,
        hash: hashed
      })
    })

    .then((passwordHash) => {
      // console.log(lastUserID)
      if (!passwordHash) {
        throw new Error('') // becomes internalError
      }
      let object = {
          userID: userID,
          username: username,
          firstName: firstName,
          lastName: lastName,
          email: email,
          profilePicture: null,
          profileDesc: profileDesc,
          friends: [],
          groups: [],
          chats: [],
          signUpDate: Date.now()
      }

      return User.create(object) // Promise
    })

    .then((user) => { // User successfully created, create Metadata
        // console.log(user)
        token = generateToken(userID)
        let object = {
            userID: userID,
            username: username,
            lastSeen: Date.now(),
            deviceIDs: [deviceID],
            activeTokens: [token]
        }
        return Metadata.create(object) // Promise
    })

    .then((metadata) => {
        // console.log(metadata)
        let response = {
            success: true,
            errors: [],
            token: token,
            userID: userID
        }
        res.json(response)
        this.busy_signUp = false;
    })

    .catch((err) => { // one error handler for the chain of Promises
        this.busy_signUp = false;
        if (err == 'Error: usernameTaken') {
          errorKeys.push('usernameTaken')
          sendError()
        } else if (err == 'Error: emailTaken') {
          errorKeys.push('emailTaken')
          sendError()
        } else {
            console.log(err)
            errorKeys.push('internalError')
            sendError()
        }
    })
  }
}

var busy_signUp = false;
module.exports = class SVS {

  constructor(pUser, pMetadata, pLastUserID, pPasswordHash) {
    User = pUser
    Metadata = pMetadata
    LastUserID = pLastUserID,
    PasswordHash = pPasswordHash
  }

  authenticate(req, res, next) {
    // TODO: check if in req.query, req.body or req.param
    let userID = req.query.userID || req.params.userID || req.body.userID
    console.log('userID', userID)
    Metadata.findOne({ userID: userID }).exec()

    .then((metadata) => {
      if (!metadata) {
        res.status(401).json({
          success: false,
          errors: common.errorObjectBuilder(['invalidUsername'])
        })
      } else {
        // if (metadata.activeTokens.includes())
        let authorizationToken = req.get('token')

        if (!authorizationToken) {
          throw new Error("Missing Token")
        }
        // authorizationToken = authorizationToken.slice(7)
        // console.log(authorizationToken)

        // console.log(metadata.activeTokens)
        if (!metadata.activeTokens.includes(authorizationToken)) {
          throw new Error('Unauthorized')
        }

        next()
      }
    })

    .catch((err) => {
      console.log(err)
      if (err == 'Error: Missing Token') {
        res.status(401).json({
          success: false,
          errors: common.errorObjectBuilder(['missingToken'])
        })
        return
      } else if (err == 'Error: Unauthorized') {
        res.status(401).json({
          success: false,
          errors: common.errorObjectBuilder(['invalidToken'])
        })
      }
      res.json({  // TODO: verify status code
        success: false,
        errors: common.errorObjectBuilder(['internalError'])
      })
    })

  }



  // validateRequest(req, res, callback) {
  //   if (req.params.userID) {  // accept userID passed via URL
  //     req.body.userID = req.params.userID
  //   } else if (req.query.userID) {
  //     req.body.userID = req.query.userID
  //   }
  //
  //   let errorKeys = []
  //
  //   function sendError() { // assumption: variables are in closure
  //       let response = {
  //           success: false,
  //           errors: common.errorObjectBuilder(errorKeys)
  //       }
  //       res.status(401).json(response)
  //   }
  //   // if (!('token' in req.body) && !('token' in req.query)) {
  //   //     errorKeys.push('missingToken')
  //   // }
  //   if (!req.body.userID) {
  //       errorKeys.push('missingUserID')
  //   }
  //
  //   let token = req.body.token || req.query.token
  //
  //   // if (!validateToken(req.body.userID, token)) {
  //   //     errorKeys.push('invalidToken')
  //   // }
  //
  //   if (errorKeys.length) {
  //     sendError()
  //   } else {
  //     let userID = req.body.userID
  //     // update last seen
  //     Metadata.findOne({ userID: userID }).exec()
  //         .then((metadata) => {
  //             if (!metadata) {  // TODO: new test case w/ invalid user ID
  //               errorKeys.push('invalidUserID')
  //               sendError()
  //               return
  //             }
  //             metadata.lastSeen = Date.now()
  //             metadata.save()
  //             callback(req, res)
  //         })
  //         .catch((err) => {
  //             console.log(err)
  //             errorKeys.push('internalError')
  //             sendError()
  //         })
  //   }
  //
  // }

  signUp(req, res) {
    while (1==1) {
      if (busy_signUp) {
        setTimeout(() => {
          console.log("waiting")
        }, 100);
      } else {
        console.log("calling signUpImpl")
        signUpImpl(req, res);
        break;
      }
    }
  }

  login(req, res) {
    // let username = req.body.username
    let username = req.params.username
    let password = req.query.password

    let errorKeys = []
    if (!username) errorKeys.push('missingUsername')
    if (!password) errorKeys.push('missingPassword')

    function sendError() { // uses variables in closure
        let response = {
            success: false,
            errors: common.errorObjectBuilder(errorKeys)
        }
        res.json(response)
    }

    if (errorKeys.length) {
        sendError()
    } else {
        // TODO: validate username and password
        let token;
        let userID;

        Metadata.findOne({ username: username }).exec()

        .then((metadata) => {
            if (!metadata) { // cannot find metadata on the user
                errorKeys.push('invalidUsername')
                sendError()
                throw Error('invalidUsername')
            } else {
                token = generateToken(metadata.userID)
                userID = metadata.userID;
                if (!metadata.activeTokens.includes(token)) {
                    metadata.activeTokens.push(token) // TODO: SIGN OUT ROUTE - REMOVES A TOKEN
                }
                return metadata.save()
            }
        })

        .then((metadata) => {
            return PasswordHash.findOne({ userID: userID })
        })

        .then((passwordHash) => {
          let hash = passwordHash.hash
          let success = bcrypt.compareSync(password, hash)

          if (success) {
            let response = {
                success: true,
                errors: [],
                token: token,
                userID: userID
            }
            res.json(response)
          } else {
            res.status(401).json({
              success: false,
              errors: common.errorObjectBuilder['invalidPassword'],
              token: null,
              userID: null
            })

          }

        })

        .catch((err) => {
          if (err != 'Error: invalidUsername') {
            errorKeys.push('internalError')
            sendError()
          }
        })
    }
  }
}
