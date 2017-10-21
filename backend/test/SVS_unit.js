// chai - assertions framework
const chai = require('chai');
const should = chai.should();
const expect = chai.expect;
const assert = chai.assert;

const SVS = require('../controllers/SVS');
var svs;

const User = require('../models/user');

// sinon - mocking
const sinon = require('sinon');

describe('SVS unit tests', () => {

  before(() => {
    svs = new SVS(User);
  })

  describe('generateToken', () => {
    it('should generate a token', (done) => {
      let token = svs.generateToken(1);
      token.should.not.equal(null);
      done();
    });
  });

  describe('hashSaltPassword', () => {
    it('a hash and salt should be generated', (done) => {
      let hash = svs.hashSaltPassword('hunter2');
      hash.should.not.equal(null);
      done();
    });

    it('should produce differnt outputs even if the input is the same', (done) => {
      let hash = svs.hashSaltPassword('hunter2');
      let hash2 = svs.hashSaltPassword('hunter2');
      hash.should.not.equal(hash2);
      done();
    });
  });

  describe('validatePassword', () => {
    it('should be able to validate passwords', (done) => {
      let hash = svs.hashSaltPassword('hunter2');
      let hash2 = svs.hashSaltPassword('hunter2');
      svs.validatePassword('hunter2', hash).should.equal(true);
      svs.validatePassword('hunter2', hash2).should.equal(true);
      svs.validatePassword('hunter3', hash).should.equal(false);
      svs.validatePassword('hunter3', hash2).should.equal(false);
      done();
    });
  });

  describe('isValidUser', () => {
    let userFindOneStub;

    afterEach(() => userFindOneStub.restore()); // restore original behaviour

    it('should reject an invalidUserID', (done) => {
      var mockFindOne = {
        where: () => this,
        equals: () => this,
        exec: () => new Promise((resolve, reject) => resolve(null))
      };
      userFindOneStub = sinon.stub(User, 'findOne').returns(mockFindOne);

      svs.isValidUser(1)
      .then((user) => {
        console.log('Should not go here');
        assert.equal(1, 0);
      })
      .catch((err) => {
        err.should.equal('invalidUserID');
        done();
      })
    });

    it('should resolve() on a valid user', (done) => {
      var mockFindOne = {
        where: () => this,
        equals: () => this,
        exec: () => new Promise((resolve, reject) => resolve({
          // stub of a user. Not a complete user object
          userID: 1
        }))
      };
      userFindOneStub = sinon.stub(User, 'findOne').returns(mockFindOne);

      svs.isValidUser(1)
      .then((user) => {
        done();
      })
      .catch((err) => {
        console.log('Should not go here');
        assert.equal(1, 0);
      })
    });

  });

})
