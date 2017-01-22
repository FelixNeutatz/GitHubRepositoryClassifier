import pickle

from ml.config import Config

with open("../persist/model.p") as f:
  stacker = pickle.load(f)

path_list_a = ["attachmentA.feature.extraction.output.path",
               "attachmentA.feature_text.extraction.output.path",
               "attachmentA.feature_text_names.extraction.output.path"]
path_list_b = ["attachmentB.feature.extraction.output.path",
               "attachmentB.feature_text.extraction.output.path",
               "attachmentB.feature_text_names.extraction.output.path"]
dir_list_a = [Config.get2(p) for p in path_list_a]
dir_list_b = [Config.get2(p) for p in path_list_b]

print "Stacker Test Attachment A"
stacker.test_dirs(dir_list_a)
print "Stacker Test Attachment B"
stacker.test_dirs(dir_list_b)
print "Stacker TSNE"
stacker.visualize_by_tsne()

'''
print "Test other"
stacker.test_dirs(dir_list, True)
'''

'''
print probabilities on attachment A
print myio.category_list
frames = myio.concat(myio.read(Config.get2(path_list_a[0]), 5))
ids = frames[frames.columns[0]].as_matrix()
labels = frames[frames.columns[-1]].as_matrix()
probas = stacker.predict_dirs(dir_list_a)

category_list_inv = {0: "DATA", 1: "EDU", 2: "HW", 3: "DOCS", 4: "DEV", 5: "WEB"}

for id, probs, label in zip(ids, probas, labels):
    out = str(id) + " - "
    for i in range(0, len(probs)):
        out += category_list_inv[i] + ": " + str(probs[i]) + " "
    out += "label: " + str(label) + " - " + str(myio.category_list[str(label)])
    print out
'''
