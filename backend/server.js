// Imports
// Express
const express = require('express')
const mongoose = require('mongoose')
const bodyParser = require('body-parser')
const cors = require('cors');

const app = express()
app.use(bodyParser.json())
app.use(cors())

// Common constants/variables
const common = require('./common')
const addMetas = common.addMetas

// Use JS Promises library
mongoose.Promise = global.Promise

// Error handling for invalid JSON
app.use(function (error, req, res, next) {
  if (error instanceof SyntaxError) {
    res.json({
      success: false,
      errors: common.errorObjectBuilder(['invalidJSON'])
    })
  } else {
    next();
  }
});

// export the mongoose object so it is accessible by other subsystems
// module.exports.mongoose = mongoose

// Systems
const ums = require('./UMS')
const svs = require('./SVS')
const gms = require('./GMS')

// connect to mongoDB
// mongoose.connect('mongodb://localhost/radar', // production
mongoose.connect('mongodb://localhost/radarTest',
  { useMongoClient: true },
  (err) => { // TODO: see if this breaks
    // if (!err) console.log('Connected to mongoDB')
    // else console.log('Failed to connect to mongoDB')
    if (err) {
      console.log(err)
    }
})

// Functions
// app.post("/SVS/signUp", svs.signUp)
// app.post("/SVS/login", svs.login)

app.get("/", (req, res) => {
  res.json(addMetas({}, "/"))
})

app.get("/api", (req, res) => {
  res.json(addMetas({}, "/api"))
})

app.get("/api/accounts/:userID", (req, res) => {
  res.json(addMetas({}, "/api/accounts/:userID"))
})

// object: auth
// signup and login
app.post("/api/auth", svs.signUp)
app.get("/api/auth/:username", svs.login)

// object: accounts
// friends
app.post("/api/accounts/:userID/friends", ums.addFriend)
app.get("/api/accounts/:userID/friendRequests", ums.getFriendRequests)
app.delete("/api/accounts/:userID/friendRequests/:requestID", ums.respondToRequest)
app.get("/api/accounts/:userID/friends", ums.getFriends)

// online statuses
app.get("/api/accounts/:userID/usersOnlineStatuses", ums.isOnline)

// object: users
// users
app.get("/api/users", ums.search) // get all users (only if query specified)
app.get("/api/users/:userID", ums.getInformation)

// object: groups
// app.get("/api/groups", gms.getAllGroups)  // TODO: extend below function - requires username, token
app.get("/api/groups", (req, res) => {
  res.json(addMetas({}, "/api/groups"))
})
app.get("/api/groups/:groupID", gms.getGroupInfo)
app.post("/api/groups", gms.newGroup)

app.listen(3000, function(req, res) {
  // console.log("Listening at port 3000.")
})

// export the app, for testing
module.exports = app
