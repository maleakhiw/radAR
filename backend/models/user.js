const mongoose = require('mongoose')
const Schema = mongoose.Schema
const uniqueValidator = require('mongoose-unique-validation')

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
  chats: [Number],
  signUpDate: Date
})
userSchema.plugin(uniqueValidator);

module.exports = mongoose.model('User', userSchema)
