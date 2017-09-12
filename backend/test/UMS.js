let mongoose = require('mongoose')

const Metadata = require('../models/metadata')
const User = require('../models/user')
const Request = require('../models/request')
const LastRequestID = require('../models/lastRequestID')
const LastUserID = require('../models/lastUserID')

// dev dependencies
let chai = require('chai')
let chaiHttp = require('chai-http')
let server = require('../server')
let should = chai.should()
let expect = chai.expect

chai.use(chaiHttp)

mongoose.Promise = global.Promise

describe('UMS', () => {

  before((done) => {
    mongoose.connect('mongodb://localhost/radarTest',
      { useMongoClient: true },
      (err) => {
        if (err) console.log(err)
      }
    )

    // clean database before tests
    Metadata.remove({}).exec()
    .then(() => Request.remove({}))
    .then(() => LastRequestID.remove({}))
    .then(() => User.remove({}))
    .then(() => LastUserID.remove({}))
    .then(() => {
      // create dummy users
      chai.request(server)
      .post('/api/accounts')
      .send({
          "firstName": "User1",
          "lastName": "LastName",
          "email": "email@example.com",
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

        chai.request(server)
        .post('/api/accounts')
        .send({
            "firstName": "User2",
            "lastName": "LastName",
            "email": "email@example.com",
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
          done()
        })


      })
    })

    .catch((err) => {
      throw new Error(err)
    })



  })

  describe('GET /api/users/:userID/friends', () => {
    it('should send a friend request from user1 to user2', (done) => {
      chai.request(server)
        .post('/api/users/1/friends')
        .send({
          invitedUserID: 2,
          token: "79"
        })
        .end((err, res) => {
          res.should.have.status(200)
          expect(res).to.be.json
          expect(res.body.success).to.equal(true)

          Request.findOne({requestID: 1}).exec()
          .then((request) => {
            done() // a request exists
          })
          .catch((err) => {
            throw new Error('Database error')
          })
        })
    })

    it('should send a friend request from user1 to userID 10 (and fail - does not exist)', (done) => {
      chai.request(server)
        .post('/api/users/1/friends')
        .send({
          invitedUserID: 10,
          token: "79"
        })
        .end((err, res) => {
          res.should.have.status(200)
          expect(res).to.be.json
          expect(res.body.success).to.equal(false)

          done()
        })
    })

    it('user1 should not be able to send another friend request to user2 while it is pending', (done) => {
      chai.request(server)
        .post('/api/users/1/friends')
        .send({
          invitedUserID: 2,
          token: "79"
        })
        .end((err, res) => {
          res.should.have.status(200)
          expect(res).to.be.json
          expect(res.body.success).to.equal(false)

          done()
        })
    })


  })

  describe('GET /api/users/:userID/friendRequests and DELETE /api/users/:userID/friendRequests/:requestID', () => {
    it('user2 should receive a friend request from user1', (done) => {
      chai.request(server)
        .get('/api/users/2/friendRequests')
        .send({
          token: "79"
        })
        .end((err, res) => {
          res.should.have.status(200)
          expect(res).to.be.json
          expect(res.body.success).to.equal(true)
          expect(res.body.requestDetails[0].requestID).to.equal(1)
          expect(res.body.requestDetails[0].from).to.equal(1)

          done()
        })
    })

    it('user2 should accept the friend request from user1', (done) => {
      chai.request(server)
        .delete('/api/users/2/friendRequests/1')
        .send({
          token: "79",
          action: "accept"
        })
        .end((err, res) => {
          res.should.have.status(200)
          expect(res).to.be.json
          expect(res.body.success).to.equal(true)

          done()
        })
    })

  })

  describe('GET /api/users/:userID/friends', () => {
    it("user1 should have user2 in user1's friends list", (done) => {
      chai.request(server)
        .get('/api/users/1/friends')
        .send({
          token: "79"
        })
        .end((err, res) => {
          res.should.have.status(200)
          expect(res).to.be.json
          expect(res.body.success).to.equal(true)

          expect(res.body.friends.map((entry) => entry.userID)).contains(2)

          done()

        })
    })

  })

})
