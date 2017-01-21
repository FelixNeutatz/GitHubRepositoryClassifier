# -*- coding: utf-8 -*-

import abc
from sklearn.preprocessing import StandardScaler

from ml.config import Config
from ml.myio import *
from ml.stacking.StackingModule import StackingModule
from ml.util import *


class MetaDataModule(StackingModule):
    __metaclass__ = abc.ABCMeta

    def __init__(self, max_samples_per_category, dev_size, test_size, labeled_data_filter=None):
        super(MetaDataModule, self).__init__(max_samples_per_category, dev_size, test_size)
        self.labeled_data_filter = labeled_data_filter
        self.scaler = None

    def load_data(self):
        path_train = "feature.extraction.output.path"
        dir_train = Config.get2(path_train)
        category_frames = read(dir_train, self.max_samples_per_category)
        if self.labeled_data_filter is not None:
            category_frames = filter_frames(category_frames, self.labeled_data_filter)
        self.load_data_from_frames(path_train, category_frames)

    def transform(self, dir_):
        frame = concat(read(dir_, self.max_samples_per_category))
        mask = np.asarray(np.ones((1, frame.shape[1]), dtype=bool))[0]
        mask[0] = False
        mat = dataframe_to_numpy_matrix_single(frame, mask)
        X, y = split_target_from_data(mat)
        X_transformed = self.scaler.transform(X)
        return X_transformed, np.array(y).tolist()

    def preprocess(self):
        # normalize
        self.scaler = StandardScaler()
        self.X1 = self.scaler.fit_transform(self.X1)
        self.X2 = self.scaler.transform(self.X2)
        if self.X3.shape[0] > 0:
            self.X3 = self.scaler.transform(self.X3)
