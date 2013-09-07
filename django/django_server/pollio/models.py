from django.db import models

# Create your models here.

class User(models.Model):
    name = models.CharField(max_length=200)
    registration_id = models.CharField(max_length=1024)
    def __unicode__(self):
        return self.name

class Poll(models.Model):
    owner = models.ForeignKey(User, related_name='owner')
    question = models.CharField(max_length=200)
    pollees = models.ManyToManyField(User, related_name='pollees')
    pub_date = models.DateTimeField('date published')
    results_pending = models.BooleanField()
    def __unicode__(self):
        return self.question

class Choice(models.Model):
    poll = models.ForeignKey(Poll)
    choice = models.CharField(max_length=200)
    backers = models.ManyToManyField(User, related_name='backers')
    votes = models.IntegerField()
    def __unicode__(self):
        return self.choice
