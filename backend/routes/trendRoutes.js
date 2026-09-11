const r=require('express').Router(),c=require('../controllers/trendController');r.get('/',c.get);module.exports=r;
