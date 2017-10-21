// chai - assertions framework
const chai = require('chai');
const should = chai.should();
const expect = chai.expect;
const assert = chai.assert;

const GroupsHelper = require('../helpers/groupsHelper');
let groupsHelper;

const Group = require('../models/group');
const Message = require('../models/message');
const User = require('../models/user');

let sampleGroup = {
  "_id": "59ea95651dbaca4cfa290498",  // should not be returned
  "name": "malwijay",
  "groupID": 9,
  "meetingPoint": null,
  "isTrackingGroup": false,
  "footprints": [],
  "admins": [
    1
  ],
  "members": [
    1,
    5
  ],
  "createdOn": "2017-10-21T00:31:33.990Z",
  "__v": 0  // should not be returned
};

// sinon - mocking
const sinon = require('sinon');

describe('groupsHelper unit tests', () => {

  describe('formatGroupInfo', () => {
    groupsHelper = new GroupsHelper(Group, Message, User);

    it('should return nothing if no group is passed', done => {
      res = groupsHelper.formatGroupInfo(null);
      expect(res).to.equal(null);
      done();
    })

    it('should return public group info given a Group JSON object', done => {
      res = groupsHelper.formatGroupInfo(sampleGroup);
      expect(res._id).to.equal(undefined);
      expect(res.__v).to.equal(undefined);
      expect(res.name).to.equal("malwijay");
      expect(res.groupID).to.equal(9);
      expect(res.isTrackingGroup).to.equal(false);
      done();
    })
  })

})
