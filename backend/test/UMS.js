// let mongoose = require('mongoose')
//
// const User = require('../models/user')
// const Request = require('../models/request')
//
// // dev dependencies
// let chai = require('chai')
// let chaiHttp = require('chai-http')
// let server = require('../server')
// let should = chai.should()
// let expect = chai.expect
//
// chai.use(chaiHttp)
//
// mongoose.Promise = global.Promise
//
// describe('UMS', () => {
//
//   let user1token, user2token
//
//   before((done) => {
//     mongoose.connect('mongodb://localhost/radarTest',
//       { useMongoClient: true },
//       (err) => {
//         if (err) console.log(err)
//       }
//     )
//
//     // clean database before tests
//     Metadata.remove({}).exec()
//     .then(() => Request.remove({}))
//     .then(() => LastRequestID.remove({}))
//     .then(() => User.remove({}))
//     .then(() => LastUserID.remove({}))
//     .then(() => PasswordHash.remove({}))
//     .then(() => {
//       // create dummy users
//       chai.request(server)
//       .post('/api/auth')
//       .send({
//           "firstName": "User1",
//           "lastName": "LastName",
//           "email": "email1@example.com",
//           "username": "user1",
//           "profileDesc": "",
//           "password": "hunter2",
//           "deviceID": "memes"
//       })
//       .end((err, res) => {
//         res.should.have.status(200)
//         expect(res).to.be.json
//         expect(res.body.success).to.equal(true)
//         expect(res.body.userID).to.equal(1)
//         user1token = res.body.token
//
//         chai.request(server)
//         .post('/api/auth')
//         .send({
//             "firstName": "User2",
//             "lastName": "LastName",
//             "email": "email2@example.com",
//             "username": "user2",
//             "profileDesc": "",
//             "password": "hunter2",
//             "deviceID": "memes"
//         })
//         .end((err, res) => {
//           res.should.have.status(200)
//           expect(res).to.be.json
//           expect(res.body.success).to.equal(true)
//           expect(res.body.userID).to.equal(2)
//           user2token = res.body.token
//           done()
//         })
//
//
//       })
//     })
//
//     .catch((err) => {
//       throw new Error(err)
//     })
//
//
//
//   })
//
//   describe('POST /api/accounts/:userID/friends', () => {
//     it('should send a friend request from user1 to user2', (done) => {
//       chai.request(server)
//       .post('/api/accounts/1/friends')
//       .set('token', user1token)
//       .send({
//         invitedUserID: 2,
//       })
//       .end((err, res) => {
//         res.should.have.status(200)
//         expect(res).to.be.json
//         expect(res.body.success).to.equal(true)
//
//         Request.findOne({requestID: 1}).exec()
//         .then((request) => {
//           done() // a request exists
//         })
//         .catch((err) => {
//           throw new Error('Database error')
//         })
//       })
//     })
//
//     it('user1 should not be able to send another friend request to user2 while it is pending', (done) => {
//       chai.request(server)
//       .post('/api/accounts/1/friends')
//       .set('token', user1token)
//       .send({
//         invitedUserID: 2,
//       })
//       .end((err, res) => {
//
//         expect(res).to.be.json
//         expect(res.body.success).to.equal(false)
//
//         done()
//       })
//     })
//
//
//   })
//
//   describe('GET /api/accounts/:userID/friends', () => {
//     it('should send a friend request from user1 to userID 10 (and fail - does not exist)', (done) => {
//       chai.request(server)
//       .post('/api/accounts/1/friends')
//       .set('token', user1token)
//       .send({
//         invitedUserID: 10,
//       })
//       .end((err, res) => {
//
//         expect(res).to.be.json
//         expect(res.body.success).to.equal(false)
//
//         done()
//       })
//     })
//
//
//   })
//
//   describe('GET /api/accounts/:userID/friendRequests and DELETE /api/accounts/:userID/friendRequests/:requestID', () => {
//     it('user2 should receive a friend request from user1', (done) => {
//       chai.request(server)
//       .get('/api/accounts/2/friendRequests')
//       .set('token', user2token)
//       .query({
//       })
//       .end((err, res) => {
//         console.log(res.body)
//         res.should.have.status(200)
//         expect(res).to.be.json
//         expect(res.body.success).to.equal(true)
//         expect(res.body.requestDetails[0].requestID).to.equal(1)
//         expect(res.body.requestDetails[0].from).to.equal(1)
//
//         done()
//       })
//     })
//
//     it('user2 should accept the friend request from user1', (done) => {
//       chai.request(server)
//       .post('/api/accounts/2/friendRequests/1')
//       .set('token', user2token)
//       .send({
//         action: "accept"
//       })
//       .end((err, res) => {
//         res.should.have.status(200)
//         expect(res).to.be.json
//         expect(res.body.success).to.equal(true)
//
//         done()
//       })
//     })
//
//   })
//
//   describe('GET /api/accounts/:userID/friends', () => {
//     it("user1 should have user2 in user1's friends list", (done) => {
//       chai.request(server)
//       .get('/api/accounts/1/friends')
//       .set('token', user1token)
//       .end((err, res) => {
//         res.should.have.status(200)
//         expect(res).to.be.json
//         expect(res.body.success).to.equal(true)
//
//         expect(res.body.friends.map((entry) => entry.userID)).contains(2)
//
//         done()
//
//       })
//     })
//
//   })
//
//   describe('GET /api/users/ (search for users)', () => {
//     it("search by name (query: user, existing users: User1 and User2)", (done) => {
//       chai.request(server)
//       .get('/api/users/')
//       .set('token', user1token)
//       .query({
//         userID: 1,
//         query: "user",
//         searchType: "name"
//       })
//       .end((err, res) => {
//         res.should.have.status(200)
//         expect(res).to.be.json
//         expect(res.body.success).to.equal(true)
//
//         expect(res.body.results.map((entry) => entry.userID)).includes(1, 2)
//
//         done()
//       })
//     })
//
//     it("search by email (query: user, existing users: User1 and User2)", (done) => {
//       chai.request(server)
//       .get('/api/users')
//       .set('token', user1token)
//       .query({
//         userID: 1,  // TODO move userID to header
//         query: "email1@example.com",
//         searchType: "email"
//       })
//       .end((err, res) => {
//         res.should.have.status(200)
//         expect(res).to.be.json
//
//         expect(res.body.results.map((entry) => entry.userID)).includes(1)
//         done()
//       })
//     })
//
//     it("search by email and get no results", (done) => {
//       chai.request(server)
//       .get('/api/users')
//       .query({
//         userID: 1,
//         query: "email@example.com",
//         searchType: "email"
//       })
//       .set('token', user1token)
//       .end((err, res) => {
//         res.should.have.status(200)
//         expect(res).to.be.json
//
//         expect(res.body.results.map((entry) => entry.userID).length).to.equal(0)
//         done()
//       })
//     })
//
//     it("search by username and get one result (user1)", (done) => {
//       chai.request(server)
//       .get('/api/users')
//       .query({
//         userID: 1,
//         query: "user1",
//         searchType: "username"
//       })
//       .set('token', user1token)
//       .end((err, res) => {
//         res.should.have.status(200)
//         expect(res).to.be.json
//
//         expect(res.body.results.map((entry) => entry.userID).length).to.equal(1)
//         done()
//       })
//     })
//   })
//
// })
