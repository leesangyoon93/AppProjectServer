/**
 * Created by daddyslab on 2016. 10. 29..
 */
'use strict';

var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var ScheduleSchema = new Schema({
    nursingHome: {type: Schema.Types.ObjectId, ref: 'NursingHome', unique: false},
    title: {
        type: String,
        unique: false
    },
    author: {
        type: String,
        unique: false
    },
    created: String,
    modified: String,
    content: {
        type: String
    }
});

mongoose.model('Schedule', ScheduleSchema);