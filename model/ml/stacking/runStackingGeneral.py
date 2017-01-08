# -*- coding: utf-8 -*-

from ml.stacking.MetaDataXGB import MetaDataXGB
from ml.stacking.Stacker import Stacker
from ml.stacking.TextDataSVM import TextDataSVM
from ml.stacking.NameDataSVM import NameDataSVM


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

path_list = [
             "attachmentA.feature.extraction.output.path"
             ,"attachmentA.feature_text.extraction.output.path"
             ,"attachmentA.feature_text_names.extraction.output.path"
             ]

print "Stacker Test"
stacker.test()
print "Stacker Test Attachment A"
stacker.test_path(path_list)

stacker.visualize_by_tsne()

#print "Modules Test"
#stacker.test_all_modules_alone()  # path_list)

print "Modules Test on Attachment A"
stacker.test_all_modules_alone(path_list)
