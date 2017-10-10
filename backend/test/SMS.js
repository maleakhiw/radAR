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
    it('should create a new group', (done) => {
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
        expect(res.body.groups).to.include(1);

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

  describe('DELETE /api/accounts/:userID/chats/:groupID', () => {
    it('should not delete the group (not admin)', done => {
      chai.request(server)
      .delete('/api/accounts/2/groups/1')
      .set('token', user2token)
      .end((err, res) => {
        console.log(res.body);
        expect(res).to.be.json;
        expect(res.body.success).to.equal(false);

        Group.findOne({groupID: 2}).exec()
        .then(group => {
          if (!group) {
            done();
          } else {
            throw 'Group should not be deleted';
          }
        })
      })
    });

    it('should delete the group if admin deletes it', done => {
      chai.request(server)
      .delete('/api/accounts/1/groups/1')
      .set('token', user1token)
      .end((err, res) => {
        console.log(res.body);
        expect(res).to.be.json;
        expect(res.body.success).to.equal(true);
        Group.findOne({groupID: 2}).exec()
        .then(group => {
          // console.log(group)
          if (!group) {
            done();
          } else {
            throw 'Group should be deleted';
          }
        })
      })
    });

  })


})
