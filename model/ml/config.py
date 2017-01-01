# -*- coding: utf-8 -*-
import re

class Config:
    config = dict()

    @staticmethod
    def load():
        configFile = open("../../commons/src/main/resources/conf/conf.properties")

        with configFile as f:
            for line in f:
                splits = re.split('=|\n',line)
                Config.config[splits[0]] = splits[1]

    @staticmethod
    def get(key):
        if len(Config.config) == 0:
            Config.load()
        return Config.config[key]


    @staticmethod
    def load2():
        configFile = open("../../../commons/src/main/resources/conf/conf.properties")

        with configFile as f:
            for line in f:
                splits = re.split('=|\n', line)
                Config.config[splits[0]] = splits[1]

    @staticmethod
    def get2(key):
        if len(Config.config) == 0:
            Config.load2()
        return Config.config[key]
