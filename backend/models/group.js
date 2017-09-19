const mongoose = require('mongoose')
const Schema = mongoose.Schema

// TODO: move MeetingPoint and Footprint to their own files
const meetingPointSchema = new Schema({
  lat: Number,
  lon: Number,
  description: String
})
// MeetingPoint = mongoose.model('MeetingPoint', meetingPointSchema)

const footprintSchema = new Schema({
  from: Number,
  description: String,
  resourceID: Number
})
// Footprint = mongoose.model('Footprint', footprintSchema)

const groupSchema = new Schema({
  groupID: Number,
  name: String,
  members: [Number],
  admins: [Number],
  footprints: [footprintSchema],
  meetingPoint: [meetingPointSchema]
})

module.exports = mongoose.model('Group', groupSchema)
