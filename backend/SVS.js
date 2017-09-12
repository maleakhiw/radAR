/**
 * Server Validation System server-side component.
 * Handles user authentication, validation, sign up and login services.
 */

const common = require('./common')
    // const mongoose = module.parent.exports.mongoose   // import from index.js

// Mongoose models
const User = require('./models/user')
const Metadata = require('./models/metadata')
const LastUserID = require('./models/lastUserID')

// validate a request token
function validateToken(userID, token) { // TODO: stub
    return true
}

function generateToken(userID) { // TODO: stub
    return "79"
}

// validates request tokens - if token is valid, call callback
// also checks if userID and token is present in the request.
const validateRequest = function(req, res, callback) {
    let errorKeys = []

    function sendError() { // assumption: variables are in closure
        let response = {
            success: false,
            errors: common.errorObjectBuilder(errorKeys)
        }
        res.json(response)
    }
    if (!('token' in req.body)) {
        errorKeys.push('missingToken')
    }
    if (!req.body.userID) {
        errorKeys.push('missingUserID')
    }
    if (!validateToken(req.body.userID, req.body.token)) {
        errorKeys.push('invalidToken')
    }

    let userID = req.body.userID
        // update last seen
    Metadata.findOne({ userID: userID }).exec()
        .then((metadata) => {
            metadata.lastSeen = Date.now()
            metadata.save()
            callback(req, res)
        })
        .catch((err) => {
            console.log(err)
            errorKeys.push('internalError')
            sendError()
        })
}

module.exports.validateRequest = validateRequest

function getUserID(callback) {
    LastUserID.findOneAndRemove({}, callback)
}

// callback for '/SVS/signUp' route
module.exports.signUp = function(req, res) {
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

    function sendError() { // assumption: variables are in closure
        let response = {
            success: false,
            errors: common.errorObjectBuilder(errorKeys)
        }
        res.json(response)
    }

    if (errorKeys.length) {
        sendError()
    } else {
        let userID = null
        let token = null

        User.find({ username: username }).exec()
            .then((user) => {
                if (user.length) {
                    errorKeys.push('usernameTaken')
                    sendError()
                    throw Error('usernameTaken') // TODO: somehow reject the promise?
                } else {
                    return LastUserID.findOneAndRemove({})
                }
            })

        .then((lastUserID) => {
            console.log(lastUserID)
            if (lastUserID) {
                userID = lastUserID.userID + 1
            } else {
                userID = 1
            }
            return LastUserID.create({ userID: userID }) // Promise
        })

        .then((lastUserID) => {
            console.log(lastUserID)
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

            // TODO: add to Username/Password collection
            return User.create(object) // Promise
        })

        .then((user) => { // User successfully created, create Metadata
            console.log(user)
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
            console.log(metadata)
            let response = {
                success: true,
                errors: [],
                token: token,
                userID: userID
            }
            res.json(response)
        })

        .catch((err) => { // one error handler for the chain of Promises
            if (err == 'usernameTaken') {

            } else {
                console.log(err)
                errorKeys.push('internalError')
                sendError()
            }
        })
    }
}

module.exports.login = function(req, res) {
    let username = req.body.username
    let password = req.body.password

    errorKeys = []
    if (!username) errorKeys.push('missingUsername')
    if (!password) errorKeys.push('missingPassword')

    function sendError() { // uses variables in closure
        response = {
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
        Metadata.findOne({ username: username }).exec()

        .then((metadata) => {
            if (!metadata) { // cannot find metadata on the user
                errorKeys.push('invalidUsername')
                sendError()
                throw Error('')
            } else {
                token = generateToken(metadata.userID)
                if (!metadata.activeTokens.includes(token)) {
                    metadata.activeTokens.push(token) // TODO: SIGN OUT ROUTE - REMOVES A TOKEN
                }
                return metadata.save()
            }
        })

        .then((metadata) => {
            response = {
                success: true,
                errors: [],
                token: token // TODO: stub
            }
            res.json(response)
        })

        .catch((err) => {
            console.log(err)
            errorKeys.push('internalError')
            sendError()
        })
    }

}