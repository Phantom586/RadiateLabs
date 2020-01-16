from django.db import models


class Mytable(models.Model):
    id = models.IntegerField(primary_key=True)
    name = models.CharField(db_column='Name', max_length=50)  # Field name made lowercase.
    branch = models.CharField(db_column='Branch', max_length=30)  # Field name made lowercase.

    class Meta:
        managed = False
        db_table = 'myTable'
