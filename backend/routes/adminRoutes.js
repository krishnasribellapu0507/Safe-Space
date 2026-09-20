const r=require('express').Router(),c=require('../controllers/adminController');
const {requireRole}=require('../middleware/roleMiddleware');
r.use(requireRole('administrator','counsellor','support-worker','supervisor'));
r.get('/overview',c.overview);
r.get('/alerts',c.alerts);
r.get('/support-requests',c.supportRequests);
r.get('/appointments',c.appointments);
r.get('/audit',c.auditLog);
module.exports=r;
