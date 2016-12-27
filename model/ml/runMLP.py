# -*- coding: utf-8 -*-

import matplotlib.pyplot as plt
from sklearn.decomposition import PCA
from sklearn.multiclass import OneVsRestClassifier
from sklearn.neural_network import MLPClassifier
from sklearn.preprocessing import StandardScaler

from config import Config
from ml import util
from myio import *

category_frames = read(Config.get("feature.extraction.output.path"), 2000)
train_frame, test_frame = split_train_test(category_frames, test_size=0.1)
mask = np.asarray(np.ones((1, train_frame.shape[1]), dtype=bool))[0]
mask[0] = False
train_matrix, test_matrix = dataframe_to_numpy_matrix(train_frame, test_frame, mask)
train_x, train_y = split_target_from_data(train_matrix)
test_x, test_y = split_target_from_data(test_matrix)
attachment_a_frames = read(Config.get("attachmentA.feature.extraction.output.path"), 150)
attachment_a_frame = concat(attachment_a_frames)
attachment_a_matrix = dataframe_to_numpy_matrix_single(attachment_a_frame, mask)
attachment_a_x, attachment_a_y = split_target_from_data(attachment_a_matrix)

scaler = StandardScaler()
train_x = scaler.fit_transform(train_x)
test_x = scaler.transform(test_x)
attachment_a_x = scaler.transform(attachment_a_x)

pca = PCA(n_components=300)
train_x = pca.fit_transform(train_x)
test_x = pca.transform(test_x)
attachment_a_x = pca.transform(attachment_a_x)

clf = OneVsRestClassifier(MLPClassifier(random_state=42))
params = {
  'estimator__alpha': np.logspace(-11, -3, 5),
  'estimator__hidden_layer_sizes': [(10,), (50,), (200,), (150, 25)]
}
# clf = util.fit_cv(clf, train_x, train_y, params)
# print("Best parameters are %s with a score of %0.2f" % (clf.best_params_, clf.best_score_))

clf = OneVsRestClassifier(MLPClassifier(verbose=0, random_state=42, alpha=1e-9, hidden_layer_sizes=(150, 25)))
clf.fit(train_x, train_y)
print("Confusion matrix on training data")
util.test(clf, train_x, train_y)
print("Confusion matrix on test data")
util.test(clf, test_x, test_y)
print("Confusion matrix on attachment a")
util.test(clf, attachment_a_x, attachment_a_y)

util.plot_learning_curve(clf, "MLP", train_x, train_y, n_jobs=4)
plt.savefig("lc_mlp.png")
plt.show()
