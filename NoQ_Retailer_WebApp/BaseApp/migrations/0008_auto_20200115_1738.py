# Generated by Django 2.2.5 on 2020-01-15 12:08

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('BaseApp', '0007_delete_usersappprofile'),
    ]

    operations = [
        migrations.CreateModel(
            name='SessionTable',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('session_id', models.CharField(blank=True, db_column='Session_ID', max_length=20, null=True)),
                ('store_id', models.IntegerField(blank=True, db_column='Store_ID', null=True)),
                ('phone_number', models.CharField(db_column='Phone_Number', max_length=20)),
                ('category', models.CharField(blank=True, db_column='Category', max_length=30, null=True)),
                ('barcode', models.CharField(blank=True, db_column='Barcode', max_length=20, null=True)),
                ('number_of_items', models.IntegerField(blank=True, db_column='Number_of_Items', null=True)),
                ('timestamp', models.DateTimeField(db_column='Timestamp')),
            ],
            options={
                'db_table': 'Session_Table',
                'managed': False,
            },
        ),
        migrations.CreateModel(
            name='TemporaryBufferTable',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('session_id', models.IntegerField(db_column='Session ID')),
                ('barcode', models.CharField(db_column='Barcode', max_length=20)),
            ],
            options={
                'db_table': 'Temporary_Buffer_Table',
                'managed': False,
            },
        ),
        migrations.AlterModelOptions(
            name='profile',
            options={'managed': False},
        ),
    ]