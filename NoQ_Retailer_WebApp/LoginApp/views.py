from django.shortcuts import render, redirect
from django.contrib import messages
from .forms import UserRegisterForm
from BaseApp.models import StoreTable


def register(request):

    if request.method == "POST":

        form = UserRegisterForm(request.POST)

        if form.is_valid():
            form.save()
            u_name = form.cleaned_data.get('username')
            s_name = form.cleaned_data.get('store_name')
            s_addr = form.cleaned_data.get('store_address')
            s_city = form.cleaned_data.get('store_city')
            pin = form.cleaned_data.get('pincode')
            s_state = form.cleaned_data.get('store_state')
            s_country = form.cleaned_data.get('store_country')

            st = StoreTable.objects.create(store_name=s_name, store_address=s_addr, store_city=s_city, pincode=pin, store_state=s_state, store_country=s_country)

            messages.success(request, f'Account Added for {u_name}!')

            return redirect("login")
    
    else:

        form = UserRegisterForm()

    return render(request, "LoginApp/register.html", {'form':form})


def index(request):

    if request.method == "POST":

        uname = request.POST['user_name']
        passw = request.POST['user_pass']
        print("Username :", uname)
        print("Password : ", passw)

    return render(request, "LoginApp/login.html", {})
