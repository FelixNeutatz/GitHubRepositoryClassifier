# -*- coding: utf-8 -*-

from sklearn.feature_extraction.stop_words import ENGLISH_STOP_WORDS
from sklearn.feature_extraction.text import CountVectorizer, TfidfTransformer
from sklearn.linear_model import SGDClassifier
from sklearn.pipeline import Pipeline

from ml.config import Config
from ml.stacking.StackingModule import StackingModule
from ml.myio import *
from ml.util import *


class TextModule(StackingModule):

    def __init__(self, max_samples_per_category, dev_size, test_size, path_train, labeled_data_filter=None):
        super(TextModule, self).__init__(max_samples_per_category, dev_size, test_size)
        self.path_train = path_train
        self.labeled_data_filter = labeled_data_filter
        self.pipeline = None

    def load_data(self):
        dir_train = Config.get2(self.path_train)
        category_frames = read_native(dir_train, self.max_samples_per_category)
        if self.labeled_data_filter is not None:
            category_frames = filter_frames(category_frames, self.labeled_data_filter)
        self.load_data_from_frames(self.path_train, category_frames)

    def transform(self, dir_):
        frame = concat(read_native(dir_, self.max_samples_per_category))
        mask = np.asarray(np.ones((1, frame.shape[1]), dtype=bool))[0]
        mask[0] = False
        mat = dataframe_to_numpy_matrix_single(frame, mask)
        X, y = split_target_from_data(mat)
        X_transformed = self.pipeline.transform(X.A1)
        return X_transformed, np.array(y).tolist()

    def preprocess(self):
        my_stop_words = read_stop_words2()
        stop_words = ENGLISH_STOP_WORDS.union(my_stop_words)
        self.pipeline = Pipeline([('vect', CountVectorizer(stop_words=stop_words, max_df=0.5, ngram_range=(1, 1))),
                             ('tfidf', TfidfTransformer())])
        self.X1 = self.X1.A1
        self.X2 = self.X2.A1
        self.X3 = self.X3.A1
        self.y1 = np.array(self.y1).tolist()
        self.y2 = np.array(self.y2).tolist()
        self.y3 = np.array(self.y3).tolist()
        self.X1 = self.pipeline.fit_transform(self.X1, self.y1)
        self.X2 = self.pipeline.transform(self.X2)
        if self.X3.shape[0] > 0:
            self.X3 = self.pipeline.transform(self.X3)
