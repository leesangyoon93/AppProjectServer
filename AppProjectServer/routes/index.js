var express = require('express');
var router = express.Router();
var mongoose = require('mongoose');
var bcrypt = require("bcrypt-nodejs");
var User = mongoose.model('User');
var NursingHome = mongoose.model('NursingHome');
var Patient = mongoose.model('Patient');
var Notice = mongoose.model('Notice');
var Schedule = mongoose.model('Schedule');
var QA = mongoose.model('QA');
var Gallery = mongoose.model('Gallery');
var NoticeComment = mongoose.model('NoticeComment');
var GalleryComment = mongoose.model('GalleryComment');
var ScheduleComment = mongoose.model('ScheduleComment');
var QAComment = mongoose.model('QAComment');
var Category = mongoose.model('Category');
var CustomCategory = mongoose.model('CustomCategory');
var gcm = require('node-gcm');
var fs = require('fs');
var ObjectId = require('mongodb').ObjectId;

/* GET home page. */
router.get('/', function (req, res) {
    res.render('index', {title: 'AppProjectServer'});
});

router.post('/sendMessage', function(req, res) {
    var message = new gcm.Message();

    var message = new gcm.Message({
        collapseKey: 'demo',
        delayWhileIdle: true,
        timeToLive: 3,
        data: {
            title: '데일리실버 수급자 정보 업데이트',
            message: '오늘의 열람정보를 확인하세요!'
        }
    });

    var server_api_key = 'AIzaSyC80etKP8vJgmAJSIiUOfjUUU7opH7zR-M';
    var sender = new gcm.Sender(server_api_key);
    var registrationIds = [];

    var token = req.body.token;
    registrationIds.push(token);

    sender.send(message, registrationIds, 4, function (err, result) {
        return res.json({'result': 'success'})
    });
})

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
        if (nursingHome) return res.json({'result': 'homeOverlap'});
        else {
            User.findOne({'userId': req.body.adminId}, function (err, user) {
                if (err) return res.json({'result': 'fail'});
                if (user) return res.json({'result': 'userOverlap'});
                else {
                    var admin = new User();
                    admin.userId = req.body.adminId;
                    admin.password = req.body.adminPassword;
                    admin.userName = req.body.adminName;
                    admin.phoneNumber = req.body.adminPhoneNumber;
                    admin.gender = req.body.adminGender;
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
                    newWorker.gender = req.body.workerGender;
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
            else if (user.auth == 2) {
                Patient.findOne({'protector': id}, function (err, patient) {
                    if (err) return res.json({'result': 'fail'});
                    if (patient)
                        patient.remove();
                    user.remove();
                    return res.json({'result': 'success'});
                })
            }
        }
        else return res.json({'result': 'fail'})
    })
});


// 게시판 API
// showArticle, editArticle, deleteArticle 통합 request에 path 파라미터로 구분

router.get('/getArticles', function (req, res) {
    var path = req.query.path;
    var id = req.query.nursingHomeId;
    switch (path) {
        case 'notice':
            Notice.find({'nursingHome': new Object(id)}, function (err, notices) {
                if (err) return res.json({'result': 'fail'});
                if (notices) {
                    notices.sort(function(a, b) {
                        if(a.modified < b.modified)
                            return 1;
                        else return -1;
                    });
                    return res.json(notices);
                }
                else return res.json({'result': 'fail'});
            });
            break;
        case 'gallery':
            Gallery.find({'nursingHome': new ObjectId(id)}, function (err, galleries) {
                if (err) return res.json({'result': 'fail'});
                if (galleries) {
                    galleries.sort(function(a, b) {
                        if(a.modified < b.modified)
                            return 1;
                        else return -1;
                    });
                    return res.json(galleries);
                }
                else return res.json({'result': 'fail'});
            });
            break;
        case 'schedule':
            Schedule.find({'nursingHome': new ObjectId(id)}, function (err, schedules) {
                if (err) return res.json({'result': 'fail'});
                if (schedules) {
                    schedules.sort(function(a, b) {
                        if(a.modified < b.modified)
                            return 1;
                        else return -1;
                    });
                    return res.json(schedules);
                }
                else return res.json({'result': 'fail'});
            });
            break;
        case 'qa':
            QA.find({'nursingHome': new ObjectId(id)}, function (err, qas) {
                if (err) return res.json({'result': 'fail'});
                if (qas) {
                    qas.sort(function(a, b) {
                        if(a.modified < b.modified)
                            return 1;
                        else return -1;
                    });
                    return res.json(qas);
                }
                else return res.json({'result': 'fail'});
            });
            break;
    }
});

