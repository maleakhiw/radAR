const mongoose = require('mongoose')
const Schema = mongoose.Schema

const lastUserIDSchema = new Schema({
  userID: Number
})

module.exports = mongoose.model('LastUserID', lastUserIDSchema)
