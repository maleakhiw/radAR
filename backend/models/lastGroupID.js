const mongoose = require('mongoose')
const Schema = mongoose.Schema

const lastGroupIDSchema = new Schema({
  groupID: Number
})

module.exports = mongoose.model('LastGroupID', lastGroupIDSchema)
