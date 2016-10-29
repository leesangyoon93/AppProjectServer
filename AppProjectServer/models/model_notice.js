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
    modified: {
        type: String,
        null: true,
        default: new Date().toISOString().slice(0, 10)
    },
    content: {
        type: String
    },
    commentCount: {
        type: Number,
        default: 0
    }
});

mongoose.model('Notice', NoticeSchema);