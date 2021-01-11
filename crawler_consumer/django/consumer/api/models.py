from django.db import models

class Author(models.Model):
    #author name must be populated
    author_name = models.CharField(max_length=255)

class Keyword(models.Model):
    #must be a word
    word = models.CharField(max_length=128)
    created_date = models.DateTimeField(auto_now_add=True)
    modified_date = models.DateTimeField(auto_now=True)

class Article(models.Model):
    #must have title
    title = models.TextField()
    source_url = models.TextField(blank=True, default='')
    authors = models.ManyToManyField(Author)
    keywords = models.ManyToManyField(Keyword)
    digital_object_id = models.CharField(max_length=128, blank=True, default='')
    refering_url = models.CharField(max_length=255, blank=True, default='')
    description = models.TextField(blank=True, default='')
    parse_date = models.DateTimeField(null=True)
    upload_date = models.DateTimeField(null=True)
    parsed = models.BooleanField(default=False)
    enriched = models.BooleanField(default=False)
    published = models.BooleanField(default=False)
    created_date = models.DateTimeField(auto_now_add=True)
    modified_date = models.DateTimeField(auto_now=True)

    class Meta:
        ordering = ['digital_object_id']

    def __str__(self):
        return self.digital_object_id

class ArticleFile(models.Model):
    article = models.ForeignKey(Article, on_delete=models.CASCADE)
    keywords = models.ManyToManyField(Keyword)
    #must have file name
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
