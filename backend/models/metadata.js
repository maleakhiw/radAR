const mongoose = require('mongoose')
const Schema = mongoose.Schema

const metadataSchema = new Schema({
  userID: Number,
  username: String,
  lastSeen: Date,
  deviceIDs: [String],
  activeTokens: [String]
})

module.exports = mongoose.model('Metadata', metadataSchema)
