# -*- coding: utf-8 -*-

from ml.stacking.MetaDataXGB import MetaDataXGB
from ml.stacking.MetaDataPCA import MetaDataPCA
from ml.stacking.Stacker import Stacker
from ml.stacking.TextDataSVM import TextDataSVM
from ml.stacking.TextDataTruncatedSVD import TextDataTruncatedSVD


max_samples_per_category = 230
test_size = 0.5

meta_data_xgb = MetaDataXGB(max_samples_per_category, test_size)
#meta_data_pca = MetaDataPCA(max_samples_per_category, test_size, n_components=3)
text_data_svm = TextDataSVM(max_samples_per_category, test_size)
#text_data_tsvd = TextDataTruncatedSVD(max_samples_per_category, test_size, n_components=3)

stacker = Stacker()
stacker.add(meta_data_xgb)
#stacker.add(meta_data_pca)
stacker.add(text_data_svm)
#stacker.add(text_data_tsvd)

stacker.build()
stacker.train()

path_list = ["attachmentA.feature.extraction.output.path",
             #"attachmentA.feature.extraction.output.path",
             #"attachmentA.feature_text.extraction.output.path",
             "attachmentA.feature_text.extraction.output.path"]
a_X, a_y = stacker.transform(path_list)

stacker.test(a_X, a_y)

stacker.test_all_modules_alone(path_list)
