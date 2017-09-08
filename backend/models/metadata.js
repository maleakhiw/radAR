const mongoose = require('mongoose')
const Schema = mongoose.Schema

const metadataSchema = new Schema({
  userID: Number,
  lastSeen: Date,
  deviceIDs: [String],
  activeTokens: [String]
})

module.exports = mongoose.model('Metadata', metadataSchema)
