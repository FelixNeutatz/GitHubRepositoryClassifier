# -*- coding: utf-8 -*-

import xgboost as xgb
from sklearn.preprocessing import StandardScaler

from ml.config import Config
from ml.myio import *
from ml.stacking.StackingModule import StackingModule
from ml.util import *


class MetaDataXGB(StackingModule):
    name = "MetaDataXG-Boost"

    def __init__(self, max_samples_per_category=210, test_size=0.5):
        self.max_samples_per_category = max_samples_per_category
        self.test_size = test_size

        self.scaler = None

        self.X1 = None
        self.y1 = None
        self.X2 = None
        self.y2 = None
        self.schema = None

        self.clf = None

        self.run()

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
         # print "X1:", X1.shape, "X2:", X2.shape


    def create_meta_features(self, path_a="attachmentA.feature.extraction.output.path"):
        X,y = self.transform(path_a)
        return self.predict_proba(X),y


    def transform(self, path_a = "attachmentA.feature.extraction.output.path"):
        a_frame = concat(read(Config.get(path_a), self.max_samples_per_category))
        mask = np.asarray(np.ones((1, a_frame.shape[1]), dtype=bool))[0]
        mask[0] = False
        a_mat = dataframe_to_numpy_matrix_single(a_frame, mask)
        X_test, y_test = split_target_from_data(a_mat)
        X_test_transformed = self.scaler.transform(X_test)
        return X_test_transformed, np.array(y_test).tolist()


    def run(self):
        self.load_data()
        self.preprocess()
        self.train()


    def preprocess(self):
        # normalize
        self.scaler = StandardScaler()
        self.X1 = self.scaler.fit_transform(self.X1)
        self.X2 = self.scaler.transform(self.X2)
        # we can't do dimensionality reduction because this would screw the feature schema which is given to xgb
        # pca = PCA(n_components=300)
        # X1 = pca.fit_transform(X1)
        # X2 = pca.transform(X2)
        # Xa = pca.transform(Xa)

    def train(self):
        xgdmat = xgb.DMatrix(self.X1, self.y1, feature_names=self.schema)
        our_params = {'eta': 0.1, 'seed': 0, 'subsample': 0.8, 'colsample_bytree': 0.8,
                  'objective': 'multi:softprob', 'num_class': 6, 'max_depth': 3, 'min_child_weight': 1}
        self.clf = xgb.train(our_params, xgdmat, num_boost_round=3000)

    def test(self, X, y):
        y_pred = self.clf.predict(xgb.DMatrix(X, feature_names=self.schema))
        print confusion_matrix(y, y_pred.argmax(axis=1))
        print f1_score(y, y_pred.argmax(axis=1), average='weighted')

    def test_path(self, path_a="attachmentA.feature.extraction.output.path"):
        X,y = self.transform(path_a)
        self.test(X,y)

    def predict_proba(self, X):
        xgdmat = xgb.DMatrix(X, feature_names=self.schema)
        y = self.clf.predict(xgdmat)  # xgb always predicts probabilities
        return y