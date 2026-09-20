const r=require('express').Router(),c=require('../controllers/journalController'),v=require('../middleware/validationMiddleware');
r.get('/',c.list);
r.get('/:id',c.getOne);
r.post('/',v.requireFields('text'),v.maxStringLength('text',10000),c.create);
r.put('/:id',v.maxStringLength('text',10000),c.update);
r.delete('/:id',c.remove);
module.exports=r;
