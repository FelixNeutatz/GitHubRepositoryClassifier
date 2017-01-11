# GitHub Repository Classifier

## Setup
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


## Label it - Extension
This little program allows you to label a given list of repository list (in a csv folder) and outputs your manual classification in another csv file.
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
