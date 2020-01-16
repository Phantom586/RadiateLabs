from django.db import models
from django.contrib.auth.models import User


class Profile(models.Model):
    user = models.OneToOneField(User, on_delete=models.CASCADE)
    image = models.ImageField(default='default.png', upload_to='profile_pics')

    class Meta:
        managed = False
        db_table = 'BaseApp_profile'

    def __str__(self):
        return f'{self.user.username} Profile'


class BasketTable(models.Model):
    user = models.CharField(db_column='User', max_length=13)  # Field name made lowercase.
    session_id = models.CharField(db_column='Session_ID', max_length=300)  # Field name made lowercase.
    store_id = models.IntegerField(db_column='Store_ID', blank=True, null=True)  # Field name made lowercase.
    barcode = models.CharField(db_column='Barcode', max_length=20)  # Field name made lowercase.
    number_of_items = models.IntegerField(db_column='Number_of_Items', blank=True, null=True)  # Field name made lowercase.
    timestamp = models.DateTimeField(db_column='Timestamp')  # Field name made lowercase.
    mrp_per_item = models.FloatField(db_column='MRP_per_Item', blank=True, null=True)  # Field name made lowercase.
    total_mrp = models.FloatField(db_column='Total_MRP', blank=True, null=True)  # Field name made lowercase.
    retailer_price_per_item = models.FloatField(db_column='Retailer_Price_per_Item', blank=True, null=True)  # Field name made lowercase.
    total_retailer_price = models.FloatField(db_column='Total_Retailer_Price', blank=True, null=True)  # Field name made lowercase.
    our_price_per_item = models.FloatField(db_column='Our_Price_per_Item', blank=True, null=True)  # Field name made lowercase.
    discount_per_item = models.FloatField(db_column='Discount_per_Item', blank=True, null=True)  # Field name made lowercase.
    total_our_price = models.FloatField(db_column='Total_Our_Price', blank=True, null=True)  # Field name made lowercase.
    total_discount = models.FloatField(db_column='Total_Discount', blank=True, null=True)  # Field name made lowercase.

    class Meta:
        managed = False
        db_table = 'Basket_Table'

    def __str__(self):
        return f'{self.user} | {self.barcode}'


class InvoiceTable(models.Model):
    receipt_no = models.BigAutoField(db_column='Receipt_No', unique=True , primary_key=True)  # Field name made lowercase.
    txn_id = models.CharField(db_column='Txn_ID', max_length=500)  # Field name made lowercase.
    order_id = models.CharField(db_column='Order_ID', max_length=500)  # Field name made lowercase.
    payment_mode = models.CharField(db_column='Payment_Mode', max_length=50)  # Field name made lowercase.
    user = models.CharField(db_column='User', max_length=13)  # Field name made lowercase.
    session_id = models.CharField(db_column='Session_ID', max_length=300)  # Field name made lowercase.
    store_id = models.IntegerField(db_column='Store_ID', blank=True, null=True)  # Field name made lowercase.
    total_mrp = models.FloatField(db_column='Total_MRP', blank=True, null=True)  # Field name made lowercase.
    total_retailers_price = models.FloatField(db_column='Total_Retailers_Price')  # Field name made lowercase.
    total_our_price = models.FloatField(db_column='Total_Our_Price')  # Field name made lowercase.
    total_discount = models.FloatField(db_column='Total_Discount', blank=True, null=True)  # Field name made lowercase.
    referral_balance_used = models.FloatField(db_column='Referral_Balance_Used', blank=True, null=True)  # Field name made lowercase.
    final_amount_paid = models.FloatField(db_column='Final_Amount_Paid', blank=True, null=True)  # Field name made lowercase.
    total_savings = models.FloatField(db_column='Total_Savings', blank=True, null=True)  # Field name made lowercase.
    timestamp = models.DateTimeField(db_column='Timestamp')  # Field name made lowercase.
    comments = models.CharField(db_column='Comments', max_length=100, blank=True, null=True)  # Field name made lowercase.

    class Meta:
        managed = False
        db_table = 'Invoice_Table'

    def __str__(self):
        return f'{self.receipt_no} | {self.user}'


