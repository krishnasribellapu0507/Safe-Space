const {load}=require('../config/database');
module.exports=(req,res,next)=>{
  const header=String(req.headers.authorization||'');
  if(!header.startsWith('Bearer ')) return res.status(401).json({error:'Authentication required'});
  const token=header.slice(7).trim();
  if(!token.startsWith('demo-')) return res.status(401).json({error:'Invalid demo session'});
  const id=token.slice(5);
  const user=load().users.find(u=>u.id===id);
  if(!user) return res.status(401).json({error:'Invalid demo session'});
  req.user={id:user.id,role:user.role||'user',email:user.email};
  next();
};
