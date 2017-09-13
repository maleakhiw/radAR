const mongoose = require('mongoose')
const Schema = mongoose.Schema

const chatSchema = new Schema({
  chatID: Number,
  members: [Number],
  admins: [Number]
})

module.exports = mongoose.model('Chat', chatSchema)
