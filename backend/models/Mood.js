module.exports=d=>({id:d.id||Date.now().toString(),userId:d.userId||'demo',mood:d.mood||'okay',note:d.note||'',createdAt:d.createdAt||new Date().toISOString()});
