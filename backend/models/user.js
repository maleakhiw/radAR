const mongoose = require('mongoose')
const Schema = mongoose.Schema

const userSchema = new Schema({
  userID: Number,
  firstName: String,
  lastName: String,
  email: String,
  profilePicture: String,
  profileDesc: String,
  friends: [Number],
  groups: [Number],
  chats: [Number],
  signUpDate: Date
})

module.exports = mongoose.model('User', userSchema)
