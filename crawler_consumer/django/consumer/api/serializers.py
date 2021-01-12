from api.models import ArticleFile, Author, Keyword, Article
from rest_framework.compat import unicode_http_header
from rest_framework import serializers

class AuthorSerializer(serializers.ModelSerializer):
   # def to_internal_value(self, validated_data):
   #     instance, _ = Author.objects.get_or_create(**validated_data)
   #     return instance
    class Meta:
        model = Author
        fields = '__all__'

class KeywordSerializer(serializers.ModelSerializer):
   # def to_internal_value(self, validated_data):
   #     instance, _ = Keyword.objects.get_or_create(**validated_data)
   #     return instance
    class Meta:
        model = Keyword
        fields = '__all__'

class ArticleFileSerializer(serializers.ModelSerializer):
    keywords = KeywordSerializer(many=True)
   # def to_internal_value(self, validated_data):
   #     instance, _ = ArticleFile.objects.get_or_create(**validated_data)
   #     return instance

    class Meta:
        model = ArticleFile
        fields = ("keywords", "file_name", "url", "download_url", "digital_object_id",
                  "description", "refering_url", "size")

class ArticleSerializer(serializers.ModelSerializer):
    keywords = KeywordSerializer(many=True)
    authors = AuthorSerializer(many=True)
    files = ArticleFileSerializer(many=True)
   # def to_internal_value(self, validated_data):
   #     instance, _ = Article.objects.get_or_create(**validated_data)
   #     return instance

    class Meta:
        model = Article
        fields = ("title", "source_url", "authors", "keywords", "files",
                  "digital_object_id", "refering_url", "description",
                  "parse_date", "upload_date", "parsed", "enriched", "published",
                  "created_date", "modified_date")

    def create(self, validated_data):
        keywords_data = validated_data.pop('keywords')
        author_list = list()
        authors_data = validated_data.pop('authors')
        files_data = validated_data.pop('files')
        for author_data in authors_data:
            author_list.append(Author.objects.get_or_create(**author_data))
        article = Article.objects.get_or_create(**validated_data)
        article.authors.set(author_list)
        for keyword_data in keywords_data:
            Keyword.objects.get_or_create(article=article, **keyword_data)
        for file_data in files_data:
            ArticleFile.objects.get_or_create(article=article, **file_data)
        return article



