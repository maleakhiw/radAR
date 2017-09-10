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
describe('User', () => {

  before((done) => {  // NOTE: you can also return a Promise instead of
                          // using the done object
    mongoose.connect('mongodb://localhost/radarTest',
      { useMongoClient: true },
      (err) => { // TODO: see if this breaks
        if (!err) console.log('Connected to mongoDB (test)')
        else console.log('Failed to connect to mongoDB (test)')
    })

    User.remove({}).exec()
    .then(() => {
      console.log('Users cleared')
      return Metadata.remove({})
    })
    .then(() => {
      console.log('Metadatas cleared')
      return LastUserID.remove({})
    })
    .then(() => {
      console.log('LastUserIDs cleared')
      done()
    })
    .catch((err) => {
      console.log('err', err)
      done()
    })
  })

  // beforeEach((done) => {done()})

  describe('POST SVS/signUp', () => {

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

  })

  describe('POST SVS/login', () => {

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

  })

})
