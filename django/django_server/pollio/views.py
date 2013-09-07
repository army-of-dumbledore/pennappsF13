from pollio.models import Poll
from pollio.models import User
from django.http import HttpResponse
from django.http import Http404
import datetime
import urllib2
import json


regid = 'APA91bE1FWvX9PR0jyoGAAjJ6FWwqyfbH-NJbIqmwes6Bs-4yQc5wgNFAgA47dPbuHmyBgMdLoNumVInzsLsh7iJbe8tGgFPyypsXu4OZY177iBIsslGO2tmgL45EF2SHHhxhI3MJlLglbmsm0o5mTUMRap__rIzGQuEnkoRBwH9Xknpk3ovwZAnzKiGDr2D8fS9N1awqsMZ'

def index(request):
    return HttpResponse(json.dumps({'somestuff': 0, 'something':'crapcrapcrap', 'otherthing':':-)'}))

def initialize(request):
    u = User(name=request.REQUEST['name'], registration_id=request.REQUEST['reg_id'])
    u.save()
    url = 'https://android.googleapis.com/gcm/send'
    data = json.dumps({'registration_ids':[u.registration_id], 'dry-run' : False, 'data' : {'command' : 'init_conf'}})
    req = urllib2.Request(url, data, {'Content-Type': 'application/json', 'Authorization': 'key=AIzaSyAtdsjZg81RipQY_4mreAEbiJPcT3iRtIA'})
    result = json.loads(urllib2.urlopen(req).read())
    return HttpResponse(json.dumps({'user_id' : u.id}))

def new_poll(request): 
    user=request.REQUEST['user']
    question=request.REQUEST['question']
    choice_string=request.REQUEST['choice_string']
#    p = Poll(owner=User.objects.get(id=user), question=question, pub_date=datetime.datetime.now())
    p = Poll(question=question, pub_date=datetime.datetime.now())
    p.save()
    for choice in choice_string.split('|'):
        p.choice_set.create(choice=choice, votes=0)
    p.save()
    #Talk to GCM to yell at the pollees
    return HttpResponse(json.dumps({'poll_id': p.id}))

def request_poll(request):
    poll_id=request.REQUEST['poll_id']
    try:
        p = Poll.objects.get(pk=poll_id)
    except Poll.DoesNotExist:
        raise Http404
    response = {}
    response['question'] = p.question
    for choice in p.choice_set.all(): response['choice_%d'%choice.pk] = choice.choice
    return HttpResponse(json.dumps(response))

def request_results(request):
    poll_id=request.REQUEST['poll_id']
    print 'HERE!'
    try:
        p = Poll.objects.get(pk=poll_id)
    except Poll.DoesNotExist:
        raise Http404
    print 'after try/except block!'
    response = {}
    for choice in p.choice_set.all(): 
	response[choice.choice] = choice.votes
    return HttpResponse(json.dumps(response))

def submit_vote(request):
    poll_id=request.REQUEST['poll_id']
    choice_id=request.REQUEST['choice_id']
    try:
        p = Poll.objects.get(pk=poll_id)
    except Poll.DoesNotExist:
        raise Http404
    c = p.choice_set.get(pk=choice_id)
    c.votes += 1
    c.save()
    response = {}
    for choice in p.choice_set.all(): 
	response[choice.choice] = choice.votes
    url = 'https://android.googleapis.com/gcm/send'
    data = json.dumps({'registration_ids':[regid], 'dry-run' : False, 'data' : {'command' : 'results', 'poll_id' : poll_id}})
    req = urllib2.Request(url, data, {'Content-Type': 'application/json', 'Authorization': 'key=AIzaSyAtdsjZg81RipQY_4mreAEbiJPcT3iRtIA'})
    result = json.loads(urllib2.urlopen(req).read())
    print 'leaving submit vote'
    return HttpResponse(json.dumps({'success' : 0}))


