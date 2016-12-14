# -*- coding: utf-8 -*-

from myio import *
import xgboost as xgb
from sklearn.grid_search import GridSearchCV
from sklearn.metrics import confusion_matrix
from sklearn.metrics import f1_score
import seaborn as sns
import matplotlib.pyplot as plt


def run():
    #category_frames = read("../../samplegeneration/src/main/resources/data/generated_29_11_16")
    category_frames = read(
        "/home/felix/GitHubRepositoryClassifier/GitHubRepositoryClassifier/featureextraction/src/main/resources/data/features/data",
        1000)

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
    xgdmat = xgb.DMatrix(train_x, train_y, feature_names={"numberBranches","numberForks","numberOpenIssues","totalNumberIssues","repoSize","numberStars","numberSubscribers","numberWatchers","numberReleases","indexHTMLfileLength","numberCommits","numberContributors","hasDownloads","descriptionLength","numberProgrammingLanguages","hasLicense","readmeSize","projectTotalTime","startTime","AddedLinesAverage","AddedLinesStdDev","AddedLinesMin","AddedLinesMax","ChangedLinesAverage","ChangedLinesStdDev","ChangedLinesMin","ChangedLinesMax","DeletedLinesAverage","DeletedLinesStdDev","DeletedLinesMin","DeletedLinesMax","TimeBetweenCommitsAverage","TimeBetweenCommitsStdDev","TimeBetweenCommitsMin","TimeBetweenCommitsMax","weekDayCount0","weekDayCount1","weekDayCount2","weekDayCount3","weekDayCount4","weekDayCount5","weekDayCount6","language_JavaScript","language_Java","language_Python","language_CSS","language_PHP","language_Ruby","language_C++","language_C","language_Shell","language_C#","language_Objective-C","language_R","language_VimL","language_Go","language_Perl","language_CoffeeScript","language_TeX","language_Swift","language_Scala","language_Emacs","language_Lisp","language_Haskell","language_Lua","language_Clojure","language_Matlab","language_Arduino","language_Groovy","language_Puppet","language_Rust","language_PowerShell","language_Erlang","language_Visual_Basic","language_Processing","language_Assembly","language_TypeScript","language_XSLT","language_ActionScript","language_ASP","language_OCaml","language_D","language_Scheme","language_Dart","language_Common_Lisp","language_Julia","language_F#","language_Elixir","language_FORTRAN","language_Haxe","language_Racket","language_Logos"})  # Create our DMatrix to make XGBoost more efficient

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

    sns.set(font_scale=1.5)

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

    xgdmat.feature_names

    #importances = final_gb.get_fscore()
    #print importances


    print cv_xgb.tail(5)

    testdmat = xgb.DMatrix(test_x, feature_names={"numberBranches","numberForks","numberOpenIssues","totalNumberIssues","repoSize","numberStars","numberSubscribers","numberWatchers","numberReleases","indexHTMLfileLength","numberCommits","numberContributors","hasDownloads","descriptionLength","numberProgrammingLanguages","hasLicense","readmeSize","projectTotalTime","startTime","AddedLinesAverage","AddedLinesStdDev","AddedLinesMin","AddedLinesMax","ChangedLinesAverage","ChangedLinesStdDev","ChangedLinesMin","ChangedLinesMax","DeletedLinesAverage","DeletedLinesStdDev","DeletedLinesMin","DeletedLinesMax","TimeBetweenCommitsAverage","TimeBetweenCommitsStdDev","TimeBetweenCommitsMin","TimeBetweenCommitsMax","weekDayCount0","weekDayCount1","weekDayCount2","weekDayCount3","weekDayCount4","weekDayCount5","weekDayCount6","language_JavaScript","language_Java","language_Python","language_CSS","language_PHP","language_Ruby","language_C++","language_C","language_Shell","language_C#","language_Objective-C","language_R","language_VimL","language_Go","language_Perl","language_CoffeeScript","language_TeX","language_Swift","language_Scala","language_Emacs","language_Lisp","language_Haskell","language_Lua","language_Clojure","language_Matlab","language_Arduino","language_Groovy","language_Puppet","language_Rust","language_PowerShell","language_Erlang","language_Visual_Basic","language_Processing","language_Assembly","language_TypeScript","language_XSLT","language_ActionScript","language_ASP","language_OCaml","language_D","language_Scheme","language_Dart","language_Common_Lisp","language_Julia","language_F#","language_Elixir","language_FORTRAN","language_Haxe","language_Racket","language_Logos"})  # Create our DMatrix to make XGBoost more efficient


    y_pred = final_gb.predict(testdmat)

    ypred_test = np.argmax(y_pred, axis=1) #choose class with highest probability per sample

    print confusion_matrix(test_y, ypred_test)
    print f1_score(test_y, ypred_test, average='weighted')

run()