# -*- coding: utf-8 -*-
from sklearn.decomposition import PCA
from sklearn.preprocessing import StandardScaler
import matplotlib.pyplot as plt

from config import Config
from myio import *

category_frames = read(Config.get("feature.extraction.output.path"), 2000)
category_frames = filter_frames(category_frames, get_labeled_data_filter("../../mturk/our_labels/labels.csv"))
train_frame, test_frame = split_train_test(category_frames, test_size=0)
mask = np.asarray(np.ones((1, train_frame.shape[1]), dtype=bool))[0]
mask[0] = False
train_matrix, _ = dataframe_to_numpy_matrix(train_frame, test_frame, mask)
train_x, train_y = split_target_from_data(train_matrix)

scaler = StandardScaler()
train_x = scaler.fit_transform(train_x)

vals = PCA().fit(train_x, train_y).explained_variance_
plt.plot(range(len(vals)), sorted(vals, reverse=True))
plt.title("PC variance based on meta features")
plt.show()
