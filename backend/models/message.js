const mongoose = require('mongoose')
const Schema = mongoose.Schema
const uniqueValidator = require('mongoose-unique-validator')

const messageSchema = new Schema({
  from: Number,
  groupID: Number,
  time: Date,
  contentType: String,  // message, file
    // TODO: images can be compressed (later)
  text: String,
  contentResourceID: String
})
messageSchema.plugin(uniqueValidator);

module.exports = mongoose.model('Message', messageSchema)
