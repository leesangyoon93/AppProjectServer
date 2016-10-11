/**
 * Created by daddyslab on 2016. 10. 11..
 */
'use strict';

var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var NursingHomeSchema = new Schema({
    host: {
        type: {type: Schema.Types.ObjectId, ref: 'User'}
    },
    homeName: {
        type: String,
        unique: true
    },
    address: {
        type: String
    },
    phoneNumber: {
        type: String
    },
    created: {
        type: Date,
        default: Date.now()
    }
});


mongoose.model('NursingHome', NursingHomeSchema);