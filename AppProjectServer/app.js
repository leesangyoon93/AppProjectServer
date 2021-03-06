var express = require('express');
var path = require('path');
var favicon = require('serve-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');
var mongoose = require('mongoose');
var bcrypt = require('bcrypt-nodejs');

var app = express();

mongoose.Promise = global.Promise;
mongoose.connect('mongodb://localhost/appProject', function(err) {
  if(err) {
    console.log("DB ERROR :", err);
    throw err;
  }
  else
    console.log("DB connected!");
});

require('./models/model_user');
require('./models/model_nursingHome');
require('./models/model_patient');
require('./models/model_notice');
require('./models/model_gallery');
require('./models/model_schedule');
require('./models/model_qa');
require('./models/model_noticeComment');
require('./models/model_galleryComment');
require('./models/model_scheduleComment');
require('./models/model_QAComment');
require('./models/model_category');
require('./models/model_customCategory');
var User = mongoose.model('User');

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');

// uncomment after placing your favicon in /public
//app.use(favicon(path.join(__dirname, 'public', 'favicon.ico')));
app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

var routes = require('./routes/index');
app.use('/', routes);

// catch 404 and forward to error handler
app.use(function(req, res, next) {
  var err = new Error('Not Found');
  err.status = 404;
  next(err);
});

// error handlers

// development error handler
// will print stacktrace
if (app.get('env') === 'development') {
  app.use(function(err, req, res, next) {
    res.status(err.status || 500);
    res.render('error', {
      message: err.message,
      error: err
    });
  });
}

// production error handler
// no stacktraces leaked to user
app.use(function(err, req, res, next) {
  res.status(err.status || 500);
  res.render('error', {
    message: err.message,
    error: {}
  });
});


module.exports = app;
