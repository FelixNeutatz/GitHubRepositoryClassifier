# -*- coding: utf-8 -*-

from myio import *
import config
from sklearn.svm import SVC
from sklearn.svm import LinearSVC
from sklearn.metrics import confusion_matrix
from sklearn.metrics import f1_score
from sklearn.preprocessing import StandardScaler
from sklearn.model_selection import StratifiedShuffleSplit
from sklearn.model_selection import GridSearchCV
from sklearn.multiclass import OneVsRestClassifier
from config import Config


def run():
    category_frames = read(Config.get("feature.extraction.output.path"), 2000)
    category_frames = filter_frames(category_frames, get_labeled_data_filter("../../mturk/our_labels/labels.csv"))

    train_frame, test_frame = split_train_test(category_frames, test_size=0.2)

    mask = np.asarray(np.ones((1, train_frame.shape[1]), dtype=bool))[0]
    mask[0] = False

    train_matrix, test_matrix = dataframe_to_numpy_matrix(train_frame, test_frame, mask)

    train_x, train_y = split_target_from_data(train_matrix)
    test_x, test_y = split_target_from_data(test_matrix)

    # It is usually a good idea to scale the data for SVM training.

    scaler = StandardScaler()
    train_x = scaler.fit_transform(train_x)
    test_x = scaler.transform(test_x)


    '''
    C_range = np.logspace(-2, 10, 13)
    gamma_range = np.logspace(-9, 3, 13)
    param_grid = dict(gamma=gamma_range, C=C_range)
    cv = StratifiedShuffleSplit(n_splits=5, test_size=0.2, random_state=42)
    grid = GridSearchCV(SVC(decision_function_shape='ovo'), param_grid=param_grid, cv=cv, verbose=10, n_jobs=4)
    grid.fit(train_x, train_y)

    C_range = np.logspace(-4, 4, 9)
    param_grid = dict(estimator__C=C_range)
    cv = StratifiedShuffleSplit(n_splits=5, test_size=0.2, random_state=42)
    grid = GridSearchCV(OneVsRestClassifier(SVC(kernel="linear")), param_grid=param_grid, cv=cv, verbose=10, n_jobs=4)
    grid.fit(train_x, train_y)

    print("The best parameters are %s with a score of %0.2f"
          % (grid.best_params_, grid.best_score_))

    clf = grid.best_estimator_
    '''

    clf = OneVsRestClassifier(SVC(kernel="linear", C=.01))
    clf.fit(train_x, train_y)

    y_train_pred = clf.predict(train_x)

    print confusion_matrix(train_y, y_train_pred)
    print f1_score(train_y, y_train_pred, average='weighted')

    y_test_pred = clf.predict(test_x)

    print confusion_matrix(test_y, y_test_pred)
    print f1_score(test_y, y_test_pred, average='weighted')


run()
