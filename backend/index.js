// Imports
// Express
const express = require('express')
const mongoose = require('mongoose')
const bodyParser = require('body-parser')
const cors = require('cors');

const app = express()
app.use(bodyParser.json())
app.use(cors())

// Server Components
const common = require('./common')

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

// Systems
const ums = require('./UMS')
const svs = require('./SVS')
const gms = require('./GMS')

// Functions
app.post("/SVS/signUp", svs.signUp)
app.post("/SVS/login", svs.login)

app.post("/UMS/isOnline", ums.isOnline)
app.post("/UMS/addFriend", ums.addFriend)
app.post("/UMS/getInformation", ums.getInformation)

app.post("/GMS/getGroupInfo", gms.getGroupInfo)

app.listen(3000, function(req, res) {
  console.log("Listening at port 3000.")
})
