import matplotlib.pyplot as plt
import os

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

def minmax_normalization(x,y):
    m1 = max(x)
    m2 = min(x)
    m3 = max(y)
    m4 = min(y)
    x_norm = [(xi-m2)/(m1-m2) for xi in x]
    y_norm = [(yi-m4)/(m3-m4) for yi in y]
    return x_norm,y_norm
        
def graph(points,filename):
    fig = plt.figure()
    ax = fig.add_subplot(1, 1, 1)
    x = list()
    y = list()
    for i in range(len(points)):
        x.append(points[i][0])
        y.append(points[i][1])
    ax.scatter(x,y)
    plt.title('plot')
    plt.ylabel('y')
    plt.xlabel('x')
    plt.savefig(filename.split('.')[0]+".png")



#find all visualizations 
directories = ["datasets/data_1/","datasets/data_2/"]
visualizes=["datasets/visualize_1/","datasets/visualize_2/"]
for d,v in zip(directories,visualizes):
    for file in os.listdir(d):
        print(d+file)
        points=get_data(d+file)
        graph(points,v+file)
    