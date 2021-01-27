import json

from api.api_lib.serializers import ArticleSerializer as apiserializer
from api.api_lib.validators import ArticleValidator
from api.models import Article, ArticleFile, Author, Keyword
from api.serializers import ArticleSerializer
from django.test import TestCase

from api_tests.article_test_data_1 import test_data_1


class ArticleSerializerTC(TestCase):
    def setUp(self) -> None:
        pass

    def serializer_data_1_ok(self):
        validator = ArticleValidator(test_data_1)
        data = validator.get_validated_data()
        print("validated data: " + str(json.dumps(data, indent=4)))
        serializer = ArticleSerializer(data=data)
        serializer.is_valid(raise_exception=True)
        a = serializer.save()
        print(Article.objects.get(digital_object_id=a).to_json())

    def serializer_data_2_ok(self):
        validator = ArticleValidator(test_data_2)
        data = validator.get_validated_data()
        print("validated data: " + str(json.dumps(data, indent=4)))
        serializer = ArticleSerializer(data=data)
        serializer.is_valid(raise_exception=True)
        a = serializer.save()
        print(Article.objects.get(digital_object_id=a).to_json())

    def api_serializer_data_2_ok(self):
        validator = ArticleValidator(test_data_2)
        data = validator.get_validated_data()
        print("validated data: " + str(json.dumps(data, indent=4)))
        serializer = apiserializer(data=data)
        # serializer.is_valid(raise_exception=True)
        a = serializer.save()
        print(Article.objects.get(digital_object_id=a).to_json())