router.get('/showArticle', function (req, res) {
    var path = req.query.path;
    var id = new ObjectId(req.query.articleId);
    switch (path) {
        case 'notice':
            Notice.findById(id, function (err, article) {
                if (err) return res.json({'result': 'fail'});
                if (article) return res.json(article);
                else return res.json({'result': 'fail'});
            });
            break;
        case 'gallery':
            Gallery.findById(id, function (err, gallery) {
                if (err) return res.json({'result': 'fail'});
                if (gallery) return res.json(gallery);
                else return res.json({'result': 'fail'});
            });
            break;
        case 'schedule':
            Schedule.findById(id, function (err, schedule) {
                if (err) return res.json({'result': 'fail'});
                if (schedule) return res.json(schedule);
                else return res.json({'result': 'fail'});
            });
            break;
        case 'qa':
            QA.findById(id, function (err, qa) {
                if (err) return res.json({'result': 'fail'});
                if (qa) return res.json(qa);
                else return res.json({'result': 'fail'});
            });
            break;
    }
});


router.get('/showComments', function (req, res) {
    var path = req.query.path;
    var id = req.query.articleId;
    switch (path) {
        case 'notice':
            NoticeComment.find({'notice': new ObjectId(id)}, function (err, comments) {
                if (err) return res.json({'result': 'fail'});
                if (comments)
                    return res.json(comments);
                else return res.json({'result': 'fail'});
            });
            break;
        case 'gallery':
            GalleryComment.find({'gallery': new ObjectId(id)}, function (err, comments) {
                if (err) return res.json({'result': 'fail'});
                if (comments)
                    return res.json(comments);
                else return res.json({'result': 'fail'});
            });
            break;
        case 'schedule':
            ScheduleComment.find({'schedule': new ObjectId(id)}, function (err, comments) {
                if (err) return res.json({'result': 'fail'});
                if (comments)
                    return res.json(comments);
                else return res.json({'result': 'fail'});
            });
            break;
        case 'qa':
            QAComment.find({'QA': new ObjectId(id)}, function (err, comments) {
                if (err) return res.json({'result': 'fail'});
                if (comments)
                    return res.json(comments);
                else return res.json({'result': 'fail'});
            });
            break;
    }

});

