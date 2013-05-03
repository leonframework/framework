# coding=utf-8

from unittest import TestCase

from leon.arg_conversions import convert_to_type, list_of


class TestArgConversions(TestCase):
    def test_int_by_type(self):
        self.assertEqual(convert_to_type("1", int), 1)

    def test_int_by_default_value(self):
        self.assertEqual(convert_to_type("1", 5), 1)

    def test_list_by_type(self):
        self.assertEqual(convert_to_type("1", list), ["1"])

    def test_list_of_ints(self):
        self.assertEqual(convert_to_type("1", list_of(int)), [1])

    def test_bool(self):
        self.assertTrue(convert_to_type("true", bool))
        self.assertTrue(convert_to_type("on", bool))
        self.assertTrue(convert_to_type("yes", bool))
        self.assertTrue(convert_to_type("1", bool))
        self.assertFalse(convert_to_type("false", bool))
        self.assertFalse(convert_to_type("off", bool))
        self.assertFalse(convert_to_type("no", bool))
        self.assertFalse(convert_to_type("0", bool))

    def test_argument_conversion_bool_wrong_value(self):
        self.assertRaises(Exception, lambda: convert_to_type("X", bool))
