# coding=utf-8

NOT_OPTIONAL_MARKER = object()


class list_of:
    def __init__(self, member_type):
        self.member_type = member_type

    def convert_list(self, alist):
        return [convert_to_type(i, self.member_type) for i in alist]


def convert_to_type(value, default_value_or_type):
    if type(default_value_or_type) is type:
        target_type = default_value_or_type
    else:
        target_type = type(default_value_or_type)

    # int
    if target_type is int:
        return int(value)

    # bool
    if target_type is bool:
        val = value.lower().strip()
        if val in ('true', 'on', 'yes', '1'):
            return True
        elif val in ('false', 'off', 'no', '0'):
            return False
        else:
            raise Exception('The string "%s" can not be converted to a bool value. '
                            'Supported values are: true/false, on/off, 1/0, yes/no' % value)

    # list
    if target_type is list and type(value) != list:
        return list(value)

    # ListOf
    if isinstance(default_value_or_type, list_of):
        value = value if isinstance(value, list) else [value]
        return default_value_or_type.convert_list(value)

    return value
