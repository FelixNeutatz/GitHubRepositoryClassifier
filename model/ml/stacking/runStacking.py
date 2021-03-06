# -*- coding: utf-8 -*-

import os
import xgboost as xgb
import matplotlib.pyplot as plt
from sklearn.decomposition import PCA
from sklearn.feature_extraction.stop_words import ENGLISH_STOP_WORDS
from sklearn.feature_extraction.text import CountVectorizer, TfidfTransformer
from sklearn.linear_model import LogisticRegression
from sklearn.linear_model import SGDClassifier
from sklearn.multiclass import OneVsRestClassifier
from sklearn.neural_network import MLPClassifier
from sklearn.pipeline import Pipeline
from sklearn.preprocessing import StandardScaler
from sklearn.svm import SVC

from ml.config import Config
from ml.util import *
from ml.myio import *


def get_data_meta():
    X1, y1, X2, y2, schema, Xa, ya = load_data(True)
    X1, X2, Xa = preprocess_meta(X1, X2, Xa)
    return X1, y1, X2, y2, schema, Xa, ya


def get_data_text():
    X1, y1, X2, y2, schema, Xa, ya = load_data(False)
    X1, y1, X2, y2, Xa, ya = preprocess_text(X1, y1, X2, y2, Xa, ya)
    return X1, y1, X2, y2, schema, Xa, ya


def load_data(meta_or_text):
    max_samples_per_category = 210
    if meta_or_text:  # True is meta
        path_train = "feature.extraction.output.path"
        path_a = "attachmentA.feature.extraction.output.path"
        category_frames = read(Config.get2(path_train), max_samples_per_category)
        a_frame = concat(read(Config.get2(path_a), max_samples_per_category))
    else:
        path_train = "feature_text.extraction.output.path"
        path_a = "attachmentA.feature_text.extraction.output.path"
        category_frames = read_native(Config.get2(path_train), max_samples_per_category)
        a_frame = concat(read_native(Config.get2(path_a), max_samples_per_category))
    schema = get_schema(Config.get2(path_train))
    frame1, frame2 = split_train_test(category_frames, test_size=0.5)
    mask = np.asarray(np.ones((1, frame1.shape[1]), dtype=bool))[0]
    mask[0] = False
    mat1, mat2 = dataframe_to_numpy_matrix(frame1, frame2, mask)
    X1, y1 = split_target_from_data(mat1)
    X2, y2 = split_target_from_data(mat2)
    a_mat = dataframe_to_numpy_matrix_single(a_frame, mask)
    Xa, ya = split_target_from_data(a_mat)
    return X1, y1, X2, y2, schema, Xa, ya


def preprocess_meta(X1, X2, Xa):
    # normalize
    scaler = StandardScaler()
    X1 = scaler.fit_transform(X1)
    X2 = scaler.transform(X2)
    Xa = scaler.transform(Xa)
    # we can't do dimensionality reduction because this would screw the feature schema which is given to xgb
    # pca = PCA(n_components=300)
    # X1 = pca.fit_transform(X1)
    # X2 = pca.transform(X2)
    # Xa = pca.transform(Xa)
    return X1, X2, Xa


def preprocess_text(X1, y1, X2, y2, Xa, ya):
    my_stop_words = read_stop_words2()
    stop_words = ENGLISH_STOP_WORDS.union(my_stop_words)
    pipeline = Pipeline([('vect', CountVectorizer(stop_words=stop_words, max_df=0.5, ngram_range=(1, 1))),
                         ('tfidf', TfidfTransformer())])
    X1 = X1.A1
    X2 = X2.A1
    Xa = Xa.A1
    y1 = np.array(y1).tolist()
    y2 = np.array(y2).tolist()
    ya = np.array(ya).tolist()
    X1 = pipeline.fit_transform(X1, y1)
    X2 = pipeline.transform(X2)
    Xa = pipeline.transform(Xa)
    return X1, y1, X2, y2, Xa, ya


