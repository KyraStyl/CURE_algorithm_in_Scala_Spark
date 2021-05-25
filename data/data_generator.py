#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
This script will generate 2d points for scalability test of CURE
"""
import sys
from sklearn.datasets import make_blobs
import matplotlib.pyplot as plt

if __name__=="__main__":
    points = int(sys.argv[1])
    number_of_clusters = int(sys.argv[2])
    working_directory="datasets/generated_datasets/"
    filename="data_"+str(points)+"_"+str(number_of_clusters)
    #create dataset and ground truth
    X,y=make_blobs(n_samples=points,centers=number_of_clusters,n_features=2,cluster_std=0.5)
    plt.scatter([i[0] for i in X],[i[1] for i in X])
    with open(working_directory+filename+".txt","w") as f:
        for point in X:
            f.write(",".join(list(map(str,point)))+"\n")
    with open(working_directory+filename+".ground_truth","w") as f:
        for label in y:
            f.write(str(label)+"\n")

