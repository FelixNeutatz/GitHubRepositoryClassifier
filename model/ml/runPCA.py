# -*- coding: utf-8 -*-
from sklearn.decomposition import PCA
from sklearn.preprocessing import StandardScaler
import matplotlib.pyplot as plt

from config import Config
from myio import *

category_frames = read(Config.get("feature.extraction.output.path"), 250)
train_frame, test_frame = split_train_test(category_frames, test_size=0)
mask = np.asarray(np.ones((1, train_frame.shape[1]), dtype=bool))[0]
mask[0] = False
train_matrix, _ = dataframe_to_numpy_matrix(train_frame, test_frame, mask)
train_x, train_y = split_target_from_data(train_matrix)

scaler = StandardScaler()
train_x = scaler.fit_transform(train_x)

vals = PCA().fit(train_x, train_y).explained_variance_
plt.plot(range(len(vals)), sorted(vals, reverse=True))
plt.show()
