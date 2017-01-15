import sys
import time
from PyQt4.QtCore import *
from PyQt4.QtGui import *
from PyQt4.QtWebKit import *
from PIL import ImageTk
import PIL.Image
from Tkinter import *
from functools import partial
import pandas as pd
import os.path
import csv
import random
import Queue
import numpy as np
from optparse import OptionParser
import webbrowser


class Screenshot(QWebView):
    def __init__(self):
        self.app = QApplication(sys.argv)
        QWebView.__init__(self)
        self._loaded = False
        self.loadFinished.connect(self._loadFinished)

    def capture(self, url, output_file):
        self.load(QUrl(url))
        self.wait_load()
        # set to webpage size
        frame = self.page().mainFrame()
        self.page().setViewportSize(frame.contentsSize())
        # render image
        image = QImage(self.page().viewportSize(), QImage.Format_ARGB32)

        image = image.copy(0, 99, image.size().width(), image.size().height() - 99)

        painter = QPainter(image)
        frame.render(painter)
        painter.end()
        print 'saving', output_file

        new_image = image.copy(0, 99, image.size().width(), image.size().height() - 99)

        new_image.save(output_file)
        return new_image

    def wait_load(self, delay=0):
        # process app events until page loaded
        while not self._loaded:
            self.app.processEvents()
            time.sleep(delay)
        self._loaded = False

    def _loadFinished(self, result):
        self._loaded = True


parser = OptionParser()
parser.add_option("-i", "--input", dest="input",
                  help="input csv file\ndefault: '../../mturk/task.csv'", metavar="FILE",
                  default="../../mturk/task.csv")
parser.add_option("-o", "--output", dest="output",
                  help="output csv file\ndefault: 'labels.csv'", metavar="FILE",
                  default="labels.csv")
parser.add_option("-n", "--numberRepos", dest="n",
                  help="number of repositories to label")
parser.add_option("-c", "--columnURLindex", dest="column_index",
                  help="index of the column which contains the repository urls (0 .. n-1)\ndefault:last column")
parser.add_option("-l", "--labeledDataFile", dest="labels", help="path to previous output file, will skip already"
                                                                 "labeled urls")

(options, args) = parser.parse_args()


input = options.input
output = options.output

column_index = options.column_index
if column_index is None:
    column_index = -1

df = pd.read_csv(input, encoding='utf-8')
urls = df[df.columns[column_index]].tolist()

# remove already labeled files from list of urls
labels_file = options.labels
if labels_file is not None:
    with open(labels_file) as f:
        labeled_urls = np.asarray([unicode(l.strip().split(",")[0]) for l in f.readlines()])
    print "Dismissing {0} samples".format(len([u for u in urls if u in labeled_urls]))
    urls = [u for u in urls if u not in labeled_urls]

N = 0
if options.n is None:
    N = len(urls)
else:
    N = int(options.n)

ids = range(0,N)
random.shuffle(ids)


with open(output, 'wb', buffering=1) as csvfile:
    label_writer = csv.writer(csvfile, delimiter=",")

    root = Tk()  # main window
    root.wm_title("Label it")

    top = Frame(root)
    top.pack(side=TOP)

    img_counter = 0

    def callback(event):
        webbrowser.open(urls[ids[img_counter]], new=0, autoraise=True)

    def save_click_and_continue(label=None, number_records=1):
        global img_counter
        global img

        print str(urls[ids[img_counter]]) + " " + str(label) + " " + str(img_counter)
        label_writer.writerow([urls[ids[img_counter]], label])

        # update image
        if imagesQ.empty():
            root.destroy()
            print "end!"
            return

        pil_img_new = imagesQ.get()
        img.paste(pil_img_new)

        panel.configure(image=img)

        img_counter += 1

        # preloading
        if img_counter + 4 < number_records:
            imagesQ.put(get_screenshot(img_counter + 4, urls, ids))


    imagesQ = Queue.Queue(maxsize=10)

    s = Screenshot()

    def get_screenshot(i, urls, ids):
        s.capture(urls[ids[i]], "/tmp/img" + str(ids[i]) + ".jpeg")
        pil_img = PIL.Image.open("/tmp/img" + str(ids[i]) + ".jpeg")
        os.remove("/tmp/img" + str(ids[i]) + ".jpeg")
        return pil_img


    img = ImageTk.PhotoImage(get_screenshot(0, urls, ids))
    for i in range(1, min(5, N)):
        imagesQ.put(get_screenshot(i, urls, ids))


    panel = Label(root, image = img)
    panel.pack(side = "bottom", fill = "both", expand = "yes")
    panel.bind("<Button-1>", callback)

    button_values = {'DATA': 'DATA', 'EDU':'EDU', 'WEB':'WEB', 'HW':'HW', 'DOCS':'DOCS', 'DEV':'DEV', 'OTHER': 'OTHER',
                     'not sure': '?'}

    for key, value in button_values.iteritems():
        button = Button(root, text = key, command = partial(save_click_and_continue, value, N))
        button.pack(in_=top, side=LEFT, pady=20, padx = 20)

    root.mainloop()
