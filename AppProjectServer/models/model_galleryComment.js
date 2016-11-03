/**
 * Created by daddyslab on 2016. 11. 1..
 */
'use strict';

var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var GalleryCommentSchema = new Schema({
    gallery: {type: Schema.Types.ObjectId, ref: 'Gallery', unique: false},
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

mongoose.model('GalleryComment', GalleryCommentSchema);