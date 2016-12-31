# -*- coding: utf-8 -*-
import abc

class StackingModule:
    __metaclass__ = abc.ABCMeta

    def create_meta_features(self, path):
        X,y = self.transform(path)
        return self.predict_proba(X),y

    def run(self):
        self.load_data()
        self.preprocess()
        self.train()

    @abc.abstractmethod
    def load_data(self):
        """Method documentation"""
        return


    @abc.abstractmethod
    def preprocess(self):
        """Method documentation"""
        return

    @abc.abstractmethod
    def preprocess(self):
        """Method documentation"""
        return

    @abc.abstractmethod
    def train(self):
        """Method documentation"""
        return

    @abc.abstractmethod
    def test(self, X, y):
        """Method documentation"""
        return

    @abc.abstractmethod
    def test_path(self, path):
        """Method documentation"""
        return

    @abc.abstractmethod
    def predict_proba(self, X):
        """Method documentation"""
        return