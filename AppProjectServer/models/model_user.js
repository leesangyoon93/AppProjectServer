/**
 * Created by daddyslab on 2016. 10. 11..
 */
'use strict';

var mongoose = require('mongoose');
var Schema = mongoose.Schema;
var bcrypt = require('bcrypt-nodejs');

var UserSchema = new Schema({
    nursingHome: {type: Schema.Types.ObjectId, ref: 'NursingHome'},
    userId: {
        type: String,
        unique: true
    },
    userName: {
        type: String,
        unique: false
    },
    gender: {
        type: String,
        default: "male"
    },
    password: {
        type: String
    },
    phoneNumber: {
        type: String
    },
    auth: {
        type: Number,
        default: 0
    },
    role: String,
    created: {
        type: Date,
        default: Date.now()
    }
});

UserSchema.pre('save', function (next) {
    var user = this;

    if(!user.isModified('password'))
        return next();
    else {
        user.password = bcrypt.hashSync(user.password);
        return next();
    }
});

mongoose.model('User', UserSchema);