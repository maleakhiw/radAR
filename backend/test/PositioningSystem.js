let mongoose = require('mongoose')

const User = require('../models/user')
const LocationModel = require('../models/location')

// dev dependencies
let chai = require('chai')
let chaiHttp = require('chai-http')
let server = require('../server')
let should = chai.should()
let expect = chai.expect

const fs = require('fs')

chai.use(chaiHttp)

mongoose.Promise = global.Promise

describe('PositioningSystem', () => {

  let user1token, user2token;

  before(done => {
    User.remove({}).exec()
    .then(() => {
      return LocationModel.remove({})
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
        user1token = res.body.token

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
          user2token = res.body.token
          done()
          })
      })

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
        .catch((err) => {
          console.log('err', err)
          done()
        })
      })
    })

  })

  describe('POST api/accounts/:userID/location', () => {
    it('should update the location on the server', done => {
      chai.request(server)
      .post('/api/accounts/1/location')
      .set('token', user1token)
      .send({
      	"lat": -37.8516938,
      	"lon": 144.9772355,
      	"accuracy": 8,
      	"heading": 0
      })
      .end((err, res) => {
        res.should.have.status(200);
        expect(res).to.be.json;
        expect(res.body.success).to.equal(true);

        LocationModel.findOne({userID: 1}).exec()
        .then(location => {
          expect(location).to.not.equal(null);
          done();
        })
        .catch((err) => {
          console.log('err', err)
        })
      })
    })
  })

})
