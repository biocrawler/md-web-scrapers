from api.api_lib.log import build_logger
from api.models import Article, Author, Keyword, ArticleFile

log = build_logger(class_name=__name__)

fields = ("title", "source_url",
          "digital_object_id", "refering_url", "description",
          "parsed", "enriched", "published",
          "parse_date", "upload_date", "created_date", "modified_date")

related_fields = ("authors", "keywords", "files")


class ArtcileSerializer(object):

    def __init__(self, data: dict):
        self.data = data
        self.keyword_list = list()
        self.author_list = list()
        self.file_list = list()
        self.title = ""
        self.source_url = ""
        self.digital_object_id = ""
        self.refering_url = ""
        self.description = ""
        self.parsed = False
        self.enriched = False
        self.published = False
        self.parse_date = ""
        self.upload_date = ""
        self.created_date = ""
        self.modified_date = ""

    def _parse_data(self):
        for field in fields + related_fields:
            try:
                d = self.data[field]
                log.info("attempting to setting attribute: " + field + " as " + str(d))
                setattr(self, field, d)

            except Exception as e:
                log.info("cloud not set field: " + field + " for data: " + str(self.data))

    def save(self):
        self._parse_data()
        log.info("saving article")
        article, _ = Article.objects.get_or_create(title=self.title,
                                                   source_url=self.source_url,
                                                   digital_object_id=self.digital_object_id,
                                                   refering_url=self.refering_url,
                                                   description=self.description,
                                                   parsed=self.parsed,
                                                   enriched=self.enriched,
                                                   published=self.published,
                                                   parse_date=self.parse_date,
                                                   upload_date=self.upload_date,
                                                   created_date=self.created_date,
                                                   modified_date=self.modified_date)
        for author_data in self.data['authors']:
            log.info("saving author data: " + str(author_data))
            name = author_data.pop("name")
            assert author_data == {}
            aut, _ = Author.objects.get_or_create(name=name)
            log.info("aut value: " + str(aut))
            article.authors.add(aut)

        print("validated kws data: " + str(self.keyword_list))
        for keyword_data in self.data['keywords']:
            print("validated kw data: " + str(keyword_data))
            kw_word = keyword_data.pop("word")
            kw_created_date = keyword_data.pop("created_date")
            kw_modified_date = keyword_data.pop("modified_date")
            assert keyword_data == {}
            kw,_ = Keyword.objects.get_or_create(word=kw_word,
                                                 created_date=kw_created_date,
                                                 modified_date=kw_modified_date)
            log.info("kw value: " + str(kw))
            article.keywords.add(kw)

        for file_data in self.data['files']:
            f_keywords = file_data.pop("keywords")
            f_file_name = file_data.pop("file_name")
            f_url = file_data.pop("url")
            f_download_url = file_data.pop("url")
            f_digial_object_id = file_data.pop("digital_object_id")
            f_description = file_data.pop("description")
            f_refering_url = file_data.pop("refering_url")
            f_size = file_data.pop("size")
            assert file_data == []
            af,_ = ArticleFile.objects.get_or_create(article=article,
                                                     keworkds=f_keywords,
                                                     file_name=f_file_name,
                                                     url=f_url,
                                                     download_url=f_download_url,
                                                     digital_object_id=f_digial_object_id,
                                                     description=f_description,
                                                     refering_url=f_refering_url,
                                                     size=f_size)
            log.info("articlefile: " + str(af))
        log.info("article save result: " + str(article))
        return article