router.post('/saveArticle', function (req, res) {
    var path = req.body.path;
    var id = new ObjectId(req.body.articleId);
    switch (path) {
        case 'notice':
            Notice.findById(id, function (err, notice) {
                if (err) return res.json({'result': 'fail'});
                if (notice) {
                    notice.title = req.body.title;
                    notice.content = req.body.content;
                    var date = new Date().toISOString();
                    notice.modified = date;
                    notice.save();
                    return res.json({'result': 'success', 'articleId': notice._id});
                }
                else {
                    var newNotice = new Notice();
                    newNotice.title = req.body.title;
                    newNotice.content = req.body.content;
                    newNotice.author = req.body.userId;
                    var date = new Date().toISOString();
                    newNotice.date = date.slice(0, 10);
                    NursingHome.findById(req.body.nursingHomeId, function (err, nursingHome) {
                        if (err) return res.json({'result': 'fail'});
                        if (nursingHome) {
                            newNotice.nursingHome = nursingHome;
                            newNotice.save();
                            return res.json({'result': 'success', 'articleId': newNotice._id});
                        }
                        else
                            return res.json({'result': 'fail'});
                    });
                }
            });
            break;
        case 'gallery':
            Gallery.findById(id, function (err, gallery) {
                if (err) return res.json({'result': 'fail'});
                if (gallery) {
                    gallery.title = req.body.title;
                    gallery.content = req.body.content;
                    gallery.image = req.body.image;
                    var date = new Date().toISOString();
                    gallery.modified = date;
                    gallery.save();
                    return res.json({'result': 'success', 'galleryId': gallery._id});
                }
                else {
                    var newGallery = new Gallery();
                    newGallery.title = req.body.title;
                    newGallery.content = req.body.content;
                    newGallery.image = req.body.image;
                    newGallery.author = req.body.userId;
                    var date = new Date().toISOString();
                    newGallery.date = date.slice(0, 10);
                    NursingHome.findById(req.body.nursingHomeId, function (err, nursingHome) {
                        if (err) return res.json({'result': 'fail'});
                        if (nursingHome) {
                            newGallery.nursingHome = nursingHome;
                            newGallery.save();
                            return res.json({'result': 'success', 'galleryId': newGallery._id});
                        }
                        else return res.json({'result': 'fail'});
                    })
                }
            });
            break;
        case 'schedule':
            Schedule.findById(id, function (err, schedule) {
                if (err) return res.json({'result': 'fail'});
                if (schedule) {
                    schedule.title = req.body.title;
                    schedule.content = req.body.content;
                    var date = new Date().toISOString();
                    schedule.modified = date;
                    schedule.save();
                    return res.json({'result': 'success', 'articleId': schedule._id});
                }
                else {
                    var newSchedule = new Schedule();
                    newSchedule.title = req.body.title;
                    newSchedule.content = req.body.content;
                    newSchedule.author = req.body.userId;
                    var date = new Date().toISOString();
                    newSchedule.date = date.slice(0, 10);
                    NursingHome.findById(req.body.nursingHomeId, function (err, nursingHome) {
                        if (err) return res.json({'result': 'fail'});
                        if (nursingHome) {
                            newSchedule.nursingHome = nursingHome;
                            newSchedule.save();
                            return res.json({'result': 'success', 'articleId': newSchedule._id});
                        }
                        else
                            return res.json({'result': 'fail'});
                    });
                }
            });
            break;
        case 'qa':
            QA.findById(id, function (err, qa) {
                if (err) return res.json({'result': 'fail'});
                if (qa) {
                    qa.title = req.body.title;
                    qa.content = req.body.content;
                    var date = new Date().toISOString();
                    qa.modified = date;
                    qa.save();
                    return res.json({'result': 'success', 'articleId': qa._id});
                }
                else {
                    var newQA = new QA();
                    newQA.title = req.body.title;
                    newQA.content = req.body.content;
                    newQA.author = req.body.userId;
                    var date = new Date().toISOString();
                    newQA.date = date.slice(0, 10);
                    NursingHome.findById(req.body.nursingHomeId, function (err, nursingHome) {
                        if (err) return res.json({'result': 'fail'});
                        if (nursingHome) {
                            newQA.nursingHome = nursingHome;
                            newQA.save();
                            return res.json({'result': 'success', 'articleId': newQA._id});
                        }
                        else
                            return res.json({'result': 'fail'});
                    });
                }
            });
            break;
    }

});

