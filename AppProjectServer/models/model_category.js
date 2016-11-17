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
    mealEnabled: {
        type: Boolean,
        default: true
    },
    clean: {
        type: String,
        default: ""
    },
    cleanEnabled: {
        type: Boolean,
        default: true
    },
    activity: {
        type: String,
        default: ""
    },
    activityEnabled: {
        type: Boolean,
        default: true
    },
    moveTrain: {
        type: String,
        default: ""
    },
    moveTrainEnabled: {
        type: Boolean,
        default: true
    },
    comment: {
        type: String,
        default: ""
    },
    commentEnabled: {
        type: Boolean,
        default: true
    },
    restRoom: {
        type: String,
        default: ""
    },
    restRoomEnabled: {
        type: Boolean,
        default: false
    },
    medicine: {
        type: String,
        default: ""
    },
    medicineEnabled: {
        type: Boolean,
        default: false
    },
    mentalTrain: {
        type: String,
        default: ""
    },
    mentalTrainEnabled: {
        type: Boolean,
        default: false
    },
    physicalCare: {
        type: String,
        default: ""
    },
    physicalCareEnabled: {
        type: Boolean,
        default: false
    },
    // custom: {type: Schema.Types.ObjectId, ref: 'CustomCategory', null: true}
});

mongoose.model('Category', CategorySchema);