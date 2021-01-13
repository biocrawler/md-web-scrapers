#!/usr/bin/env python3
import json
import os

import django

from api.api_lib.log import build_logger
from api.api_lib.validators import ArticleValidator

log = build_logger()


def load():
    with open('/home/sgarcia/export.json') as s:
        return json.load(s)


def pase_enriched_value(d):
    return d.get("enriched", False) if d.get("enriched") != None else False


def parse_parsed_value(d):
    return d.get("parsed", False) if d.get("parsed") != None else False


def parse_description(d):
    return d.get("description", "") if d.get("description") != None else ""


def parse_digital_object_id(d):
    return d.get("digital_object_id", "") if d.get("digital_object_id") != None else ""



def parse_data(data) -> list:
    parsed = list()
    titled_data = [x for x in data if x['title']]
    for d in titled_data:
        validator = ArticleValidator(d)
        validated_data = validator.get_validated_data()
        #log.info("validated data: " + json.dumps(validated_data))
        parsed.append(validated_data)
    return parsed


def main():
    global article_id
    from api.serializers import ArticleSerializer
    data = parse_data(load())

    for serialized_data in data:
        serializer = ArticleSerializer(data=serialized_data)
        print("serializer valid result: " + str(serializer.is_valid(raise_exception=True)))
        article_id = -1
        try:
            article_id = serializer.save()
        except Exception as e:
            log.error("could not save data using serializer: " + json.dumps(serialized_data))
            log.error("error", exc_info=True)
        if article_id:
            print("article id: " + str(article_id))


if __name__ == '__main__':
    os.environ.setdefault("DJANGO_SETTINGS_MODULE", "api.settings")
    django.setup()
    main()
