exports.requireRole=(...roles)=>(req,res,next)=>{
  if(!req.user || !roles.includes(req.user.role)) return res.status(403).json({error:'You do not have permission to access this resource'});
  next();
};
