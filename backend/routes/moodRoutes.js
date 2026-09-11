const r=require('express').Router(),c=require('../controllers/moodController');r.post('/',c.create);r.get('/history',c.history);module.exports=r;