def verify_data(X_meta, y_meta, X_text, y_text):
    # number of samples
    samples_meta = X_meta.shape[0]
    samples_text = X_text.shape[0]
    print("Samples - meta: %s, text: %s" % (samples_meta, samples_text))
    # sample sizes must be the same for meta and text samples
    assert samples_meta == samples_text
    assert samples_meta == len(y_meta)
    assert samples_text == len(y_text)
    # labels must be the same
    assert sum(y_meta - y_text) == 0

def train_meta(X, y, schema):
    xgdmat = xgb.DMatrix(X, y, feature_names=schema)
    our_params = {'eta': 0.1, 'seed': 0, 'subsample': 0.8, 'colsample_bytree': 0.8,
                  'objective': 'multi:softprob', 'num_class': 6, 'max_depth': 3, 'min_child_weight': 1}
    clf = xgb.train(our_params, xgdmat, num_boost_round=3000)
    return clf


def train_text(X, y):
    clf = SGDClassifier(loss='log', alpha=0.0001, penalty='elasticnet')
    clf = clf.fit(X, y)
    return clf


def train_stacking(X, y):
    clf = OneVsRestClassifier(SVC())
    params = [
      {
        'estimator__kernel': ['rbf'],
        'estimator__C': np.logspace(-3, 6, 5),
        'estimator__gamma': np.logspace(-7, 3, 5)
      },
      {
        'estimator__kernel': ['linear'],
        'estimator__C': np.logspace(-3, 6, 5)
      }
    ]
    cv = StratifiedShuffleSplit(n_splits=5, test_size=0.1)
    clf = fit_cv(clf, X, y, params, cv)
    return clf


def build_stacking_data(clf_meta, X_meta, schema_meta, clf_text, X_text):
    xgdmat = xgb.DMatrix(X_meta, feature_names=schema_meta)
    f1 = clf_meta.predict(xgdmat)  # xgb always predicts probabilities
    f2 = clf_text.predict_proba(X_text)
    X = np.concatenate((f1, f2), axis=1)
    return X


def test_text(clf, X, y):
    y_pred = clf.predict(X)
    print "text classifier: "
    print confusion_matrix(y, y_pred)
    print f1_score(y, y_pred, average='weighted')


def test_meta(clf, X, y, schema_meta):
    y_pred = clf.predict(xgb.DMatrix(X, feature_names=schema_meta))
    print "meta classifier: "
    print confusion_matrix(y, y_pred.argmax(axis=1))
    print f1_score(y, y_pred.argmax(axis=1), average='weighted')


file_ = "data.npz"

if not os.path.exists(file_):
    X1_meta, y1_meta, X2_meta, y2_meta, schema_meta, Xa_meta, ya_meta = get_data_meta()
    X1_text, y1_text, X2_text, y2_text, schema_text, Xa_text, ya_text = get_data_text()
    verify_data(X1_meta, y1_meta, X1_text, y1_text)
    verify_data(X2_meta, y2_meta, X2_text, y2_text)
    verify_data(Xa_meta, ya_meta, Xa_text, ya_text)
    # labels are the same across meta and feature samples
    y1 = y1_meta
    y2 = y2_meta
    ya = ya_meta
    # train meta classifier and text classifiers on X1 and y1
    clf_meta = train_meta(X1_meta, y1_meta, schema_meta)
    clf_text = train_text(X1_text, y1_text)
    # test first level classifier on second half of the data
    test_text(clf_text, Xa_text, ya)
    test_meta(clf_meta, Xa_meta, ya, schema_meta)
    # train stacked classifier based on classification of first level classifiers (meta and text)
    X2 = build_stacking_data(clf_meta, X2_meta, schema_meta, clf_text, X2_text)
    Xa = build_stacking_data(clf_meta, Xa_meta, schema_meta, clf_text, Xa_text)
    np.savez(file_, X2, y2, Xa, ya)
else:
    arrs_files = np.load(file_)
    arr_names = sorted(arrs_files.files)
    X2, y2, Xa, ya = [arrs_files[name] for name in arr_names]
# train stacking model
clf_stacked = train_stacking(X2, y2)
# test on attachement A
test(clf_stacked, Xa, ya)
