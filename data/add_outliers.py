#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Tue May 25 10:05:26 2021

@author: mavroudo
"""
import random
import matplotlib.pyplot as plt
from sklearn.cluster import KMeans
import numpy as np
from statistics import mean, stdev
import sys


def get_empty_cells(file):
    data=[]
    with open(file,"r") as f:
        for line in f:
            x,y=line.strip().split(",")
            data.append([float(x),float(y)])
    xs = [i[0] for i in data]
    ys = [i[1] for i in data]
    minx,maxx = min(xs),max(xs)
    miny,maxy = min(ys),max(ys)
    rangex=maxx-minx   
    rangey=maxy-miny
    cells=[]
    for size in range(5,20):
        numx=[minx+(rangex/size)*i  for i in range(1,size)]
        numy=[miny+(rangey/size)*i  for i in range(1,size)]
        nums=[[0 for _ in range(size)] for _ in range(size)]
        for d in data:
            indexx=0
            for x in numx:
                if x<d[0]:
                    indexx+=1
                else:
                    break
            indexy=0
            for y in numy:
                if y<d[1]:
                    indexy+=1
                else:
                    break
            nums[indexx][indexy]+=1
        for x,a in enumerate(nums):
            for y,b in enumerate(a): #b is the number x,y are the indexes
                if b==0:
                    xvalue = minx
                    if x >0:
                        xvalue=numx[x-1]
                    yvalue = miny
                    if y >0:
                        yvalue=numy[y-1]
                    cells.append([xvalue,yvalue,rangex/size,rangey/size,size])
    return cells,xs,ys,data



if __name__ == "__main__":
    filename =sys.argv[1]
    microclusters = int(sys.argv[2])
    number_outliers= int(sys.argv[3])
    #file = "datasets/data_1/data1.txt"
    cells,xs,ys,data = get_empty_cells(filename)
    #create clusters kmeans
    X=np.array(data)
    kmeans=KMeans(n_clusters=microclusters, init='k-means++',precompute_distances=True,verbose=1,n_jobs=-1,algorithm="elkan").fit(X)
    distances=[min([np.linalg.norm(d-c) for c in kmeans.cluster_centers_]) for d in np.array(data)]
    mean_distance=mean(distances)
    stdev_distance=stdev(distances)
    # create outliers in empty cells deviating more than 4 times std from the others
    i=0
    outliers=[]
    while len(outliers)<number_outliers and i<len(cells):
        c=cells[i]
        outlier=[random.gauss(c[0]+c[2],c[2]/8),random.gauss(c[1]+c[3],c[3]/8)]
        min_dist= min([np.linalg.norm(np.array(outlier)-c) for c in kmeans.cluster_centers_])
        if min_dist >= mean_distance+4*stdev_distance:
            outliers.append(outlier)
        i+=1
    
    for outlier in outliers:
        xs.append(outlier[0])
        ys.append(outlier[1])
    
    plt.scatter(xs,ys)
    plt.savefig("with_outliers.png")
    
    with open("data_with_outliers.txt","w") as f:
        for d in zip(xs,ys):
            f.write(str(d[0])+","+str(d[1])+"\n")
    
    with open("outliers.txt","w") as f:
        for out in outliers:
            f.write(str(out[0])+","+str(out[1])+"\n")



 



             
#random point in a cell

#cell [xvalue,yvalue,rangex/size,rangey/size,size]


                
                
                