router.post('/saveComment', function (req, res) {
    var path = req.body.path;
    switch (path) {
        case 'notice':
            Notice.findById(req.body.articleId, function (err, notice) {
                if (err) return res.json({'result': 'fail'});
                if (notice) {
                    var comment = new NoticeComment();
                    comment.notice = notice;
                    comment.author = req.body.userId;
                    comment.content = req.body.content;
                    var date = new Date().toISOString();
                    comment.date = date.slice(0, 10);
                    comment.save();
                    notice.commentCount += 1;
                    notice.save();
                    return res.json({'result': 'success'});
                }
                else return res.json({'result': 'fail'});
            });
            break;
        case 'gallery':
            Gallery.findById(req.body.articleId, function (err, gallery) {
                if (err) return res.json({'result': 'fail'});
                if (gallery) {
                    var comment = new GalleryComment();
                    comment.gallery = gallery;
                    comment.author = req.body.userId;
                    comment.content = req.body.content;
                    var date = new Date().toISOString();
                    comment.date = date.slice(0, 10);
                    comment.save();
                    gallery.commentCount += 1;
                    gallery.save();
                    return res.json({'result': 'success'});
                }
                else return res.json({'result': 'fail'});
            });
            break;
        case 'schedule':
            Schedule.findById(req.body.articleId, function (err, schedule) {
                if (err) return res.json({'result': 'fail'});
                if (schedule) {
                    var comment = new ScheduleComment();
                    comment.schedule = schedule;
                    comment.author = req.body.userId;
                    comment.content = req.body.content;
                    var date = new Date().toISOString();
                    comment.date = date.slice(0, 10);
                    comment.save();
                    schedule.commentCount += 1;
                    schedule.save();
                    return res.json({'result': 'success'});
                }
                else return res.json({'result': 'fail'});
            });
            break;
        case 'qa':
            QA.findById(req.body.articleId, function (err, qa) {
                if (err) return res.json({'result': 'fail'});
                if (qa) {
                    var comment = new QAComment();
                    comment.QA = qa;
                    comment.author = req.body.userId;
                    comment.content = req.body.content;
                    var date = new Date().toISOString();
                    comment.date = date.slice(0, 10);
                    comment.save();
                    qa.commentCount += 1;
                    qa.save();
                    return res.json({'result': 'success'});
                }
                else return res.json({'result': 'fail'});
            });
            break;
    }

});

router.post('/deleteArticle', function (req, res) {
    var path = req.body.path;
    var id = new ObjectId(req.body.articleId);
    switch (path) {
        case 'notice':
            Notice.findById(id, function (err, notice) {
                if (err) return res.json({'result': 'fail'});
                if (notice) {
                    NoticeComment.find({'notice': id}, function (err, comments) {
                        if (err) return res.json({'result': 'fail'});
                        if (comments) {
                            for (var i = 0; i < comments.length; i++)
                                comments[i].remove();
                            notice.remove();
                            return res.json({'result': 'success'});
                        }
                        else return res.json({'result': 'fail'});
                    })
                }
                else return res.json({'result': 'fail'});
            });
            break;
        case 'gallery':
            Gallery.findById(id, function (err, gallery) {
                if (err) return res.json({'result': 'fail'});
                if (gallery) {
                    GalleryComment.find({'gallery': id}, function (err, comments) {
                        if (err) return res.json({'result': 'fail'});
                        if (comments) {
                            for (var i = 0; i < comments.length; i++)
                                comments[i].remove();
                            gallery.remove();
                            return res.json({'result': 'success'});
                        }
                        else return res.json({'result': 'fail'});
                    })
                }
                else return res.json({'result': 'fail'});
            });
            break;
        case 'schedule':
            Schedule.findById(id, function (err, schedule) {
                if (err) return res.json({'result': 'fail'});
                if (schedule) {
                    ScheduleComment.find({'schedule': id}, function (err, comments) {
                        if (err) return res.json({'result': 'fail'});
                        if (comments) {
                            for (var i = 0; i < comments.length; i++)
                                comments[i].remove();
                            schedule.remove();
                            return res.json({'result': 'success'});
                        }
                        else return res.json({'result': 'fail'});
                    })
                }
                else return res.json({'result': 'fail'});
            });
            break;
        case 'qa':
            QA.findById(id, function (err, qa) {
                if (err) return res.json({'result': 'fail'});
                if (qa) {
                    QAComment.find({'QA': id}, function (err, comments) {
                        if (err) return res.json({'result': 'fail'});
                        if (comments) {
                            for (var i = 0; i < comments.length; i++)
                                comments[i].remove();
                            qa.remove();
                            return res.json({'result': 'success'});
                        }
                        else return res.json({'result': 'fail'});
                    })
                }
                else return res.json({'result': 'fail'});
            });
            break;
    }
});

