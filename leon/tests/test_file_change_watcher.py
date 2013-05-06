# coding=utf-8

import os
from unittest import TestCase
from leon.file_change_watcher import FileChangeWatcher


class TestFileChangeWatcher(TestCase):
    def test_start_stop(self):
        watcher = FileChangeWatcher(os.path.abspath(os.path.join(os.path.dirname(__file__), 'files', 'watcher')),
                                    None)
        watcher.start()
        self.assertTrue(watcher.is_alive())
        watcher.stop()
        watcher.join(1)
        self.assertFalse(watcher.is_alive())

    # def test_callback(self):
    #     self.called = False
    #
    #     def callback():
    #         self.called = True
    #
    #     watcher = FileChangeWatcher(os.path.abspath(os.path.join(os.path.dirname(__file__), 'files', 'watcher')),
    #                                 callback)
    #
    #     test_file = os.path.abspath(os.path.join(os.path.dirname(__file__), 'files', 'watcher', 'index.html'))
    #     open(test_file, 'w').close()
    #     self.assertTrue(self.called)
