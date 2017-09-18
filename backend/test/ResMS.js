let mongoose = require('mongoose')

const User = require('../models/user')
const Metadata = require('../models/metadata')
const LastUserID = require('../models/lastUserID')
const Resource = require('../models/resource')

// dev dependencies
let chai = require('chai')
let chaiHttp = require('chai-http')
let server = require('../server')
let should = chai.should()
let expect = chai.expect

const fs = require('fs')

chai.use(chaiHttp)

mongoose.Promise = global.Promise

describe('ResMS', () => {

  before((done) => {  // clean up the database
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
      return Resource.remove({})
    })
    .then(() => {
      // create a user account
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

        // create another user account - should not be able to access resources of 1st user account
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
          done()
          })
      })
    })
    .catch((err) => {
      console.log('err', err)
      done()
    })


  })

  let fileID;

  describe('POST /api/accounts/:userID/resources', () => {

    it('should upload test.txt for user1', (done) => {
      chai.request(server)
      .post('/api/accounts/1/resources')
      .field('token', '79')
      .attach('file', fs.readFileSync('test/test.txt'), 'test.txt') // last param: filename that the server sees
      .end((err, res) => {
        res.should.have.status(200)
        res.body.success.should.equal(true)
        fileID = res.body.resourceID

        done()
      })
    })

  })

  describe('GET /api/accounts/:userID/resources/:resourceID', () => {

    it('should send test.txt back', (done) => {
      chai.request(server)
      .get('/api/accounts/1/resources/' + fileID)
      .query({token: '79'})
      .end((err, res) => {
        res.should.have.status(200)
        res.type.should.equal('text/plain')
        res.text.should.equal('Hello world!\n')

        done()

      })
    })

  })

})
