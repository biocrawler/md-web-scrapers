import json
from datetime import  datetime
import re

from api.api_lib.log import build_logger

log = build_logger()

ARTICLE_KEYS = {"title": "",
                "source_url": "",
                "authors": [],
                "keywords": [],
                "files": [],
                "digital_object_id": "",
                "refering_url": "",
                "description": "",
                "parse_date": "",
                "upload_date": "",
                "parsed": False,
                "enriched": False,
                "published": False,
                "created_date": "",
                "modified_date": ""}


class ArticleValidator(object):
    def __init__(self, data):
        self.data = self.load_data(data)

    def load_data(self, data: object) -> dict:
        if data == None:
            return dict()
        if isinstance(data, dict):
            self.data = data
        if isinstance(data, str):
            try:
                self.data = json.loads(data)
            except:
                log.info("cloud not load json from data string")
        return self.data

    def get_validated_data(self) -> dict:
        for key in ARTICLE_KEYS:
            try:
                if self.data.get(key) == None:
                    self.data[key] = ARTICLE_KEYS.get(key)
            except:
                log.error("exception during adding of key: " + key + " to data: " + str(self.data))
            try:
                import dateutil.parser
                if 'date' in key and self.data[key] == None:
                    #self.data[key] = str(datetime.now().isoformat())
                    self.data[key] = None
                    continue
                if 'date' in key and self.data[key] == "":
                    #self.data[key] = str(datetime.now().isoformat())
                    self.data[key] = None
                    continue
                if 'date' in key:
                    d = self.data[key]
                    assert isinstance(d, str)
                    parsed_date = dateutil.parser.parse(d).isoformat()
                    self.data[key] = str(parsed_date)
            except Exception as e:
                log.error("error while processing dates in data", e)
            try:
                if 'authors' in key and self.data[key] == None:
                    self.data[key] = []
                    continue
            except Exception as e:
                log.error("error while processing authors in data", e)
            try:
                if 'keywords' in key and self.data[key] == None:
                    self.data[key] = []
                    continue
                if 'keywords' in key and isinstance(self.data[key], list) and len(self.data[key]) > 1:
                    if hasattr(self.data[key][0], "word"):
                        continue
                if 'keywords' in key:
                    kw_list = list()
                    for kw in self.data[key]:
                        if hasattr(kw, "word"):
                            log.info("word: " + str(kw))
                            kw_list.append({"word": kw.get("word"), "created_date": "", "modified_date": ""})
                        else:
                            log.info("keyword not valid: " + str(kw) + " adding to keyword object")
                            kw_list.append({"word": kw, "created_date": None, "modified_date": None})
                    self.data[key] = kw_list
            except Exception as e:
                log.error("error while processing keywords in data", e)
            try:
                if 'source_url' in key:
                    self.data[key] = self.data[key] if self.data[key] else self.data.get("sourceUrl", "")
                    continue

            except Exception as e:
                log.error("error while processing source in data", e)

            try:
                if 'digital_object_id' in key:
                    self.data[key] = self.data[key] if self.data[key] else self.data.get("digitalObjectId", "")
                    continue

            except Exception as e:
                log.error("error while processing source in data", e)
            try:
                if 'files' in key and self.data[key] == None:
                    self.data[key] = []
                    continue
                if 'files' in key and isinstance(self.data[key], list):
                    files = self.data[key]
                    file_list = list()
                    for file in files:
                        if not file.get("file_name"):
                            continue
                        if not file.get('keywords'):
                            file['keywords'] = []
                            file_list.append(file)
                        if not file.get("download_url"):
                            file['download_url'] = ''
                        if not file.get("url"):
                            file['url'] = ''
                        if not file.get("digital_object_id"):
                            file['digital_object_id'] = ''
                    self.data[key] = file_list

            except Exception as e:
                log.error("error while processing authors in data", e)
        log.info("validator returing data: " + str(self.data))
        return self.data

def snake_to_camel(word):
    return ''.join(x.capitalize() or '_' for x in word.split('_'))

