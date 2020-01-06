from django.urls import path
from . import views


urlpatterns = [
    path('', views.index, name='noq-homepage'),
    path('profile/', views.show_profile, name='profile'),
]