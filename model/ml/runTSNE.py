# -*- coding: utf-8 -*-

from myio import *

from sklearn import manifold
import matplotlib.pyplot as plt
from sklearn.preprocessing import StandardScaler

def plot (Y, train_y):
    category_list = {0: "DATA", 1: "EDU", 2: "HW", 3: "DOCS", 4: "DEV", 5: "WEB"}

    colors = ['b', 'g', 'c', 'm', 'y', 'r']

    plts = []
    labels = []
    for i in range(0,len(category_list)):
        mask = np.where(train_y == i)
        plts.append(plt.scatter(Y[mask, 0], Y[mask, 1], c=colors[i], cmap=plt.cm.Spectral))
        labels.append(category_list[i])

    plt.legend(plts,
               labels,
               scatterpoints=1,
               loc='lower left',
               ncol=3,
               fontsize=8)

    plt.show()

def run():
    category_frames = read("../../samplegeneration/src/main/resources/data/generated_29_11_16")

    train_frame, test_frame = split_train_test(category_frames, test_size=0.01)

    mask = np.asarray(np.ones((1, train_frame.shape[1]), dtype=bool))[0]
    mask[0] = False

    train_matrix, test_matrix = dataframe_to_numpy_matrix(train_frame, test_frame, mask)

    train_x, train_y = split_target_from_data(train_matrix)

    scaler = StandardScaler()
    train_x = scaler.fit_transform(train_x)

    #http://alexanderfabisch.github.io/t-sne-in-scikit-learn.html
    tsne = manifold.TSNE(n_components=2, init='random', #method='barnes_hut',
                         random_state=0, learning_rate=1000, n_iter= 1000, perplexity=
                         verbose=2)
    Y = tsne.fit_transform(train_x)

    plot(Y,train_y)


run()