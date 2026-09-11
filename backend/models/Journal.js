module.exports=d=>({id:d.id||Date.now().toString(),userId:d.userId||'demo',text:d.text||'',mood:d.mood||'Reflection',favorite:!!d.favorite,createdAt:d.createdAt||new Date().toISOString()});
