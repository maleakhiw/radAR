const mongoose = require('mongoose')
const Schema = mongoose.Schema
const uniqueValidator = require('mongoose-unique-validator')

const requestSchema = new Schema({
  requestID: Number,
  from: Number,
  to: Number,
  for: String,  // "friend" or "tracking"
  responded: Boolean
})
requestSchema.plugin(uniqueValidator);

module.exports = mongoose.model('Request', requestSchema)
