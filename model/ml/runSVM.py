# -*- coding: utf-8 -*-

from myio import *
from sklearn.svm import SVC
from sklearn.metrics import confusion_matrix
from sklearn.metrics import f1_score


def run():
    category_frames = read("../../samplegeneration/src/main/resources/data/generated_29_11_16")

    train_frame, test_frame = split_train_test(category_frames)

    mask = np.asarray(np.ones((1, train_frame.shape[1]), dtype=bool))[0]
    mask[0] = False

    train_matrix, test_matrix = dataframe_to_numpy_matrix(train_frame, test_frame, mask)

    train_x, train_y = split_target_from_data(train_matrix)
    test_x, test_y = split_target_from_data(train_matrix)

    clf = SVC(decision_function_shape='ovo')
    clf.fit(train_x, train_y)

    y_test_pred = clf.predict(test_x)

    print confusion_matrix(test_y, y_test_pred)

    print f1_score(test_y, y_test_pred, average='weighted')




run()