# -*- coding: utf-8 -*-

from setuptools import setup, find_packages


with open('README.rst') as f:
    readme = f.read()

with open('LICENSE') as f:
    license = f.read()

setup(
    name='ml',
    version='0.0.1',
    description='GitHub Repository Classifier',
    long_description=readme,
    author='Felix Neutatz, Kevin Kepp',
    author_email='neutatz@gmail.com, keppkevin@gmail.com',
    url='https://github.com/FelixNeutatz/GitHubRepositoryClassifier',
    license=license,
    packages=find_packages(exclude=('tests', 'docs'))
)

