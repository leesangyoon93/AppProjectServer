var express = require('express');
var router = express.Router();
var mongoose = require('mongoose');
var bcrypt = require("bcrypt-nodejs");
var User = mongoose.model('User');
var NursingHome = mongoose.model('NursingHome');
var Patient = mongoose.model('Patient');
var ObjectId = require('mongodb').ObjectId;

/* GET home page. */
router.get('/', function (req, res, next) {
    res.render('index', {title: 'Express'});
});

router.post('/login', function (req, res) {
    User.findOne({'userId': req.body.userId}, function (err, user) {
        if (err) return res.json({'result': 'fail'});
        if (user) {
            if (bcrypt.compareSync(req.body.password, user.password))
                return res.json(user);
            else return res.json({'result': 'failPw'});
        }
        else return res.json({'result': 'failId'});
    })
});

router.post('/changePassword', function (req, res) {
    User.findOne({'userId': req.body.userId}, function (err, user) {
        if (err) return res.json({'result': 'fail'});
        if (user) {
            if (bcrypt.compareSync(req.body.currentPassword, user.password)) {
                user.password = req.body.newPassword1;
                user.save();
                return res.json({'result': 'success'});
            }
            else return res.json({'result': 'failPw'});
        }
        else return res.json({'resut': 'fail'});
    })
});

router.post('/getUser', function (req, res) {
    User.findOne({'userId': req.body.userId}, function (err, user) {
        if (err) return res.json({'result': 'fail'});
        if (user) return res.json(user);
        else return res.json({'result': 'fail'});
    })
});

router.post('/getNursingHome', function (req, res) {
    var id = new ObjectId(req.body.nursingHomeId);
    NursingHome.findById(id, function (err, nursingHome) {
        if (err) return res.json({'result': 'fail'});
        if (nursingHome) return res.json(nursingHome);
        else return res.json({'result': 'fail'});
    })
});

router.post('/createNursingHome', function (req, res) {
    NursingHome.findOne({'homeName': req.body.homeName}, function (err, nursingHome) {
        if (err) return res.json({'result': 'fail'});
        if (nursingHome) return res.json({'result': 'overlap'});
        else {
            var admin = new User();
            admin.userId = req.body.adminId;
            admin.password = req.body.adminPassword;
            admin.userName = req.body.adminName;
            admin.phoneNumber = req.body.adminPhoneNumber;
            admin.role = "관리자";
            admin.auth = 0;
            var newNursingHome = new NursingHome();
            newNursingHome.homeName = req.body.homeName;
            newNursingHome.address = req.body.address;
            newNursingHome.phoneNumber = req.body.nursingHomePhoneNumber;

            admin.nursingHome = newNursingHome;
            newNursingHome.host = admin;

            admin.save();
            newNursingHome.save();

            return res.json({'result': 'success'});
        }
    })
});

router.get('/getNursingHomeWorkers', function (req, res) {
    var id = new ObjectId(req.query.nursingHomeId);
    User.find({'nursingHome': id, 'auth': 1}, function (err, users) {
        if (err) return res.json({'result': 'fail'});
        if (users) return res.json(users);
        else return res.json({'result': 'fail'});
    })
});

router.post('/createWorker', function (req, res) {
    var id = new ObjectId(req.body.nursingHomeId);
    NursingHome.findById(id, function (err, nursingHome) {
        if (err) return res.json({'result': 'fail'});
        if (nursingHome) {
            User.findOne({'userId': req.body.workerId}, function (err, user) {
                if (err) return res.json({'result': 'fail'});
                if (user) return res.json({'result': 'overlap'});
                else {
                    var newWorker = new User();
                    newWorker.nursingHome = nursingHome;
                    newWorker.userId = req.body.workerId;
                    newWorker.userName = req.body.workerName;
                    newWorker.password = req.body.workerPassword;
                    newWorker.phoneNumber = req.body.workerPhoneNumber;
                    newWorker.auth = 1;
                    newWorker.role = "요양사";
                    newWorker.save();
                    return res.json({'result': 'success'});
                }
            })
        }
        else return res.json({'result': 'fail'});
    })
});

router.post('/deleteUser', function (req, res) {
    User.findOne({'userId': req.body.userId}, function (err, user) {
        if (err) return res.json({'result': 'fail'});
        if (user) {
            var id = new ObjectId(user._id);
            if (user.auth == 1) {
                Patient.find({'worker': id}, function (err, patients) {
                    if (err) return res.json({'result': 'fail'});
                    if (patients) {
                        for (var i in patients)
                            patients[i].worker = "";
                    }
                    user.remove();
                    return res.json({'result': 'success'});
                })
            }
            else if(user.auth == 2) {
                Patient.findOne({'protector': id}, function(err, patient) {
                    if(err) return res.json({'result': 'fail'});
                    if(patient)
                        patient.remove();
                    user.remove();
                    return res.json({'result': 'success'});
                })
            }
        }
        else return res.json({'result': 'fail'})
    })
});

router.post('/createPatient', function (req, res) {
    var id = new ObjectId(req.body.nursingHomeId);
    NursingHome.findById(id, function (err, nursingHome) {
        if (err) return res.json({'result': 'fail'});
        if (nursingHome) {
            User.findOne({'userId': protectorId}, function (err, user) {
                if (err) return res.json({'result': 'fail'});
                if (user)
                    return res.json({'result': 'overlap'});
                else {
                    User.findOne({'userId': workerId}, function (err, worker) {
                        if (err) return res.json({'result': 'fail'});
                        if (worker) {
                            var protector = new User();
                            protector.nursingHome = nursingHome;
                            protector.userId = req.body.protectorId;
                            protector.userName = req.body.userName;
                            protector.password = req.body.password;
                            protector.phoneNumber = req.body.phoneNumber;
                            protector.auth = 2;
                            protector.role = "보호자";

                            var patient = new Patient();
                            patient.protector = protector;
                            patient.worker = worker;
                            patient.patientName = req.body.patientName;
                            patient.birthday = req.body.birthday;
                            patient.relation = req.body.relation;
                            patient.roomNumber = req.body.roomNumber;

                            protector.save();
                            patient.save();
                            return res.json({'result': 'success'});
                        }
                        else return res.json({'result': 'fail'})
                    })
                }
            })
        }
        else return res.json({'result': 'fail'});
    })
});

module.exports = router;
