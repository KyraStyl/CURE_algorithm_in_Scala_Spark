import matplotlib.pyplot as plt
from IPython import get_ipython

def get_data():
    f = open("input.txt", 'r')
    points = list()
    for line in f:
        cols = line.strip().split(',')
        try:
            if cols[0]!='' or cols[1]!='':
                points.append([float(cols[0]), float(cols[1])])
        except:
            continue
    return points

        
def graph(points):
    get_ipython().run_line_magic('matplotlib','qt')
    fig = plt.figure()
    ax = fig.add_subplot(1, 1, 1)
    x = list()
    y = list()
    for i in range(len(points)):
        x.append(points[i][0])
        y.append(points[i][1])
    '''m1 = max(x)
    m2 = min(x)
    m3 = max(y)
    m4 = min(y)
    x = [(xi-m2)/(m1-m2) for xi in x]
    y = [(yi-m4)/(m3-m4) for yi in y]
    '''
    ax.scatter(x,y)
    plt.title('plot')
    plt.ylabel('y')
    plt.xlabel('x')
    plt.show()

graph(get_data())
