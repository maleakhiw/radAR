let mongoose = require('mongoose')

const User = require('../models/user');
const Request = require('../models/request');
const Group = require('../models/group');
const Message = require('../models/message');

let chai = require('chai')
let chaiHttp = require('chai-http')
let server = require('../server')
let should = chai.should()
let expect = chai.expect

chai.use(chaiHttp)

mongoose.Promise = global.Promise;

describe('SMS', () => {
  let user1token, user2token, user3token;

  before((done) => {
    mongoose.connect('mongodb://localhost/radarTest',
      { useMongoClient: true },
      (err) => {
        if (err) console.log(err)
      }
    )

    // clean database before tests
    Request.remove({}).exec()
    .then(() => User.remove({}))
    .then(() => Group.remove({}))
    .then(() => Message.remove({}))
    .then(() => {
      // create dummy users
      chai.request(server)
      .post('/api/auth')
      .send({
          "firstName": "User1",
          "lastName": "LastName",
          "email": "email1@example.com",
          "username": "user1",
          "profileDesc": "",
          "password": "hunter2",
          "deviceID": "memes"
      })
      .end((err, res) => {
        res.should.have.status(200)
        expect(res).to.be.json
        expect(res.body.success).to.equal(true)
        expect(res.body.userID).to.equal(1)
        user1token = res.body.token

        chai.request(server)
        .post('/api/auth')
        .send({
            "firstName": "User2",
            "lastName": "LastName",
            "email": "email2@example.com",
            "username": "user2",
            "profileDesc": "",
            "password": "hunter2",
            "deviceID": "memes"
        })
        .end((err, res) => {
          res.should.have.status(200)
          expect(res).to.be.json
          expect(res.body.success).to.equal(true)
          expect(res.body.userID).to.equal(2)
          user2token = res.body.token

          chai.request(server)
          .post('/api/auth')
          .send({
              "firstName": "User3",
              "lastName": "LastName",
              "email": "email3@example.com",
              "username": "user3",
              "profileDesc": "",
              "password": "hunter2",
              "deviceID": "memes"
          })
          .end((err, res) => {
            res.should.have.status(200)
            expect(res).to.be.json
            expect(res.body.success).to.equal(true)
            expect(res.body.userID).to.equal(3)
            user3token = res.body.token;

            done();
          })
        })


      })
    })

    .catch((err) => {
      throw new Error(err)
    })

  })

  describe('POST /api/accounts/:userID/chats (sms.newGroup)', () => {
    it('should create a new chat', (done) => {
      chai.request(server)
      .post('/api/accounts/1/chats')
      .set('token', user1token)
      .send({
        name: 'test group',
        participantUserIDs: [2]
      })
      .end((err, res) => {
        res.should.have.status(200);
        expect(res).to.be.json;
        expect(res.body.success).to.equal(true);
        expect(res.body.group).to.not.equal(null);
        expect(res.body.group.groupID).to.equal(1); // important for deletion route
        // console.log(res.body);
        done();
      })
    })


  })

  describe('GET /api/accounts/:userID/chats (sms.getGroupsForUser)', () => {
    it('user2 should be in the group (chat, upgradeable to TrackingGroup)', (done) => {
      chai.request(server)
      .get('/api/accounts/2/chats')
      .set('token', user2token)
      .end((err, res) => {
        res.should.have.status(200);
        expect(res).to.be.json;
        expect(res.body.success).to.equal(true);
        console.log(res.body);
        expect(res.body.groups[0].members.includes(2)).to.equal(true);

        done();
      })
    })
  })

  describe('POST /api/accounts/:userID/chats/:groupID/messages (sms.sendMessage)', () => {
    it('should send a message to the group', (done) => {
      chai.request(server)
      .post('/api/accounts/1/chats/1/messages') // TODO remove hardcoded group ID
      .set('token', user1token)
      .send({
        message: 'Hello world'
      })
      .end((err, res) => {
        // console.log(res.body);
        res.should.have.status(200);
        expect(res).to.be.json;
        expect(res.body.success).to.equal(true);
        expect(res.body.sentMessage).to.not.equal(null);
        expect(res.body.sentMessage.text).to.equal('Hello world');
        expect(res.body.sentMessage.from).to.equal(1);
        expect(res.body.sentMessage.contentType).to.equal('text');

        done();
      })
    })
  })

  describe('GET /api/accounts/:userID/chats/:groupID/messages (sms.getMessages)', () => {
    it('should see a message in the group', (done) => {
      chai.request(server)
      .get('/api/accounts/2/chats/1/messages') // TODO remove hardcoded group ID
      .set('token', user2token)
      .end((err, res) => {
        // console.log(res.body);
        res.should.have.status(200);
        expect(res).to.be.json;
        expect(res.body.success).to.equal(true);
        // expect(res.body.groups).to.include(1);
        expect(res.body.messages.length).to.equal(1);
        expect(res.body.messages[0].from).to.equal(1);
        expect(res.body.messages[0].contentType).to.equal('text');
        expect(res.body.messages[0].text).to.equal('Hello world');

        done();
      })
    })

  })

  describe('GET /api/accounts/:userID/chats (sms.getGroupsForUser)', () => {
    it('user2 should see the last sent message', (done) => {
      chai.request(server)
      .get('/api/accounts/2/chats')
      .set('token', user2token)
      .end((err, res) => {
        res.should.have.status(200);
        expect(res).to.be.json;
        expect(res.body.success).to.equal(true);

        // console.log(res.body);

        expect(res.body.groupsLastMessages['1']).to.not.equal(null);
        expect(res.body.groupsLastMessages['1'].text).to.equal('Hello world');
        expect(res.body.groups[0].groupID).to.equal(1);

        expect(res.body.groups[0].usersDetails).to.not.equal(null);
        expect(res.body.groups[0].usersDetails['1'].username).to.equal('user1');
        expect(res.body.groups[0].usersDetails['2'].username).to.equal('user2');

        done();
      })
    })
  })

  describe('GET /api/accounts/:userID/chats/:chatID (sms.getGroup)', () => {
    it('user2 should be able to access group details', (done) => {
      chai.request(server)
      .get('/api/accounts/2/chats/1')
      .set('token', user2token)
      .end((err, res) => {
        // console.log(res.body.group.usersDetails[1].commonGroups);
        res.should.have.status(200);
        expect(res).to.be.json;
        expect(res.body.success).to.equal(true);
        expect(res.body.group).to.not.equal(null);
        expect(res.body.group.lastMessage).to.not.equal(null);
        // TODO more assertions
        done();
      })
    })
  })

  describe('DELETE /api/accounts/:userID/groups/:groupID', () => {
    it('should leave the group', done => {
      chai.request(server)
      .delete('/api/accounts/2/groups/1')
      .set('token', user2token)
      .end((err, res) => {
        expect(res).to.be.json;
        expect(res.body.success).to.equal(true);

        Group.findOne({groupID: 1}).exec()
        .then(group => {
          if (group) {
            expect(group.members.includes(2)).to.equal(false);
            done();
          }
        })
        .catch(err => console.log(err))

      })
    });

  })


})

