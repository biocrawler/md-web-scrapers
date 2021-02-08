import json

from django.db import models


class Author(models.Model):
    # author name must be populated
    name = models.CharField(max_length=255)

    def to_json(self):
        return {
            "name": self.name
        }


class Keyword(models.Model):
    # must be a word
    word = models.CharField(max_length=128)
    created_date = models.DateTimeField(auto_now_add=True, null=True)
    modified_date = models.DateTimeField(auto_now=True, null=True)

    def to_json(self):
        return {
            "word": self.word,
            "created_date": str(self.created_date),
            "modified_date": str(self.modified_date)
        }


class Article(models.Model):
    # must have title
    title = models.TextField()
    source_url = models.TextField(blank=True, default='')
    authors = models.ManyToManyField(Author)
    keywords = models.ManyToManyField(Keyword)
    digital_object_id = models.CharField(max_length=128, blank=True, default='')
    refering_url = models.CharField(max_length=255, blank=True, default='')
    description = models.TextField(blank=True, default='')
    parsed = models.BooleanField(default=False)
    enriched = models.BooleanField(default=False)
    published = models.BooleanField(default=False)
    parse_date = models.DateTimeField(null=True)
    upload_date = models.DateTimeField(null=True)
    created_date = models.DateTimeField(auto_now_add=True, null=True)
    modified_date = models.DateTimeField(auto_now=True, null=True)
    additional_data = models.TextField(default='')

    def files(self):
        return self.articlefile_set.all()

    class Meta:
        ordering = ['digital_object_id']

    def __str__(self):
        return self.digital_object_id

    def to_json(self):
        return json.dumps({
            "title": self.title,
            "source_url": self.source_url,
            "authors": [x.to_json() for x in self.authors.all()],
            "keywords": [x.to_json() for x in self.keywords.all()],
            "files": [x for x in self.articlefile_set.all()],
            "digital_object_id": self.digital_object_id,
            "refering_url": self.refering_url,
            "description": self.description,
            "parsed": self.parsed,
            "enriched": self.enriched,
            "published": self.published,
            "parse_date": str(self.parse_date),
            "upload_date": str(self.upload_date),
            "created_date": str(self.created_date),
            "modified_date": str(self.modified_date),
        }, indent=4)


class ArticleFile(models.Model):
    article = models.ForeignKey(Article, on_delete=models.CASCADE)
    keywords = models.ManyToManyField(Keyword)
    # must have file name
    file_name = models.CharField(max_length=255)
    url = models.CharField(max_length=255, blank=True, default='')
    download_url = models.CharField(max_length=255, blank=True, default='')
    digital_object_id = models.CharField(max_length=128, blank=True, default='')
    description = models.TextField(blank=True, default='')
    refering_url = models.CharField(max_length=255, blank=True, default='')
    size = models.FloatField(null=True)

    class Meta:
        ordering = ['file_name']

    def __str__(self):
        return self.file_name
