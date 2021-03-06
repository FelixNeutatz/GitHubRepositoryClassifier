# -*- coding: utf-8 -*-
from sklearn.multiclass import OneVsRestClassifier
from sklearn.svm import SVC

from ml.util import test, fit_cv
from myio import *
from sklearn.pipeline import Pipeline
from sklearn.feature_extraction.text import TfidfTransformer
from sklearn.feature_extraction.text import CountVectorizer
from sklearn.naive_bayes import MultinomialNB
from sklearn.linear_model import SGDClassifier
from pprint import pprint
from time import time
from sklearn.model_selection import GridSearchCV
from config import Config
from sklearn.feature_extraction import text
from sklearn.calibration import CalibratedClassifierCV
import matplotlib.pyplot as plt
from visualize import validate
from visualize import array_to_itemlist


# http://scikit-learn.org/stable/tutorial/text_analytics/working_with_text_data.html
# http://scikit-learn.org/stable/auto_examples/model_selection/grid_search_text_feature_extraction.html#sphx-glr-auto-examples-model-selection-grid-search-text-feature-extraction-py
# https://www.kaggle.com/c/word2vec-nlp-tutorial/details/part-1-for-beginners-bag-of-words

def visualize(y_real, y_pred):
    validate(np.array(y_real).tolist(), np.array(y_pred).tolist())


def run():
    category_frames = read_native(Config.get("feature_text.extraction.output.path"), 2000)
    category_frames = filter_frames(category_frames, get_labeled_data_filter("../../mturk/our_labels/labels.csv"))

    train_frame, test_frame = split_train_test(category_frames, test_size=0.3)

    mask = np.asarray(np.ones((1, train_frame.shape[1]), dtype=bool))[0]
    mask[0] = False

    train_matrix, test_matrix = dataframe_to_numpy_matrix(train_frame, test_frame, mask)

    train_x, train_y = split_target_from_data(train_matrix)
    test_x, test_y = split_target_from_data(test_matrix)

    my_stop_words = read_stop_words()
    print my_stop_words

    stop_words = text.ENGLISH_STOP_WORDS.union(my_stop_words)

    pipeline = Pipeline([('vect', CountVectorizer(stop_words=stop_words, max_df=0.5)),
                         ('tfidf', TfidfTransformer()),
                         # SGDClassifier(loss='log')
                         ('clf', OneVsRestClassifier(SVC(kernel="linear", C=1, class_weight='balanced', probability=True))),
                         ])

    '''
    # uncommenting more parameters will give better exploring power but will
    # increase processing time in a combinatorial way
    parameters = {
        'vect__max_df':(0.5, 0.75, 1.0),
        # 'vect__max_features': (None, 5000, 10000, 50000),
        'vect__ngram_range':((1, 1), (1, 2)),  # unigrams or bigrams
        # 'tfidf__use_idf': (True, False),
        # 'tfidf__norm': ('l1', 'l2'),
        'clf__alpha':(0.0001, 0.00001, 0.000001),
        'clf__penalty':('l2', 'elasticnet')
        # 'clf__n_iter': (10, 50, 80),
    }


    # find the best parameters for both the feature extraction and the
    # classifier
    grid_search = GridSearchCV(pipeline, parameters, n_jobs=4, verbose=10)

    print("Performing grid search...")
    print("pipeline:", [name for name, _ in pipeline.steps])
    print("parameters:")
    pprint(parameters)
    t0 = time()
    grid_search.fit(train_x.A.ravel(), np.array(train_y).tolist())
    print("done in %0.3fs" % (time() - t0))
    print()

    print("Best score: %0.3f" % grid_search.best_score_)
    print("Best parameters set:")
    best_parameters = grid_search.best_estimator_.get_params(deep=True)
    for param_name in sorted(parameters.keys()):
        print("\t%s: %r" % (param_name, best_parameters[param_name]))

     best_parameters = {
        'vect__max_df': (0.5),
        # 'vect__max_features': (None, 5000, 10000, 50000),
        'vect__ngram_range': ((1, 2)),  # unigrams or bigrams
        # 'tfidf__use_idf': (True, False),
        # 'tfidf__norm': ('l1', 'l2'),
        'clf__alpha': (0.00001),
        'clf__penalty': ('elasticnet')
        # 'clf__n_iter': (10, 50, 80),
    }

    best_parameters = {
        'vect__max_df': (0.5),
        # 'vect__max_features': (None, 5000, 10000, 50000),
        'vect__ngram_range': ((1, 1)),  # unigrams or bigrams
        # 'tfidf__use_idf': (True, False),
        # 'tfidf__norm': ('l1', 'l2'),
        'clf__alpha': (0.0001),
        'clf__penalty': ('elasticnet')
        # 'clf__n_iter': (10, 50, 80),
    }

    pipeline.set_params(**best_parameters)
    '''

    X_numpy = train_x
    y_numpy = train_y

    X = X_numpy.A.ravel()
    y = np.array(y_numpy).tolist()

    # params = {"clf__estimator__C": np.logspace(-4, 4, 9)}
    # clf = fit_cv(pipeline, X, y, params)
    clf = pipeline.fit(X, y)


    # feature visualization
    vec = clf.named_steps['vect']
    vocabulary = vec.vocabulary_

    classifier = clf.named_steps['clf']
    weights = classifier.coef_.toarray()

    category_list = {0: "DATA", 1: "EDU", 2: "HW", 3: "DOCS", 4: "DEV", 5: "WEB"}

    k = 20
    for id in range(0, len(category_list)):
        max_ids = np.argsort(-weights[id])
        values = np.sort(-weights[id])

        feature_names = [""] * k
        feature_weights = [0] * k
        for key_d, item in vocabulary.iteritems():
            for i in range(0, k):
                if max_ids[i] == item:
                    feature_names[i] = key_d
                    feature_weights[i] = -values[i]

        print feature_names
        array_to_itemlist(feature_names)
        print feature_weights

        ind = np.arange(k)
        plt.bar(ind, feature_weights)
        plt.title('top ' + str(k) + ' features for ' + category_list[id])
        plt.xticks(np.arange(k) + 0.5, feature_names, rotation='vertical')
        plt.xlabel('features')
        plt.ylabel('weight')
        plt.tight_layout()
        # plt.show()


    #y_pred_proba = clf.predict_proba(test_x.A.ravel())
    #print y_pred_proba

    # y_pred = clf.predict(test_x.A.ravel())
    # visualize(test_y, y_pred)
    test(clf, test_x.A.ravel(), np.array(test_y, dtype=np.float))


    # attachment A
    attachment_a_frames = read_native(Config.get("attachmentA.feature_text.extraction.output.path"), 150)
    attachment_a_frame = concat(attachment_a_frames)
    attachment_a_matrix = dataframe_to_numpy_matrix_single(attachment_a_frame, mask)
    attachment_a_x, attachment_a_y = split_target_from_data(attachment_a_matrix)
    #attachment_a_y_pred = clf.predict_proba(attachment_a_x.A.ravel())
    # print attachment_a_y_pred
    #attachment_a_ypred_test = np.argmax(attachment_a_y_pred, axis=1)  # choose class with highest probability per sample
    #print attachment_a_ypred_test
    #visualize(attachment_a_y, attachment_a_ypred_test)
    test(clf, attachment_a_x.A.ravel(), np.array(attachment_a_y, dtype=np.float))

run()
