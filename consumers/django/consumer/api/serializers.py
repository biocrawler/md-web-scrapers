from api.models import ArticleFile, Author, Keyword, Article
from rest_framework import serializers

DATE_FORMATS = ['%Y-%m-%dT%H:%M:%SZ', ]


class AuthorSerializer(serializers.ModelSerializer):
   # def to_internal_value(self, validated_data):
   #     instance, _ = Author.objects.get_or_create(**validated_data)
   #     return instance
    class Meta:
        model = Author
        fields = '__all__'

class KeywordSerializer(serializers.ModelSerializer):
    word = serializers.CharField()
   # def to_internal_value(self, validated_data):
   #     instance, _ = Keyword.objects.get_or_create(**validated_data)
   #     return instance
    class Meta:
        model = Keyword
        fields = '__all__'
        #fields = []

class ArticleFileSerializer(serializers.ModelSerializer):
    keywords = KeywordSerializer(many=True)
   # def to_internal_value(self, validated_data):
   #     instance, _ = ArticleFile.objects.get_or_create(**validated_data)
   #     return instance

    class Meta:
        model = ArticleFile
        fields = '__all__'
        #fields = ("keywords", "file_name", "url", "download_url", "digital_object_id",
        #          "description", "refering_url", "size")

class ArticleSerializer(serializers.ModelSerializer):
    keywords = KeywordSerializer(many=True)
    authors = AuthorSerializer(many=True)
    files = ArticleFileSerializer(many=True)
    parse_date = serializers.CharField(required=False)
    
    #def validate_parse_date(self, attrs, source=None):
    #    return attrs

    class Meta:
        model = Article
        fields = '__all__'
        #fields = ("title", "source_url", "authors", "keywords", "files",
        #          "digital_object_id", "refering_url", "description",
        #          "parsed", "enriched", "published",
        #          "parse_date",) #"upload_date","created_date", "modified_date")

    def create(self, validated_data):
        keywords_data = validated_data.pop('keywords')
        authors_data = validated_data.pop('authors')
        files_data = validated_data.pop('files')

        title = validated_data.pop("title")
        source_url = validated_data.pop("source_url")
        digital_object_id = validated_data.pop("digital_object_id")
        refering_url = validated_data.pop("refering_url")
        description = validated_data.pop("description")
        parsed = validated_data.pop("parsed")
        enriched = validated_data.pop("enriched")
        published = validated_data.pop("published")
        print("validated data: " + str(validated_data))
        parse_date = validated_data.pop("parse_date")
        upload_date = validated_data.pop("upload_date")
        created_date = validated_data.pop("created_date")
        modified_date = validated_data.pop("modified_date")

        assert validated_data == []

        article, _ = Article.objects.get_or_create(title=title,
                                                   source_url=source_url,
                                                   digital_object_id=digital_object_id,
                                                   refering_url=refering_url,
                                                   description=description,
                                                   #parse_date=parse_date,
                                                   upload_date=upload_date,
                                                   parsed=parsed,
                                                   enriched=enriched,
                                                   published=published,
                                                   created_date=created_date,
                                                   modified_date=modified_date)
        for author_data in authors_data:
            name = author_data.pop("name")
            assert author_data == []
            aut, _ = Author.objects.get_or_create(name=name)
            article.authors.add(aut)

        for keyword_data in keywords_data:
            kw_word = keyword_data.pop("word")
            kw_created_date = keyword_data.pop("created_date")
            kw_modified_date = keyword_data.pop("modified_date")
            assert keyword_data == []
            kw,_ = Keyword.objects.get_or_create(word=kw_word,
                                                 created_date=kw_created_date,
                                                 modified_date=kw_modified_date)
            article.keywords.add(kw)

        for file_data in files_data:
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
        return article