// 게시판 API 끝

router.get('/getPatients', function (req, res) {
    var id = req.query.nursingHomeId;
    User.findOne({'nursingHome': new Object(id), 'userId': req.query.userId}, function (err, worker) {
        if (err) return res.json({'result': 'fail'});
        if (worker) {
            Patient.find({'worker': worker}, function (err, patients) {
                if (err) return res.json({'result': 'fail'});
                if (patients) {
                    return res.json(patients);
                }
                else return res.json({'result': 'fail'});
            });
        }
        else return res.json({'result': 'fail'});
    })

});

router.post('/createPatient', function (req, res) {
    var id = new ObjectId(req.body.nursingHomeId);
    NursingHome.findById(id, function (err, nursingHome) {
        if (err) return res.json({'result': 'fail'});
        if (nursingHome) {
            User.findOne({'userId': req.body.userId}, function (err, user) {
                if (err) return res.json({'result': 'fail'});
                if (user)
                    return res.json({'result': 'userOverlap'});
                else {
                    User.findOne({'userId': req.body.workerId, 'nursingHome': id}, function (err, worker) {
                        if (err) return res.json({'result': 'fail'});
                        if (worker) {
                            Patient.findOne({
                                'patientName': req.body.patientName,
                                'birthday': req.body.birthday
                            }, function (err, patient) {
                                if (err) return res.json({'result': 'fail'});
                                if (patient)
                                    return res.json({'result': 'patientOverlap'});
                                else {
                                    var protector = new User();
                                    protector.nursingHome = nursingHome;
                                    protector.userId = req.body.userId;
                                    protector.userName = req.body.userName;
                                    protector.password = req.body.password;
                                    protector.phoneNumber = req.body.phoneNumber;
                                    protector.gender = "male";
                                    protector.auth = 2;
                                    protector.role = "보호자";

                                    var patient = new Patient();
                                    patient.protector = protector;
                                    patient.worker = worker;
                                    patient.patientName = req.body.patientName;
                                    patient.gender = req.body.gender;
                                    patient.birthday = req.body.birthday;
                                    patient.roomNumber = req.body.roomNumber;
                                    patient.relation = req.body.relation;
                                    patient.image = req.body.image;
                                    patient.roomNumber = req.body.roomNumber;

                                    var category = new Category();
                                    category.patient = patient;
                                    category.date = req.body.date;
                                    category.save();

                                    protector.save();
                                    patient.save();
                                    return res.json({'result': 'success'});
                                }
                            })
                        }
                        else return res.json({'result': 'notWorker'})
                    })
                }
            })
        }
        else return res.json({'result': 'fail'});
    })
});

router.post('/getPatient', function (req, res) {
    User.findOne({'userId': req.body.userId}, function (err, user) {
        if (err) return res.json({'result': 'fail'});
        if (user) {
            Patient.findOne({protector: user._id}, function (err, patient) {
                if (err) return res.json({'result': 'fail'});
                if (patient) return res.json(patient);
                else return res.json({'result': 'fail'});
            })
        }
        else return res.json({'result': 'fail'})
    })
});

