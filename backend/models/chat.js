const mongoose = require('mongoose')
const Schema = mongoose.Schema
const uniqueValidator = require('mongoose-unique-validator')

const chatSchema = new Schema({
  name: String,
  chatID: {type: Number, unique: true},
  members: [Number],
  admins: [Number]
})
chatSchema.plugin(uniqueValidator);

module.exports = mongoose.model('Chat', chatSchema)
