from pollio.models import Poll
from pollio.models import User
from django.http import HttpResponse
from django.http import Http404
import datetime
import urllib2
import json

def index(request):
    return HttpResponse(json.dumps({'somestuff': 0, 'something':'crapcrapcrap', 'otherthing':':-)'}))

def initialize(request):
    n=request.REQUEST['name']
    r=request.REQUEST['reg_id']
    try:
        u = User.objects.get(registration_id=r)
    except User.DoesNotExist:
    	u = User(name=n, registration_id=r)
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
    p = Poll(owner=User.objects.get(id=user), question=question, pub_date=datetime.datetime.now(), results_pending=False)
    p.save()
    for choice in choices.strip('|').split('|'):
        p.choice_set.create(choice=choice, votes=0)
    for pollee in pollees.strip('|').split('|'):
	if pollee == user : continue
        p.pollees.add(User.objects.get(id=pollee))
    p.save()
    header = {'Content-Type': 'application/json', 'Authorization': 'key=AIzaSyAtdsjZg81RipQY_4mreAEbiJPcT3iRtIA'}
    for pollee in p.pollees.all():
        url = 'https://android.googleapis.com/gcm/send'
        data = json.dumps({'registration_ids':[pollee.registration_id], 'dry-run' : False, 'data' : {'command' : 'poll', 'poll_id' : p.id, 'owner' : p.owner.name}})
        req = urllib2.Request(url, data, header)
        result = json.loads(urllib2.urlopen(req).read())
	print 'pushing to', pollee, result
    return HttpResponse(json.dumps({'poll_id': p.id}))

def request_poll(request):
    poll_id=request.REQUEST['poll_id']
    try:
        p = Poll.objects.get(pk=poll_id)
    except Poll.DoesNotExist:
        raise Http404
    response = {}
    response['question'] = p.question
    response['type'] = 'mc' 
    response['choices'] = [] 
    for choice in p.choice_set.all(): 
	response['choices'].append({'id' : choice.pk, 'text' : choice.choice})
    return HttpResponse(json.dumps(response))

def request_results(request):
    poll_id=request.REQUEST['poll_id']
    try:
        p = Poll.objects.get(pk=poll_id)
    except Poll.DoesNotExist:
        raise Http404
    response = {}
    response['question'] = p.question
    response['choices'] = [] 
    for choice in p.choice_set.all(): 
	response['choices'].append({'text' : choice.choice, 'count' : choice.votes, 'backers' : [u.id for u in choice.backers.all()]})
    p.results_pending = False
    return HttpResponse(json.dumps(response))

def submit_vote(request):
    user=request.REQUEST['user_id']
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
    if not(p.results_pending) : 
	p.results_pending = True
    	url = 'https://android.googleapis.com/gcm/send'
    	header = {'Content-Type': 'application/json', 'Authorization': 'key=AIzaSyAtdsjZg81RipQY_4mreAEbiJPcT3iRtIA'}
    	data = json.dumps({'registration_ids':[p.owner.registration_id], 'dry-run' : False, 'data' : {'command' : 'results', 'poll_id' : poll_id, 'owner' : User.objects.get(id=user).name}})
    	req = urllib2.Request(url, data, header)
    	result = json.loads(urllib2.urlopen(req).read())
    return HttpResponse(json.dumps({'success' : 0}))


