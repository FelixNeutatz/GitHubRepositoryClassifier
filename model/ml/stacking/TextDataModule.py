# -*- coding: utf-8 -*-

from ml.stacking.TextModule import TextModule


class TextDataModule(TextModule):

    def __init__(self, max_samples_per_category, dev_size, test_size):
        path_train = "feature_text.extraction.output.path"
        super(TextDataModule, self).__init__(max_samples_per_category, dev_size, test_size, path_train)
