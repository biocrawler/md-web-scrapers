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

def parse_data(data) -> list:
    parsed = list()
    #titled_data = [x for x in data if x['title']]
    for d in data:
        validator = ArticleValidator(d)
        validated_data = validator.get_validated_data()
        #log.info("validated data: " + json.dumps(validated_data))
        parsed.append(validated_data)
    return parsed


def main():
    global article_id
    data = parse_data(load())

    for serialized_data in data:
        serializer = ArticleSerializer(data=serialized_data)
        article_id = -1
        try:
            article_id = serializer.save()
        except Exception as e:
            log.error("could not save data using serializer: " + json.dumps(serialized_data))
            log.error("error", exc_info=True)
        if article_id and article_id != -1:
            continue
            #log.info("article id: " + str(article_id))


if __name__ == '__main__':
    os.environ.setdefault("DJANGO_SETTINGS_MODULE", "api.settings")
    django.setup()
    from api.api_lib.serializers import ArticleSerializer
    main()
