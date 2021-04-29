#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""

"""
import sys
from collections import Counter

def find_score(points,true_labels):
    pairs=[]
    for p,tl in zip(points,true_labels):
        pairs.append(str(p[2])+","+str(tl))
    p=Counter(pairs)
    match=dict()
    for pair in p:
        s,t=pair.split(",")
        if s not in match:
            match[s]=[t,p[pair]]
        else:
            if match[s][1]<p[pair]:
                match[s]=[t,p[pair]]
    
    found=sum([match[s][1] for s in match])
    return found/len(points)

if __name__ == "__main__":
    file = sys.argv[1] #has format x,y,label
    ground_truth = sys.argv[2] #has format labels
    
    #the ground truth has to be checked as sets and not labels 1 by 1
    points=[]
    with open(file,"r") as f:
        for line in f:
            points.append(line.strip().split(","))
    
    true_labels=[]
    with open(ground_truth,"r") as f:
        for line in f:
            true_labels.append(line.strip())
            
    print(find_score(points,true_labels))
    
    
    
