# -*- coding: utf-8 -*-

from ml.stacking.MetaDataXGB import MetaDataXGB
from ml.stacking.MetaDataPCA import MetaDataPCA
from ml.stacking.Stacker import Stacker
from ml.stacking.TextDataSVM import TextDataSVM
from ml.stacking.TextDataTruncatedSVD import TextDataTruncatedSVD
from ml.stacking.NameDataSVM import NameDataSVM


max_samples_per_category = 230
dev_size = 0.5
test_size = 0.1

meta_data_xgb = MetaDataXGB(max_samples_per_category, dev_size, test_size)
meta_data_pca = MetaDataPCA(max_samples_per_category, dev_size, test_size, n_components=3)

text_data_svm = TextDataSVM(max_samples_per_category, dev_size, test_size)
text_data_tsvd = TextDataTruncatedSVD(max_samples_per_category, dev_size, test_size, n_components=3)

name_data_svm = NameDataSVM(max_samples_per_category, dev_size, test_size)

stacker = Stacker()

stacker.add(meta_data_xgb)
#stacker.add(meta_data_pca)

stacker.add(text_data_svm)
#stacker.add(text_data_tsvd)

stacker.add(name_data_svm)

stacker.build()
stacker.train()

path_list = [
             "attachmentA.feature.extraction.output.path"
             #,"attachmentA.feature.extraction.output.path"
             ,"attachmentA.feature_text.extraction.output.path"
             #,"attachmentA.feature_text.extraction.output.path"
             ,"attachmentA.feature_text_names.extraction.output.path"
             ]
print "Stacker Test"
stacker.test()
print "Stacker Test Attachment A"
stacker.test_path(path_list)
#print "Modules Test"
#stacker.test_all_modules_alone()  # path_list)

print "Modules Test on Attachment A"
stacker.test_all_modules_alone(path_list)
