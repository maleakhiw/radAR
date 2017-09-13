const mongoose = require('mongoose')
const Schema = mongoose.Schema

const lastChatIDSchema = new Schema({
  chatID: Number
})

module.exports = mongoose.model('LastChatID', lastChatIDSchema)
