let mongoose = require('mongoose')

const User = require('../models/user')
const Metadata = require('../models/metadata')
const LastUserID = require('../models/lastUserID')

// dev dependencies
let chai = require('chai')
let chaiHttp = require('chai-http')
let server = require('../server')
let should = chai.should()
let expect = chai.expect

chai.use(chaiHttp)

mongoose.Promise = global.Promise

// parent block
describe('User', () => {

  before((done) => {  // NOTE: you can also return a Promise instead of
                          // using the done object
    mongoose.connect('mongodb://localhost/radarTest',
      { useMongoClient: true },
      (err) => { // TODO: see if this breaks
        if (!err) console.log('Connected to mongoDB')
        else console.log('Failed to connect to mongoDB')
    })

    User.remove({}).exec()
    .then(() => {
      console.log('Users cleared')
      return Metadata.remove({})
    })
    .then(() => {
      console.log('Metadatas cleared')
      return LastUserID.remove({})
    })
    .then(() => {
      console.log('LastUserIDs cleared')
      done()
    })
    .catch((err) => {
      console.log('err', err)
      done()
    })
  })

  // beforeEach((done) => {done()})

  describe('/POST SVS/signUp', () => {
    it('it should create a new user', (done) => {
      chai.request(server)
        .post('/SVS/signUp')
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
          console.log(res.body)
          expect(res.body.success).to.equal(true)
          // User.find({username: "manshar"}).exec()
          //   .then((users) => {
          //     users.map((user) => {
          //       console.log(user)
          //     }
          //   })

          done()
        })
    })
  })

})
