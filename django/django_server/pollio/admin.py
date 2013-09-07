from pollio.models import Poll
from pollio.models import Choice
from pollio.models import User 
from django.contrib import admin

class ChoiceInline(admin.TabularInline):
    model = Choice
    extra = 3

class UserAdmin(admin.ModelAdmin):
    fieldsets = [
        (None,               {'fields': ['name']}),
        ('registration key', {'fields': ['registration_id'], 'classes': ['collapse']}),
    ]
    list_display = ('name', 'registration_id')

class PollAdmin(admin.ModelAdmin):
    fieldsets = [
        (None,               {'fields': ['question']}),
        ('Date information', {'fields': ['pub_date'], 'classes': ['collapse']}),
    ]
    inlines = [ChoiceInline]
    list_display = ('question', 'pub_date')

admin.site.register(User, UserAdmin)
admin.site.register(Poll, PollAdmin)
