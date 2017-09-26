const mongoose = require('mongoose')
const Schema = mongoose.Schema

const resourceSchema = new Schema({
  fileID: {type: String, unique: true},
  filename: String,
  mimetype: String,
  owners: [Number],
  chatID: Number,
  groupID: Number
})

module.exports = mongoose.model('Resource', resourceSchema)
