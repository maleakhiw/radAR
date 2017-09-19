const mongoose = require('mongoose')
const Schema = mongoose.Schema
const uniqueValidator = require('mongoose-unique-validator')

const metadataSchema = new Schema({
  userID: {type: Number, unique: true},
  username: String,
  lastSeen: Date,
  deviceIDs: [String],
  activeTokens: [String]
})
metadataSchema.plugin(uniqueValidator);

module.exports = mongoose.model('Metadata', metadataSchema)
