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
var ObjectId = require('mongodb').ObjectId;

/* GET home page. */
router.get('/', function (req, res) {
    res.render('index', {title: 'AppProjectServer'});
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
                    return res.json(notices);
                }
                else return res.json({'result': 'fail'});
            });
            break;
        case 'gallery':
            Gallery.find({'nursingHome': new ObjectId(id)}, function (err, galleries) {
                if (err) return res.json({'result': 'fail'});
                if (galleries) {
                    return res.json(galleries);
                }
                else return res.json({'result': 'fail'});
            });
            break;
        case 'schedule':
            Schedule.find({'nursingHome': new ObjectId(id)}, function (err, schedules) {
                if (err) return res.json({'result': 'fail'});
                if (schedules) {
                    return res.json(schedules);
                }
                else return res.json({'result': 'fail'});
            });
            break;
        case 'qa':
            QA.find({'nursingHome': new ObjectId(id)}, function (err, qas) {
                if (err) return res.json({'result': 'fail'});
                if (qas) {
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

router.post('/aaaa', function (req, res) {
    console.log(req.body);
    return res.json({'result': 'success'});
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
                                    patient.relation = req.body.relation;
                                    patient.image = req.body.image;
                                    patient.roomNumber = req.body.roomNumber;

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

Patient.find(function(err, patients) {
    for(var i in patients) {
        console.log(patients[i]);
        var date = new Date().toISOString();
        Category.findOne({patient: patients[i], date: date.slice(0, 10)}, function(err, category) {
            if(!category) {
                console.log("카테고리 생성");
                var category = new Category();
                category.patient = patients[i];

                category.date = date.slice(0, 10);
                category.save();
            }
        })
    }
});

setInterval(function () {
    console.log("timer start");
    Patient.find(function(err, patients) {
        for(var i in patients) {
            console.log(patients[i]);
            var date = new Date().toISOString();
            Category.findOne({patient: patients[i], date: date.slice(0, 10)}, function(err, category) {
                if(!category) {
                    console.log("카테고리 생성");
                    var category = new Category();
                    category.patient = patients[i];

                    category.date = date.slice(0, 10);
                    category.save();
                }
            })
        }
    })
}, 1000*60*60);

module.exports = router;
