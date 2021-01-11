from django.contrib import admin
from django.urls import path
from django.conf.urls import include, url, re_path
from django.views.generic.base import TemplateView
from django.views.static import serve
from drf_yasg.views import get_schema_view
from drf_yasg import openapi
from rest_framework.documentation import include_docs_urls
from rest_framework import permissions
import api.views.serial_views as serial_views

schema_view = get_schema_view(
   openapi.Info(
      title="MdCrawler API",
      default_version='1.0.0',
      description="This is the swagger page for the md crawler api http://mdcrawler-api.perpetualnetworks.org. For this sample, you can use the api key special-key to test the authorization filters.",
      #terms_of_service="https://www.google.com/policies/terms/",
      contact=openapi.Contact(email="sgarcia00@gmail.com"),
      license=openapi.License(name="BSD License"),
   ),
   public=True,
   permission_classes=(permissions.AllowAny,),
)

urlpatterns = [
   #serialized views:
   url(r'^swagger/articles/?$', serial_views.ArticleSerialView.as_view(), name="List Articles"),
   url(r'^swagger/articles/(?P<article_id>\d+)$', serial_views.ArticleSerialViewSingle.as_view(), name="Article"),
   url(r'^swagger/article-files/?$', serial_views.ArticleFileSerialView.as_view(), name="List Article Files"),
   url(r'^swagger/article-files/(?P<article_file_id>\d+)$', serial_views.ArticleFileSerialViewSingle.as_view(), name="Article File"),
   url(r'^swagger/keywords/?$', serial_views.KeywordSerialView.as_view(), name="List Keywords"),
   url(r'^swagger/keywords/(?P<keyword_id>\d+)$', serial_views.KeywordSerialViewSingle.as_view(), name="Keyword"),
   url(r'^swagger/authors/?$', serial_views.AuthorSerialView.as_view(), name="List Authors"),
   url(r'^swagger/authors/(?P<author_id>\d+)$', serial_views.AuthorSerialViewSingle.as_view(), name="Author"),
   #base views:
   url(r'^swagger(?P<format>\.json|\.yaml)$', schema_view.without_ui(cache_timeout=0), name='schema-json'),
   url(r'^swagger/redoc/?', schema_view.with_ui('redoc', cache_timeout=0), name="schema-redoc"),
   url(r'^swagger/detail/?', include_docs_urls(title='MdCrawler API Detailed')),
   url(r'^swagger/?', schema_view.with_ui('swagger', cache_timeout=0), name='schema-swagger-ui'),
   path('admin/', admin.site.urls),
]
