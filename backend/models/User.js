module.exports=d=>({id:d.id||Date.now().toString(),name:d.name||'',email:d.email||'',language:d.language||'English',createdAt:d.createdAt||new Date().toISOString()});
