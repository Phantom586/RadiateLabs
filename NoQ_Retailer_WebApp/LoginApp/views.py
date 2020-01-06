from django.shortcuts import render, redirect
from django.contrib import messages
from .forms import UserRegisterForm


def register(request):

    if request.method == "POST":

        form = UserRegisterForm(request.POST)

        if form.is_valid():
            form.save()
            u_name = form.cleaned_data.get('username')
            messages.success(request, f'Account Added for {u_name}!')

            return render(request, "LoginApp/login.html", {})
    
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
