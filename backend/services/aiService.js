const {compute}=require('./trendEngine');module.exports={analyse:(db,userId='demo')=>compute(db.moods.filter(x=>x.userId===userId),db.journey.filter(x=>x.userId===userId))};
