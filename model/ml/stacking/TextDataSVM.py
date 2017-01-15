# -*- coding: utf-8 -*-
from sklearn.linear_model import LogisticRegression
from sklearn.linear_model import SGDClassifier
from sklearn.multiclass import OneVsRestClassifier
import scipy.sparse
from sklearn.svm import SVC

from ml.stacking.TextDataModule import TextDataModule
from ml.util import *
from ml.visualize import validate


class TextDataSVM(TextDataModule):
    name = "TextDataSVM"

    def __init__(self, max_samples_per_category=210, dev_size=0.5, test_size=0.1, labeled_data_filter=None):
        super(TextDataSVM, self).__init__(max_samples_per_category, dev_size, test_size, labeled_data_filter)
        self.run()

    def train(self):
        # self.clf = SGDClassifier(loss='log', penalty='elasticnet')  # alpha=0.0001
        # params = {'alpha': np.logspace(-6, -2, 5)}
        # self.clf = OneVsRestClassifier(LogisticRegression(class_weight='balanced'))
        # self.clf = fit_cv(self.clf, self.X, self.y, {'estimator__C': np.logspace(-4, 4, 9)})
        self.clf = OneVsRestClassifier(SVC(class_weight='balanced', probability=True))
        params = {'estimator__kernel': ['linear'], 'estimator__C': np.logspace(-4, 4, 9)}
        self.clf = fit_cv(self.clf, self.X1, self.y1, params)
        # self.clf = self.clf.fit(self.X1, self.y1)

    def retrain(self):
        X = scipy.sparse.vstack((self.X1, self.X2))
        y = np.concatenate((self.y1, self.y2))
        params = {'estimator__kernel': ['linear'], 'estimator__C': np.logspace(-4, 4, 9)}
        self.clf = fit_cv(self.clf, X, y, params)

    def _test(self, X, y):
        y_pred = self.clf.predict(X)
        validate(y, y_pred)

    def test(self):
        self._test(self.X3, self.y3)

    def test_dir(self, dir_):
        X, y = self.transform(dir_)
        self._test(X, y)

    def predict_proba(self, X):
        y = self.clf.predict_proba(X)
        return y