router.get('/getCategories', function (req, res) {
    var result = [];
    var id = new ObjectId(req.query.patientId);
    Patient.findById(id, function (err, patient) {
        Category.findOne({patient: patient.id, date: req.query.date}, function (err, category) {
            if (err) return res.json({'result': 'fail'});
            if (category) {
                if (category.mealEnabled)
                    result.push({'title': '오늘의 식사', 'content': category.meal, 'state': category.mealEnabled, 'num': 0});
                if (category.cleanEnabled)
                    result.push({
                        'title': '신체위생관리',
                        'content': category.clean,
                        'state': category.cleanEnabled,
                        'num': 1
                    });
                if (category.activityEnabled)
                    result.push({
                        'title': '산책/외출',
                        'content': category.activity,
                        'state': category.activityEnabled,
                        'num': 2
                    });
                if (category.moveTrainEnabled)
                    result.push({
                        'title': '생활기능훈련',
                        'content': category.moveTrain,
                        'state': category.moveTrainEnabled,
                        'num': 3
                    });
                if (category.commentEnabled)
                    result.push({
                        'title': '특이사항',
                        'content': category.comment,
                        'state': category.commentEnabled,
                        'num': 4
                    });
                if (category.restRoomEnabled)
                    result.push({
                        'title': '배변횟수',
                        'content': category.restRoom,
                        'state': category.restRoomEnabled,
                        'num': 5
                    });
                if (category.medicineEnabled)
                    result.push({
                        'title': '투약관리',
                        'content': category.medicine,
                        'state': category.medicineEnabled,
                        'num': 6
                    });
                if (category.mentalTrainEnabled)
                    result.push({
                        'title': '정신기능훈련',
                        'content': category.mentalTrain,
                        'state': category.mentalTrainEnabled,
                        'num': 7
                    });
                if (category.physicalCareEnabled)
                    result.push({
                        'title': '물리치료',
                        'content': category.physicalCare,
                        'state': category.physicalCareEnabled,
                        'num': 8
                    });
                if (category.custom.length != 0) {
                    for (var i = 0; i < category.custom.length; i++)
                        if (category.custom[i].state) {
                            result.push({
                                'title': category.custom[i].title,
                                'content': category.custom[i].content,
                                'state': category.custom[i].state,
                                'num': category.custom[i].num
                            });
                        }
                }
                return res.json(result);
            }
            else {
                result.push({'result': 'nothing'});
                return res.json(result);
            }
        })
    })
});

router.get('/getCategoryState', function (req, res) {
    var result = [];
    var id = new ObjectId(req.query.patientId);
    Patient.findById(id, function (err, patient) {
        if (err) return res.json({'result': 'fail'});
        if (patient) {
            Category.findOne({'patient': patient, 'date': req.query.date}, function (err, category) {
                if (err) return res.json({'result': 'fail'});
                if (category) {
                    //if (category.mealEnabled)
                    result.push({'title': '오늘의 식사', 'content': category.meal, 'state': category.mealEnabled, 'num': 0});
                    //if (category.cleanEnabled)
                    result.push({
                        'title': '신체위생관리',
                        'content': category.clean,
                        'state': category.cleanEnabled,
                        'num': 1
                    });
                    //if (category.activityEnabled)
                    result.push({
                        'title': '산책/외출',
                        'content': category.activity,
                        'state': category.activityEnabled,
                        'num': 2
                    });
                    //if (category.moveTrainEnabled)
                    result.push({
                        'title': '생활기능훈련',
                        'content': category.moveTrain,
                        'state': category.moveTrainEnabled,
                        'num': 3
                    });
                    //if (category.commentEnabled)
                    result.push({
                        'title': '특이사항',
                        'content': category.comment,
                        'state': category.commentEnabled,
                        'num': 4
                    });
                    //if (category.restRoomEnabled)
                    result.push({
                        'title': '배변횟수',
                        'content': category.restRoom,
                        'state': category.restRoomEnabled,
                        'num': 5
                    });
                    //if (category.medicineEnabled)
                    result.push({
                        'title': '투약관리',
                        'content': category.medicine,
                        'state': category.medicineEnabled,
                        'num': 6
                    });
                    //if (category.mentalTrainEnabled)
                    result.push({
                        'title': '정신기능훈련',
                        'content': category.mentalTrain,
                        'state': category.mentalTrainEnabled,
                        'num': 7
                    });
                    //if (category.physicalCareEnabled)
                    result.push({
                        'title': '물리치료',
                        'content': category.physicalCare,
                        'state': category.physicalCareEnabled,
                        'num': 8
                    });
                    //if(category.custom.length != 0) {
                    for (var i = 0; i < category.custom.length; i++) {
                        result.push({
                            'title': category.custom[i].title,
                            'content': category.custom[i].content,
                            'state': category.custom[i].state,
                            'num': category.custom[i].num
                        });
                    }
                    return res.json(result);
                }
                else return res.json({'result': 'fail'})
            })
        }
        else return res.json({'result': 'fail'})
    })
});

