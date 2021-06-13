#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Just to visualize the outcome of the CURE algorithm. The file should have format
x_coordinate,y_cordinate,int label (in which cluster this belongs to)
"""
import matplotlib.pylab as plt
import numpy as np
import sys
def get_data(file):
    f = open(file, 'r')
    points = list()
    for line in f:
        cols = line.strip().split(',')
        try:
            if cols[0]!='' and cols[1]!='' and cols[2]!="":
                points.append([float(cols[0]), float(cols[1]), int(cols[2])])
        except:
            continue
    return points

def plot_clusters(points): 
    fig = plt.figure()
    ax = fig.add_subplot(1, 1, 1) 
    for i in np.unique([k[2] for k in points]):
        data=[point for point in points if point[2] == i]
        ax.scatter([k[0] for k in data] , [k[1] for k in data] , label = i)
    plt.title('plot')
    plt.ylabel('y')
    plt.xlabel('x')
    plt.savefig("results.png")
    

if __name__ == "__main__":
    #filename=sys.argv[1] #pass the file from command line
    filename = "../implementation/results/p16924,perc0.3,alpha0.5,repr20/points.txt"
    #filename = "points.txt"
    print("the plot will be saved in the same file as this script, with the name results.png")
    data=get_data(filename)
    plot_clusters(data)
    