# -*- coding: utf-8 -*-


import pandas as pd
import csv
from optparse import OptionParser
import subprocess
import os.path
import pickle

parser = OptionParser()
parser.add_option("-i", "--input", dest="input",
                  help="input csv file\ndefault: ...", metavar="FILE",
                  default="/home/felix/GitHubRepositoryClassifier/test_set/test.csv")
parser.add_option("-o", "--output", dest="output",
                  help="output csv file default: 'output.csv'", metavar="FILE",
                  default="output.csv")
parser.add_option("-c", "--columnURLindex", dest="column_index",
                  help="index of the column which contains the repository urls (0 .. n-1) default:first(0) column")

(options, args) = parser.parse_args()

input = options.input
output = options.output

column_index = options.column_index
if column_index is None:
    column_index = 0

df = pd.read_csv(input, encoding='utf-8', header=None)

urls = df[df.columns[column_index]].as_matrix()

print urls

# load classification model
stacker = pickle.load(open("persist/model.p", "rb"))


if not os.path.exists("../../samplegeneration/target/samplegeneration-1.0-SNAPSHOT.jar"):
    subprocess.call(['mvn -f ../../pom.xml clean install -DskipTests'], shell=True)

# download via API
subprocess.call(['java -cp ../../samplegeneration/target/samplegeneration-1.0-SNAPSHOT.jar ' +
                 'tu.kn.ghrepoclassifier.generation.main.DownloadRepos ' +
                 '-input ' + input + ' ' +
                 '-columnURLindex ' + str(column_index)
                 ], shell=True)

# download compressed repository content
subprocess.call(['java -cp ../../samplegeneration/target/samplegeneration-1.0-SNAPSHOT.jar ' +
                 'tu.kn.ghrepoclassifier.generation.main.RepoContentDownloader'
                 ], shell=True)

if not os.path.exists("../../featureextraction/target/featureextraction-1.0-SNAPSHOT.jar"):
    subprocess.call(['mvn -f ../../pom.xml clean install -DskipTests'], shell=True)

#extract features
subprocess.call(['java -cp ../../featureextraction/target/featureextraction-1.0-SNAPSHOT.jar ' +
                 'tu.kn.ghrepoclassifier.featureextraction.ExtractTest'
                 ], shell=True)


