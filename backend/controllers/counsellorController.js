const {load}=require('../config/database');exports.list=(req,res)=>res.json(load().counsellors);
