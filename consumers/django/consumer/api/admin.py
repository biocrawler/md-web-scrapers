from django.contrib import admin

from api.models import Author, ArticleFile, Keyword, Article

class AuthorAdmin(admin.ModelAdmin):
  pass

class ArticleFileAdmin(admin.ModelAdmin):
  pass

class KeywordAdmin(admin.ModelAdmin):
  pass

class ArticleAdmin(admin.ModelAdmin):
  pass

admin.site.register(Author, AuthorAdmin)
admin.site.register(ArticleFile, ArticleFileAdmin)
admin.site.register(Keyword, KeywordAdmin)
admin.site.register(Article, ArticleAdmin)
