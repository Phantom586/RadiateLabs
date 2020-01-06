from django.shortcuts import render
from django.contrib.auth.decorators import login_required

# Create your views here.
@login_required
def index(request):

    return render(request, 'BaseApp/index.html', {})

@login_required
def show_profile(request):

    return render(request, 'BaseApp/profile.html', {})