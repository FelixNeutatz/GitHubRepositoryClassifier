# -*- coding: utf-8 -*-

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
from sklearn.metrics import confusion_matrix
from sklearn.metrics import f1_score


# http://scikit-learn.org/stable/tutorial/text_analytics/working_with_text_data.html
# http://scikit-learn.org/stable/auto_examples/model_selection/grid_search_text_feature_extraction.html#sphx-glr-auto-examples-model-selection-grid-search-text-feature-extraction-py
# https://www.kaggle.com/c/word2vec-nlp-tutorial/details/part-1-for-beginners-bag-of-words

def run():
    category_frames = readNative(Config.get("feature.extraction.output.path"), 169)

    train_frame, test_frame = split_train_test(category_frames, test_size=0.1)

    mask = np.asarray(np.ones((1, train_frame.shape[1]), dtype=bool))[0]
    mask[0] = False

    train_matrix, test_matrix = dataframe_to_numpy_matrix(train_frame, test_frame, mask)

    train_x, train_y = split_target_from_data(train_matrix)
    test_x, test_y = split_target_from_data(test_matrix)

    my_stop_words = read_stop_words()
    print my_stop_words

    stop_words = text.ENGLISH_STOP_WORDS.union(my_stop_words)

    pipeline = Pipeline([('vect', CountVectorizer(stop_words=stop_words)),
                         ('tfidf', TfidfTransformer()),
                         ('clf', SGDClassifier(loss='hinge')),
                         ])

    # uncommenting more parameters will give better exploring power but will
    # increase processing time in a combinatorial way
    parameters = {
        'vect__max_df': (0.5, 0.75, 1.0),
        # 'vect__max_features': (None, 5000, 10000, 50000),
        'vect__ngram_range': ((1, 1), (1, 2)),  # unigrams or bigrams
        # 'tfidf__use_idf': (True, False),
        # 'tfidf__norm': ('l1', 'l2'),
        'clf__alpha': (0.00001, 0.000001),
        'clf__penalty': ('l2', 'elasticnet'),
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
    best_parameters = grid_search.best_estimator_.get_params()
    for param_name in sorted(parameters.keys()):
        print("\t%s: %r" % (param_name, best_parameters[param_name]))


    #pipeline.fit()
    #pipeline.named_steps['vect']

    X = train_x.A.ravel()
    y = np.array(train_y).tolist()

    clf = pipeline.fit(X, y, best_parameters)
    y_pred = clf.predict(test_x.A.ravel())

    print confusion_matrix(np.array(test_y).tolist(), np.array(y_pred).tolist())
    print f1_score(np.array(test_y).tolist(), np.array(y_pred).tolist(), average='weighted')

run()