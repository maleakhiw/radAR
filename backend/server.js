// Imports
// Express
const express = require('express');
const https = require('https');
const fs = require('fs');

const mongoose = require('mongoose')

const bodyParser = require('body-parser')
const multer = require('multer')  // for multipart/form-data, https://github.com/expressjs/multer
const cors = require('cors')
const _ = require('lodash');

// logging framework
const winston = require('winston');
winston.add(winston.transports.File, { filename: 'server.log' });
winston.level = 'debug';  // TODO use environment variable

// multer
const upload = multer({
  dest: 'uploads/'  // automatically gives unique filename
})

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

// Use JS Promises library for Mongoose
mongoose.Promise = global.Promise

// Error handling for invalid JSON
app.use(function (error, req, res, next) {
  res.json({
    success: false,
    errors: common.errorObjectBuilder(['invalidJSON'])
  })
});

// handle rapid-fire duplicate requests
var lastRequests = [];
var REQ_TIME_THRES = 1000;
var DELAY_BASE = 100;

// Fibonacci delay
// function fibo(n) {
//   if (n==0) {
//     return 0; // special case: no entries in queue
//   }
//   if (n==1) {
//     return 5;
//   }
//   if (n==2) {
//     return 11;
//   }
//   else {
//     return fibo(n-1) + fibo(n-2);
//   }
// }

// app.use(function(req, res, next) {
//   // give a little delay so the array has time to be updated
//   // let time = Date.now();
//   // while (Date.now() - time < 10*fibo(lastRequests.length)) {
//   //   ;
//   // }
//
//   // remove requests older than threshold
//   lastRequests = lastRequests.filter((entry) => {
//    return (Date.now() - entry.time) < REQ_TIME_THRES;
//   })
//
//   console.log(lastRequests.length);
//
//   // check if req.body is in array
//   let isInArray = false;
//   lastRequests.map((entry) => {
//    if (_.isEqual(entry.reqBody, req.body)) {
//      isInArray = true;
//    }
//   });
//   console.log('isInArray', isInArray);
//
//   lastRequests.push({
//    reqBody: req.body,
//    time: Date.now()
//   });
//
//   if (!isInArray) {
//     console.log('accepted');
//     next(); // let the handlers handle it
//   } else {
//     console.log('rejected');
//     // block the request
//   }
//
// })

// connect to mongoDB
// mongoose.connect('mongodb://localhost/radar', // production
const connection = mongoose.connect('mongodb://localhost/radarTest',
  { useMongoClient: true },
  (err) => { // TODO: see if this breaks
    // if (!err) console.log('Connected to mongoDB')
    // else console.log('Failed to connect to mongoDB')
    if (err) {
      winston.error(err)
    }
})
module.exports.connection = connection

// Systems
const UMS = require('./controllers/UMS')
const ums = new UMS(User, Request)
// const svs = require('./controllers/SVS')
const SVS = require('./controllers/SVS')
const svs = new SVS(User)
const authenticate = svs.authenticate

const SMS = require('./controllers/SMS')
const sms = new SMS(Group, Message, User)

const ResMS = require('./controllers/ResMS')
const resms = new ResMS(Resource, User)

const PositioningSystem = require('./controllers/PositioningSystem');
const positioningSystem = new PositioningSystem(LocationModel, User);

const GroupSystem = require('./controllers/GroupSystem');
const groupSystem = new GroupSystem(Group, Message, User, LocationModel);

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
app.post("/api/accounts/:userID/chats", authenticate, sms.newGroup)
app.get("/api/accounts/:userID/chats", authenticate, sms.getGroupsForUser)
app.get("/api/accounts/:userID/chats/:groupID", authenticate, sms.getGroup)
app.put("/api/accounts/:userID/chats/:groupID", authenticate, groupSystem.promoteToTrackingGroup)
app.get("/api/accounts/:userID/chats/:groupID/messages", authenticate, sms.getMessages)
app.post("/api/accounts/:userID/chats/:groupID/messages", authenticate, sms.sendMessage);

// groups
app.post("/api/accounts/:userID/groups", authenticate, groupSystem.newGroup);
app.get("/api/accounts/:userID/groups", authenticate, groupSystem.getGroupsForUser);  // TODO stub
app.get("/api/accounts/:userID/groups/:groupID", authenticate, sms.getGroup);
app.get("/api/accounts/:userID/groups/:groupID/locations", authenticate, groupSystem.getLocations);
app.post("/api/accounts/:userID/groups/:groupID/meetingPoint", authenticate, groupSystem.updateMeetingPoint);
app.put("/api/accounts/:userID/groups/:groupID/meetingPoint", authenticate, groupSystem.updateMeetingPoint);

// online statuses
app.get("/api/accounts/:userID/usersOnlineStatuses", authenticate, ums.isOnline)

// locations
app.post("/api/accounts/:userID/location", authenticate, positioningSystem.updateLocation);

// object: users
// users
app.get("/api/users", ums.search) // get all users (only if query specified)
app.get("/api/users/:userID", ums.getInformation)

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
// app.get("/api/groups/:groupID", authenticate, gms.getGroupInfo)
// app.post("/api/groups", authenticate, gms.newGroup)


// for Let's Encrypt
app.get('/health-check', (req, res) => res.sendStatus(200));
app.use(express.static('static'));

// const options = {
//     cert: fs.readFileSync('./sslcert/fullchain.pem'),
//     key: fs.readFileSync('./sslcert/privkey.pem')
// }

const http = require('http');
// TODO environment variable
let HTTPS_MODE = false;
if (!HTTPS_MODE) {
  app.listen(8080, (req, res) => {
    //
  });
} else {
  // http.createServer((req, res) => {
  //     res.writeHead(301, { "Location": "https://" + req.headers['host'] + req.url });
  //     res.end();
  // }).listen(8080);
  // https.createServer(options, app).listen(8443);
}



// export the app, for testing
module.exports = app
