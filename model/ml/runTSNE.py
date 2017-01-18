# -*- coding: utf-8 -*-
from ml.PointBrowser import PointBrowser
from myio import *

from sklearn import manifold
import matplotlib.pyplot as plt
from sklearn.preprocessing import StandardScaler
import matplotlib.lines as mlines
import colorsys
from optparse import OptionParser


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
    fig.canvas.set_window_title('t-SNE Browser')

    plt.show()

def run(dir_features):
    category_frames = read(dir_features, 2000)
    category_frames = filter_frames(category_frames, get_labeled_data_filter("../../mturk/our_labels/labels.csv"))

    train_frame, test_frame = split_train_test(category_frames, test_size=0)

    train_repo_names = train_frame[train_frame.columns[0]]
    train_labels_string = train_frame[train_frame.columns[-1]]

    mask = np.asarray(np.ones((1, train_frame.shape[1]), dtype=bool))[0]
    mask[0] = False

    train_matrix, test_matrix = dataframe_to_numpy_matrix(train_frame, test_frame, mask)

    train_x, train_y = split_target_from_data(train_matrix)

    scaler = StandardScaler()
    train_x = scaler.fit_transform(train_x)

    #http://alexanderfabisch.github.io/t-sne-in-scikit-learn.html
    tsne = manifold.TSNE(n_components=2, init='random', #method='barnes_hut',
                         random_state=0, learning_rate=1000, n_iter=1000,
                         verbose=2)
    Y = tsne.fit_transform(train_x)

    plot(Y, train_y, train_repo_names)


parser = OptionParser()
parser.add_option("-i", "--input", dest="input",
                  help="input folder", metavar="FOLDER",
                  default="../../featureextraction/src/main/resources/data/features/dataMeta")

(options, args) = parser.parse_args()

input = options.input
run(input)