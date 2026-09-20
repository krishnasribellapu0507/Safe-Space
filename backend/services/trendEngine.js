const difficultWords=new Set(['low','worried','angry','overwhelmed','confused','stressed','sad']);
function moodValue(v){
  if(typeof v==='number') return clamp(v,1,5);
  const m=String(v||'okay').toLowerCase();
  if(['great','happy'].includes(m)) return 5;
  if(['good','calm'].includes(m)) return 4;
  if(m==='okay') return 3;
  if(['low','sad','worried'].includes(m)) return 2;
  if(['overwhelmed','stressed','angry','confused'].includes(m)) return 1;
  return difficultWords.has(m)?2:3;
}
const norm=v=>(v-1)*25;
const avg=a=>a.length?a.reduce((s,v)=>s+v,0)/a.length:3;
function metric(records,key,fallback=3){
  const vals=records.map(r=>key==='mood'?moodValue(r.mood):Number(r[key])).filter(v=>Number.isFinite(v)&&v>=1&&v<=5);
  return vals.length?avg(vals):fallback;
}
function snapshot(records){
  return Math.round(norm(metric(records,'mood'))*.25+norm(metric(records,'sleep'))*.15+(100-norm(metric(records,'stress')))*.20+norm(metric(records,'energy'))*.15+norm(metric(records,'safety',4))*.15+norm(metric(records,'connection'))*.10);
}
function compute(checkins=[],events=[]){
  const records=checkins.slice(-30);
  if(records.length<4)return{score:snapshot(records),band:'Building baseline',reasons:['more check-ins are needed before comparing patterns'],helping:[],disclaimer:'Wellbeing reflection only; not a medical diagnosis.'};
  const recent=records.slice(-3),base=records.slice(0,Math.max(1,records.length-3));
  const score=snapshot(recent),baseScore=snapshot(base),delta=score-baseScore,reasons=[];
  for(const [key,inverse,label] of [['mood',false,'lower mood'],['sleep',false,'less sleep'],['stress',true,'higher stress'],['energy',false,'lower energy'],['safety',false,'lower sense of safety'],['connection',false,'less social connection']]){
    const d=metric(recent,key,key==='safety'?4:3)-metric(base,key,key==='safety'?4:3);
    if((!inverse&&d<=-.55)||(inverse&&d>=.55))reasons.push(label);
  }
  if(events.slice(-2).some(x=>['Hearing','Trial'].includes(x.stage)))reasons.push('an upcoming high-stress justice stage');
  const safety=metric(recent,'safety',4),stress=metric(recent,'stress',3);
  let band='Stable';
  if(safety<=2||stress>=4.6||delta<=-25)band='Consider additional support';
  else if(delta<=-14)band='Needs attention';
  else if(delta<=-6)band='Slight change';
  if(!reasons.length)reasons.push('recent check-ins are close to the personal baseline');
  const helping=[];
  if(metric(recent,'sleep')-metric(base,'sleep')>=.45)helping.push('sleep improved');
  if(metric(recent,'energy')-metric(base,'energy')>=.45)helping.push('energy improved');
  return{score,baselineScore:baseScore,delta,band,reasons,helping,disclaimer:'Wellbeing reflection only; not a medical diagnosis.'};
}
function clamp(v,min,max){return Math.max(min,Math.min(max,v))}
module.exports={compute,snapshot,moodValue};
