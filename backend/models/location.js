const mongoose = require('mongoose');
const Schema = mongoose.Schema;
const uniqueValidator = require('mongoose-unique-validator');

const locationSchema = new Schema({
  lat: Number,
  lon: Number,
  heading: Number, // in degrees
  accuracy: Number, // in metres
  timeUpdated: {type: Date, default: Date.now}
});

module.exports = mongoose.model('Location', locationSchema);
