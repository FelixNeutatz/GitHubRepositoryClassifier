# -*- coding: utf-8 -*-

import abc
from sklearn.preprocessing import StandardScaler

from ml.config import Config
from ml.myio import *
from ml.stacking.StackingModule import StackingModule
from ml.util import *


class MetaDataModule(StackingModule):
    __metaclass__ = abc.ABCMeta

    def __init__(self, max_samples_per_category, dev_size, test_size):
        super(MetaDataModule, self).__init__(max_samples_per_category, dev_size, test_size)
        self.scaler = None

    def load_data(self):
         path_train = "feature.extraction.output.path"
         category_frames = read(Config.get2(path_train), self.max_samples_per_category)
         self.load_data_from_frames(path_train, category_frames)

    def transform(self, dir):
        a_frame = concat(read(dir, self.max_samples_per_category))
        mask = np.asarray(np.ones((1, a_frame.shape[1]), dtype=bool))[0]
        mask[0] = False
        a_mat = dataframe_to_numpy_matrix_single(a_frame, mask)
        X_test, y_test = split_target_from_data(a_mat)
        X_test_transformed = self.scaler.transform(X_test)
        return X_test_transformed, np.array(y_test).tolist()

    def preprocess(self):
        # normalize
        self.scaler = StandardScaler()
        self.X1 = self.scaler.fit_transform(self.X1)
        self.X2 = self.scaler.transform(self.X2)
        self.X3 = self.scaler.transform(self.X3)
