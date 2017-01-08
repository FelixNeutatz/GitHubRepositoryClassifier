# -*- coding: utf-8 -*-

from sklearn.linear_model import SGDClassifier
from ml.stacking.TextDataModule import TextDataModule
from ml.util import *
from ml.visualize import validate


class TextDataSVM(TextDataModule):
    name = "TextDataSVM"

    def __init__(self, max_samples_per_category = 210, dev_size=0.5, test_size=0.1):
        super(TextDataSVM, self).__init__(max_samples_per_category, dev_size, test_size)
        self.run()

    def train(self):
        self.clf = SGDClassifier(loss='log', penalty='elasticnet')  # alpha=0.0001
        # self.clf = self.clf.fit(self.X1, self.y1)
        self.clf = fit_cv(self.clf, self.X1, self.y1, {'alpha': np.logspace(-6, -2, 5)})

    def _test(self, X, y):
        y_pred = self.clf.predict(X)
        validate(y, y_pred)

    def test(self):
        self._test(self.X3, self.y3)

    def test_path(self, path_a="attachmentA.feature_text.extraction.output.path"):
        X, y = self.transform(path_a)
        self._test(X, y)

    def predict_proba(self, X):
        y = self.clf.predict_proba(X)
        return y
