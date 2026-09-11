module.exports={requireFields:(...f)=>(req,res,next)=>{const m=f.filter(k=>req.body?.[k]===undefined);return m.length?res.status(400).json({error:'Missing fields',fields:m}):next()}};
