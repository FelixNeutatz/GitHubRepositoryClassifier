# -*- coding: utf-8 -*-

from sklearn.linear_model import LogisticRegression
from sklearn.multiclass import OneVsRestClassifier

from ml.util import *
from ml.myio import *

class Stacker:

    def __init__(self):
        self.module_list = []

        self.X = None
        self.y = None
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
        length = len(self.module_list[0].y2)
        feature_list = []
        for module in self.module_list:
            assert len(module.y2) == length
            f = module.predict_proba(module.X2)
            feature_list.append(f)
        self.X = np.concatenate(feature_list, axis=1)
        self.y = self.module_list[0].y2

    def train(self):
        self.clf = OneVsRestClassifier(LogisticRegression(random_state=42))
        self.clf = fit_cv(self.clf, self.X, self.y, {'estimator__C': np.logspace(-5, 5, 11)})
        #print("Best parameters are %s with a score of %0.2f" % (self.clf.best_params_, self.clf.best_score_))

    def test(self, X, y):
        y_pred = self.clf.predict(X)
        print confusion_matrix(y, y_pred)
        print f1_score(y, y_pred, average='weighted')

    def test_all_modules_alone(self, path_list):
        for i in range(0, len(self.module_list)):
            print self.module_list[i].name
            self.module_list[i].test_path(path_list[i])