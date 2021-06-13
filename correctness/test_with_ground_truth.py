#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""

"""
import sys
from collections import Counter

def init_zeros(size):
    return [0 for _ in range(size)]

def calc_tp_fp_fn(d2, d1, matches):
    tp, fp, fn = init_zeros(5),init_zeros(5),init_zeros(5)
    for i,true_cl in enumerate(d2):
        clID = d1[i]
        lists = matches[true_cl]
        for l in lists:
            if l[0] == clID:
                tp[i] = l[1]
            else:
                fp[i]+= l[1]
                if l[0] in d1:    
                    pos = d1.index(l[0])
                    fn[pos] += l[1]
      #just for debug          
    #for i in range(5):
     #   print(d2[i],", tp = ",tp[i],", fp = ",fp[i], ", fn = ", fn[i])
    return tp, fp, fn

# calculates the f1_score for a multiclass classification problem
def calc_f1_score(tp, fp, fn):
    # tp, fp, fn are lists
    # precision = tp / (tp + fp)
    #micro ap = tp1 + tp2 + ... / (tp1 + tp2 +...+fp1 +fp2 +...)
    #macro ap = pr1 + pr2 + pr3 +.. /5
    
    # recall = tp / (tp + fn)
    prec = init_zeros(5)
    rec = init_zeros(5)
    for i in range(len(tp)):
        num = tp[i]
        denprec = tp[i]+ fp[i]
        denrec = tp[i] + fn[i]
        if denprec != 0:
            prec[i] = num/denprec
        if denrec != 0:
            rec[i] = num/denrec
    
    tprec = sum(prec)/len(prec)
    trec = sum(rec)/len(rec)
    
    f1 = 2*(tprec*trec)/(tprec+trec)
    return tprec, trec, f1

def find_score(points,true_labels):
    d = {x[0]+","+x[1]:[x[2]] for x in points} 
    for x in true_labels:
        if x[0]+","+x[1] in d:
            d[x[0]+","+x[1]].append(x[2])
    
    pairs = [d[i] for i in d]
    p=Counter([x[0]+","+x[1] for x in pairs])
    match=dict()
    for pair in p:
        t,s=pair.split(",")
        if s not in match:
            match[s]=[]
        match[s].append([t,p[pair]])
    
    #d1 {clID} --> set null
    #d2 {true_clID} --> 
 
    d1 = []
    d2 = []
    # match:[[clID,numPoints],...]
    # maxP [cl, (clID, numPoints)]
    for i in range(5):
        maxP = None
        for cl in match:
            if cl in d2:
                continue
            for l in match[cl]:
                if l[0] in d1:
                    continue
                if maxP is None:
                    maxP = [cl,l]
                else:
                    if l[1]>maxP[1][1]:
                        maxP = [cl,l]
        
        if maxP is not None:
            d1.append(maxP[1][0])
            d2.append(maxP[0])
    
    tp, fp, fn = calc_tp_fp_fn(d2, d1, match)
    
    return calc_f1_score(tp, fp, fn)
    

if __name__ == "__main__":
    file = sys.argv[1] #has format x,y,label
    ground_truth = sys.argv[2] #has format labels
    
    #file="demo_data.txt"
    #ground_truth="demo_gt.txt"
    
    #the ground truth has to be checked as sets and not labels 1 by 1
    points=[]
    with open(file,"r") as f:
        for line in f:
            points.append(line.strip().split(","))
    
    true_labels=[]
    with open(ground_truth,"r") as f:
        for line in f:
            true_labels.append(line.strip().split(","))
    #k=len(list(set(true_labels)))        
    prec, rec, f1 = find_score(points,true_labels)
    print("precision = ",prec)
    print("recall = ", rec)
    print("f1 = ",f1)
    
    
    
