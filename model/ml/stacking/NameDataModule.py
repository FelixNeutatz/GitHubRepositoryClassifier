# -*- coding: utf-8 -*-

from ml.stacking.TextModule import TextModule


class NameDataModule(TextModule):

    def __init__(self, max_samples_per_category, dev_size, test_size):
        super(NameDataModule, self).__init__(max_samples_per_category, dev_size, test_size,
                                             "feature_text_names.extraction.output.path")
