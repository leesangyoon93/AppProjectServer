/**
 * Created by daddyslab on 2016. 10. 29..
 */
'use strict';

var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var NoticeCommentSchema = new Schema({
    notice: {type: Schema.Types.ObjectId, ref: 'Notice', unique: false},
    author: {
        type: String,
        unique: false
    },
    content: {
        type: String,
        unique: false
    },
    created: String
});

mongoose.model('NoticeComment', NoticeCommentSchema);