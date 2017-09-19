const mongoose = require('mongoose')
const Schema = mongoose.Schema

const messageSchema = new Schema({
  from: Number,
  chatID: Number,
  time: Date,
  contentType: String,  // message, file
    // TODO: images can be compressed (later)
  text: String,
  contentResourceID: String
})

module.exports = mongoose.model('Message', messageSchema)
