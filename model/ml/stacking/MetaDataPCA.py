# -*- coding: utf-8 -*-

from sklearn.decomposition import PCA
from ml.stacking.MetaDataModule import MetaDataModule


class MetaDataPCA(MetaDataModule):
    name = "MetaDataPCA"

    def __init__(self, max_samples_per_category=210, dev_size=0.5, test_size=0.1, n_components=100):
        super(MetaDataPCA, self).__init__(max_samples_per_category, dev_size, test_size)
        self.n_components = n_components
        self.run()

    def train(self):
        self.clf = PCA(n_components=self.n_components)
        self.clf.fit(self.X1)

    def test(self):
        print "not implemented"

    def test_path(self, path_a="attachmentA.feature.extraction.output.path"):
        print "not implemented"

    def predict_proba(self, X):
        y = self.clf.transform(X)
        return y
