/**
 * Created by daddyslab on 2016. 11. 19..
 */
'use strict';

var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var CustomCategorySchema = new Schema({
    enable: {
        type: Boolean,
        default: true
    },
    title: {
        type:String,
        default: ""
    },
    content: {
        type: String,
        default: ""
    }
});

mongoose.model('CustomCategory', CustomCategorySchema);