// let chai = require('chai')
// let should = chai.should()
// let expect = chai.expect
// let assert = chai.assert //TODO
//
// const sinon = require('sinon')
//
// let SVS = require('../SVS') // our SUT: system under test
//
// const User = require('../models/user')
// const Request = require('../models/request')
// const LastRequestID = require('../models/lastRequestID')
// const LastUserID = require('../models/lastUserID')
// const PasswordHash = require('../models/passwordHash')
//
// describe('SVS unit tests', () => {
//   let Metadata = require('../models/metadata')  // shadow the external Metadata
//   let metadataFindOneStub
//
//   describe('validateRequest(req, res, callback)', () => {
//
//     afterEach(() => {
//       metadataFindOneStub.restore() // reset to unstubbed state
//     })
//
//     it('should handle errors gracefully', (done) => {
//       // stub findOne of Metadata
//       var mockFindOne = {
//         where: () => this,
//         equals: () => this,
//         exec: () => new Promise((resolve, reject) => {  // throw an error to see how the system reacts
//           throw new Error('Some random error')
//         })
//       }
//       metadataFindOneStub = sinon.stub(Metadata, "findOne").returns(mockFindOne)
//
//       // SVS
//       let svs = new SVS(null, Metadata, null, null)
//
//       let callback = (req, res) => {
//         done()
//       }
//       // mocks of req and res objects?
//       let req = {
//         body: {
//           userID: 1
//         },
//         params: {},
//         query: {},
//         get: (key) => {
//           return "80"  // token
//         }
//       }
//       let res = {
//         json: (obj) => {
//           res.sent = obj
//           return res
//         },
//         status: (statusCode) => {
//           res.statusCode = statusCode
//           return res
//         }
//       }
//
//       svs.authenticate(req, res, callback)
//       done()
//     })
//
//     it('should pass if userID and token is present and valid', (done) => {
//       // stub findOne of Metadata
//       var mockFindOne = {
//         where: () => this,
//         equals: () => this,
//         exec: () => new Promise((resolve, reject) => {
//           resolve({
//             userID: 1,
//             username: "test",
//             lastSeen: Date.now(),
//             deviceIDs: [],
//             activeTokens: ["80"],
//
//             // methods
//             save: () => new Promise((resolve, reject) => {
//               resolve({
//                 userID: 1,
//                 username: "test",
//                 lastSeen: Date.now(),
//                 deviceIDs: [],
//                 activeTokens: []
//               })
//             })
//
//           })
//         })
//       }
//       metadataFindOneStub = sinon.stub(Metadata, "findOne").returns(mockFindOne)
//
//       // SVS
//       let svs = new SVS(null, Metadata, null, null)
//
//       let callback = (req, res) => {
//         done()
//       }
//       // mocks of req and res objects?
//       let req = {
//         body: {
//           userID: 1,
//         },
//         params: {},
//         query: {},
//         get: (key) => {
//           return "80"  // token
//         }
//       }
//       let res = {
//         json: (obj) => {
//           res.sent = obj
//           return res
//         },
//         status: (statusCode) => {
//           res.statusCode = statusCode
//           return res
//         }
//       }
//
//       svs.authenticate(req, res, callback)
//       // expect(callback.called).to.equal(true) does not work because Promises
//       // executes *after* expect(callback.called).to.equal(true)
//     })
//
//     it('should send an error if userID is invalid', (done) => {
//       // stub findOne of Metadata
//       var mockFindOne = {
//         where: () => this,
//         equals: () => this,
//         exec: () => new Promise((resolve, reject) => resolve(null))
//       }
//       metadataFindOneStub = sinon.stub(Metadata, "findOne").returns(mockFindOne)
//
//       // SVS
//       let svs = new SVS(null, Metadata, null, null)
//
//       let callback
//       let req = {
//         params: {userID: 1},
//         body: {},
//         query: {},
//         get: (key) => {
//           return "80"  // token
//         }
//       }
//
//       let res = {
//         json: (obj) => {
//           res.sent = obj
//           res.sent.success.should.equal(false)
//           done()
//           return res
//         },
//         status: (statusCode) => {
//           res.statusCode = statusCode
//           return res
//         }
//       }
//
//       svs.authenticate(req, res, callback)
//
//     })
//
//     it('should send an error if token is missing', (done) => {
//       // stub findOne of Metadata
//       var mockFindOne = {
//         where: () => this,
//         equals: () => this,
//         exec: () => new Promise((resolve, reject) => {
//           resolve({
//             userID: 1,
//             username: "test",
//             lastSeen: Date.now(),
//             deviceIDs: [],
//             activeTokens: ["80"],
//
//             // methods
//             save: () => new Promise((resolve, reject) => {
//               resolve({
//                 userID: 1,
//                 username: "test",
//                 lastSeen: Date.now(),
//                 deviceIDs: [],
//                 activeTokens: []
//               })
//             })
//
//           })
//         })
//       }
//       metadataFindOneStub = sinon.stub(Metadata, "findOne").returns(mockFindOne)
//
//       // SVS
//       let svs = new SVS(null, Metadata, null, null)
//
//       let callback
//       let req = {
//         params: {
//           userID: 1
//         },
//         body: {},
//         query: {},
//         get: (key) => {
//           return null  // token
//         }
//       }
//
//       let res = {
//         json: (obj) => {
//           res.sent = obj
//           res.sent.success.should.equal(false)
//           done()
//           return res
//         },
//         status: (statusCode) => {
//           this.statusCode = statusCode
//           return res
//         }
//       }
//
//       // console.log(res)
//       svs.authenticate(req, res, callback)
//
//     })
//
//   })
//
//   describe('signUp(req, res)', () => {})
//
//   describe('login(req, res)', () => {
//     // mock
//   })
//
//   after(() => {
//     let svs = new SVS(User, Metadata, LastUserID, PasswordHash) // reinitialise with actual modules for other (integration) tests
//   })
//
// })
