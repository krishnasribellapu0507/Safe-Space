from distress_score import score
from explainable_ai import explain
if __name__=='__main__':
    c=['okay','worried','low'];stage='Hearing'
    s,b=score(c,stage)
    print({"score":s,"band":b,"why":explain(c,stage),"disclaimer":"prototype signal, not a diagnosis"})
