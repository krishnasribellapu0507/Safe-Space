module.exports=d=>({id:d.id||Date.now().toString(),userId:d.userId||'demo',stage:d.stage||'Complaint Registered',date:d.date||new Date().toISOString(),note:d.note||''});
