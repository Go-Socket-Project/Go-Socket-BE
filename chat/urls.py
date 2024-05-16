from django.urls import path
from chat.views import CreateChatRoomView, ListChatRoomsView

urlpatterns = [
    path('create-chat-room/', CreateChatRoomView.as_view(), name='create_chat_room'),
    path('list-chat-rooms/', ListChatRoomsView.as_view(), name='list_chat_rooms'),
]

