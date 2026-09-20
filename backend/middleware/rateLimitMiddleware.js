const buckets=new Map();
const WINDOW_MS=60*1000;
const MAX=Number(process.env.RATE_LIMIT_PER_MINUTE||90);
module.exports=(req,res,next)=>{
  const now=Date.now(),key=req.ip||req.socket?.remoteAddress||'local';
  let b=buckets.get(key);
  if(!b||now-b.startedAt>=WINDOW_MS)b={startedAt:now,count:0};
  b.count+=1;buckets.set(key,b);
  if(b.count>MAX){res.set('Retry-After','60');return res.status(429).json({error:'Too many requests. Please try again shortly.'});}
  next();
};
