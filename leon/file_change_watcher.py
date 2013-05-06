# coding=utf-8

import os
from threading import Thread
from time import sleep


class FileChangeWatcher(Thread):
    def __init__(self, path_to_watch, callback_on_change):
        super(FileChangeWatcher, self).__init__()
        self.setDaemon(True)
        self.pattern = ['html', 'css']
        self.stopped = False
        self.path_to_watch = path_to_watch
        self.callback_on_change = callback_on_change

    def stop(self):
        self.stopped = True

    def run(self):

        for top, dirs, files in os.walk(self.path_to_watch):
            for nm in files:
                print(os.path.join(top, nm))

        while not self.stopped:
            sleep(0.1)
