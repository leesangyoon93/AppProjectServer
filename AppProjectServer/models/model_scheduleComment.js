/**
 * Created by daddyslab on 2016. 11. 14..
 */
'use strict';

var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var ScheduleCommentSchema = new Schema({
    schedule: {type: Schema.Types.ObjectId, ref: 'Schedule', unique: false},
    author: {
        type: String,
        unique: false
    },
    content: {
        type: String,
        unique: false
    },
    date: String
});

mongoose.model('ScheduleComment', ScheduleCommentSchema);