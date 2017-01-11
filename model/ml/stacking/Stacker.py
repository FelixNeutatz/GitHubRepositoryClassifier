# -*- coding: utf-8 -*-

from sklearn.linear_model import LogisticRegression
from sklearn.multiclass import OneVsRestClassifier

from ml.util import *
from ml.myio import *
from ml.visualize import validate
from sklearn import manifold
from ml.visualize import plot


class Stacker:

    def __init__(self):
        self.module_list = []
        self.X = None
        self.y = None
        self.id = None
        self.X_test = None
        self.y_test = None
        self.clf = None
        self.min_proba = None

    def add(self, stacking_module):
        self.module_list.append(stacking_module)

    def transform(self, path_list):
        y_test = None
        feature_list = []
        for i in range(0, len(self.module_list)):
            f, y_test = self.module_list[i].create_meta_features(path_list[i])
            feature_list.append(f)
        X_test = np.concatenate(feature_list, axis=1)
        return X_test, y_test

    def build(self):
        self.build_train()
        self.build_test()

    def build_train(self):
        y = self.module_list[0].y2
        id2 = self.module_list[0].id2
        feature_list = []
        for module in self.module_list:
            assert len(module.y2) == len(y)
            assert np.all(module.id2 == id2)
            assert sum(module.y2 - y) == 0
            f = module.predict_proba(module.X2)
            feature_list.append(f)
        self.X = np.concatenate(feature_list, axis=1)
        self.y = y
        self.id = id2

    def build_test(self):
        y = self.module_list[0].y3
        id3 = self.module_list[0].id3
        feature_list = []
        for module in self.module_list:
            assert len(module.y3) == len(y)
            assert np.all(module.id3 == id3)
            assert sum(module.y3 - y) == 0
            f = module.predict_proba(module.X3)
            feature_list.append(f)
        self.X_test = np.concatenate(feature_list, axis=1)
        self.y_test = y

    def train(self):
        self.clf = OneVsRestClassifier(LogisticRegression(class_weight='balanced'))
        self.clf = fit_cv(self.clf, self.X, self.y, {'estimator__C': np.logspace(-4, 4, 9)})
        # calculate min max probability for other categorization
        probas = self.clf.predict_proba(self.X)
        assert np.sum(np.argmax(probas, axis=1) - self.clf.predict(self.X)) == 0
        max_proba_per_sample = np.max(probas, axis=1)
        self.min_proba = np.min(max_proba_per_sample)

    def _test(self, X, y, with_other):
        y_pred = self.clf.predict(X) if not with_other else self.predict_other(X)
        validate(y, y_pred, with_other)

    def test(self, with_other=False):
        self._test(self.X_test, self.y_test, with_other)

    def visualize_by_tsne(self):
        tsne = manifold.TSNE(n_components=2, init='random',  # method='barnes_hut',
                             random_state=0, learning_rate=1000, n_iter=1000,
                             verbose=2)
        Y = tsne.fit_transform(self.X)
        plot(Y, self.y, self.id)

    def test_path(self, path_list, with_other=False):
        X_a, y_a = self.transform(path_list)
        self._test(X_a, y_a, with_other)

    def test_all_modules_alone(self, path_list=None):
        for i in range(0, len(self.module_list)):
            print self.module_list[i].name
            mod = self.module_list[i]
            print mod.name, "Test"
            mod.test()
            if path_list is not None:
              print mod.name, "Test Attachement A"
              mod.test_path(path_list[i])

    def predict_other(self, X, other_value=6):
        # predict probabilities on data set X
        probas = self.clf.predict_proba(X)
        # get predicted labels by max probabilities
        y_pred = np.argmax(probas, axis=1)
        # get probabilities of the predicted label for each sample
        probas_max = probas[np.arange(probas.shape[0]), y_pred]
        # find label probabilities that are below "other" threshold
        other_args = np.where(probas_max < self.min_proba)
        # predict these as "other"
        y_pred[other_args] = other_value
        return y_pred
