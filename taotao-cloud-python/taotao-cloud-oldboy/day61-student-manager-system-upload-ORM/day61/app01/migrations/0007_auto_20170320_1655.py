# -*- coding: utf-8 -*-
# Generated by Django 1.10.6 on 2017-03-20 08:55
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('app01', '0006_auto_20170320_1541'),
    ]

    operations = [
        migrations.AlterUniqueTogether(
            name='usertotag',
            unique_together=set([]),
        ),
        migrations.RemoveField(
            model_name='usertotag',
            name='t',
        ),
        migrations.RemoveField(
            model_name='usertotag',
            name='u',
        ),
        migrations.AddField(
            model_name='user',
            name='d',
            field=models.ManyToManyField(related_name='b', to='app01.User'),
        ),
        migrations.DeleteModel(
            name='Tag',
        ),
        migrations.DeleteModel(
            name='UserToTag',
        ),
    ]
