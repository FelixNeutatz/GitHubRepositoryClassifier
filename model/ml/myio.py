# -*- coding: utf-8 -*-

from os import listdir
import pandas as pd
import numpy as np
import csv
import sys
import math

category_list = {"DATA": 0, "EDU": 1, "HW": 2, "DOCS": 3, "DEV": 4, "WEB": 5, "null": 6}


def find_csv_filenames(path_to_dir, suffix=".csv"):
    filenames = listdir(path_to_dir)
    return [filename for filename in filenames if filename.endswith(suffix)]


def get_schema(input_dir):
    schema_df = pd.read_csv(input_dir + "/" + "schema.csv", index_col=None, header=None)

    schema_list = []
    for e in schema_df.as_matrix().tolist():
        schema_list.append(e[0])

    return tuple(schema_list)


def read_stop_words():
    stop_word_list = []
    file_ = open("stop_words/stop_words_search.txt", 'rb')
    try:
        reader = csv.reader(file_)
        for row in reader:
            stop_word_list.append(row)
    finally:
        file_.close()

    return np.asarray(stop_word_list).ravel()


def read_stop_words2():
    stop_word_list = []
    file_ = open("../stop_words/stop_words_search.txt", 'rb')
    try:
        reader = csv.reader(file_)
        for row in reader:
            stop_word_list.append(row)
    finally:
        file_.close()

    return np.asarray(stop_word_list).ravel()


# read data
def read(input_dir, max_samples_per_category):
    all_csv_files = find_csv_filenames(input_dir)

    category_frames = []

    for category_file_prefix in category_list.keys():
        category_files = [f for f in all_csv_files if f.startswith("data" + category_file_prefix)]

        frame = pd.DataFrame()
        for file_ in category_files:
            df = pd.read_csv(input_dir + "/" + file_, index_col=None, header=None, encoding='utf-8', quotechar="\'")

            frame = frame.append(df.head(n=max_samples_per_category))

        category_frames.append(frame)

    return category_frames


def read_native(input_dir, max_samples_per_category):
    csv.field_size_limit(sys.maxsize)

    all_csv_files = find_csv_filenames(input_dir)

    category_frames = []

    for category_file_prefix in category_list.keys():
        category_files = [f for f in all_csv_files if f.startswith("data" + category_file_prefix)]

        frame = pd.DataFrame()
        for file_s in category_files:
            file_data_array = []
            file_ = open(input_dir + "/" + file_s, 'rb')
            try:
                reader = csv.reader(file_, delimiter=",", quotechar="\'")
                for row in reader:
                    # TODO why can len(row) < 3
                    if len(row) < 3:
                        print "Error: row", row, "has only length", len(row)
                    else:
                        file_data_array.append(row)
            finally:
                file_.close()
            df = pd.DataFrame(file_data_array)
            frame = frame.append(df.head(n=max_samples_per_category))

        category_frames.append(frame)

    return category_frames


# split into train and test set
def split_train_test(category_frames, test_size=0.5, random_seed=42):
    train_list_ = []
    test_list_ = []
    np.random.seed(random_seed)
    for df in category_frames:
        train_len = int(math.ceil(len(df) * (1 - test_size)))
        test_len = int(math.floor(len(df) * test_size))
        msk = np.ones((train_len,))
        msk = np.concatenate((msk, np.zeros((test_len,))))
        msk = msk == 1.
        np.random.shuffle(msk)

        train = df[msk]
        test = df[~msk]

        train_list_.append(train)
        test_list_.append(test)

    train_frame = pd.concat(train_list_)
    test_frame = pd.concat(test_list_)

    return train_frame, test_frame


def concat(category_frames):
    df_list_ = []
    for df in category_frames:
        df_list_.append(df)

    df_frame = pd.concat(df_list_)

    return df_frame


def encode_label(label):
    # todo: what is going wrong here
    if label is None:
        print "warning"
        return -1
    return category_list[label]


def dataframe_to_matrix(frame, mask):
    column_selection = np.where(mask)[0].tolist()

    # remove url
    numerical_frame = frame.ix[:, column_selection]

    # encode labels
    numerical_frame[numerical_frame.columns[-1]] = \
        numerical_frame[numerical_frame.columns[-1]].apply(encode_label)

    return numerical_frame


def get_id_from_frame(frame):
    return frame[frame.columns[0]]


def dataframe_to_numpy_matrix_single(frame, mask):
    return np.matrix(dataframe_to_matrix(frame, mask))


def dataframe_to_numpy_matrix(train_frame, test_frame, mask):
    train_numerical_frame = dataframe_to_numpy_matrix_single(train_frame, mask)
    test_numerical_frame = dataframe_to_numpy_matrix_single(test_frame, mask)

    return train_numerical_frame, test_numerical_frame


def split_target_from_data(all_data):
    x = all_data[:, 0:-1]
    y = all_data[:, -1].A1
    return x, y


def get_labeled_data_filter(path):
    with open(path) as f:
        lines = f.readlines()
        labeled_data = [l.strip().split(",") for l in lines if "," in l != -1]
        labeled_data += [l.strip().split(" ") for l in lines if " " in l != -1]
        labeled_data_filter = {}
        for repo, label in labeled_data:
            if label not in labeled_data_filter:
                labeled_data_filter[label] = []
            labeled_data_filter[label].append(repo)
        return labeled_data_filter


def filter_frames(category_frames, labeled_data_filter):
    print([f.shape for f in category_frames])
    # only keep correctly labeled data in category frames
    for i in range(len(category_frames)):
        category = category_list.keys()[i]
        if category != "null":
            frames = category_frames[i]
            repos = labeled_data_filter[category]
            category_frames[i] = frames[frames[0].isin(repos)]
            # remove repos from filter that are kept in category frames
            # labeled_data_filter[category] = [r for r in repos if r not in category_frames[i][0].tolist()]
    print([f.shape for f in category_frames])
    return category_frames