router.post("/saveCategoryState", function (req, res) {
    var id = new ObjectId(req.body.patientId);
    var token = true;
    Patient.findById(id, function (err, patient) {
        if (err) return res.json({'result': 'fail'});
        if (patient) {
            Category.findOne({patient: patient, date: req.body.date}, function (err, category) {
                if (err) return res.json({'result': 'fail'});
                if (category) {
                    var length = Object.keys(req.body).length;
                    category.mealEnabled = req.body.state0 == 'true';
                    category.cleanEnabled = req.body.state1 == 'true';
                    category.activityEnabled = req.body.state2 == 'true';
                    category.moveTrainEnabled = req.body.state3 == 'true';
                    category.commentEnabled = req.body.state4 == 'true';
                    category.restRoomEnabled = req.body.state5 == 'true';
                    category.medicineEnabled = req.body.state6 == 'true';
                    category.mentalTrainEnabled = req.body.state7 == 'true';
                    category.physicalCareEnabled = req.body.state8 == 'true';
                    for (var i = 0; i < category.custom.length; i++) {
                        var tmp = 'state' + (i + 9);
                        if (req.body[tmp] == 'true') {
                            token = true;
                        }
                        else {
                            token = false;
                        }
                        Category.update({'custom.num': i+9, 'patient': patient, 'date': req.body.date}, {'$set': {
                            'custom.$.state': token
                        }}, function(err) {
                            if(err) return res.json({'result': 'fail'})
                        })
                    }
                    category.save();
                    return res.json({'result': 'success'})
                }
                else return res.json({'result': 'fail'})
            })
        }
        else return res.json({'result': 'fail'})
    })
});

router.post('/getWorker', function (req, res) {
    var id = new ObjectId(req.body.patientId);
    Patient.findById(id, function (err, patient) {
        if (err) return res.json({'result': 'fail'});
        if (patient) {
            User.findById(patient.worker, function (err, user) {
                if (err) return res.json({'result': 'fail'});
                if (user) return res.json(user);
                else return res.json({'result': 'fail'})
            })
        }
        else return res.json({'result': 'fail'});
    })
});

router.post('/saveCategory', function (req, res) {
    var id = new ObjectId(req.body.patientId);
    Patient.findById(id, function (err, patient) {
        if (err) return res.json({'result': 'fail'});
        if (patient) {
            Category.findOne({patient: patient.id, date: req.body.date}, function (err, category) {
                if (err) return res.json({'result': 'fail'});
                if (category) {
                    switch (req.body.position) {
                        case '0':
                            category.meal = req.body.content;
                            break;
                        case '1':
                            category.clean = req.body.content;
                            break;
                        case '2':
                            category.activity = req.body.content;
                            break;
                        case '3':
                            category.moveTrain = req.body.content;
                            break;
                        case '4':
                            category.comment = req.body.content;
                            break;
                        case '5':
                            category.restRoom = req.body.content;
                            break;
                        case '6':
                            category.medicine = req.body.content;
                            break;
                        case '7':
                            category.mentalTrain = req.body.content;
                            break;
                        case '8':
                            category.physicalCare = req.body.content;
                            break;
                        default:
                            Category.update({'custom.num': parseInt(req.body.position), 'patient': patient, 'date': req.body.date}, {'$set': {
                                'custom.$.content': req.body.content
                            }}, function(err) {
                                if(err) return res.json({'result': 'fail'})
                            });
                            break;
                    }
                    category.save();
                    return res.json({'result': 'success'});
                }
                else return res.json({'result': 'fail'})
            })
        }
        else return res.json({'result': 'fail'});
    })
});

