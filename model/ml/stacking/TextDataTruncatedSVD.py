# -*- coding: utf-8 -*-

from sklearn.decomposition import TruncatedSVD
from ml.stacking.TextDataModule import TextDataModule


class TextDataTruncatedSVD(TextDataModule):
    name = "TextDataTruncatedSVD"

    def __init__(self, max_samples_per_category=210, test_size=0.5, n_components=3):
        self.max_samples_per_category = max_samples_per_category
        self.test_size = test_size
        self.n_components = n_components

        self.scaler = None

        self.X1 = None
        self.y1 = None
        self.X2 = None
        self.y2 = None
        self.schema = None

        self.clf = None

        self.run()

    def train(self):
        self.clf = TruncatedSVD(n_components=self.n_components)
        self.clf.fit(self.X1)

    def test(self, X, y):
        print "not implemented"

    def test_path(self, path_a="attachmentA.feature_text.extraction.output.path"):
        print "not implemented"

    def predict_proba(self, X):
        y = self.clf.transform(X)
        return y