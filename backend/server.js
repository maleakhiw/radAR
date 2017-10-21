// Imports
// load variables from .env to process.env
require('dotenv-safe').load();

// Express
const express = require('express');
const http = require('http');
const https = require('https');
const fs = require('fs');
const bodyParser = require('body-parser')
const cors = require('cors')
const _ = require('lodash');
const multer = require('multer')  // for multipart/form-data
const upload = multer({
  dest: 'uploads/'  // automatically gives unique filename
})

// mongoose
const mongoose = require('mongoose')
mongoose.Promise = global.Promise   // Use JS Promises library for Mongoose

// logging framework
const winston = require('winston');
winston.add(winston.transports.File, { filename: 'server.log' });
winston.level = 'debug';  // TODO use environment variable

// data models
const Group = require('./models/group')
const User = require('./models/user')
const Request = require('./models/request')
const Message = require('./models/message')
const Resource = require('./models/resource')
const LocationModel = require('./models/location');

const app = express();
app.use(bodyParser.json());
app.use(cors());

// Common constants/variables
const common = require('./common')
const addMetas = common.addMetas

// Error handling
app.use(function (error, req, res, next) {
  winston.error(error);
  if (error instanceof SyntaxError) {
    res.json({
      success: false,
      errors: common.errorObjectBuilder(['invalidJSON'])
    })
  }
});

var DEV = 'dev';
var PRODUCTION = 'production';

console.log(process.env.ENVIRONMENT);

var server_environment = process.env.ENVIRONMENT;
if (!server_environment) {
  server_environment = DEV;
}

// connect to mongoDB
// mongoose.connect('mongodb://localhost/radar', // production
let mongoURL;
if (server_environment == DEV) {
  mongoURL = 'mongodb://localhost/radarTest'
} else {
  mongoURL = 'mongodb://localhost/radar'
}

const connection = mongoose.connect(mongoURL,
  { useMongoClient: true },
  (err) => {
    if (err) {
      winston.error(err);
      // TODO force exit
    }
})

module.exports.connection = connection

// Systems
const UMS = require('./controllers/UMS')
const ums = new UMS(User, Request)

const SVS = require('./controllers/SVS')
const svs = new SVS(User)
const authenticate = svs.authenticate // authentication middleware

const SMS = require('./controllers/SMS')
const sms = new SMS(Group, Message, User)

const ResMS = require('./controllers/ResMS')
const resms = new ResMS(Resource, User)

const PositioningSystem = require('./controllers/PositioningSystem');
const positioningSystem = new PositioningSystem(LocationModel, User);

const GroupSystem = require('./controllers/GroupSystem');
const groupSystem = new GroupSystem(Group, Message, User, LocationModel);


/* Routes */
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
// profile
app.put("/api/accounts/:userID", authenticate, ums.updateProfile);

// friends
app.post("/api/accounts/:userID/friends", authenticate, ums.addFriend)
app.get("/api/accounts/:userID/friendRequests", authenticate, ums.getFriendRequests)
app.delete("/api/accounts/:userID/friendRequests/:requestID", authenticate, ums.cancelRequest);
app.post("/api/accounts/:userID/friendRequests/:requestID", authenticate, ums.respondToRequest)
app.get("/api/accounts/:userID/friends", authenticate, ums.getFriends)

// resources
app.post("/api/accounts/:userID/resources", authenticate, upload.single('file'), resms.uploadResource)
app.get("/api/accounts/:userID/resources/:resourceID", authenticate, resms.getResource)

// chats
app.post("/api/accounts/:userID/chats", authenticate, sms.newGroup)
app.get("/api/accounts/:userID/chats", authenticate, sms.getGroupsForUser)
app.get("/api/accounts/:userID/chats/with/:queryUserID", authenticate, sms.getOneToOneChat);

var groupAuthorisedMiddleware = (req, res, next) => {
  let groupID = parseInt(req.params.groupID);
  let userID = parseInt(req.params.userID);
  Group.findOne({groupID: groupID}).exec()
  console.log(groupID, userID);
  .then(group => {
    if (!group) {
      common.sendError(res, ['invalidGroupID']);
    } else {
      console.log(userID);
      console.log(group.members);
      if (!group.members.includes(userID)) {
        common.sendUnauthorizedError(res);
      } else {
        next();
      }
    }
  })
}

