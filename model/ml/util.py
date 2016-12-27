from sklearn.metrics import confusion_matrix
from sklearn.metrics import f1_score
from sklearn.model_selection import GridSearchCV
import matplotlib.pyplot as plt
from sklearn.model_selection import StratifiedShuffleSplit
from sklearn.model_selection import learning_curve
import numpy as np


cv_def = StratifiedShuffleSplit(n_splits=5, test_size=0.2, random_state=42)


def fit_cv(clf, train_x, train_y, params, cv=cv_def):
    # wrap clf with grid search
    grid = GridSearchCV(clf, param_grid=params, cv=cv, scoring='f1_weighted', verbose=10, n_jobs=4)
    grid.fit(train_x, train_y)
    return grid


def test(clf, x, y):
    y_pred = clf.predict(x)
    print confusion_matrix(y, y_pred)
    print f1_score(y, y_pred, average='weighted')


def plot_learning_curve(estimator, title, X, y, ylim=None, cv=cv_def,
                        n_jobs=1, train_sizes=np.linspace(.1, 1.0, 5)):
    plt.figure()
    plt.title(title)
    if ylim is not None:
        plt.ylim(*ylim)
    plt.xlabel("Training examples")
    plt.ylabel("Score")
    train_sizes, train_scores, test_scores = learning_curve(
        estimator, X, y, cv=cv, n_jobs=n_jobs, train_sizes=train_sizes)
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
