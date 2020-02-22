from django.urls import path
from . import views


urlpatterns = [
    path('', views.index, name='noq-homepage'),
    path('profile/', views.show_profile, name='profile'),
    path('my_products/', views.myProd, name="myproduct"),
    path('@noq_admin/', views.noq_admin, name="noqadmin" ),
]