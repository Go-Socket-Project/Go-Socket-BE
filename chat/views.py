from django.contrib.auth import get_user_model
from django.views.decorators.csrf import csrf_exempt
from django.utils.decorators import method_decorator

from rest_framework import permissions, status
from rest_framework.response import Response
from rest_framework.views import APIView
from chat.models import ChatRoom
from chat.serializers import ChatRoomSerializer


User = get_user_model()

@method_decorator(csrf_exempt, name='dispatch')
class CreateChatRoomView(APIView):
    permission_classes = [permissions.IsAuthenticated]

    def post(self, request):
        target_email = request.data.get('email')
        if target_email:
            target_user = User.objects.filter(email=target_email).first()
            if target_user:
                chat_room, created = ChatRoom.objects.get_or_create()
                if created:
                    chat_room.participants.add(request.user, target_user)
                    chat_room.save()
                    return Response({
                        "message": "Chat room created successfully",
                        "chat_room_id": chat_room.id  # 채팅방 ID 반환
                    }, status=status.HTTP_201_CREATED)
                else:
                    return Response({
                        "message": "Chat room already exists",
                        "chat_room_id": chat_room.id  # 이미 존재하는 경우에도 ID 반환
                    }, status=status.HTTP_200_OK)
            return Response({"error": "No user found with this email"}, status=status.HTTP_404_NOT_FOUND)
        return Response({"error": "Email is required"}, status=status.HTTP_400_BAD_REQUEST)

@method_decorator(csrf_exempt, name='dispatch')
class ListChatRoomsView(APIView):
    permission_classes = [permissions.IsAuthenticated]

    def get(self, request):
        chat_rooms = ChatRoom.objects.filter(participants=request.user)
        if chat_rooms.exists():
            serializer = ChatRoomSerializer(chat_rooms, many=True)
            return Response(serializer.data, status=status.HTTP_200_OK)
        return Response({"message": "No chat rooms found"}, status=status.HTTP_404_NOT_FOUND)
