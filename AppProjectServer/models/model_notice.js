/**
 * Created by daddyslab on 2016. 10. 29..
 */
'use strict';

var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var NoticeSchema = new Schema({
    nursingHome: {type: Schema.Types.ObjectId, ref: 'NursingHome', unique: false},
    title: {
        type: String,
        unique: false
    },
    author: {
        type: String,
        unique: false
    },
    date: String,
    modified: String,
    content: {
        type: String
    },
    commentCount: {
        type: Number,
        default: 0
    }
});

mongoose.model('Notice', NoticeSchema);