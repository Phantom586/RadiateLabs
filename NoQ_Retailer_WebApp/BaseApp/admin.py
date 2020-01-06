from django.contrib import admin
from .models import Profile, BasketTable, InvoiceTable, ProductDatabaseDesiFarms, StoreTable, ImageUpload, UserTable

# Register your models here.
admin.site.register(Profile)
admin.site.register(BasketTable)
admin.site.register(InvoiceTable)
admin.site.register(ProductDatabaseDesiFarms)
admin.site.register(StoreTable)
admin.site.register(ImageUpload)
admin.site.register(UserTable)
