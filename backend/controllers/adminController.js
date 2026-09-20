const {load,save}=require('../config/database');
const trend=require('../services/trendEngine');

function recordAudit(req,action){
  const db=load();
  db.audit_logs=db.audit_logs||[];
  db.audit_logs.push({id:'audit-'+Date.now(),actorId:req.user.id,action,at:new Date().toISOString()});
  save(db);
}

exports.overview=(req,res)=>{
  const db=load();
  recordAudit(req,'admin.overview');
  const ids=db.users.filter(u=>(u.role||'user')==='user').map(u=>u.id);
  const notable=ids.filter(id=>{
    const t=trend.compute(db.moods.filter(x=>x.userId===id),db.journey.filter(x=>x.userId===id));
    return t.band==='Needs attention'||t.band==='Consider additional support';
  }).length;
  res.json({
    enrolledUsers:ids.length,
    recentCheckins:db.moods.length,
    notableWellbeingChanges:notable,
    pendingSupportRequests:(db.support_requests||[]).filter(x=>x.status==='pending').length,
    appointments:(db.appointments||[]).length
  });
};

exports.alerts=(req,res)=>{
  const db=load();
  recordAudit(req,'admin.alerts');
  const rows=db.users.filter(u=>(u.role||'user')==='user').map(u=>{
    const t=trend.compute(db.moods.filter(x=>x.userId===u.id),db.journey.filter(x=>x.userId===u.id));
    return {userId:u.id,displayName:u.name,band:t.band,score:t.score,reasons:t.reasons};
  }).filter(x=>x.band!=='Stable'&&x.band!=='Building baseline');
  res.json(rows);
};

exports.supportRequests=(req,res)=>{
  const db=load();
  recordAudit(req,'admin.support_requests');
  res.json((db.support_requests||[]).map(x=>({id:x.id,userId:x.userId,type:x.type,status:x.status,createdAt:x.createdAt})));
};

exports.appointments=(req,res)=>{
  recordAudit(req,'admin.appointments');
  res.json(load().appointments||[]);
};

exports.auditLog=(req,res)=>res.json((load().audit_logs||[]).slice(-100).reverse());
