const mongoose = require('mongoose')
const Schema = mongoose.Schema

const chatSchema = new Schema({
  name: String,
  chatID: Number,
  members: [Number],
  admins: [Number]
})

module.exports = mongoose.model('Chat', chatSchema)
