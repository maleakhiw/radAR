const mongoose = require('mongoose')
const Schema = mongoose.Schema

const lastRequestIDSchema = new Schema({
  requestID: Number
})

module.exports = mongoose.model('LastRequestID', lastRequestIDSchema)
