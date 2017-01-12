# -*- coding: utf-8 -*-
from sklearn.linear_model import LogisticRegression
from sklearn.linear_model import SGDClassifier
import scipy.sparse
from sklearn.multiclass import OneVsRestClassifier

from ml.stacking.NameDataModule import NameDataModule
from ml.util import *
from ml.visualize import validate


class NameDataSVM(NameDataModule):
    name = "NameDataSVM"

    def __init__(self, max_samples_per_category = 210, dev_size=0.5, test_size=0.1, labeled_data_filter=None):
        super(NameDataSVM, self).__init__(max_samples_per_category, dev_size, test_size, labeled_data_filter)
        self.run()

    def train(self):
        # self.clf = SGDClassifier(loss='log', penalty='elasticnet', class_weight="balanced")  # alpha=0.0001
        # params = {'alpha': np.logspace(-6, -2, 5)}
        self.clf = OneVsRestClassifier(LogisticRegression(class_weight='balanced'))
        params = {'estimator__C': np.logspace(-4, 4, 9)}
        # self.clf = self.clf.fit(self.X1, self.y1)
        self.clf = fit_cv(self.clf, self.X1, self.y1, params)

    def retrain(self):
        X = scipy.sparse.vstack((self.X1, self.X2))
        y = np.concatenate((self.y1, self.y2))
        self.clf = fit_cv(self.clf, X, y, {'alpha': np.logspace(-6, -2, 5)})

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
