module.exports={
  requireFields:(...fields)=>(req,res,next)=>{
    const missing=fields.filter(k=>req.body?.[k]===undefined||req.body?.[k]===null||req.body?.[k]==='');
    return missing.length?res.status(400).json({error:'Missing fields',fields:missing}):next();
  },
  maxStringLength:(field,max)=>(req,res,next)=>{
    const value=req.body?.[field];
    if(value!==undefined&&(typeof value!=='string'||value.length>max))return res.status(400).json({error:`${field} must be text up to ${max} characters`});
    next();
  }
};
