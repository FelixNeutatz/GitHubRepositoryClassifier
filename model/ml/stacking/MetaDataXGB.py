# -*- coding: utf-8 -*-

import xgboost as xgb
from sklearn.preprocessing import StandardScaler

from ml.config import Config
from ml.myio import *
from ml.stacking.MetaDataModule import MetaDataModule
from ml.util import *


class MetaDataXGB(MetaDataModule):
    name = "MetaDataXG-Boost"

    def __init__(self, max_samples_per_category=210, dev_size=0.5, test_size=0.1):
        super(MetaDataXGB, self).__init__(max_samples_per_category, dev_size, test_size)
        self.clf = None
        self.run()

    def train(self):
        xgdmat = xgb.DMatrix(self.X1, self.y1, feature_names=self.schema)
        our_params = {'eta': 0.1, 'seed': 0, 'subsample': 0.8, 'colsample_bytree': 0.8,
                  'objective': 'multi:softprob', 'num_class': 6, 'max_depth': 3, 'min_child_weight': 1}
        self.clf = xgb.train(our_params, xgdmat, num_boost_round=3000, verbose_eval=False)

    def _test(self, X, y):
        y_pred = self.clf.predict(xgb.DMatrix(X, feature_names=self.schema))
        print confusion_matrix(y, y_pred.argmax(axis=1))
        print f1_score(y, y_pred.argmax(axis=1), average='weighted')

    def test(self):
        self._test(self.X3, self.y3)

    def test_path(self, path_a="attachmentA.feature.extraction.output.path"):
        X, y = self.transform(path_a)
        self._test(X, y)

    def predict_proba(self, X):
        xgdmat = xgb.DMatrix(X, feature_names=self.schema)
        y = self.clf.predict(xgdmat)  # xgb always predicts probabilities
        return y
