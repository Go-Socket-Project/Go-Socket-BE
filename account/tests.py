from django.test import TestCase
from django.urls import reverse

class UserSignUpTest(TestCase):
    def test_signup(self):
        url = reverse('signup')
        data = {
            'username': 'newuser',
            'password': 'newpassword123',
            'email': 'user@example.com'
        }
        response = self.client.post(url, data)
        self.assertEqual(response.status_code, 201)
        # 추가적으로 생성된 유저 검증 등의 테스트를 할 수 있습니다.
