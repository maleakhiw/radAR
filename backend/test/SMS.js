let mongoose = require('mongoose')

const User = require('../models/user');
const Request = require('../models/request');
const Group = require('../models/group');

let chai = require('chai')
let chaiHttp = require('chai-http')
let server = require('../server')
let should = chai.should()
let expect = chai.expect

chai.use(chaiHttp)

mongoose.Promise = global.Promise;

describe('SMS', () => {
  let user1token, user2token;

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
          done()
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
        console.log(res.body);
        done();
      })
    })

  })


})
