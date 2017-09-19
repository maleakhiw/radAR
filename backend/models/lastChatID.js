const mongoose = require('mongoose')
const Schema = mongoose.Schema
const uniqueValidator = require('mongoose-unique-validator')

const lastChatIDSchema = new Schema({
  chatID: {type: Number, unique: true}
})
lastChatIDSchema.plugin(uniqueValidator);

module.exports = mongoose.model('LastChatID', lastChatIDSchema)
