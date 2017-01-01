# -*- coding: utf-8 -*-

from sklearn.feature_extraction.stop_words import ENGLISH_STOP_WORDS
from sklearn.feature_extraction.text import CountVectorizer, TfidfTransformer
from sklearn.linear_model import SGDClassifier
from sklearn.pipeline import Pipeline

from ml.config import Config
from ml.stacking.StackingModule import StackingModule
from ml.myio import *
from ml.util import *


class TextDataModule(StackingModule):

    def __int__(self, max_samples_per_category, dev_size, test_size):
        super(TextDataModule, self).__init__(max_samples_per_category, dev_size, test_size)
        self.pipeline = None

    def load_data(self):
        path_train = "feature_text.extraction.output.path"
        category_frames = read_native(Config.get2(path_train), self.max_samples_per_category)
        self.load_data_from_frames(path_train, category_frames)

    def transform(self, path_a = "attachmentA.feature_text.extraction.output.path"):
        a_frame = concat(read_native(Config.get(path_a), self.max_samples_per_category))
        mask = np.asarray(np.ones((1, a_frame.shape[1]), dtype=bool))[0]
        mask[0] = False
        a_mat = dataframe_to_numpy_matrix_single(a_frame, mask)
        X_test, y_test = split_target_from_data(a_mat)
        X_test_transformed = self.pipeline.transform(X_test.A1)
        return X_test_transformed, np.array(y_test).tolist()

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
        self.X3 = self.pipeline.transform(self.X3)
