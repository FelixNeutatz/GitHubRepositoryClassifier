# -*- coding: utf-8 -*-

import abc
from sklearn.preprocessing import StandardScaler

from sklearn.decomposition import PCA
from ml.config import Config
from ml.myio import *
from ml.stacking.StackingModule import StackingModule
from ml.util import *


class MetaDataModule(StackingModule):
    __metaclass__ = abc.ABCMeta

    def load_data(self):
         path_train = "feature.extraction.output.path"
         category_frames = read(Config.get2(path_train), self.max_samples_per_category)
         print "Shapes:", str([f.shape for f in category_frames])
         self.schema = get_schema(Config.get2(path_train))
         frame1, frame2 = split_train_test(category_frames, test_size=self.test_size)
         mask = np.asarray(np.ones((1, frame1.shape[1]), dtype=bool))[0]
         mask[0] = False
         mat1, mat2 = dataframe_to_numpy_matrix(frame1, frame2, mask)
         self.X1, self.y1 = split_target_from_data(mat1)
         self.X2, self.y2 = split_target_from_data(mat2)


    def transform(self, path_a = "attachmentA.feature.extraction.output.path"):
        a_frame = concat(read(Config.get(path_a), self.max_samples_per_category))
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