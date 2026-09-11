HARD={"low":12,"worried":10,"angry":9,"overwhelmed":14,"confused":6}
def score(checkins, justice_stage=None, engagement_drop=False):
    value=25+sum(HARD.get(str(x).lower(),0) for x in checkins[-5:])
    if justice_stage in {"Hearing","Trial"}:value+=10
    if engagement_drop:value+=8
    value=min(100,value)
    band="support recommended" if value>=65 else "needs attention" if value>=42 else "stable"
    return value,band
