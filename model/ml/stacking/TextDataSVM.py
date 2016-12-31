# -*- coding: utf-8 -*-

from sklearn.linear_model import SGDClassifier
from ml.stacking.TextDataModule import TextDataModule
from ml.util import *


class TextDataSVM(TextDataModule):
    name = "TextDataSVM"

    def __init__(self, max_samples_per_category = 210, test_size=0.5):
        self.max_samples_per_category = max_samples_per_category
        self.test_size = test_size

        self.X1 = None
        self.y1 = None
        self.X2 = None
        self.y2 = None
        self.schema = None

        self.pipeline = None

        self.clf = None

        self.run()

    def train(self):
        self.clf = SGDClassifier(loss='log', alpha=0.0001, penalty='elasticnet')
        self.clf = self.clf.fit(self.X1, self.y1)

    def test(self, X, y):
        y_pred = self.clf.predict(X)
        print confusion_matrix(y, y_pred)
        print f1_score(y, y_pred, average='weighted')

    def test_path(self, path_a="attachmentA.feature_text.extraction.output.path"):
        X,y = self.transform(path_a)
        self.test(X,y)

    def predict_proba(self, X):
        y = self.clf.predict_proba(X)
        return y