# -*- coding: utf-8 -*-

from myio import *
import xgboost as xgb
from sklearn.model_selection import GridSearchCV
# import seaborn as sns
import matplotlib.pyplot as plt
from config import Config
from visualize import dict_to_bar_chart
from visualize import validate
from xgboost import plot_tree


def run():
    category_frames = read(Config.get("feature.extraction.output.path"), 2000)
    category_frames = filter_frames(category_frames, get_labeled_data_filter("../../mturk/our_labels/labels.csv"))
    schema = get_schema(Config.get("feature.extraction.output.path"))
    train_frame, test_frame = split_train_test(category_frames, test_size=0.3)
    mask = np.asarray(np.ones((1, train_frame.shape[1]), dtype=bool))[0]
    mask[0] = False
    train_matrix, test_matrix = dataframe_to_numpy_matrix(train_frame, test_frame, mask)
    train_x, train_y = split_target_from_data(train_matrix)
    test_x, test_y = split_target_from_data(test_matrix)

    # cv_params = {'max_depth': [3, 5, 7], 'min_child_weight': [1, 3, 5]}
    # ind_params = {'learning_rate': 0.1, 'n_estimators': 1000, 'seed': 0, 'subsample': 0.8, 'colsample_bytree': 0.8,
    #               'objective': 'multi:softprob'}
    # optimized_GBM = GridSearchCV(xgb.XGBClassifier(**ind_params),
    #                              cv_params,
    #                              scoring='accuracy', cv=5, n_jobs=4, verbose=10)
    #
    # optimized_GBM.fit(train_x, train_y)
    #
    # print optimized_GBM.grid_scores_

    '''
    #grid search learning rate
    cv_params = {'learning_rate': [0.1, 0.01], 'subsample': [0.7, 0.8, 0.9]}
    ind_params = {'n_estimators': 1000, 'seed': 0, 'colsample_bytree': 0.8,
                  'objective': 'binary:logistic', 'max_depth': 3, 'min_child_weight': 1}

    optimized_GBM = GridSearchCV(xgb.XGBClassifier(**ind_params),
                                 cv_params,
                                 scoring='accuracy', cv=5, n_jobs=4, verbose=10)
    optimized_GBM.fit(train_x, train_y)

    print optimized_GBM.grid_scores_
    '''

    # see https://jessesw.com/XG-Boost/
    xgdmat = xgb.DMatrix(train_x, train_y, feature_names=schema)

    our_params = {'eta': 0.1, 'seed': 0, 'subsample': 0.8, 'colsample_bytree': 0.8,
                  'objective': 'multi:softprob', 'num_class': 6, 'max_depth': 3, 'min_child_weight': 1}
    # Grid Search CV optimized settings

    cv_xgb = xgb.cv(params=our_params, dtrain=xgdmat, num_boost_round=324, nfold=5,
                    metrics=['merror'],  # Make sure you enter metrics inside a list or you may encounter issues!
                    early_stopping_rounds=100)  # Look for early stopping that minimizes error

    print cv_xgb.tail(5)

    #print confusion_matrix(test_y, y_test_pred)

    #print f1_score(test_y, y_test_pred, average='weighted')

    our_params = {'eta': 0.1, 'seed': 0, 'subsample': 0.8, 'colsample_bytree': 0.8,
                  'objective': 'multi:softprob', 'num_class': 6, 'max_depth': 3, 'min_child_weight': 1}

    final_gb = xgb.train(our_params, xgdmat, num_boost_round=3000)

    plot_tree(final_gb)

    # sns.set(font_scale=1.5)

    '''
     importance_type : str, default "weight"
        How the importance is calculated: either "weight", "gain", or "cover"
        "weight" is the number of times a feature appears in a tree
        "gain" is the average gain of splits which use the feature
        "cover" is the average coverage of splits which use the feature
            where coverage is defined as the number of samples affected by the split
    '''
    xgb.plot_importance(final_gb, importance_type='gain') # try weight, gain, cover
    plt.show()

    print "gain:"
    importances = final_gb.get_score(importance_type='gain')
    print importances

    dict_to_bar_chart(importances, "feature gain")

    print "weight:"
    importancesW = final_gb.get_score(importance_type='weight')
    print importancesW

    dict_to_bar_chart(importancesW, "feature weight")

    print "coverage:"
    importancesC = final_gb.get_score(importance_type='cover')
    print importancesC

    dict_to_bar_chart(importancesC, "feature coverage")

    #importances = final_gb.get_fscore()
    #print importances


    print cv_xgb.tail(5)

    testdmat = xgb.DMatrix(test_x, feature_names=schema)

    y_pred = final_gb.predict(testdmat)

    ypred_test = np.argmax(y_pred, axis=1) #choose class with highest probability per sample

    validate(test_y, ypred_test)

    attachment_a_frames = read(Config.get("attachmentA.feature.extraction.output.path"), 150)

    attachment_a_frame = concat(attachment_a_frames)

    attachment_a_matrix = dataframe_to_numpy_matrix_single(attachment_a_frame, mask)

    attachment_a_x, attachment_a_y = split_target_from_data(attachment_a_matrix)

    attachment_a_mat = xgb.DMatrix(attachment_a_x, feature_names=schema)

    attachment_a_y_pred = final_gb.predict(attachment_a_mat)

    attachment_a_ypred_test = np.argmax(attachment_a_y_pred, axis=1)  # choose class with highest probability per sample

    validate(attachment_a_y, attachment_a_ypred_test)

run()
