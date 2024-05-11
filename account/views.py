
from rest_framework import status, permissions
from rest_framework.response import Response
from rest_framework.views import APIView
from account.serializers import UserRegistrationSerializer, UserLoginSerializer

class SignupView(APIView):
    permission_classes = [permissions.AllowAny]

    def post(self, request):
        serializer = UserRegistrationSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response({"message": "User created successfully"}, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

class LoginView(APIView):
    permission_classes = [permissions.AllowAny]

    def post(self, request):
        serializer = UserLoginSerializer(data=request.data)
        if serializer.is_valid():
            user = serializer.validated_data
            # Generate token or return user data
            return Response({"message": "User logged in successfully"}, status=status.HTTP_200_OK)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
# 필요한 경우 ObtainAuthToken의 serializer_class를 변경하거나 추가 설정을 할 수 있습니다.
