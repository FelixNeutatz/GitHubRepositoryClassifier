# -*- coding: utf-8 -*-

from sklearn.decomposition import TruncatedSVD
from ml.stacking.TextDataModule import TextDataModule


class TextDataTruncatedSVD(TextDataModule):
    name = "TextDataTruncatedSVD"

    def __init__(self, max_samples_per_category=210, dev_size=0.5, test_size=0.1, n_components=3):
        super(TextDataTruncatedSVD, self).__init__(max_samples_per_category, dev_size, test_size)
        self.n_components = n_components
        self.run()

    def train(self):
        self.clf = TruncatedSVD(n_components=self.n_components)
        self.clf.fit(self.X1)

    def test(self):
        print "not implemented"

    def test_path(self, path_a="attachmentA.feature_text.extraction.output.path"):
        print "not implemented"

    def predict_proba(self, X):
        y = self.clf.transform(X)
        return y
