# -*- coding: utf-8 -*-

import operator

from sklearn.metrics import confusion_matrix
from sklearn.metrics import f1_score

import matplotlib.pyplot as plt
import matplotlib.lines as mlines
import colorsys
import numpy as np
from ml.PointBrowser import PointBrowser

def plot(Y, train_y, train_repo_names):
    category_list = {0: "DATA", 1: "EDU", 2: "HW", 3: "DOCS", 4: "DEV", 5: "WEB"}

    fig, (ax) = plt.subplots(1, 1)

    plts = []
    labels = []

    color_list = np.zeros((Y[:, 0].size, 3))
    for i in range(0,len(category_list)):
        mask = np.where(train_y == i)
        color_float = float(i) / 6.0
        rgb = colorsys.hsv_to_rgb(color_float, 1.0, 1.0)
        color_list[mask] = rgb

        plts.append(mlines.Line2D([], [], color=rgb, markersize=15, label=category_list[i]))
        labels.append(category_list[i])

    ax.scatter(Y[:, 0], Y[:, 1], c=color_list, picker=5)

    ax.legend(plts,labels,
               scatterpoints=1,
               loc='lower left',
               ncol=3,
               fontsize=8)

    browser = PointBrowser(fig, ax, Y[:, 0], Y[:, 1], train_repo_names.tolist())

    fig.canvas.mpl_connect('pick_event', browser.onpick)
    fig.canvas.mpl_connect('key_press_event', browser.onpress)

    plt.show()


def validate(y, y_pred):
    category_list = ["DATA", "EDU", "HW", "DOCS", "DEV", "WEB"]

    c_matrix = confusion_matrix(y, y_pred)
    confusion_matrix_to_latex(c_matrix, category_list)

    print confusion_matrix(y, y_pred)
    print f1_score(y, y_pred, average='weighted')

def dict_to_bar_chart(dictionary, y_label, width=8, height=4, n=20):

    sorted_dict = sorted(dictionary.items(), key=operator.itemgetter(1), reverse=True)

    chart = "\\begin{tikzpicture}\n" + \
    "\\begin{axis}[\n" + \
    "width=" + str(width) + "cm,\n" + \
    "height=" + str(height) + "cm,\n" + \
    "xticklabels={"

    i = 1
    for key, value in sorted_dict:
        chart += str(key).replace ("_", "").replace("Average", "Avg")
        if i < n:
            chart += ","
        else:
            break
        i += 1

    chart += "},\nxtick={1,...," + str(n) +"},\n" + \
    "x tick label style={rotate=90},\n" + \
	"ylabel=" + y_label + ",\n" + \
	"enlargelimits=0.05,\n" + \
	"ybar,\n" + \
    "]\n" + \
    "\\addplot\n" + \
	"coordinates {\n"

    i = 1
    for key, value in sorted_dict:
        chart += "(" + str(i) + "," + str(value) + ")"
        if i < n:
            chart += " "
        else:
            break
        i += 1

    chart += "};\n" + \
    "\\end{axis}\n" + \
    "\\end{tikzpicture}\n"

    print "######"
    print chart
    print "######"

def array_to_itemlist(a, n=10):
    itemlist = "\\begin{enumerate}\n"
    for i in range(0,n):
        itemlist += "\t\\item " + a[i] + "\n"
    itemlist += "\\end{enumerate}\n"

    print "######"
    print itemlist
    print "######"



def confusion_matrix_to_latex(confusion_matrix, class_names):

    n = len(class_names)
    plot = "\\begin{tabularx}{.7\\textwidth}{c | "

    for i in range(0, n):
        plot += "c "

    plot += "| }\n"

    for i in range(0, n-1):
        plot += " & " + class_names[i].replace ("_", "")

    plot +=" & \\multicolumn{1}{c}{"+ class_names[n-1] +"} \n" + \
    "\\hhline{--"

    bars = ""
    for i in range(0, n):
        bars += "-"

    plot += bars + "}\n"

    for y in range(0, n):
        plot += class_names[y] + " & "
        for x in range(0, n):
            plot += str(confusion_matrix[y,x]) + " "

            if y == x:
                plot += "\\cellcolor[gray]{.8}"

            plot += "& "
        plot += "\n"

    plot += "\\hhline{~" + bars + \
    "}\n" + \
    "\\end{tabularx}"

    print "######"
    print plot
    print "######"
