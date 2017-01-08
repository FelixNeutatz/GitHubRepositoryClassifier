# -*- coding: utf-8 -*-

from sklearn.linear_model import LogisticRegression
from sklearn.multiclass import OneVsRestClassifier

from ml.util import *
from ml.myio import *
from ml.visualize import validate


class Stacker:

    def __init__(self):
        self.module_list = []
        self.X = None
        self.y = None
        self.X_test = None
        self.y_test = None
        self.clf = None

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
        self.clf = OneVsRestClassifier(LogisticRegression(random_state=42))
        self.clf = fit_cv(self.clf, self.X, self.y, {'estimator__C': np.logspace(-5, 5, 11)})

    def _test(self, X, y):
        y_pred = self.clf.predict(X)
        validate(y, y_pred)


    def test(self):
        self._test(self.X_test, self.y_test)

    def test_path(self, path_list):
        a_X, a_y = self.transform(path_list)
        self._test(a_X, a_y)

    def test_all_modules_alone(self, path_list=None):
        for i in range(0, len(self.module_list)):
            print self.module_list[i].name
            mod = self.module_list[i]
            print mod.name, "Test"
            mod.test()
            if path_list is not None:
              print mod.name, "Test Attachement A"
              mod.test_path(path_list[i])
