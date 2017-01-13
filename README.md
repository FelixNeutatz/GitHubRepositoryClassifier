# GitHub Repository Classifier

## Setup

Disclaimer: We implemented everything running on Linux. So this code might not run smoothly on Windows!

First, you need to configure the paths `../GitHubRepositoryClassifier/commons/src/main/resources/conf/conf.properties`.
More over you need to list all GitHub Authentication Files in a csv file and specify this path as `sample.generation.git-accounts.file`.

### Configuration

#### API authentication tokens
First, you need to generate a [GitHub access token](https://help.github.com/articles/creating-an-access-token-for-command-line-use/). Now create a file, e.g. "/path/mygittoken.txt". Then you enter the generated token in the following way (without brackets):
```
oauth=[my GitHub access token]
```
If you want to use multiple tokens to speed up downloading, save each token in another file e.g. "/path/mygittoken2.txt" ...
Then create a file "/path/tokenfilelist.csv" which contains all the token files like this:
```
/path/mygittoken.txt
/path/mygittoken2.txt
...
```
Now you specify this file in `../GitHubRepositoryClassifier/commons/src/main/resources/conf/conf.properties`:
```
sample.generation.git-accounts.file=/path/tokenfilelist.csv
```

#### Downloading metadata information by queries using API
If you wanto run [GenerateSamples](https://github.com/FelixNeutatz/GitHubRepositoryClassifier/blob/master/samplegeneration/src/main/java/tu/kn/ghrepoclassifier/generation/main/GenerateSamples.java) you need to specify where you want to store the downloaded meta information. To do so you can specify it in `../GitHubRepositoryClassifier/commons/src/main/resources/conf/conf.properties`:
```
sample.generation.output.path=/path/downloadedMetaData
```
#### Downloading metadata information by queries using API for the attachments
If you wanto run [DownloadAttachmentRepos](https://github.com/FelixNeutatz/GitHubRepositoryClassifier/blob/master/samplegeneration/src/main/java/tu/kn/ghrepoclassifier/generation/main/DownloadAttachmentRepos.java) you need to specify where you want to store the downloaded meta information of the repositories in attachment A and B. To do so you can specify it in `../GitHubRepositoryClassifier/commons/src/main/resources/conf/conf.properties`:
```
attachmentA.download.output.path=/path/downloadedMetaDataAttachmentA
attachmentB.download.output.path=/path/downloadedMetaDataAttachmentB
```
Moreover you need to specify where the repository list for each attachment is stored:
```
attachmentA.repos.file=/path/attachmentA.csv
attachmentB.repos.file=/path/attachmentB.csv
```

#### Downloading repository content
If you wanto run [ContentDownloader](https://github.com/FelixNeutatz/GitHubRepositoryClassifier/blob/master/samplegeneration/src/main/java/tu/kn/ghrepoclassifier/generation/main/ContentDownloader.java) you need to specify where you want to store the downloaded content of all queried repositories and all repositories of the attachments. To do so you can specify it in `../GitHubRepositoryClassifier/commons/src/main/resources/conf/conf.properties`:
```
sample.generation.content.output.path=/path/RepoContent
```

#### Extracting features
If you wanto run [Extractor](https://github.com/FelixNeutatz/GitHubRepositoryClassifier/blob/master/featureextraction/src/main/java/tu/kn/ghrepoclassifier/featureextraction/Extractor.java) you need to specify where you want to store the extracted metadata features of all queried repositories and all repositories of the attachments. To do so you can specify it in `../GitHubRepositoryClassifier/commons/src/main/resources/conf/conf.properties`:
```
feature.extraction.output.path=/path/MetaDataFeaturesQueries
attachmentA.feature.extraction.output.path=/path/MetaDataFeaturesAttachmentA
attachmentB.feature.extraction.output.path=/path/MetaDataFeaturesAttachmentB
```
Moreover you need to specify where to store the text features:
```
feature_text.extraction.output.path=/path/TextFeaturesQueries
attachmentA.feature_text.extraction.output.path=/path/TextFeaturesAttachmentA
attachmentB.feature_text.extraction.output.path=/path/TextFeaturesAttachmentB
```
And finally you need to define where to store the file and folder names features:
```
feature_text_names.extraction.output.path=/path/NameFeaturesQueries
attachmentA.feature_text_names.extraction.output.path=/path/NameFeaturesAttachmentA
attachmentB.feature_text_names.extraction.output.path=/path/NameFeaturesAttachmentB
```

### Maven build
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
