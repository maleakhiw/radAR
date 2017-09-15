let chai = require('chai')
let should = chai.should()
let expect = chai.expect
let assert = chai.assert //TODO

const sinon = require('sinon')

let SVS = require('../SVS') // our SUT: system under test

const User = require('../models/user')
const Request = require('../models/request')
const LastRequestID = require('../models/lastRequestID')
const LastUserID = require('../models/lastUserID')

describe('SVS unit tests', () => {
  let Metadata = require('../models/metadata')  // shadow the external Metadata
  let metadataFindOneStub

  describe('validateRequest(req, res, callback)', () => {

    afterEach(() => {
      metadataFindOneStub.restore()
    })

    it('should handle errors gracefully', (done) => {
      // stub findOne of Metadata
      var mockFindOne = {
        where: () => this,
        equals: () => this,
        exec: () => new Promise((resolve, reject) => {
          throw new Error('Some random error')
        })
      }
      metadataFindOneStub = sinon.stub(Metadata, "findOne").returns(mockFindOne)

      // SVS
      let svs = new SVS(null, Metadata, null)

      let callback = (req, res) => {
        done()
      }
      // mocks of req and res objects?
      let req = {
        body: {
          userID: 1,
          token: "80"
        },
        params: {},
        query: {}
      }
      let res = {
        json: (obj) => {
          res.sent = obj
          return res
        },
        status: (statusCode) => {
          res.statusCode = statusCode
          return res
        }
      }

      svs.validateRequest(req, res, callback)
      done()
    })

    it('should pass if userID and token is present and valid', (done) => {
      // stub findOne of Metadata
      var mockFindOne = {
        where: () => this,
        equals: () => this,
        exec: () => new Promise((resolve, reject) => {
          resolve({
            userID: 1,
            username: "test",
            lastSeen: Date.now(),
            deviceIDs: [],
            activeTokens: [],

            // methods
            save: () => new Promise((resolve, reject) => {
              resolve({
                userID: 1,
                username: "test",
                lastSeen: Date.now(),
                deviceIDs: [],
                activeTokens: []
              })
            })

          })
        })
      }
      metadataFindOneStub = sinon.stub(Metadata, "findOne").returns(mockFindOne)

      // SVS
      let svs = new SVS(null, Metadata, null)

      let callback = (req, res) => {
        done()
      }
      // mocks of req and res objects?
      let req = {
        body: {
          userID: 1,
          token: "80"
        },
        params: {},
        query: {}
      }
      let res = {
        json: (obj) => {
          res.sent = obj
          return res
        },
        status: (statusCode) => {
          res.statusCode = statusCode
          return res
        }
      }

      svs.validateRequest(req, res, callback)
      // expect(callback.called).to.equal(true) does not work because Promises
      // executes *after* expect(callback.called).to.equal(true)
    })

    it('should send an error if userID is invalid', (done) => {
      // stub findOne of Metadata
      var mockFindOne = {
        where: () => this,
        equals: () => this,
        exec: () => new Promise((resolve, reject) => resolve(null))
      }
      metadataFindOneStub = sinon.stub(Metadata, "findOne").returns(mockFindOne)

      // SVS
      let svs = new SVS(null, Metadata, null)

      let callback
      let req = {
        body: {
          userID: 1,
          token: "80"
        },
        params: {},
        query: {}
      }

      let res = {
        json: (obj) => {
          res.sent = obj
          return res
        },
        status: (statusCode) => {
          res.statusCode = statusCode
          return res
        }
      }

      svs.validateRequest(req, res, callback)

      done()
    })

    it('should send an error if token is missing', (done) => {
      // stub findOne of Metadata
      var mockFindOne = {
        where: () => this,
        equals: () => this,
        exec: () => new Promise((resolve, reject) => resolve(null))
      }
      metadataFindOneStub = sinon.stub(Metadata, "findOne").returns(mockFindOne)

      // SVS
      let svs = new SVS(null, Metadata, null, null)

      let callback
      let req = {
        body: {
          userID: 1
        },
        params: {},
        query: {}
      }

      let res = {
        json: (obj) => {
          res.sent = obj
          return res
        },
        status: (statusCode) => {
          this.statusCode = statusCode
          return res
        }
      }

      svs.validateRequest(req, res, callback)
      res.sent.success.should.equal(false)

      done()
    })

  })

  describe('signUp(req, res)', () => {})

  describe('login(req, res)', () => {
    // mock
  })

  after(() => {
    let svs = new SVS(User, Metadata, LastUserID) // reinitialise with actual modules for other (integration) tests
  })

})
