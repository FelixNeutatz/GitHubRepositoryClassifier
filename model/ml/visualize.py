# -*- coding: utf-8 -*-

import operator

from sklearn.metrics import confusion_matrix
from sklearn.metrics import f1_score


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
        chart += str(key).replace ("_", "")
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
