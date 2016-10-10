var express = require('express');
var router = express.Router();
var mongoose = require('mongoose');
var bcrypt = require("bcrypt-nodejs");
//var User = mongoose.model('User');

/* GET home page. */
module.exports = function () {
  router.get('/', function (req, res) {
    res.render('index');
  });

  return router;
};