class ProductDatabaseDesiFarms(models.Model):
    barcode = models.CharField(db_column='Barcode', primary_key=True, max_length=20)  # Field name made lowercase.
    store_id = models.IntegerField(db_column='Store_ID')  # Field name made lowercase.
    brand_name = models.CharField(db_column='Brand_Name', max_length=40, blank=True, null=True)  # Field name made lowercase.
    name_of_the_product = models.CharField(db_column='Name_of_the_Product', max_length=40, blank=True, null=True)  # Field name made lowercase.
    times_purchased = models.IntegerField(db_column='Times_Purchased')  # Field name made lowercase.
    mrp = models.FloatField(db_column='MRP', blank=True, null=True)  # Field name made lowercase.
    retailer_discount = models.FloatField(db_column='Retailer_Discount', blank=True, null=True)  # Field name made lowercase.
    retailer_price = models.FloatField(db_column='Retailer_Price', blank=True, null=True)  # Field name made lowercase.
    our_discount_percentage = models.FloatField(db_column='Our_Discount_Percentage', blank=True, null=True)  # Field name made lowercase.
    our_discount = models.FloatField(db_column='Our_Discount', blank=True, null=True)  # Field name made lowercase.
    our_price = models.FloatField(db_column='Our_Price', blank=True, null=True)  # Field name made lowercase.
    total_discount = models.FloatField(db_column='Total_Discount', blank=True, null=True)  # Field name made lowercase.
    savings_due_to_retailer = models.FloatField(db_column='Savings_due_to_Retailer', blank=True, null=True)  # Field name made lowercase.
    savings_due_to_us = models.FloatField(db_column='Savings_Due_to_Us', blank=True, null=True)  # Field name made lowercase.
    total_savings = models.FloatField(db_column='Total_Savings', blank=True, null=True)  # Field name made lowercase.

    class Meta:
        managed = False
        db_table = 'Product_Database_Desi_Farms'
        unique_together = (('barcode', 'store_id'),)

    def __str__(self):
        return f'{self.barcode} | {self.store_id} | {self.name_of_the_product}'


class SessionTable(models.Model):
    session_id = models.CharField(db_column='Session_ID', max_length=20, blank=True, null=True)  # Field name made lowercase.
    store_id = models.IntegerField(db_column='Store_ID', blank=True, null=True)  # Field name made lowercase.
    phone_number = models.CharField(db_column='Phone_Number', max_length=20)  # Field name made lowercase.
    category = models.CharField(db_column='Category', max_length=30, blank=True, null=True)  # Field name made lowercase.
    barcode = models.CharField(db_column='Barcode', max_length=20, blank=True, null=True)  # Field name made lowercase.
    number_of_items = models.IntegerField(db_column='Number_of_Items', blank=True, null=True)  # Field name made lowercase.
    timestamp = models.DateTimeField(db_column='Timestamp')  # Field name made lowercase.

    class Meta:
        managed = False
        db_table = 'Session_Table'


class StoreTable(models.Model):
    store_id = models.AutoField(db_column='Store_ID', primary_key=True)  # Field name made lowercase.
    store_name = models.CharField(db_column='Store_Name', max_length=200, blank=True, null=True)  # Field name made lowercase.
    store_address = models.CharField(db_column='Store_Address', max_length=350, blank=True, null=True)  # Field name made lowercase.
    store_city = models.CharField(db_column='Store_City', max_length=120, blank=True, null=True)  # Field name made lowercase.
    pincode = models.IntegerField(db_column='Pincode', blank=True, null=True)  # Field name made lowercase.
    store_state = models.CharField(db_column='Store_State', max_length=20, blank=True, null=True)  # Field name made lowercase.
    store_country = models.CharField(db_column='Store_Country', max_length=20, blank=True, null=True)  # Field name made lowercase.

    class Meta:
        managed = False
        db_table = 'Store_Table'

    def __str__(self):
        return f'{self.store_id} | {self.store_name}'


