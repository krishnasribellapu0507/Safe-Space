const r=require('express').Router(),c=require('../controllers/authController'),v=require('../middleware/validationMiddleware');
r.post('/register',v.requireFields('name','email','password'),c.register);
r.post('/login',v.requireFields('email','password'),c.login);
module.exports=r;
