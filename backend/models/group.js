const mongoose = require('mongoose')
const Schema = mongoose.Schema
const uniqueValidator = require('mongoose-unique-validator')

const meetingPointSchema = new Schema({
  lat: Number,
  lon: Number,
  name: String,
  description: String,
  timeAdded: Date
})
// MeetingPoint = mongoose.model('MeetingPoint', meetingPointSchema)

const footprintSchema = new Schema({
  from: Number,
  description: String,
  resourceID: Number,
  timeAdded: Date
})

const groupSchema = new Schema({
  name: String,
  groupID: {type: Number, unique: true},
  members: [Number],
  admins: [Number],
  footprints: [footprintSchema],
  meetingPoint: meetingPointSchema,
  isTrackingGroup: Boolean  // true if the Group is a Tracking Group, false if the Group is just a Chat
})

groupSchema.plugin(uniqueValidator);

module.exports = mongoose.model('Group', groupSchema)
