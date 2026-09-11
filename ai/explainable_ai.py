def explain(checkins, justice_stage=None, engagement_drop=False):
    reasons=[]
    hard=[x for x in checkins[-5:] if str(x).lower() in {"low","worried","angry","overwhelmed","confused"}]
    if hard:reasons.append(f"{len(hard)} recent difficult check-in(s)")
    if justice_stage in {"Hearing","Trial"}:reasons.append(f"current justice stage: {justice_stage}")
    if engagement_drop:reasons.append("reduced recent engagement")
    return reasons or ["recent signals are close to the personal demo baseline"]
