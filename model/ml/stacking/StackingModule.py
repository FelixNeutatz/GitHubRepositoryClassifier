# -*- coding: utf-8 -*-
import abc

from ml.config import Config
from ml.util import *
from ml.myio import *


class StackingModule:
    __metaclass__ = abc.ABCMeta

    def __init__(self, max_samples_per_category, dev_size, test_size):
      self.max_samples_per_category = max_samples_per_category
      self.dev_size = dev_size
      self.test_size = test_size
      self.X1 = None
      self.y1 = None
      self.id1 = None
      self.X2 = None
      self.y2 = None
      self.id2 = None
      self.X3 = None
      self.y3 = None
      self.id3 = None
      self.schema = None
      self.clf = None

    def create_meta_features(self, path):
        X,y = self.transform(path)
        return self.predict_proba(X),y

    def run(self):
        self.load_data()
        self.preprocess()
        self.train()

    def load_data_from_frames(self, path_train, category_frames):
      print self.name, "- Shapes:", str([f.shape for f in category_frames])
      self.schema = get_schema(Config.get2(path_train))
      frame12, frame3 = split_train_test(category_frames, test_size=self.test_size)
      frame1, frame2 = split_train_test([frame12], test_size=self.dev_size)

      self.id1 = get_id_from_frame(frame1)
      self.id2 = get_id_from_frame(frame2)
      self.id3 = get_id_from_frame(frame3)

      # delete repo url from features
      mask = np.asarray(np.ones((1, frame1.shape[1]), dtype=bool))[0]
      mask[0] = False
      mat1, mat2 = dataframe_to_numpy_matrix(frame1, frame2, mask)
      mat3 = dataframe_to_numpy_matrix_single(frame3, mask)
      self.X1, self.y1 = split_target_from_data(mat1)
      self.X2, self.y2 = split_target_from_data(mat2)
      self.X3, self.y3 = split_target_from_data(mat3)

    @abc.abstractmethod
    def load_data(self):
        """Method documentation"""
        return


    @abc.abstractmethod
    def preprocess(self):
        """Method documentation"""
        return

    @abc.abstractmethod
    def preprocess(self):
        """Method documentation"""
        return

    @abc.abstractmethod
    def train(self):
        """Method documentation"""
        return

    @abc.abstractmethod
    def test(self):
        """Method documentation"""
        return

    @abc.abstractmethod
    def test_path(self, path):
        """Method documentation"""
        return

    @abc.abstractmethod
    def predict_proba(self, X):
        """Method documentation"""
        return
