const mongoose = require('mongoose')
const Schema = mongoose.Schema

const resourceSchema = new Schema({
  fileID: String,
  filename: String,
  mimetype: String,
  owners: [Number],
  chatID: Number,
  groupID: Number
})

module.exports = mongoose.model('Resource', resourceSchema)