app.get("/api/accounts/:userID/chats/:groupID", authenticate,
          groupAuthorisedMiddleware, sms.getGroup)
app.put("/api/accounts/:userID/chats/:groupID", authenticate,
          groupAuthorisedMiddleware, groupSystem.promoteToTrackingGroup)
app.get("/api/accounts/:userID/chats/:groupID/messages", authenticate,
          groupAuthorisedMiddleware, sms.getMessages)
app.post("/api/accounts/:userID/chats/:groupID/messages", authenticate,
          groupAuthorisedMiddleware, sms.sendMessage);

// chats and groups
app.delete("/api/accounts/:userID/chats/:groupID", authenticate,
          groupAuthorisedMiddleware, groupSystem.leaveGroup);
app.delete("/api/accounts/:userID/groups/:groupID", authenticate,
          groupAuthorisedMiddleware, groupSystem.leaveGroup);
app.delete("/api/accounts/:userID/groups/:groupID/members/:memberUserID", authenticate,
          groupAuthorisedMiddleware, groupSystem.removeMember);
app.put("/api/accounts/:userID/groups/:groupID/members", authenticate,
          groupAuthorisedMiddleware, groupSystem.addMembers);
app.put("/api/accounts/:userID/chats/:groupID/members", authenticate,
          groupAuthorisedMiddleware, groupSystem.addMembers);

// groups
app.put("/api/accounts/:userID/groups/:groupID", authenticate, groupSystem.updateGroupDetails);
app.post("/api/accounts/:userID/groups", authenticate, groupSystem.newGroup);
app.get("/api/accounts/:userID/groups", authenticate, groupSystem.getGroupsForUser);  // TODO stub
app.get("/api/accounts/:userID/groups/:groupID", authenticate, sms.getGroup);
app.get("/api/accounts/:userID/groups/:groupID/locations", authenticate, groupSystem.getLocations);
app.post("/api/accounts/:userID/groups/:groupID/meetingPoint", authenticate, groupSystem.updateMeetingPoint);
app.put("/api/accounts/:userID/groups/:groupID/meetingPoint", authenticate, groupSystem.updateMeetingPoint);

// online statuses - unused
app.get("/api/accounts/:userID/usersOnlineStatuses", authenticate, ums.isOnline)

// locations
app.post("/api/accounts/:userID/location", authenticate, positioningSystem.updateLocation);

// object: users
// users
app.get("/api/users", ums.search) // get all users (only if query specified)
app.get("/api/users/:userID", ums.getInformation) // NOTE: added new param: ?userID=(requesterUserID)

// object: groups
app.get("/api/groups", (req, res) => {
  // TODO: let obj = get all groups
  let obj = {}
  res.json(addMetas(obj, "/api/groups"))
})

// object: locations
app.get("/api/users/:queryUserID/location", authenticate, positioningSystem.getLocation);

// object: groups
// app.get("/api/groups", gms.getAllGroups)  // TODO: extend below function - requires username, token
app.get("/api/groups", (req, res) => {
  res.json(addMetas({}, "/api/groups"))
})

// for Let's Encrypt
app.get('/health-check', (req, res) => res.sendStatus(200));
app.use(express.static('static'));


if (server_environment == DEV) {  // serve over HTTP
  app.listen(8080, (req, res) => {});
} else {  // serve over HTTPS
  const options = {
      cert: fs.readFileSync('./radar.fadhilanshar.com/fullchain.pem'),
      key: fs.readFileSync('./radar.fadhilanshar.com/privkey.pem')
  }
  http.createServer((req, res) => {
      res.writeHead(301, { "Location": "https://" + req.headers['host'] + req.url });
      res.end();
  }).listen(8080);
  https.createServer(options, app).listen(8443, () => {
    console.log('Listening on port 8443');
  });
}

// export the app, for Mocha tests
module.exports = app
