# -*- coding: utf-8 -*-
import random
import pickle
import numpy as np
from ml import myio
from ml.stacking.MetaDataXGB import MetaDataXGB
from ml.stacking.Stacker import Stacker
from ml.stacking.TextDataSVM import TextDataSVM
from ml.stacking.NameDataSVM import NameDataSVM
from ml.config import Config
from ml.myio import get_labeled_data_filter
from ml.util import plot_learning_curve, plot_learning_curve_no_comp

max_samples_per_category = 2000
dev_size = 0.5
test_size = 0.2

max_samples_possible = 102
train_sizes = [int(x) for x in np.linspace(0.2, 1.0, 5) * max_samples_possible]
seeds = [52, 53, 54, 55, 56]
train_results = []
train_results_per_cat = []
test_results = []
test_results_per_cat = []
for train_size in train_sizes:
    myio.max_samples = train_size

    train_scores = []
    train_scores_per_cat = []
    test_scores = []
    test_scores_per_cat = []
    for seed in seeds:
        np.random.seed(seed)
        myio.random_seed = seed

        print "Training and evaluation with train_size", train_size, "and seed", seed

        labeled_data_filter = get_labeled_data_filter("../../../mturk/our_labels/labels.csv")
        # labeled_data_filter = None
        meta_data_xgb = MetaDataXGB(max_samples_per_category, dev_size, test_size, labeled_data_filter)
        text_data_svm = TextDataSVM(max_samples_per_category, dev_size, test_size, labeled_data_filter)
        name_data_svm = NameDataSVM(max_samples_per_category, dev_size, test_size, labeled_data_filter)

        stacker = Stacker()

        stacker.add(meta_data_xgb)
        stacker.add(text_data_svm)
        stacker.add(name_data_svm)

        stacker.build(test_size > 0)
        stacker.train()

        '''
        print "Retraining stacker modules on all data"
        meta_data_xgb.retrain()
        text_data_svm.retrain()
        name_data_svm.retrain()
        stacker.build_test()
        print "Stacker Test"
        stacker.test()
        print "Stacker Modules Test"
        stacker.test_modules()
        '''

        fileObject = open("../persist/model_samples-%s_seed-%s.p" % (train_size, seed), "wb")
        pickle.dump(stacker, fileObject)

        path_list_a = ["attachmentA.feature.extraction.output.path",
                       "attachmentA.feature_text.extraction.output.path",
                       "attachmentA.feature_text_names.extraction.output.path"]
        path_list_b = ["attachmentB.feature.extraction.output.path",
                       "attachmentB.feature_text.extraction.output.path",
                       "attachmentB.feature_text_names.extraction.output.path"]
        dir_list_a = [Config.get(p) for p in path_list_a]
        dir_list_b = [Config.get(p) for p in path_list_b]

        print "Stacker Test Train"
        train_prec, train_prec_per_cat, train_rec, train_rec_per_cat, train_f1, train_f1_per_cat = stacker.test_train()
        train_scores.append([train_prec, train_rec, train_f1])
        train_scores_per_cat.append([train_prec_per_cat, train_rec_per_cat, train_f1_per_cat])
        print "Stacker Test"
        test_prec, test_prec_per_cat, test_rec, test_rec_per_cat, test_f1, test_f1_per_cat = stacker.test()
        test_scores.append([test_prec, test_rec, test_f1])
        test_scores_per_cat.append([test_prec_per_cat, test_rec_per_cat, test_f1_per_cat])

    train_results.append(train_scores)
    train_results_per_cat.append(train_scores_per_cat)
    test_results.append(test_scores)
    test_results_per_cat.append(test_scores_per_cat)

print "Collect results"
train_results = np.array(train_results)
train_results_per_cat = np.array(train_results_per_cat)
test_results = np.array(test_results)
test_results_per_cat = np.array(test_results_per_cat)

print "Save results to file"
np.save("results/train_sizes", np.array(train_sizes))
np.save("results/seeds", np.array(seeds))
np.save("results/train_results_weighted", train_results)
np.save("results/train_results_per_cat", train_results_per_cat)
np.save("results/test_results_weighted", test_results)
np.save("results/test_results_per_cat", test_results_per_cat)
with open("results/train_sizes.txt", "w") as f:
    f.write(str(train_sizes))
with open("results/seeds.txt", "w") as f:
    f.write(str(seeds))
with open("results/train_results_weighted.txt", "w") as f:
    f.write(str(train_results))
with open("results/train_results_per_cat.txt", "w") as f:
    f.write(str(train_results_per_cat))
with open("results/test_results_weighted.txt", "w") as f:
    f.write(str(test_results))
with open("results/test_results_per_cat.txt", "w") as f:
    f.write(str(test_results_per_cat))

print "Scores over sample sizes", train_sizes, "and seeds", seeds
print "Train scores:"
print "All", train_results
print "Precision weighted - Mean", np.mean(train_results, axis=1)[:, 0], "Std", np.std(train_results, axis=1)[:, 0]
print "Recall weighted - Mean", np.mean(train_results, axis=1)[:, 1], "Std", np.std(train_results, axis=1)[:, 1]
print "F1 weighted - Mean", np.mean(train_results, axis=1)[:, 1], "Std", np.std(train_results, axis=1)[:, 2]
print "Test scores:"
print "All", test_results
print "Precision weighted - Mean", np.mean(test_results, axis=1)[:, 0], "Std", np.std(test_results, axis=1)[:, 0]
print "Recall weighted - Mean", np.mean(test_results, axis=1)[:, 1], "Std", np.std(test_results, axis=1)[:, 1]
print "F1 weighted - Mean", np.mean(test_results, axis=1)[:, 2], "Std", np.std(test_results, axis=1)[:, 2]

train_f1_weighted = train_results[:, :, 2]
test_f1_weighted = test_results[:, :, 2]
plot = plot_learning_curve_no_comp(train_sizes, train_f1_weighted, test_f1_weighted, "Learning Curve Stacker")
plot.savefig("results/stacker_learning_curve.png")
# plot.show()
