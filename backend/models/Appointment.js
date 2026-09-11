module.exports=d=>({id:d.id||Date.now().toString(),userId:d.userId||'demo',counsellorId:d.counsellorId||'c1',status:d.status||'requested',createdAt:d.createdAt||new Date().toISOString()});
