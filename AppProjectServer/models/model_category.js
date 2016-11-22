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
    mealTitle: "오늘의 식사",
    mealEnabled: {
        type: Boolean,
        default: true
    },
    clean: {
        type: String,
        default: ""
    },
    cleanTitle: "신체위생관리",
    cleanEnabled: {
        type: Boolean,
        default: true
    },
    activity: {
        type: String,
        default: ""
    },
    activityTitle: "산책/외출",
    activityEnabled: {
        type: Boolean,
        default: true
    },
    moveTrain: {
        type: String,
        default: ""
    },
    moveTrainTitle: "생활동작훈련",
    moveTrainEnabled: {
        type: Boolean,
        default: true
    },
    comment: {
        type: String,
        default: ""
    },
    commentTitle: "특이사항",
    commentEnabled: {
        type: Boolean,
        default: true
    },
    restRoom: {
        type: String,
        default: ""
    },
    restRoomTitle: "배변횟수",
    restRoomEnabled: {
        type: Boolean,
        default: false
    },
    medicine: {
        type: String,
        default: ""
    },
    medicineTitle: "투약관리",
    medicineEnabled: {
        type: Boolean,
        default: false
    },
    mentalTrain: {
        type: String,
        default: ""
    },
    mentalTrainTitle: "정신기능훈련",
    mentalTrainEnabled: {
        type: Boolean,
        default: false
    },
    physicalCare: {
        type: String,
        default: ""
    },
    physicalCareTitle: "물리치료",
    physicalCareEnabled: {
        type: Boolean,
        default: false
    },
    custom: [{type: Schema.Types.ObjectId, ref: 'CustomCategory', null: true}]
});

mongoose.model('Category', CategorySchema);