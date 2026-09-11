POS={"calm","better","good","hopeful","supported","okay"}
NEG={"worried","overwhelmed","low","angry","unsafe","afraid","stress"}
def classify(text):
    words=set(text.lower().replace("."," ").split())
    score=len(words&POS)-len(words&NEG)
    return {"label":"positive" if score>0 else "negative" if score<0 else "neutral","score":score}
