from pollio.models import Poll
from pollio.models import User
from django.http import HttpResponse
from django.http import Http404
import datetime
import json

def index(request):
    return HttpResponse(json.dumps({'somestuff': 0, 'something':'crapcrapcrap', 'otherthing':':-)'}))

def initialize(request, name, reg_id):
    u = User(name=name, registration_id=reg_id)
    u.save()
    return HttpResponse(json.dumps({'user_id': u.id}))

def new_poll(request, question, choice_string):
    p = Poll(question=question, pub_date=datetime.datetime.now())
    p.save()
    for choice in choice_string.split('|'):
        p.choice_set.create(choice=choice, votes=0)
    p.save()
    #Talk to GCM to yell at the pollees
    return HttpResponse(json.dumps({'poll_id': p.id}))

def request_poll(request, poll_id):
    try:
        p = Poll.objects.get(pk=poll_id)
    except Poll.DoesNotExist:
        raise Http404
    response = {}
    response['question'] = p.question
    for choice in p.choice_set.all(): response['choice_%d'%choice.pk] = choice.choice
    return HttpResponse(json.dumps(response))

def request_results(request, poll_id):
    try:
        p = Poll.objects.get(pk=poll_id)
    except Poll.DoesNotExist:
        raise Http404
    response = {}
    for choice in p.choice_set.all(): 
	response[choice.choice] = choice.votes
    return HttpResponse(json.dumps(response))

def submit_vote(request, poll_id, choice_id):
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
    #Talk to GCM to tell poller results are in
    return HttpResponse(json.dumps({'success' : 0}))


