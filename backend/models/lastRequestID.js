const mongoose = require('mongoose')
const Schema = mongoose.Schema
const uniqueValidator = require('mongoose-unique-validator')

const lastRequestIDSchema = new Schema({
  requestID: {type: Number, unique: true}
})
lastRequestIDSchema.plugin(uniqueValidator);

module.exports = mongoose.model('LastRequestID', lastRequestIDSchema)
