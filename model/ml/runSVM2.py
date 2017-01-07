# -*- coding: utf-8 -*-

import matplotlib.pyplot as plt
from sklearn.decomposition import PCA
from sklearn.multiclass import OneVsRestClassifier
from sklearn.preprocessing import StandardScaler
from sklearn.svm import SVC

from config import Config
from util import *
from myio import *

category_frames = read(Config.get("feature.extraction.output.path"), 230)
train_frame, test_frame = split_train_test(category_frames, test_size=0.3)
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

clf = OneVsRestClassifier(SVC(C=100, kernel="linear"))
clf.fit(train_x, train_y)
print("Confusion matrix on training data")
test(clf, train_x, train_y)
print("Confusion matrix on test data")
test(clf, test_x, test_y)
print("Confusion matrix on attachment a")
test(clf, attachment_a_x, attachment_a_y)

'''
C_range = np.logspace(0, 3, 5)
clf = OneVsRestClassifier(SVC(kernel="linear"))
params = {'estimator__C': C_range}
clf = fit_cv(clf, train_x, train_y, params)
print("Best parameters are %s with a score of %0.2f" % (clf.best_params_, clf.best_score_))
print("Confusion matrix on training data")
test(clf, train_x, train_y)
print("Confusion matrix on test data")
test(clf, test_x, test_y)
print("Confusion matrix on attachment a")
test(clf, attachment_a_x, attachment_a_y)

C_range = np.logspace(-4, 2, 3)
gamma_range = np.logspace(-10, 2, 3)
svm_rbf = OneVsRestClassifier(SVC(kernel="rbf"))
params = {'estimator__C': C_range, 'estimator__gamma': gamma_range}
clf = fit_cv(svm_rbf, "svm_rbf", train_x, train_y, params)
test(clf, "svm_rbf", train_x, train_y, test_x, test_y)
print("Best parameters are %s with a score of %0.2f" % (clf.best_params_, clf.best_score_))
print("Confusion matrix on training data")
test(clf, train_x, train_y)
print("Confusion matrix on test data")
test(clf, test_x, test_y)
print("Confusion matrix on attachment a")
test(clf, attachment_a_x, attachment_a_y)
'''

plot_learning_curve(clf, "SVM linear", train_x, train_y)
plt.savefig("lc_svm_lin.png")
plt.show()
