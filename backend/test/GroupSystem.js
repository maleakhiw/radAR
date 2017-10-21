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

describe('GroupSystem', () => {
  let user1token, user2token, user3token, user4token;

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
            .post('/api/auth')
            .send({
                "firstName": "User4",
                "lastName": "LastName",
                "email": "email4@example.com",
                "username": "user4",
                "profileDesc": "",
                "password": "hunter2",
                "deviceID": "memes"
            })
            .end((err, res) => {
              user4token = res.body.token;
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

  describe('POST /api/accounts/:userID/groups (sms.newGroup)', () => {
    it('should create a new group', (done) => {
      chai.request(server)
      .post('/api/accounts/1/groups')
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
        expect(res.body.group.groupID).to.equal(1);

        Group.findOne({groupID: 1}).exec()
        .then(group => {
          expect(group.members).to.include(1);
          expect(group.members).to.include(2);
          expect(group.isTrackingGroup).to.equal(true);
          done();
        })
      })
    })

  })

  describe('PUT /api/accounts/:userID/groups (update group details)', () => {
    it('should change the group name', done => {
      chai.request(server)
      .put('/api/accounts/1/groups/1')
      .set('token', user1token)
      .send({
        name: "New name"
      })
      .end((err, res) => {
        res.should.have.status(200);
        expect(res).to.be.json;
        expect(res.body.success).to.equal(true);

        Group.findOne({groupID: 1}).exec()
        .then(group => {
          expect(group.name).to.equal("New name");
          done();
        })
      })
    })

  })

  describe('PUT /api/accounts/:userID/groups/:groupID/members (addMembers)', () => {
    it('should add new members', done => {
      chai.request(server)
      .put('/api/accounts/1/groups/1/members')
      .set('token', user1token)
      .send({
        participantUserIDs: [3, 4, 79]
      })
      .end((err, res) => {
        console.log(res.body);
        res.should.have.status(200);
        expect(res).to.be.json;
        expect(res.body.success).to.equal(true);

        Group.findOne({groupID: 1})
        .exec()
        .then(group => {
          expect(group.members.includes(3)).to.equal(true);
          expect(group.members.includes(79)).to.equal(false); // filtering
          return User.findOne({userID: 3})
        })
        .then(user => {
          expect(user.groups.includes(1)).to.equal(true);
          return User.findOne({userID: 4})
        })
        .then(user => {
          expect(user.groups.includes(1)).to.equal(true);
          done();
        })

      })
    })

  })

  let newChatID;
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
        newChatID = res.body.group.groupID;
        // expect(res.body.group.groupID).to.equal(1); // important for deletion route
        // console.log(res.body);
        done();
      })
    })
  });

  describe('GET /api/accounts/:userID/groups', () => {
    it('should not have a non-tracking group', done => {
      chai.request(server)
      .get('/api/accounts/1/groups')
      .set('token', user1token)
      .send()
      .end((err, res) => {
        res.should.have.status(200);
        expect(res).to.be.json;
        expect(res.body.success).to.equal(true);
        // console.log(newChatID);
        // console.log(res.body.groups);
        let groupIDs = res.body.groups.map(entry => entry.groupID);
        expect(res.body.groups.length).to.equal(1); // other group filtered off (there are 2 groups)
        expect(groupIDs.includes(newChatID)).to.equal(false);
        done();
      })
    })
  })

  describe('DELETE /api/accounts/:userID/groups/:groupID/members/:memberUserID', () => {
    it('should delete the other member', done => {
      chai.request(server)
      .delete('/api/accounts/1/groups/' + newChatID + '/members/2')
      .set('token', user1token)
      .send()
      .end((err, res) => {
        res.should.have.status(200);
        expect(res).to.be.json;
        expect(res.body.success).to.equal(true);
        Group.findOne({groupID: newChatID}).exec()
        .then(group => {
          expect(group.members.includes(2)).to.equal(false);
          done();
        })
      })
    })
  })



})
