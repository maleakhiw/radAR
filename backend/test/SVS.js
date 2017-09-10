let mongoose = require('mongoose')
let User = require('../models/user')
let Metadata = require('../models/metadata')

// dev dependencies
let chai = require('chai')
let chaiHttp = require('chai-http')
// let server = require('../server')
let serverURL = 'http://35.185.35.117/'
let should = chai.should()
let expect = chai.expect

chai.use(chaiHttp)

// parent block
describe('User', () => {

  beforeEach((done) => {
    User.remove({}).exec()
    .then(done())
    .catch((err) => {
      console.log(err)
    })
  })

  describe('/POST SVS/signUp', () => {
    it('it should create a new user', (done) => {
      chai.request(serverURL + 'SVS')
        .post('/signUp')
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
          console.log(res)
          done()
        })
    })
  })

})
