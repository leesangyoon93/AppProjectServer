/**
 * Created by daddyslab on 2016. 11. 17..
 */
'use strict';

var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var CategorySchema = new Schema({
    patient: {type: Schema.Types.ObjectId, ref: 'Patient', unique: false},
    date: String,
    meal: {
        type: String,
        default: ""
    },
    clean: {
        type: String,
        default: ""
    },
    activity: {
        type: String,
        default: ""
    },
    moveTrain: {
        type: String,
        default: ""
    },
    comment: {
        type: String,
        default: ""
    },
    restRoom: {
        type: String,
        default: ""
    },
    medicine: {
        type: String,
        default: ""
    },
    mentalTrain: {
        type: String,
        default: ""
    },
    physicalCare: {
        type: String,
        default: ""
    }
    // custom: {type: Schema.Types.ObjectId, ref: 'CustomCategory', null: true}
});

mongoose.model('Category', CategorySchema);