describe('SMS - getOneToOneChat', () => {
  let user1token, user2token, user3token;

  before((done) => {
    mongoose.connect('mongodb://localhost/radarTest',
      { useMongoClient: true },
      (err) => {
        if (err) console.log(err)
      }
    )

    // clean database before tests
    Request.remove({}).exec()
    .then(() => User.remove({}))
    .then(() => Group.remove({}))
    .then(() => Message.remove({}))
    .then(() => {
      // create dummy users
      chai.request(server)
      .post('/api/auth')
      .send({
          "firstName": "User1",
          "lastName": "LastName",
          "email": "email1@example.com",
          "username": "user1",
          "profileDesc": "",
          "password": "hunter2",
          "deviceID": "memes"
      })
      .end((err, res) => {
        res.should.have.status(200)
        expect(res).to.be.json
        expect(res.body.success).to.equal(true)
        expect(res.body.userID).to.equal(1)
        user1token = res.body.token

        chai.request(server)
        .post('/api/auth')
        .send({
            "firstName": "User2",
            "lastName": "LastName",
            "email": "email2@example.com",
            "username": "user2",
            "profileDesc": "",
            "password": "hunter2",
            "deviceID": "memes"
        })
        .end((err, res) => {
          res.should.have.status(200)
          expect(res).to.be.json
          expect(res.body.success).to.equal(true)
          expect(res.body.userID).to.equal(2)
          user2token = res.body.token

          chai.request(server)
          .post('/api/auth')
          .send({
              "firstName": "User3",
              "lastName": "LastName",
              "email": "email3@example.com",
              "username": "user3",
              "profileDesc": "",
              "password": "hunter2",
              "deviceID": "memes"
          })
          .end((err, res) => {
            res.should.have.status(200)
            expect(res).to.be.json
            expect(res.body.success).to.equal(true)
            expect(res.body.userID).to.equal(3)
            user3token = res.body.token;

            chai.request(server)
            .post('/api/accounts/1/groups')
            .set('token', user1token)
            .send({
              name: 'test group',
              participantUserIDs: [2, 3],
              meetingPoint: { "lat" : -37.7983668, "lon" : 144.95937859999998, "name" : "Baillieu Library"}
            })
            .end((err, res) => {
              res.should.have.status(200);
              expect(res).to.be.json;
              expect(res.body.success).to.equal(true);
              expect(res.body.group).to.not.equal(null);
              // console.log(res.body);
              done();
            })
          })
        })

      })
    })

    .catch((err) => {
      throw new Error(err)
    })

  })

  describe('getOneToOneChat', () => {
    let groupID;

    it('should create a new chat, since the previously created group is for 3 people', done => {
      chai.request(server)
      .get('/api/accounts/1/chats/with/2')
      .set('token', user1token)
      .end((err, res) => {
        res.should.have.status(200);
        expect(res).to.be.json;
        expect(res.body.success).to.equal(true);
        expect(res.body.group).to.not.equal(null);
        expect(res.body.group.members.includes(1)).to.equal(true);
        expect(res.body.group.members.includes(2)).to.equal(true);
        expect(res.body.group.groupID).to.not.equal(null);
        groupID = res.body.group.groupID;
        done();
      })
    })

    it('should return the existing 1-to-1 chat', done => {
      chai.request(server)
      .get('/api/accounts/1/chats/with/2')
      .set('token', user1token)
      .end((err, res) => {
        res.should.have.status(200);
        expect(res).to.be.json;
        expect(res.body.success).to.equal(true);
        expect(res.body.group).to.not.equal(null);
        expect(res.body.group.members.includes(1)).to.equal(true);
        expect(res.body.group.members.includes(2)).to.equal(true);
        expect(res.body.group.groupID).to.equal(groupID);
        done();
      })
    })

    it('should not do anything for an invalid userID', done => {
      chai.request(server)
      .get('/api/accounts/1/chats/with/79')
      .set('token', user1token)
      .end((err, res) => {
        res.should.have.status(200);
        expect(res).to.be.json;
        expect(res.body.success).to.equal(false);
        expect(res.body.errors[0].errorCode).to.equal(88);
        done();
      })
    })

  })

})
