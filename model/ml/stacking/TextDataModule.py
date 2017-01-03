# -*- coding: utf-8 -*-

from ml.stacking.TextModule import TextModule


class TextDataModule(TextModule):

    def __init__(self, max_samples_per_category, dev_size, test_size):
        super(TextDataModule, self).__init__(max_samples_per_category, dev_size, test_size,
                                             "feature_text.extraction.output.path")