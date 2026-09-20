const {load,save}=require('../config/database');
const User=require('../models/User');
const password=()=>process.env.DEMO_PASSWORD||'demo123';
const tokenFor=id=>'demo-'+id;
const publicUser=u=>({id:u.id,name:u.name,email:u.email,language:u.language,role:u.role||'user'});
exports.register=(req,res)=>{
  const db=load(),email=String(req.body.email||'').trim().toLowerCase(),pass=String(req.body.password||'');
  if(pass.length<6)return res.status(400).json({error:'Password must be at least 6 characters'});
  if(db.users.some(u=>String(u.email).toLowerCase()===email))return res.status(409).json({error:'Email exists'});
  const u=User({...req.body,email});db.users.push(u);save(db);
  res.status(201).json({user:publicUser(u),token:tokenFor(u.id),mode:'demo-auth'});
};
exports.login=(req,res)=>{
  const db=load(),email=String(req.body.email||'').trim().toLowerCase(),pass=String(req.body.password||'');
  const u=db.users.find(x=>String(x.email).toLowerCase()===email);
  if(!u||pass!==password())return res.status(401).json({error:'Invalid email or password'});
  res.json({user:publicUser(u),token:tokenFor(u.id),mode:'demo-auth'});
};
