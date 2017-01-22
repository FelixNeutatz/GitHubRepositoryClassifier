import pickle
import numpy as np

from ml import myio
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

# print "Stacker Test Attachment A"
# stacker.test_dirs(dir_list_a)
# print "Stacker Test Attachment B"
# stacker.test_dirs(dir_list_b)
# print "Stacker TSNE"
# stacker.visualize_by_tsne()

'''
print "Test other"
stacker.test_dirs(dir_list, True)
'''


def print_probas(path_list, max_samples):
    dir_list = [Config.get2(p) for p in path_list]
    frames = myio.concat(myio.read(dir_list[0], max_samples))
    ids = frames[frames.columns[0]].as_matrix()
    labels = frames[frames.columns[-1]].as_matrix()
    probas = stacker.predict_dirs(dir_list)

    category_list_inv = {0: "DATA", 1: "EDU", 2: "HW", 3: "DOCS", 4: "DEV", 5: "WEB"}

    for id, probs, label in zip(ids, probas, labels):
        label = str(label)
        out = str(id) + " - "
        pred_i = np.argmax(probs)
        out += "pred: " + category_list_inv[pred_i] + " (" + str(probs[pred_i]) + ") - "
        out += "label: " + label + " (" + str(probs[myio.category_list[label]]) + ") - "
        out += "probas: "
        for i in range(0, len(probs)):
            out += category_list_inv[i] + " " + str(probs[i]) + ", "
        print out[:-2]

# print "Predict Attachment A"
# print_probas(path_list_a, 5)
print "Predict Attachment B"
print_probas(path_list_b, 20)
