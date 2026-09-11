const {load}=require('../config/database');const ai=require('../services/aiService');exports.get=(req,res)=>res.json(ai.analyse(load(),req.user.id));
