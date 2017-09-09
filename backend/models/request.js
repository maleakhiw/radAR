const mongoose = require('mongoose')
const Schema = mongoose.Schema

const requestSchema = new Schema({
  requestID: Number,
  from: Number,
  to: Number,
  for: String,  // "friend" or "tracking"
  responded: Boolean
})

module.exports = mongoose.model('Request', requestSchema)
