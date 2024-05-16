from django.db import models
from django.contrib.auth import get_user_model

User = get_user_model()

class ChatRoom(models.Model):
    participants = models.ManyToManyField(User, related_name='chat_rooms')

    def __str__(self):
        return f"{self.id}"
