# Generated by Django 5.0.6 on 2024-05-15 08:44

from django.conf import settings
from django.db import migrations, models


class Migration(migrations.Migration):

    initial = True

    dependencies = [
        migrations.swappable_dependency(settings.AUTH_USER_MODEL),
    ]

    operations = [
        migrations.CreateModel(
            name="ChatRoom",
            fields=[
                (
                    "id",
                    models.BigAutoField(
                        auto_created=True,
                        primary_key=True,
                        serialize=False,
                        verbose_name="ID",
                    ),
                ),
                (
                    "participants",
                    models.ManyToManyField(
                        related_name="chat_rooms", to=settings.AUTH_USER_MODEL
                    ),
                ),
            ],
        ),
    ]
