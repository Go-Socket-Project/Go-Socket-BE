from rest_framework import serializers
from django.contrib.auth import get_user_model
from .models import ChatRoom

User = get_user_model()

class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = ['id', 'name']  # 'name' field added as per instructions.

class ChatRoomSerializer(serializers.ModelSerializer):
    participants = UserSerializer(many=True, read_only=True)  # Using UserSerializer to serialize participants.

    class Meta:
        model = ChatRoom
        fields = ['id', 'participants']