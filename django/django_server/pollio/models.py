from django.db import models

# Create your models here.

class User(models.Model):
    name = models.CharField(max_length=200)
    registration_id = models.CharField(max_length=1024)

class Poll(models.Model):
#    owner = models.ForeignKey(User)
    question = models.CharField(max_length=200)
    pub_date = models.DateTimeField('date published')

class Choice(models.Model):
    poll = models.ForeignKey(Poll)
    choice = models.CharField(max_length=200)
    votes = models.IntegerField()
