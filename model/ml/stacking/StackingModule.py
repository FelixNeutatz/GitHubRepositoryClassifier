# -*- coding: utf-8 -*-
import abc

class StackingModule:
    __metaclass__ = abc.ABCMeta

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
    def test(self, clf, X, y):
        """Method documentation"""
        return

    @abc.abstractmethod
    def predict_proba(self, X):
        """Method documentation"""
        return