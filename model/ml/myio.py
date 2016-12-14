# -*- coding: utf-8 -*-

from os import listdir
import pandas as pd
import numpy as np


def find_csv_filenames(path_to_dir, suffix=".csv"):
    filenames = listdir(path_to_dir)
    return [filename for filename in filenames if filename.endswith(suffix)]


def to_tuple(a):
    try:
        return tuple(to_tuple(i) for i in a)
    except TypeError:
        return a


def get_schema(input_dir):
    schema_df = pd.read_csv(input_dir + "/" + "schema.csv", index_col=None, header=None)

    schema_list = []
    for e in schema_df.as_matrix().tolist():
        schema_list.append(e[0])

    return tuple(schema_list)


# read data
def read(input_dir, max_samples_per_category):
    category_list = {"DATA": 0, "EDU": 1, "HW": 2, "DOCS": 3, "DEV": 4, "WEB": 5}

    all_csv_files = find_csv_filenames(input_dir)

    category_frames = []

    for category_file_prefix in category_list.keys():
        category_files = [f for f in all_csv_files if f.startswith("data" + category_file_prefix)]

        frame = pd.DataFrame()
        for file_ in category_files:
            df = pd.read_csv(input_dir + "/" + file_, index_col=None, header=None)

            frame = frame.append(df.head(n=max_samples_per_category))

        category_frames.append(frame)

    return category_frames


# split into train and test set
def split_train_test(category_frames, test_size=0.5):
    train_list_ = []
    test_list_ = []
    for df in category_frames:
        msk = np.random.rand(len(df)) < (1 - test_size)

        train = df[msk]
        test = df[~msk]

        train_list_.append(train)
        test_list_.append(test)

    train_frame = pd.concat(train_list_)
    test_frame = pd.concat(test_list_)

    return train_frame, test_frame


def encode_label(label):
    category_list = {"DATA": 0, "EDU": 1, "HW": 2, "DOCS": 3, "DEV": 4, "WEB": 5}
    return category_list[label]


def dataframe_to_numpy_matrix(train_frame, test_frame, mask):
    column_selection = np.where(mask)[0].tolist()

    # remove url
    train_numerical_frame = train_frame.ix[:, column_selection]
    test_numerical_frame = test_frame.ix[:, column_selection]

    # encode labels
    train_numerical_frame[train_numerical_frame.columns[-1]] = \
        train_numerical_frame[train_numerical_frame.columns[-1]].apply(encode_label)

    test_numerical_frame[test_numerical_frame.columns[-1]] = \
        test_numerical_frame[test_numerical_frame.columns[-1]].apply(encode_label)

    return np.matrix(train_numerical_frame), np.matrix(test_numerical_frame)


def split_target_from_data(all_data):
    x = all_data[:, 0:-1]
    y = all_data[:, -1].A1

    return x, y