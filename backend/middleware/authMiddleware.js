module.exports=(req,res,next)=>{req.user={id:req.headers['x-demo-user']||'demo'};next()};
