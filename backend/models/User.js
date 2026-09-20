module.exports=d=>({
  id:d.id||Date.now().toString(),
  name:String(d.name||'').trim(),
  email:String(d.email||'').trim().toLowerCase(),
  language:d.language||'English',
  role:'user',
  authMode:'demo',
  createdAt:d.createdAt||new Date().toISOString()
});
