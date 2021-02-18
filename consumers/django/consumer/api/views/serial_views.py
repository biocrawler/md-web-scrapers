from django_filters.rest_framework import DjangoFilterBackend
from drf_yasg.utils import swagger_auto_schema
from rest_framework import generics, mixins
from rest_framework.settings import api_settings

from api.serializers import *
from api.filters import *


class ArticleFileSerialView(generics.ListAPIView):
    queryset = ArticleFile.objects.all()
    serializer_class = ArticleFileSerializer
    filter_backends = [DjangoFilterBackend]
    filterset_class = ArticleFileFilter
    renderer_classes = tuple(api_settings.DEFAULT_RENDERER_CLASSES)

    @swagger_auto_schema(operation_description="Article File List",
                         responses={200: serializer_class},
                         )
    def get(self, request, *args, **kwargs):
        return self.list(request, *args, **kwargs)

class ArticleFileSerialViewSingle(mixins.RetrieveModelMixin,
                          generics.GenericAPIView):
    queryset = ArticleFile.objects.all()
    serializer_class = ArticleFileSerializer
    filter_backends = [DjangoFilterBackend]
    filterset_class = ArticleFileFilterSingle

    @swagger_auto_schema(operation_description="Article File",
                         responses={200: serializer_class},
                         )

    def get(self, request, *args, **kwargs):                                     
        return self.retrieve(request, *args, **kwargs)   


class ArticleSerialView(generics.ListAPIView):
    queryset = Article.objects.all().prefetch_related("keywords", "authors", "articlefile_set")
    serializer_class = ArticleSerializer
    filter_backends = [DjangoFilterBackend]
    filterset_class = ArticleFilter
    renderer_classes = tuple(api_settings.DEFAULT_RENDERER_CLASSES)

    @swagger_auto_schema(operation_description="Article List",
                         responses={200: serializer_class},
                         tags=["articles"],
                         )
    def get(self, request, *args, **kwargs):
        return self.list(request, *args, **kwargs)


class ArticleSerialViewSingle(mixins.RetrieveModelMixin,
                          generics.GenericAPIView):
    queryset = Article.objects.all().prefetch_related("keywords", "authors", "articlefile_set")
    serializer_class = ArticleSerializer
    filter_backends = [DjangoFilterBackend]
    filterset_class = ArticleFilterSingle

    @swagger_auto_schema(operation_description="Article",
                         responses={200: serializer_class},
                         tags=["articles"],
                         )
    def get(self, request, *args, **kwargs):
        return self.retrieve(request, *args, **kwargs)


class KeywordSerialView(generics.ListAPIView):
    queryset = Keyword.objects.all()
    serializer_class = KeywordSerializer
    filter_backends = [DjangoFilterBackend]
    filterset_class = KeywordFilter
    renderer_classes = tuple(api_settings.DEFAULT_RENDERER_CLASSES)

    @swagger_auto_schema(operation_description="",
                         responses={200: serializer_class},
                         )
    def get(self, request, *args, **kwargs):
        return self.list(request, *args, **kwargs)

class KeywordSerialViewSingle(mixins.RetrieveModelMixin,
                          mixins.UpdateModelMixin,
                          generics.GenericAPIView):
    queryset = Keyword.objects.all()
    serializer_class = KeywordSerializer
    filter_backends = [DjangoFilterBackend]
    filterset_class = KeywordFilterSingle

    @swagger_auto_schema(operation_description="",
                         responses={200: serializer_class},
                         )
    def get(self, request, *args, **kwargs):
        return self.retrieve(request, *args, **kwargs)

class AuthorSerialView(generics.ListAPIView):
    queryset = Author.objects.all()
    serializer_class = AuthorSerializer
    filter_backends = [DjangoFilterBackend]
    filterset_class = AuthorFilter
    renderer_classes = tuple(api_settings.DEFAULT_RENDERER_CLASSES)

    @swagger_auto_schema(operation_description="",
                         responses={200: serializer_class},
                         )
    def get(self, request, *args, **kwargs):
        return self.list(request, *args, **kwargs)

class AuthorSerialViewSingle(mixins.RetrieveModelMixin,
                          mixins.UpdateModelMixin,
                          generics.GenericAPIView):
    queryset = Author.objects.all()
    serializer_class = AuthorSerializer
    filter_backends = [DjangoFilterBackend]
    filterset_class = AuthorFilterSingle

    @swagger_auto_schema(operation_description="",
                         responses={200: serializer_class},
                         )
    def get(self, request, *args, **kwargs):
        return self.retrieve(request, *args, **kwargs)
