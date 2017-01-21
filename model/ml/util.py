from sklearn.model_selection import GridSearchCV
import matplotlib.pyplot as plt
from sklearn.model_selection import StratifiedKFold
from sklearn.model_selection import StratifiedShuffleSplit
from sklearn.model_selection import cross_val_predict
from sklearn.model_selection import learning_curve
import numpy as np
from ml.visualize import validate


cv_def = StratifiedShuffleSplit(n_splits=5, test_size=0.2)
cv_det = StratifiedKFold(n_splits=5)


def fit_cv(clf, train_x, train_y, params, cv=cv_def):
    # wrap clf with grid search
    grid = GridSearchCV(clf, param_grid=params, cv=cv, scoring='f1_weighted', verbose=10, n_jobs=4)
    grid.fit(train_x, train_y)
    print("Best parameters are %s with a score of %0.2f" % (grid.best_params_, grid.best_score_))
    clf_best = grid.best_estimator_
    clf_best.fit(train_x, train_y)
    return clf_best


def test(clf, X, y, cv=cv_det):
    if cv is not None:
      y_pred = cross_val_predict(clf, X, y, cv=cv, n_jobs=-1)
    else:
      y_pred = clf.predict(X)
    validate(y, y_pred)


def plot_learning_curve(estimator, title, X, y, scoring="f1_weighted", score_name="F1-weighted", ylim=None, cv=cv_def,
                        n_jobs=4, train_sizes=np.linspace(.1, 1.0, 5)):
    plt.figure()
    plt.title(title)
    if ylim is not None:
        plt.ylim(*ylim)
    plt.xlabel("Training examples")
    plt.ylabel(score_name)
    train_sizes, train_scores, test_scores = learning_curve(
        estimator, X, y, cv=cv, scoring=scoring, n_jobs=n_jobs, train_sizes=train_sizes)
    train_scores_mean = np.mean(train_scores, axis=1)
    train_scores_std = np.std(train_scores, axis=1)
    test_scores_mean = np.mean(test_scores, axis=1)
    test_scores_std = np.std(test_scores, axis=1)
    plt.grid()

    plt.fill_between(train_sizes, train_scores_mean - train_scores_std,
                     train_scores_mean + train_scores_std, alpha=0.1,
                     color="r")
    plt.fill_between(train_sizes, test_scores_mean - test_scores_std,
                     test_scores_mean + test_scores_std, alpha=0.1, color="g")
    plt.plot(train_sizes, train_scores_mean, 'o-', color="r",
             label="Training score")
    plt.plot(train_sizes, test_scores_mean, 'o-', color="g",
             label="Cross-validation score")

    plt.legend(loc="best")
    return plt
