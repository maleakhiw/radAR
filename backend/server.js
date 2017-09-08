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
mongoose.connect('mongodb://localhost/radar',
  { useMongoClient: true },
  (err) => { // TODO: see if this breaks
    if (!err) console.log('Connected to mongoDB')
    else console.log('Failed to connect to mongoDB')
})

// Functions
app.post("/SVS/signUp", svs.signUp)
app.post("/SVS/login", svs.login)

app.post("/UMS/isOnline", ums.isOnline)
app.post("/UMS/addFriend", ums.addFriend)
app.post("/UMS/getFriends", ums.getFriends)
app.post("/UMS/getInformation", ums.getInformation)
app.post("/UMS/getFriendRequests", ums.getFriendRequests)
app.post("/UMS/respondToRequest", ums.respondToRequest)
app.post("/UMS/search", ums.search)

app.post("/GMS/getGroupInfo", gms.getGroupInfo)

app.listen(3000, function(req, res) {
  console.log("Listening at port 3000.")
})
