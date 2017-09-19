// Imports
// Express
const express = require('express')
const mongoose = require('mongoose')
const bodyParser = require('body-parser')
const multer = require('multer')  // for multipart/form-data, https://github.com/expressjs/multer
const cors = require('cors')
const _ = require('lodash');

// multer
const upload = multer({
  dest: 'uploads/'  // automatically gives unique filename
})

// data models
const Chat = require('./models/chat')
const User = require('./models/user')
const Request = require('./models/request')
const Message = require('./models/message')
const Resource = require('./models/resource')
const Metadata = require('./models/metadata')
const LastChatID = require('./models/lastChatID')
const LastUserID = require('./models/lastUserID')
const PasswordHash = require('./models/passwordHash')
const LastRequestID = require('./models/lastRequestID')

const app = express()
app.use(bodyParser.json())
app.use(cors())

// Common constants/variables
const common = require('./common')
const addMetas = common.addMetas

// Use JS Promises library for Mongoose
mongoose.Promise = global.Promise

// Error handling for invalid JSON
app.use(function (error, req, res, next) {
  if (error instanceof SyntaxError) {
    res.json({
      success: false,
      errors: common.errorObjectBuilder(['invalidJSON'])
    })
  }
  else {
    next()
  }
});

// handle rapid-fire duplicate requests
var lastRequests = [];
var REQ_TIME_THRES = 5000;
app.use(function(req, res, next) {

  // remove requests older than threshold
  lastRequests = lastRequests.filter((entry) => {
   return (Date.now() - entry.time) < REQ_TIME_THRES;
  })

  console.log(lastRequests);

  // check if req.body is in array
  let isInArray = false;
  lastRequests.map((entry) => {
   if (_.isEqual(entry, req.body)) {
     isInArray = true;
   }
  });
  lastRequests.push({
   reqBody: req.body,
   time: Date.now()
  });

  if (!isInArray) {
    next(); // let the handlers handle it
  } else {
    // block the request
  }

})

// connect to mongoDB
// mongoose.connect('mongodb://localhost/radar', // production
const connection = mongoose.connect('mongodb://localhost/radarTest',
  { useMongoClient: true },
  (err) => { // TODO: see if this breaks
    // if (!err) console.log('Connected to mongoDB')
    // else console.log('Failed to connect to mongoDB')
    if (err) {
      console.log(err)
    }
})
module.exports.connection = connection

// Systems
const UMS = require('./UMS')
const ums = new UMS(Metadata, User, Request, LastRequestID, PasswordHash)
// const svs = require('./SVS')
const SVS = require('./SVS')
const svs = new SVS(User, Metadata, LastUserID, PasswordHash)
const authenticate = svs.authenticate

const gms = require('./GMS')

const SMS = require('./SMS')
const sms = new SMS(Chat, Message, User, LastRequestID, LastChatID, Metadata, LastUserID, PasswordHash)


const ResMS = require('./ResMS')
const resms = new ResMS(Resource, User, Metadata, LastUserID, PasswordHash)

// export the mongoose object so it is accessible by other subsystems
// module.exports.mongoose = mongoose

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

// app.get("/api/auth/:username", svs.login)
// app.post("/api/login",
//   passport.authenticate('local', { session: false }),
//   (req, res) => {
//     // authentication successful, send token back
//     res.json({
//       success: true,
//       token: "79",
//       userID: req.query.userID
//     })
//   })

// object: accounts
// friends
app.post("/api/accounts/:userID/friends", authenticate, ums.addFriend)
app.get("/api/accounts/:userID/friendRequests", authenticate, ums.getFriendRequests)
app.post("/api/accounts/:userID/friendRequests/:requestID", authenticate, ums.respondToRequest)
app.get("/api/accounts/:userID/friends", authenticate, ums.getFriends)

// resources
app.post("/api/accounts/:userID/resources", authenticate, upload.single('file'), resms.uploadResource)
app.get("/api/accounts/:userID/resources/:resourceID", authenticate, resms.getResource)

// chats
app.get("/api/accounts/:userID/chats", authenticate, sms.getChatsForUser)
// NOTE: mirrors chats object below
app.post("/api/accounts/:userID/chats", authenticate, sms.newChat)
app.get("/api/accounts/:userID/chats/:chatID", authenticate, sms.getChat)
app.get("/api/accounts/:userID/chats/:chatID/messages", authenticate, sms.getMessages)

// online statuses
app.get("/api/accounts/:userID/usersOnlineStatuses", authenticate, ums.isOnline)

// object: users
// users
app.get("/api/users", authenticate, ums.search) // get all users (only if query specified)
app.get("/api/users/:userID", authenticate, ums.getInformation)

// object: chats
app.get("/api/chats", (req, res) => {
  // TODO: let obj = get all chats
  let obj = {}
  res.json(addMetas(obj, "/api/chats"))
})

// chats
app.post("/api/chats", authenticate, sms.newChat)
app.get("/api/chats/:chatID", authenticate, sms.getChat)
app.post("/api/chats/:chatID/messages", authenticate, sms.sendMessage)
app.get("/api/chats/:chatID/messages", authenticate, sms.getMessages)

// object: groups
// app.get("/api/groups", gms.getAllGroups)  // TODO: extend below function - requires username, token
app.get("/api/groups", (req, res) => {
  res.json(addMetas({}, "/api/groups"))
})
app.get("/api/groups/:groupID", authenticate, gms.getGroupInfo)
app.post("/api/groups", authenticate, gms.newGroup)

app.listen(3000, function(req, res) {
  // console.log("Listening at port 3000.")
})

// export the app, for testing
module.exports = app