router.post('/addCategory', function (req, res) {
    var id = new ObjectId(req.body.patientId);
    Patient.findById(id, function (err, patient) {
        if (err) return res.json({'result': 'fail'});
        if (patient) {
            Category.findOne({patient: patient, date: req.body.date}, function (err, category) {
                if (err) return res.json({'result': 'fail'});
                if (category) {
                    category.custom.push({
                        'title': req.body.customTitle,
                        'content': "",
                        'state': true,
                        'num': category.custom.length + 9
                    });
                    category.save();
                    return res.json({'result': 'success'});
                }
                else return res.json({'result': 'fail'});
            })
        }
        else return res.json({'result': 'fail'});
    })
});

router.post('/getProtector', function(req, res) {
    var id = new ObjectId(req.body.userId);
    User.findById(id, function(err, user) {
        if(err) return res.json({'result': 'fail'});
        if(user) return res.json(user);
        else return res.json({'result': 'fail'});
    })
})


Patient.find(function (err, patients) {
    var today = new Date();
    var month1 = today.getMonth()+1;
    var dateNow = today.getFullYear().toString() + "-" + month1 + "-" + today.getDate();
    var yesterday = new Date(today.valueOf() - (24*60*60*1000*3));
    var month2 = yesterday.getMonth()+1;
    var dateYesterday = yesterday.getFullYear() + "-" + month2 + "-" + yesterday.getDate();
    Category.find({date: dateNow}, function (err, tmps) {
        if (tmps.length == 0) {
            Category.find({date: dateYesterday}, function (err, categories) {
                for (var i in patients) {
                    var category = new Category();
                    var tmp = JSON.parse(JSON.stringify(categories[i]));
                    category.patient = patients[i];
                    category.custom = tmp.custom;
                    for (var j = 0; j < category.custom.length; j++)
                        category.custom[j].content = "";
                    category.date = dateNow;
                    category.mealEnabled = tmp.mealEnabled;
                    category.cleanEnabled = tmp.cleanEnabled;
                    category.activityEnabled = tmp.activityEnabled;
                    category.moveTrainEnabled = tmp.moveTrainEnabled;
                    category.commentEnabled = tmp.commentEnabled;
                    category.restRoomEnabled = tmp.restRoomEnabled;
                    category.medicineEnabled = tmp.medicineEnabled;
                    category.mentalTrainEnabled = tmp.mentalTrainEnabled;
                    category.physicalCareEnabled = tmp.physicalCareEnabled;
                    category.save();
                }
            })
        }
    })
});

setInterval(function () {
    Patient.find(function (err, patients) {
        var today = new Date();
        var month1 = today.getMonth()+1;
        var dateNow = today.getFullYear().toString() + "-" + month1 + "-" + today.getDate();
        var yesterday = new Date(today.valueOf() - (24*60*60*1000));
        var month2 = yesterday.getMonth()+1;
        var dateYesterday = yesterday.getFullYear() + "-" + month2 + "-" + yesterday.getDate();
        Category.find({date: dateNow}, function (err, tmps) {
            if (tmps.length == 0) {
                Category.find({date: dateYesterday}, function (err, categories) {
                    for (var i in patients) {
                        var category = new Category();
                        var tmp = JSON.parse(JSON.stringify(categories[i]));
                        category.patient = patients[i];
                        category.custom = tmp.custom;
                        for (var j = 0; j < category.custom.length; j++)
                            category.custom[j].content = "";
                        category.date = dateNow;
                        category.mealEnabled = tmp.mealEnabled;
                        category.cleanEnabled = tmp.cleanEnabled;
                        category.activityEnabled = tmp.activityEnabled;
                        category.moveTrainEnabled = tmp.moveTrainEnabled;
                        category.commentEnabled = tmp.commentEnabled;
                        category.restRoomEnabled = tmp.restRoomEnabled;
                        category.medicineEnabled = tmp.medicineEnabled;
                        category.mentalTrainEnabled = tmp.mentalTrainEnabled;
                        category.physicalCareEnabled = tmp.physicalCareEnabled;
                        category.save();
                    }
                })
            }
        })
    });
}, 1000 * 60 * 60 * 24);

module.exports = router;
