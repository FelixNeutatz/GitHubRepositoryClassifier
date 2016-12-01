# -*- coding: utf-8 -*-

from myio import *
import xgboost as xgb
from sklearn.grid_search import GridSearchCV
from sklearn.metrics import confusion_matrix
from sklearn.metrics import f1_score


def run():
    category_frames = read("../../samplegeneration/src/main/resources/data/generated_29_11_16")

    train_frame, test_frame = split_train_test(category_frames)

    mask = np.asarray(np.ones((1, train_frame.shape[1]), dtype=bool))[0]
    mask[0] = False

    train_matrix, test_matrix = dataframe_to_numpy_matrix(train_frame, test_frame, mask)

    train_x, train_y = split_target_from_data(train_matrix)
    test_x, test_y = split_target_from_data(test_matrix)


    cv_params = {'max_depth': [3, 5, 7], 'min_child_weight': [1, 3, 5]}
    ind_params = {'learning_rate': 0.1, 'n_estimators': 1000, 'seed': 0, 'subsample': 0.8, 'colsample_bytree': 0.8,
                  'objective': 'multi:softmax'}
    optimized_GBM = GridSearchCV(xgb.XGBClassifier(**ind_params),
                                 cv_params,
                                 scoring='accuracy', cv=5, n_jobs=4, verbose=10)

    optimized_GBM.fit(train_x, train_y)

    print optimized_GBM.grid_scores_


    # see https://jessesw.com/XG-Boost/
    xgdmat = xgb.DMatrix(train_x, train_y)  # Create our DMatrix to make XGBoost more efficient

    our_params = {'eta': 0.1, 'seed': 0, 'subsample': 0.8, 'colsample_bytree': 0.8,
                  'objective': 'multi:softmax', 'num_class': 6, 'max_depth': 3, 'min_child_weight': 1}
    # Grid Search CV optimized settings

    cv_xgb = xgb.cv(params=our_params, dtrain=xgdmat, num_boost_round=3000, nfold=5,
                    metrics=['merror'],  # Make sure you enter metrics inside a list or you may encounter issues!
                    early_stopping_rounds=100)  # Look for early stopping that minimizes error

    print cv_xgb.tail(5)

    #print confusion_matrix(test_y, y_test_pred)

    #print f1_score(test_y, y_test_pred, average='weighted')

    


run()