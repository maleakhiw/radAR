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
      .post('/SVS/signUp')
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
        console.log('request 1 done')
        res.should.have.status(200)
        expect(res).to.be.json
        expect(res.body.success).to.equal(true)
        expect(res.body.userID).to.equal(1)

        chai.request(server)
        .post('/SVS/signUp')
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
          console.log('request 2 done')
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

  describe('POST /UMS/addFriend', () => {
    it('it should send a friend request from user1 to user2', (done) => {
      chai.request(server)
        .post('/UMS/addFriend')
        .send({
          userID: 1,
          invitedUserID: 2,
          token: "79"
        })
        .end((err, res) => {
          res.should.have.status(200)
          expect(res).to.be.json
          console.log(res.body)
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

    it('it should send a friend request from user1 to userID 10 (and fail)', (done) => {
      chai.request(server)
        .post('/UMS/addFriend')
        .send({
          userID: 1,
          invitedUserID: 10,
          token: "79"
        })
        .end((err, res) => {
          res.should.have.status(200)
          expect(res).to.be.json
          console.log(res.body)
          expect(res.body.success).to.equal(false)

          done()
        })
    })

  })


})
