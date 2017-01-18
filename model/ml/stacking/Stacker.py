# -*- coding: utf-8 -*-

from sklearn.linear_model import LogisticRegression
from sklearn.multiclass import OneVsRestClassifier
from sklearn.preprocessing import StandardScaler
from sklearn.svm import SVC

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
        self.scaler_stacker = StandardScaler()

    def add(self, stacking_module):
        self.module_list.append(stacking_module)

    def transform(self, dir_list):
        y = None
        feature_list = []
        for i in range(0, len(self.module_list)):
            f, y = self.module_list[i].create_meta_features(dir_list[i])
            feature_list.append(f)
        X = np.concatenate(feature_list, axis=1)
        X = self.scaler_stacker.transform(X)
        return X, y

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
        self.scaler_stacker.fit_transform(self.X)
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
        self.X_test = self.scaler_stacker.transform(self.X_test)
        self.y_test = y

    def train(self):
        # self.clf = OneVsRestClassifier(LogisticRegression(class_weight='balanced'))
        # self.clf = fit_cv(self.clf, self.X, self.y, {'estimator__C': np.logspace(-4, 4, 9)})
        self.clf = OneVsRestClassifier(SVC(class_weight='balanced', probability=True))
        params = {'estimator__kernel': ['linear'], 'estimator__C': np.logspace(-4, 4, 9)}
        self.clf = fit_cv(self.clf, self.X, self.y, params)
        # calculate min max probability for other categorization
        probas = self.clf.predict_proba(self.X)
        # assert np.sum(np.argmax(probas, axis=1) - self.clf.predict(self.X)) == 0
        max_proba_per_sample = np.max(probas, axis=1)
        self.min_proba = np.min(max_proba_per_sample)

    def _test(self, X, y, with_other):
        y_pred = self.clf.predict(X) if not with_other else self.predict_other(X)
        validate(y, y_pred, with_other)

    def test_train(self):
        self._test(self.X, self.y, False )

    def test(self, with_other=False):
        self._test(self.X_test, self.y_test, with_other)

    def test_modules(self):
        for mod in self.module_list:
            print mod.name
            mod.test()

    def visualize_by_tsne(self):
        tsne = manifold.TSNE(n_components=2, init='random',  # method='barnes_hut',
                             random_state=0, learning_rate=1000, n_iter=1000,
                             verbose=2)
        Y = tsne.fit_transform(self.X)
        plot(Y, self.y, self.id)

    def test_dirs(self, dir_list, with_other=False):
        X_a, y_a = self.transform(dir_list)
        X_a = self.scaler_stacker.transform(X_a)
        self._test(X_a, y_a, with_other)

    def test_modules_dirs(self, dir_list):
        for i in range(0, len(self.module_list)):
            mod = self.module_list[i]
            print mod.name
            mod.test_dir(dir_list[i])

    def predict_dirs(self, dirs):
        X, y = self.transform(dirs)
        return self.clf.predict_proba(X)

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

    def predict_other_dirs(self, dirs):
        X = self.transform(dirs)
        return self.predict_other(X)
