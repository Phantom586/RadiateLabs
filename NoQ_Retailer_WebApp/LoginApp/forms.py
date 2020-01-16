from django import forms
from django.contrib.auth.models import User
from django.contrib.auth.forms import UserCreationForm


class UserRegisterForm(UserCreationForm):
    email = forms.EmailField()
    store_name = forms.CharField(max_length=150)
    store_address = forms.CharField(max_length=200)
    store_city = forms.CharField(max_length=100)
    pincode = forms.IntegerField()
    store_state = forms.CharField(max_length=100)
    store_country = forms.CharField(max_length=100)

    class Meta:
        model = User
        fields = ['username', 'email', 'password1', 'password2', 'store_name', 'store_address', 'store_city', 'pincode', 'store_state', 'store_country']
        