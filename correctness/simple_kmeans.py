#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Test the kmeans with k=5 and visualize the results
"""



import matplotlib.pyplot as plt
from sklearn.cluster import KMeans
import numpy as np

def get_data(file):
    f = open(file, 'r')
    points = list()
    for line in f:
        cols = line.strip().split(',')
        try:
            if cols[0]!='' or cols[1]!='':
                points.append([float(cols[0]), float(cols[1])])
        except:
            continue
    return points

def plot_clusters(points,labels): 
    fig = plt.figure()
    ax = fig.add_subplot(1, 1, 1)
    for i in np.unique(labels):
        data=[points[index] for index,l in enumerate(labels) if l==i]
        ax.scatter([k[0] for k in data] , [k[1] for k in data] , label = i)
    plt.title('plot')
    plt.ylabel('y')
    plt.xlabel('x')
    plt.show()

file="data/datasets/data_1/data1.txt"
#file="data/datasets/generated_datasets/data_1000_5.txt"
points =get_data(file)
X=np.array(points)
kmeans=KMeans(n_clusters=5, init='k-means++',precompute_distances=True,verbose=1,n_jobs=-1,algorithm="elkan").fit(X)
plot_clusters(points,kmeans.labels_)

with open("output.txt","w") as f:
    for i,point in enumerate(points):
        f.write(str(point[0])+","+str(point[1])+","+str(kmeans.labels_[i])+"\n")