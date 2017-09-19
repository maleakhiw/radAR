const mongoose = require('mongoose')
const Schema = mongoose.Schema
const uniqueValidator = require('mongoose-unique-validator')

const lastUserIDSchema = new Schema({
  userID: {type: Number, unique: true}
})
lastUserIDSchema.plugin(uniqueValidator);

module.exports = mongoose.model('LastUserID', lastUserIDSchema)
