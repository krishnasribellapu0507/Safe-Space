const API=localStorage.getItem('safeSpaceApi')||'http://localhost:3000';
let sessionToken=sessionStorage.getItem('safeSpaceStaffSession')||'';
const el=id=>document.getElementById(id);
const clean=value=>String(value??'').replace(/[&<>"']/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
async function call(path,options={}){
  const headers={...(options.headers||{})};
  if(sessionToken)headers.authorization='Bearer '+sessionToken;
  const response=await fetch(API+path,{...options,headers});
  const body=await response.json().catch(()=>({}));
  if(!response.ok)throw new Error(body.error||'Request failed');
  return body;
}
function card(title,status,copy,attention){
  return '<article class="item"><div class="item-top"><strong>'+clean(title)+'</strong><span class="status '+(attention?'attention':'')+'">'+clean(status)+'</span></div><p>'+clean(copy)+'</p></article>';
}
async function load(){
  const overview=await call('/api/admin/overview');
  const alerts=await call('/api/admin/alerts');
  const support=await call('/api/admin/support-requests');
  const audit=await call('/api/admin/audit');
  el('mUsers').textContent=overview.enrolledUsers;
  el('mCheckins').textContent=overview.recentCheckins;
  el('mChanges').textContent=overview.notableWellbeingChanges;
  el('mSupport').textContent=overview.pendingSupportRequests;
  el('mAppointments').textContent=overview.appointments;
  el('alertsList').innerHTML=alerts.length?alerts.map(x=>card(x.displayName,x.band,(x.reasons||[]).join(' • '),true)).join(''):card('No notable changes','Stable','No explainable wellbeing changes currently need review.',false);
  el('supportList').innerHTML=support.length?support.map(x=>card(x.type,x.status,'Request '+x.id+' • '+new Date(x.createdAt).toLocaleString(),false)).join(''):card('No pending requests','Clear','Support requests will appear here without journal text.',false);
  el('auditList').innerHTML=audit.length?audit.slice(0,12).map(x=>card(x.action,new Date(x.at).toLocaleTimeString(),'Authorized staff: '+x.actorId,false)).join(''):card('No access events yet','Audit','Dashboard access will be recorded here.',false);
  el('connection').textContent='Connected to local Safe Space API • authorized demo session';
  el('loginPanel').classList.add('hidden');
  el('dashboard').classList.remove('hidden');
  el('signOut').classList.remove('hidden');
}
el('loginForm').addEventListener('submit',async event=>{
  event.preventDefault();el('loginError').textContent='';
  try{
    const result=await call('/api/auth/login',{method:'POST',headers:{'content-type':'application/json'},body:JSON.stringify({email:el('email').value,password:el('password').value})});
    if(!['administrator','counsellor','support-worker','supervisor'].includes(result.user.role))throw new Error('This account is not authorized for the staff dashboard.');
    sessionToken=result.token;sessionStorage.setItem('safeSpaceStaffSession',sessionToken);await load();
  }catch(error){el('loginError').textContent=error.message;}
});
el('refresh').addEventListener('click',()=>load().catch(error=>el('connection').textContent=error.message));
el('signOut').addEventListener('click',()=>{sessionStorage.removeItem('safeSpaceStaffSession');location.reload()});
if(sessionToken)load().catch(()=>sessionStorage.removeItem('safeSpaceStaffSession'));

document.querySelectorAll('.nav[data-target]').forEach(button=>button.addEventListener('click',()=>{
  document.querySelectorAll('.nav').forEach(x=>x.classList.remove('active'));
  button.classList.add('active');
  const target=button.dataset.target;
  if(target==='top') window.scrollTo({top:0,behavior:'smooth'});
  else document.getElementById(target)?.closest('.panel')?.scrollIntoView({behavior:'smooth',block:'start'});
}));
