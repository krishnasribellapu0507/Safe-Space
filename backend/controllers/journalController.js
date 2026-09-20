const {load,save}=require('../config/database');
const Journal=require('../models/Journal');
exports.list=(req,res)=>res.json(load().journals.filter(x=>x.userId===req.user.id));
exports.getOne=(req,res)=>{
  const j=load().journals.find(x=>x.id===req.params.id&&x.userId===req.user.id);
  if(!j)return res.sendStatus(404);
  res.json(j);
};
exports.create=(req,res)=>{
  const db=load(),j=Journal({...req.body,text:String(req.body.text||'').trim(),userId:req.user.id});
  db.journals.push(j);save(db);res.status(201).json(j);
};
exports.update=(req,res)=>{
  const db=load(),i=db.journals.findIndex(x=>x.id===req.params.id&&x.userId===req.user.id);
  if(i<0)return res.sendStatus(404);
  const allowed={};
  if(req.body.text!==undefined)allowed.text=String(req.body.text).trim();
  if(req.body.mood!==undefined)allowed.mood=String(req.body.mood).slice(0,64);
  if(req.body.favorite!==undefined)allowed.favorite=!!req.body.favorite;
  db.journals[i]={...db.journals[i],...allowed,id:db.journals[i].id,userId:req.user.id};
  save(db);res.json(db.journals[i]);
};
exports.remove=(req,res)=>{
  const db=load(),before=db.journals.length;
  db.journals=db.journals.filter(x=>!(x.id===req.params.id&&x.userId===req.user.id));
  save(db);res.status(before===db.journals.length?404:204).end();
};
