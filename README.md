# GitHub Repository Classifier

## Setup

Disclaimer: We implemented everything running on Linux. So this code might not run smoothly on Windows!

First, you need to configure the paths `../GitHubRepositoryClassifier/commons/src/main/resources/conf/conf.properties`.
More over you need to list all GitHub Authentication Files in a csv file and specify this path as `sample.generation.git-accounts.file`.

Then you can run:

```
git clone https://github.com/FelixNeutatz/GitHubRepositoryClassifier.git
cd GitHubRepositoryClassifier
mvn clean install
```

If you don't want to specify the paths you can just run:

```
mvn clean install -DskipTests
```

In order to be able to run all Python programs, please make sure you have installed [all required packages](https://github.com/FelixNeutatz/GitHubRepositoryClassifier/blob/master/model/requirements.txt).

## Automatic classification
You can automatically classify GitHub repositories given in a csv file. But you have to finish the setup before!

```
cd GitHubRepositoryClassifier/model/ml
python classify.py --help
Usage: classify.py [options]

Options:
  -h, --help            show this help message and exit
  -i FILE, --input=FILE
                        input csv file
  -o FILE, --output=FILE
                        output csv file default: 'output.csv'
  -c COLUMN_INDEX, --columnURLindex=COLUMN_INDEX
                        index of the column which contains the repository urls
                        (0 .. n-1) default:first(0) column
```

## Label it - Extension
This little program allows you to label a given list of repository list (in a csv folder) and outputs your manual classification in another csv file.

One requirement is that you install PyQt4: `sudo apt-get install python-qt4`

```
cd ../GitHubRepositoryClassifier/model/ml/
python label_it.py --help
Usage: label_it.py [options]

Options:
  -h, --help            show this help message and exit
  -i FILE, --input=FILE
                        input csv file default: '../../mturk/task.csv'
  -o FILE, --output=FILE
                        output csv file default: 'labels.csv'
  -n N, --numberRepos=N
                        number of repositories to label
  -c COLUMN_INDEX, --columnURLindex=COLUMN_INDEX
                        index of the column which contains the repository urls
                        (0 .. n-1) default:last column
```

It will look like this:
![screenshot_label_it_tool](https://cloud.githubusercontent.com/assets/5217389/21830339/46eef208-d79c-11e6-872b-4db396bd7bb4.png)
