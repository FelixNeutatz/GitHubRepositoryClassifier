# -*- coding: utf-8 -*-

import xgboost as xgb
from sklearn.preprocessing import StandardScaler

from ml.config import Config
from ml.myio import *
from ml.stacking.MetaDataModule import MetaDataModule
from ml.util import *
from ml.visualize import validate


class MetaDataXGB(MetaDataModule):
    name = "MetaDataXG-Boost"
    our_params = {'eta': 0.1, 'seed': 0, 'subsample': 0.8, 'colsample_bytree': 0.8,
                  'objective': 'multi:softprob', 'num_class': 6, 'max_depth': 3, 'min_child_weight': 1}

    def __init__(self, max_samples_per_category=210, dev_size=0.5, test_size=0.1, labeled_data_filter=None):
        super(MetaDataXGB, self).__init__(max_samples_per_category, dev_size, test_size, labeled_data_filter)
        self.clf = None
        self.run()

    def train(self):
        xgdmat = xgb.DMatrix(self.X1, self.y1, feature_names=self.schema)
        self.clf = xgb.train(self.our_params, xgdmat, num_boost_round=3000, verbose_eval=False)

    def retrain(self):
        X = np.concatenate((self.X1, self.X2))
        y = np.concatenate((self.y1, self.y2))
        xgdmat = xgb.DMatrix(X, y, feature_names=self.schema)
        self.clf = xgb.train(self.our_params, xgdmat, num_boost_round=3000, verbose_eval=False)

    def _test(self, X, y):
        y_pred = self.clf.predict(xgb.DMatrix(X, feature_names=self.schema))
        validate(y, y_pred.argmax(axis=1))

    def test(self):
        self._test(self.X3, self.y3)

    def test_dir(self, dir_):
        X, y = self.transform(dir_)
        self._test(X, y)

    def predict_proba(self, X):
        xgdmat = xgb.DMatrix(X, feature_names=self.schema)
        y = self.clf.predict(xgdmat)  # xgb always predicts probabilities
        return y
