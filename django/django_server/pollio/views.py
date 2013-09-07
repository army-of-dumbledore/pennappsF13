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
    user=request.REQUEST['user_id']
    question=request.REQUEST['question']
    choices=request.REQUEST['choices']
    pollees=request.REQUEST['pollees']
    p = Poll(owner=User.objects.get(id=user), question=question, pub_date=datetime.datetime.now())
    p.save()
    for choice in choices.split('|'):
        p.choice_set.create(choice=choice, votes=0)
    for pollee in pollees.split('|'):
        p.pollees.add(User.objects.get(id=pollee))
    p.save()
    header = {'Content-Type': 'application/json', 'Authorization': 'key=AIzaSyAtdsjZg81RipQY_4mreAEbiJPcT3iRtIA'}
    for pollee in p.pollees.all():
        url = 'https://android.googleapis.com/gcm/send'
        data = json.dumps({'registration_ids':[pollee.registration_id], 'data' : {'command' : 'poll', 'poll_id' : p.id}})
        req = urllib2.Request(url, data, header)
        result = json.loads(urllib2.urlopen(req).read())
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
    try:
        p = Poll.objects.get(pk=poll_id)
    except Poll.DoesNotExist:
        raise Http404
    response = {}
    for choice in p.choice_set.all(): 
	response[choice.choice] = {'count' : choice.votes, 'backers' : [u.id for u in choice.backers.all()]}
    return HttpResponse(json.dumps(response))

def submit_vote(request):
    user=request.REQUEST['user']
    poll_id=request.REQUEST['poll_id']
    choice_id=request.REQUEST['choice_id']
    try:
        p = Poll.objects.get(pk=poll_id)
    except Poll.DoesNotExist:
        raise Http404
    c = p.choice_set.get(pk=choice_id)
    c.backers.add(User.objects.get(id=user))
    c.votes += 1
    c.save()
    url = 'https://android.googleapis.com/gcm/send'
    data = json.dumps({'registration_ids':[regid], 'dry-run' : False, 'data' : {'command' : 'results', 'poll_id' : poll_id}})
    req = urllib2.Request(url, data, {'Content-Type': 'application/json', 'Authorization': 'key=AIzaSyAtdsjZg81RipQY_4mreAEbiJPcT3iRtIA'})
    result = json.loads(urllib2.urlopen(req).read())
    return HttpResponse(json.dumps({'success' : 0}))


