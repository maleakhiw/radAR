const mongoose = require('mongoose')
const Schema = mongoose.Schema
const uniqueValidator = require('mongoose-unique-validator')

// friend request or group invite request
const requestSchema = new Schema({
  requestID: {type: Number, unique: true},
  from: Number,
  to: Number,
  for: String,  // "friend" or "tracking"
  responded: Boolean,
  timeSent: {type: Date, default: Date.now}
})
requestSchema.plugin(uniqueValidator);

module.exports = mongoose.model('Request', requestSchema)
