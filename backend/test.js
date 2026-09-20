const assert=require('assert');
const fs=require('fs');
const os=require('os');
const path=require('path');

const source=path.join(__dirname,'data/demoData.json');
const temp=path.join(os.tmpdir(),'safe-space-test-'+process.pid+'.json');
fs.copyFileSync(source,temp);
process.env.DATA_FILE=temp;
process.env.RATE_LIMIT_PER_MINUTE='200';

const {compute}=require('./services/trendEngine');
const trend=compute([
  {mood:'Good',sleep:4,stress:2,energy:4,safety:5,connection:4},
  {mood:'Okay',sleep:4,stress:2,energy:4,safety:5,connection:4},
  {mood:'Low',sleep:3,stress:3,energy:3,safety:4,connection:3},
  {mood:'Low',sleep:2,stress:4,energy:2,safety:4,connection:3},
  {mood:'Overwhelmed',sleep:2,stress:5,energy:2,safety:4,connection:2}
],[]);
assert(['Needs attention','Consider additional support'].includes(trend.band));
assert(trend.reasons.includes('lower mood'));
assert(trend.reasons.includes('less sleep'));
assert(trend.reasons.includes('higher stress'));

const app=require('./server');

(async()=>{
  const server=app.listen(0);
  const base='http://127.0.0.1:'+server.address().port;
  const call=(url,opts={})=>fetch(base+url,opts);
  try{
    let r=await call('/api/journal');
    assert.strictEqual(r.status,401);

    r=await call('/api/auth/login',{method:'POST',headers:{'content-type':'application/json'},body:JSON.stringify({email:'demo@safespace.app',password:'demo123'})});
    assert.strictEqual(r.status,200);
    const login=await r.json();
    const userHeaders={authorization:'Bearer '+login.token,'content-type':'application/json'};

    r=await call('/api/journal',{headers:userHeaders});
    assert.strictEqual(r.status,200);
    const own=await r.json();
    assert(own.every(x=>x.userId==='demo'));

    r=await call('/api/journal/j-private-other',{headers:userHeaders});
    assert.strictEqual(r.status,404);

    r=await call('/api/support',{method:'POST',headers:userHeaders,body:JSON.stringify({message:'Please contact me'})});
    assert.strictEqual(r.status,400);

    r=await call('/api/support',{method:'POST',headers:userHeaders,body:JSON.stringify({type:'counsellor',message:'I would like support'})});
    assert.strictEqual(r.status,201);

    r=await call('/api/admin/overview',{headers:userHeaders});
    assert.strictEqual(r.status,403);

    r=await call('/api/auth/login',{method:'POST',headers:{'content-type':'application/json'},body:JSON.stringify({email:'admin@safespace.app',password:'demo123'})});
    assert.strictEqual(r.status,200);
    const admin=await r.json();
    r=await call('/api/admin/support-requests',{headers:{authorization:'Bearer '+admin.token}});
    assert.strictEqual(r.status,200);
    const requests=await r.json();
    assert(requests.length>=1);
    assert(!('journal' in requests[0])&&!('text' in requests[0]));

    console.log('Safe Space backend baseline, ownership and role tests passed');
  } finally {
    server.close();
    try{fs.unlinkSync(temp)}catch(e){}
  }
})().catch(err=>{console.error(err);process.exit(1)});
