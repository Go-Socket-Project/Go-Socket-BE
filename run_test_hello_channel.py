import asyncio
import os

import django
from channels.layers import get_channel_layer

os.environ["DJANGO_SETTINGS_MODULE"] = "workspace.settings"
django.setup()

async def main():
    channel_layer = get_channel_layer()
    message_dict = {'content': 'world'}  # 수정: 올바른 dictionary 형식으로 수정

    await channel_layer.send('hello', message_dict)

    response_dict = await channel_layer.receive('hello')
    is_equal = message_dict == response_dict
    print("송신/수신 데이터가 같습니다?", is_equal)

asyncio.run(main())