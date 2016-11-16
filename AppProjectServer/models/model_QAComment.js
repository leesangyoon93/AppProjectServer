/**
 * Created by daddyslab on 2016. 11. 16..
 */
'use strict';

var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var QACommentSchema = new Schema({
    QA: {type: Schema.Types.ObjectId, ref: 'QA', unique: false},
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

mongoose.model('QAComment', QACommentSchema);