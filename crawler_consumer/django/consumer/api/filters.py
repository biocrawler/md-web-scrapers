import django_filters

from api.models import ArticleFile, Author, Keyword, Article


class ArticleFileFilter(django_filters.FilterSet):
    class Meta:
        model = ArticleFile
        fields = '__all__'

class ArticleFileFilterSingle(django_filters.FilterSet):
    class Meta:
        model = ArticleFile
        fields = []

class AuthorFilter(django_filters.FilterSet):
    class Meta:
        model = Author
        fields = {'author_name': ['icontains']}

class AuthorFilterSingle(django_filters.FilterSet):
    class Meta:
        model = Author
        fields = []

class KeywordFilter(django_filters.FilterSet):
    class Meta:
        model = Keyword
        fields = {'word': ['icontains']}

class KeywordFilterSingle(django_filters.FilterSet):
    class Meta:
        model = Keyword
        fields = []


class ArticleFilter(django_filters.FilterSet):
    class Meta:
        model = Article
        fields = {'title': ['icontains'],
                  'keywords': ['icontains'],
                  'digital_object_id': ['startswith'],
                  'description': ['icontains'],
                  'parsed': ['exact'],
                 'enriched': ['exact']}

class ArticleFilterSingle(django_filters.FilterSet):
    class Meta:
        model = Article
        fields = []
