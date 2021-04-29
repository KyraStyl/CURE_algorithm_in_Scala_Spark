#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Combine kmeans with hierarchical clustering for better results
"""

import matplotlib.pyplot as plt
from sklearn.cluster import KMeans, AgglomerativeClustering
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

def combine_results_clustering(kmeans,hierarchical):
    return np.array([hierarchical.labels_[i] for i in kmeans.labels_])
    
    
    
#First perform kmeans with increased k, here we use k =100   
file="data/datasets/data_1/data1.txt"
points =get_data(file)
X=np.array(points)
kmeans=KMeans(n_clusters=100, init='k-means++',precompute_distances=True,verbose=1,n_jobs=-1,algorithm="elkan").fit(X)

#Then we use hierarchical clustering to their centers
#ward minimizes the variance of the clusters being merged => better results
#average has also good performance. Single and maximum combine the 2 left clusters
hierarchical = AgglomerativeClustering(n_clusters=5, affinity='euclidean', linkage='maximum').fit(kmeans.cluster_centers_)
new_labels=combine_results_clustering(kmeans,hierarchical)

plot_clusters(points,new_labels)