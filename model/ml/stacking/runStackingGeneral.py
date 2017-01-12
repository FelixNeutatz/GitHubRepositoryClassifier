# -*- coding: utf-8 -*-

from ml.stacking.MetaDataXGB import MetaDataXGB
from ml.stacking.Stacker import Stacker
from ml.stacking.TextDataSVM import TextDataSVM
from ml.stacking.NameDataSVM import NameDataSVM
import pickle
from ml.config import Config


max_samples_per_category = 230
dev_size = 0.5
test_size = 0.1

meta_data_xgb = MetaDataXGB(max_samples_per_category, dev_size, test_size)
text_data_svm = TextDataSVM(max_samples_per_category, dev_size, test_size)
name_data_svm = NameDataSVM(max_samples_per_category, dev_size, test_size)

stacker = Stacker()

stacker.add(meta_data_xgb)
stacker.add(text_data_svm)
stacker.add(name_data_svm)

stacker.build()
stacker.train()

fileObject = open("../persist/model.p", "wb")
pickle.dump(stacker, fileObject)

path_list_a = ["attachmentA.feature.extraction.output.path",
               "attachmentA.feature_text.extraction.output.path",
               "attachmentA.feature_text_names.extraction.output.path"]
path_list_b = ["attachmentB.feature.extraction.output.path",
               "attachmentB.feature_text.extraction.output.path",
               "attachmentB.feature_text_names.extraction.output.path"]
dir_list_a = [Config.get(p) for p in path_list_a]
dir_list_b = [Config.get(p) for p in path_list_b]

print "Stacker Test"
stacker.test()
print "Stacker Test Attachment A"
stacker.test_dirs(dir_list_a)
print "Stacker Test Attachment B"
stacker.test_dirs(dir_list_b)

stacker.visualize_by_tsne()

print "Stacker Modules Test"
stacker.test_modules()
print "Stacker Modules Test Attachment A"
stacker.test_modules_dirs(dir_list_a)
print "Stacker Modules Test Attachment B"
stacker.test_modules_dirs(dir_list_b)

'''
print "Retraining stacker modules on all data"
meta_data_xgb.retrain()
text_data_svm.retrain()
name_data_svm.retrain()
stacker.build_test()
print "Stacker Test"
stacker.test()
print "Stacker Test Attachment A"
stacker.test_dirs(dir_list)
stacker.visualize_by_tsne()
#print "Modules Test"
#stacker.test_all_modules_alone()  # dir_list)
print "Modules Test (also on Attachment A)"
stacker.test_all_modules_alone(dir_list)
'''

'''
print "Test other"
stacker.test_dirs(dir_list, True)
'''
