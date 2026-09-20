const {load,save}=require('../config/database');
exports.listOwn=(req,res)=>res.json((load().support_requests||[]).filter(x=>x.userId===req.user.id));
exports.create=(req,res)=>{
  const db=load();db.support_requests=db.support_requests||[];
  const item={id:'sr-'+Date.now(),userId:req.user.id,type:String(req.body.type||'support').slice(0,80),message:String(req.body.message||'').trim().slice(0,1000),status:'pending',createdAt:new Date().toISOString()};
  db.support_requests.push(item);save(db);res.status(201).json(item);
};
