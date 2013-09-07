from django.conf.urls.defaults import patterns, include, url

from django.contrib import admin
admin.autodiscover()

urlpatterns = patterns('pollio.views',
    url(r'^$', 'index'),
    url(r'^initialize/$', 'initialize'),
    url(r'^new_poll/$', 'new_poll'),
    url(r'^request_poll/$', 'request_poll'),
    url(r'^request_results/$', 'request_results'),
    url(r'^submit_vote/$', 'submit_vote'),
)
