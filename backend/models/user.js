const mongoose = require('mongoose')
const Schema = mongoose.Schema
const uniqueValidator = require('mongoose-unique-validator')

const userSchema = new Schema({
  userID: {type: Number, unique: true},
  username: String,
  firstName: String,
  lastName: String,
  email: {type: String, unique: true},
  profilePicture: String,
  profileDesc: String,
  friends: [Number],
  groups: [Number],
  groups: [Number],
  timeSent: {type: Date, default: Date.now},
  lastSeen: Date,
  deviceIDs: [String],
  activeTokens: [String],
  passwordHash: String,
})
userSchema.plugin(uniqueValidator);

module.exports = mongoose.model('User', userSchema)
