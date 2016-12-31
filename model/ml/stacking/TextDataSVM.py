# -*- coding: utf-8 -*-

from sklearn.feature_extraction.stop_words import ENGLISH_STOP_WORDS
from sklearn.feature_extraction.text import CountVectorizer, TfidfTransformer
from sklearn.linear_model import SGDClassifier
from sklearn.pipeline import Pipeline

from ml.config import Config
from ml.stacking.StackingModule import StackingModule
from ml.myio import *
from ml.util import *


class TextDataSVM(StackingModule):
    name = "TextDataSVM"

    def __init__(self, max_samples_per_category = 210, test_size=0.5):
        self.max_samples_per_category = max_samples_per_category
        self.test_size = test_size

        self.X1 = None
        self.y1 = None
        self.X2 = None
        self.y2 = None
        self.schema = None

        self.pipeline = None

        self.clf = None

        self.run()

    def run(self):
        self.load_data()
        self.preprocess()
        self.train()

    def load_data(self):
        path_train = "feature_text.extraction.output.path"
        category_frames = read_native(Config.get2(path_train), self.max_samples_per_category)

        print "Shapes:", str([f.shape for f in category_frames])
        self.schema = get_schema(Config.get2(path_train))
        frame1, frame2 = split_train_test(category_frames, test_size=self.test_size)
        mask = np.asarray(np.ones((1, frame1.shape[1]), dtype=bool))[0]
        mask[0] = False
        mat1, mat2 = dataframe_to_numpy_matrix(frame1, frame2, mask)
        self.X1, self.y1 = split_target_from_data(mat1)
        self.X2, self.y2 = split_target_from_data(mat2)
        # print "X1:", X1.shape, "X2:", X2.shape

    def create_meta_features(self, path_a="attachmentA.feature_text.extraction.output.path"):
        X,y = self.transform(path_a)
        return self.predict_proba(X),y


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
        self.y1 = np.array(self.y1).tolist()
        self.y2 = np.array(self.y2).tolist()
        self.X1 = self.pipeline.fit_transform(self.X1, self.y1)
        self.X2 = self.pipeline.transform(self.X2)

    def train(self):
        self.clf = SGDClassifier(loss='log', alpha=0.0001, penalty='elasticnet')
        self.clf = self.clf.fit(self.X1, self.y1)

    def test(self, X, y):
        y_pred = self.clf.predict(X)
        print confusion_matrix(y, y_pred)
        print f1_score(y, y_pred, average='weighted')

    def test_path(self, path_a="attachmentA.feature_text.extraction.output.path"):
        X,y = self.transform(path_a)
        self.test(X,y)

    def predict_proba(self, X):
        y = self.clf.predict_proba(X)
        return y