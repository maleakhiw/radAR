let mongoose = require('mongoose')

const User = require('../models/user')
const Metadata = require('../models/metadata')
const LastUserID = require('../models/lastUserID')

// dev dependencies
let chai = require('chai')
let chaiHttp = require('chai-http')
let server = require('../server')
let should = chai.should()
let expect = chai.expect

chai.use(chaiHttp)

mongoose.Promise = global.Promise

// parent block
describe('SVS', () => {

  before((done) => {  // NOTE: you can also return a Promise instead of
                          // using the done object
    mongoose.connect('mongodb://localhost/radarTest',
      { useMongoClient: true },
      (err) => {
        if (err) {
          console.log(err)
        }
    })

    User.remove({}).exec()
    .then(() => {
      return Metadata.remove({})
    })
    .then(() => {
      return LastUserID.remove({})
    })
    .then(() => {
      done()
    })
    .catch((err) => {
      console.log('err', err)
      done()
    })
  })

  // beforeEach((done) => {done()})

  describe('POST /api/accounts', () => {

    it('should create a new user', (done) => {
      chai.request(server)
        .post('/api/accounts')
        .send({
            "firstName": "Fadhil",
            "lastName": "Anshar",
            "email": "me@fadhilanshar.com",
            "username": "manshar",
            "profileDesc": "am horse",
            "password": "hunter2",
            "deviceID": "memes"
        })
        .end((err, res) => {
          res.should.have.status(200)
          expect(res).to.be.json
          expect(res.body.success).to.equal(true)

          User.findOne({username: "manshar"}).exec()
          .then((user) => {
            user.firstName.should.equal('Fadhil')
            done()
          })
          .catch((err) => {
            throw new Error('Database error')
          })
        })

    })

    it('should return an invalid email error', (done) => {
      chai.request(server)
        .post('/api/accounts')
        .send({
            "firstName": "Fadhil",
            "lastName": "Anshar",
            "email": "invalidEmail",
            "username": "manshar",
            "profileDesc": "am horse",
            "password": "hunter2",
            "deviceID": "memes"
        })
        .end((err, res) => {
          res.should.have.status(200)
          expect(res).to.be.json
          expect(res.body.success).to.equal(false)
          done()
        })

    })

    // TODO: create 2nd user, validate userID is 2


  })

  describe('POST /api/accounts/:username', () => {

    it('should log in (provide token + userID for use with future requests)', (done) => {
      chai.request(server)
        .post('/api/accounts/manshar')
        .send({
          password: "hunter2"
        })
        .end((err, res) => {
          res.should.have.status(200)
          expect(res).to.be.json
          expect(res.body.success).to.equal(true)

          // do we have a token and userID?
          expect(res.body).to.include.all.keys('token', 'userID')

          done()
        })
    })

    it('should not log in an invalid user', (done) => {
      chai.request(server)
        .post('/api/accounts/invalidUsername')
        .send({
          password: "password"
        })
        .end((err, res) => {
          res.should.have.status(200)
          expect(res).to.be.json
          expect(res.body.success).to.equal(false)

          done()
        })
    })

  })

  describe('GET request to restricted resource', () => {

    it('should tell requester that token is missing', (done) => {
      chai.request(server)
        .get('/api/users/1/friendRequests')
        .send({})
        .end((err, res) => {  // TODO: error codes
          console.log(res.body)
          res.should.have.status(200)
          expect(res).to.be.json
          expect(res.body.success).to.equal(false)

          done()
        })
    })
  })

})
