/**
 * Created by daddyslab on 2016. 10. 11..
 */
'use strict';

var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var PatientSchema = new Schema({
    protector: { type: Schema.Types.ObjectId, ref: 'User'},
    worker: { type: Schema.Types.ObjectId, ref: 'User'},
    patientName: {
        type: String,
        unique: false
    },
    birthday: String,
    relation: String,
    roomNumber: String,
    created: {
        type: Date,
        default: Date.now()
    }
});

mongoose.model('Patient', PatientSchema);