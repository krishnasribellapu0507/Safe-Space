const r=require('express').Router(),c=require('../controllers/appointmentController');r.get('/',c.list);r.post('/',c.create);module.exports=r;
