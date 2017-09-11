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

  describe('POST /SVS/signUp', () => {

    it('it should create a new user', (done) => {
      chai.request(server)
        .post('/SVS/signUp')
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
          console.log(res.body)
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

    it('it return an invalid email error', (done) => {
      chai.request(server)
        .post('/SVS/signUp')
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
          console.log(res.body)
          expect(res.body.success).to.equal(false)
          done()
        })

    })


  })

  describe('POST /SVS/login', () => {

    it('it should log in (provide token + userID for use with future requests)', (done) => {
      chai.request(server)
        .post('/SVS/login')
        .send({
          username: "manshar",
          password: "hunter2"
        })
        .end((err, res) => {
          res.should.have.status(200)
          expect(res).to.be.json
          console.log(res.body)
          expect(res.body.success).to.equal(true)

          // do we have a token and userID?
          expect(res.body).to.include.all.keys('token', 'userID')

          done()
        })
    })

    it('should not log in an invalid user', (done) => {
      chai.request(server)
        .post('/SVS/login')
        .send({
          username: "invalidUsername",
          password: "password"
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
