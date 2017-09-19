const mongoose = require('mongoose')
const Schema = mongoose.Schema
const uniqueValidator = require('mongoose-unique-validator')

const passwordHash = new Schema({
  userID: {type: Number, unique: true},
  hash: String
})
passwordHash.plugin(uniqueValidator);

module.exports = mongoose.model('PasswordHash', passwordHash)
