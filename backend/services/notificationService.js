module.exports={queue:(type,payload)=>({id:Date.now().toString(),type,payload,status:'demo-queued'})};
