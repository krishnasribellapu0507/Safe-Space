const r=require('express').Router(),c=require('../controllers/supportController'),v=require('../middleware/validationMiddleware');
r.get('/',c.listOwn);
r.post('/',v.requireFields('type'),v.maxStringLength('message',1000),c.create);
module.exports=r;
