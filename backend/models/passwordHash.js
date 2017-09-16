const mongoose = require('mongoose')
const Schema = mongoose.Schema

const passwordHash = new Schema({
  userID: Number,
  hash: String
})

module.exports = mongoose.model('PasswordHash', passwordHash)
