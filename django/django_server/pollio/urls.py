from django.conf.urls.defaults import patterns, include, url

from django.contrib import admin
admin.autodiscover()

urlpatterns = patterns('pollio.views',
    url(r'^$', 'index'),
    url(r'^initialize&name="(?P<name>.+)"&reg_id="(?P<reg_id>.+)"/$', 'initialize'),
    url(r'^new_poll&question="(?P<question>.+)"&choices="(?P<choice_string>.+)"/$', 'new_poll'),
    url(r'^request_poll&poll=(?P<poll_id>\d+)/$', 'request_poll'),
    url(r'^request_results&poll=(?P<poll_id>\d+)/$', 'request_results'),
    url(r'^submit_vote&poll=(?P<poll_id>\d+)&choice=(?P<choice_id>\d+)/$', 'submit_vote'),
)
