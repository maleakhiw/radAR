// chai - assertions framework
const chai = require('chai');
const should = chai.should();
const expect = chai.expect;
const assert = chai.assert;

const UMS = require('../controllers/UMS');

const User = require('../models/user');
const Request = require('../models/request');

// sinon - mocking
const sinon = require('sinon');

describe('UMS unit tests', () => {
  let ums, findOneStub;

  before(() => {
    ums = new UMS(User, Request);
  });

  afterEach(() => {
    if (findOneStub) {
      findOneStub.restore();
    }
  })

  describe('validateAddFriend', () => {
    it('adding yourself should be invalid', done => {
      let req = {
        params: { // will be provided by authentication middleware
          userID: 1
        },
        body: {
          invitedUserID: 1
        }
      }
      let errorKeys = ums.validateAddFriend(req);
      expect(errorKeys.length).to.equal(1);
      expect(errorKeys[0]).to.equal('selfInviteError');
      done();
    });

    it('not specifying who to add should be invalid', done => {
      let req = {
        params: {
          userID: 1
        },
        body: {}
      }
      let errorKeys = ums.validateAddFriend(req);
      expect(errorKeys.length).to.equal(1);
      expect(errorKeys[0]).to.equal('missingInvitedUserID');
      done();
    })

    it('should return no errors if user adds a different user', done => {
      let req = {
        params: { // will be provided by authentication middleware
          userID: 1
        },
        body: {
          invitedUserID: 2
        }
      }
      let errorKeys = ums.validateAddFriend(req);
      expect(errorKeys.length).to.equal(0);
      done();
    })
  })

  describe('checkIfAlreadyFriends', () => {
    it('should reject the Promise if user is already friends', done => {
      // return a fake user
      var mockFindOne = {
        where: () => this,
        equals: () => this,
        exec: () => new Promise((resolve, reject) => resolve({
          friends: [2, 3]
        }))
      };
      findOneStub = sinon.stub(User, 'findOne').returns(mockFindOne);

      ums.checkIfAlreadyFriends(1, 2)
      .then(() => {
        console.log('Should not go here');
      })
      .catch((err) => {
        if (err == 'invitedUserIDAlreadyAdded') {
          done();
        }
      });
    })

    it('should resolve the Promise if user is not already friends', done => {
      // return a fake user
      var mockFindOne = {
        where: () => this,
        equals: () => this,
        exec: () => new Promise((resolve, reject) => resolve({
          friends: [3]
        }))
      };
      findOneStub = sinon.stub(User, 'findOne').returns(mockFindOne);

      ums.checkIfAlreadyFriends(1, 2)
      .then(() => {
        done();
      })
      .catch((err) => {
        console.log('Should not go here');
      });
    })
  })

  describe('checkIfRequestAlreadySent', () => {
    it('should reject("friendRequestAlreadyExists") if a request is already sent', done => {
      var mockFindOne = {
        where: () => this,
        equals: () => this,
        exec: () => new Promise((resolve, reject) => resolve({}))
      };
      findOneStub = sinon.stub(Request, 'findOne').returns(mockFindOne);

      ums.checkIfRequestAlreadySent(1, 2)
      .then(() => {})
      .catch(err => {
        if (err == 'friendRequestAlreadyExists') done();
      })
    })

    it('should resolve() if a request is already sent', done => {
      var mockFindOne = {
        where: () => this,
        equals: () => this,
        exec: () => new Promise((resolve, reject) => resolve(null))
      };
      findOneStub = sinon.stub(Request, 'findOne').returns(mockFindOne);

      ums.checkIfRequestAlreadySent(1, 2)
      .then(() => done());
    })
  });

  describe('getRequestIDForNewRequest', () => {
    it('should resolve with 1 if there is no existing request yet', done => {
      var mockFindOne = { // pretends to be the latest request (already sorted by requestID, returning only 1)
        sort: () => mockFindOne,
        exec: () => new Promise((resolve, reject) => resolve(null))
      };
      findOneStub = sinon.stub(Request, 'findOne').returns(mockFindOne);

      ums.getRequestIDForNewRequest().then((requestID) => {
        expect(requestID).to.equal(1);
        done();
      })
    })

    it('should resolve with the last requestID + 1 if there exists a request from the database', done => {
      var mockFindOne = { // pretends to be the latest request (already sorted by requestID, returning only 1)
        sort: () => mockFindOne,
        exec: () => new Promise((resolve, reject) => resolve({
          requestID: 79
        }))
      };
      findOneStub = sinon.stub(Request, 'findOne').returns(mockFindOne);

      ums.getRequestIDForNewRequest().then((requestID) => {
        expect(requestID).to.equal(80);
        done();
      })
    })
  })

  describe('validateDeleteRequest', () => {
    it('should reject if request does not exist', done => {
      var mockFindOne = { // pretends to be the latest request (already sorted by requestID, returning only 1)
        exec: () => new Promise((resolve, reject) => resolve(null))
      };
      findOneStub = sinon.stub(Request, 'findOne').returns(mockFindOne);
      ums.validateDeleteRequest({
        params: {userID: 1, requestID: 1}
      }).then()
      .catch(err => {
        if (err == 'invalidRequestID') done();
      })
    });

    it('should reject if request is not sent by the user', done => {
      var mockFindOne = { // pretends to be the latest request (already sorted by requestID, returning only 1)
        exec: () => new Promise((resolve, reject) => resolve({
          requestID: 1,
          from: 79
        }))
      };
      findOneStub = sinon.stub(Request, 'findOne').returns(mockFindOne);
      ums.validateDeleteRequest({
        params: {userID: 1, requestID: 1}
      }).then()
      .catch(err => {
        if (err == 'invalidRequestID') done();
      })
    });

    it('should resolve otherwise', done => {
      var mockFindOne = { // pretends to be the latest request (already sorted by requestID, returning only 1)
        exec: () => new Promise((resolve, reject) => resolve({
          requestID: 1,
          from: 1
        }))
      };
      findOneStub = sinon.stub(Request, 'findOne').returns(mockFindOne);
      ums.validateDeleteRequest({
        params: {userID: 1, requestID: 1}
      }).then(() => done());
    });
  });


});
