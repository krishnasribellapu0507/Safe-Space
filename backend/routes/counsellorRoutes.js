const r=require('express').Router(),c=require('../controllers/counsellorController');r.get('/',c.list);module.exports=r;
