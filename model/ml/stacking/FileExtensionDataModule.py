# -*- coding: utf-8 -*-

from ml.stacking.TextModule import TextModule


class FileExtensionDataModule(TextModule):

    def __init__(self, max_samples_per_category, dev_size, test_size, labeled_data_filter=None):
        path_train = "feature_file_extensions.extraction.output.path"
        super(FileExtensionDataModule, self).__init__(max_samples_per_category, dev_size, test_size, path_train, labeled_data_filter)
