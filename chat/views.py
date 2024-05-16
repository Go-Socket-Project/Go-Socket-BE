from django.contrib.auth import get_user_model
from django.views.decorators.csrf import csrf_exempt
from django.utils.decorators import method_decorator

from rest_framework import permissions, status
from rest_framework.response import Response
from rest_framework.views import APIView
from chat.models import ChatRoom
from chat.serializers import ChatRoomSerializer
import datetime

User = get_user_model()

@method_decorator(csrf_exempt, name='dispatch')
class CreateChatRoomView(APIView):
    permission_classes = [permissions.IsAuthenticated]

    def post(self, request):
        target_email = request.data.get('email')
        if not target_email:
            return Response({"error": "Email is required"}, status=status.HTTP_400_BAD_REQUEST)

        target_user = User.objects.filter(email=target_email).first()
        if not target_user:
            return Response({"error": "No user found with this email"}, status=status.HTTP_404_NOT_FOUND)

        # 현재 시간을 포함한 고유한 방 이름 생성
        room_name = self._generate_unique_room_name(request.user.email, target_email)
        chat_room = ChatRoom.objects.create(name=room_name)
        chat_room.participants.add(request.user, target_user)
        chat_room.save()

        return Response({
            "message": "Chat room created successfully",
            "chat_room_id": chat_room.id,
            "chat_room_name": chat_room.name
        }, status=status.HTTP_201_CREATED)

    def _generate_unique_room_name(self, email1, email2):
        """Generate a unique room name based on two emails and the current timestamp."""
        prefix1 = email1.split('@')[0]
        prefix2 = email2.split('@')[0]
        current_time = datetime.datetime.now().strftime("%Y%m%d%H%M%S")
        return "_".join(sorted([prefix1, prefix2]) + [current_time])

@method_decorator(csrf_exempt, name='dispatch')
class ListChatRoomsView(APIView):
    permission_classes = [permissions.IsAuthenticated]

    def get(self, request):
        chat_rooms = ChatRoom.objects.filter(participants=request.user)
        if chat_rooms.exists():
            serializer = ChatRoomSerializer(chat_rooms, many=True)
            return Response(serializer.data, status=status.HTTP_200_OK)
        return Response({"message": "No chat rooms found"}, status=status.HTTP_404_NOT_FOUND)