class TemporaryBufferTable(models.Model):
    session_id = models.IntegerField(db_column='Session ID')  # Field name made lowercase. Field renamed to remove unsuitable characters.
    barcode = models.CharField(db_column='Barcode', max_length=20)  # Field name made lowercase.

    class Meta:
        managed = False
        db_table = 'Temporary_Buffer_Table'


class UserTable(models.Model):
    phone_number = models.CharField(db_column='Phone_Number', primary_key=True, max_length=20)  # Field name made lowercase.
    name = models.CharField(db_column='Name', max_length=30)  # Field name made lowercase.
    email = models.CharField(db_column='Email', max_length=30, blank=True, null=True)  # Field name made lowercase.
    sign_up_timestamp = models.DateTimeField(db_column='Sign_Up_Timestamp')  # Field name made lowercase.
    referral_phone_number = models.CharField(db_column='Referral_Phone_Number', max_length=20, blank=True, null=True)  # Field name made lowercase.
    referral_amount_acquired = models.FloatField(db_column='Referral_Amount_Acquired', blank=True, null=True)  # Field name made lowercase.
    referral_amount_used = models.FloatField(db_column='Referral_Amount_Used', blank=True, null=True)  # Field name made lowercase.
    referral_amount_balance = models.FloatField(db_column='Referral_Amount_Balance', blank=True, null=True)  # Field name made lowercase.
    number_of_referrals = models.IntegerField(db_column='Number_of_Referrals', blank=True, null=True)  # Field name made lowercase.
    logout = models.CharField(db_column='Logout', max_length=5, blank=True, null=True)  # Field name made lowercase.
    share_button_clicks = models.IntegerField(db_column='Share_Button_Clicks', blank=True, null=True)  # Field name made lowercase.

    class Meta:
        managed = False
        db_table = 'User_Table'

    def __str__(self):
        return f'{self.phone_number} | {self.name}'


class AuthUser(models.Model):
    password = models.CharField(max_length=128)
    last_login = models.DateTimeField(blank=True, null=True)
    is_superuser = models.IntegerField()
    username = models.CharField(unique=True, max_length=150)
    first_name = models.CharField(max_length=30)
    last_name = models.CharField(max_length=150)
    email = models.CharField(max_length=254)
    is_staff = models.IntegerField()
    is_active = models.IntegerField()
    date_joined = models.DateTimeField()

    class Meta:
        managed = False
        db_table = 'auth_user'


class ImageUpload(models.Model):
    image_name = models.CharField(max_length=255)

    class Meta:
        managed = False
        db_table = 'image_upload'


class Mytable(models.Model):
    id = models.IntegerField(primary_key=True)
    name = models.CharField(db_column='Name', max_length=50)  # Field name made lowercase.
    branch = models.CharField(db_column='Branch', max_length=30)  # Field name made lowercase.

    class Meta:
        managed = False
        db_table = 'myTable'


class UserFavourites(models.Model):
    user = models.CharField(db_column='User', max_length=13)  # Field name made lowercase.
    session_id = models.CharField(db_column='Session_ID', max_length=300)  # Field name made lowercase.
    store_id = models.IntegerField(db_column='Store_ID')  # Field name made lowercase.
    barcode = models.CharField(db_column='Barcode', max_length=20)  # Field name made lowercase.
    timestamp = models.DateTimeField(db_column='Timestamp')  # Field name made lowercase.
    name_of_the_product = models.CharField(db_column='Name_of_the_Product', max_length=40)  # Field name made lowercase.
    mrp = models.FloatField(db_column='MRP', blank=True, null=True)  # Field name made lowercase.
    retailer_price = models.FloatField(db_column='Retailer_Price', blank=True, null=True)  # Field name made lowercase.
    our_discount = models.FloatField(db_column='Our_Discount', blank=True, null=True)  # Field name made lowercase.
    our_price = models.FloatField(db_column='Our_Price', blank=True, null=True)  # Field name made lowercase.
    total_discount = models.FloatField(db_column='Total_Discount', blank=True, null=True)  # Field name made lowercase.

    class Meta:
        managed = False
        db_table = 'user_favourites'
