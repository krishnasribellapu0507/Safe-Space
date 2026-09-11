def baseline(scores):
    if not scores:return 50.0
    recent=scores[-7:]
    return round(sum(recent)/len(recent),2)
