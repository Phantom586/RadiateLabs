from django.shortcuts import render
from django.contrib.auth.decorators import login_required
from django.contrib.auth.models import User
from django.db.models import Sum
from .models import StoreTable, InvoiceTable, BasketTable, ProductDatabaseDesiFarms
from django.contrib.auth.decorators import user_passes_test
import datetime
import json

@login_required
def noq_admin(request):

    # getting the store_id of the current user
    user = request.user
    s_id = user.id

    st, dt= retrieve_admin_data()

    return render(request, 'BaseApp/admin.html', {'store_data': st, 'dates': dt})


@login_required
def index(request):

    # getting the store_id of the current user
    user = request.user
    s_id = user.id

    # function to retrieve all time data from the database for the respective store_id
    d1 = retrieve_all_time_data(s_id)
    # function to retrieve yesterday's data from database for the respective store_id
    d2 = retrieve_yesterdays_data(s_id)
    # function to retrieve the Top 5 Products from the database for respective store_id
    d3 = retrieve_top_products(s_id)
    # To calculate Total Value of Transactions of last 7 days 
    d4 = last_7_days_data(s_id)

    return render(request, 'BaseApp/index.html', {'all_time':d1, 'yday':d2, 'top5':d3, 'weekData':d4})


def retrieve_admin_data():

    stores = list(StoreTable.objects.all().values())
    dates = {}
    # print(stores)
    
    today = datetime.date.today()
    # Adding the Today's Date to Dates Dict.
    dates['today'] = today.strftime("%d-%b")
    # Converting datetime object to String.
    today = str(today)
    t_from = today + ' 6:00:00'
    t_to = today + ' 16:00:00'

    yesterday = datetime.date.today() - datetime.timedelta(days=1)
    # Adding the Today's Date to Dates Dict.
    dates['yesterday'] = yesterday.strftime("%d-%b")
    # Converting datetime object to String.
    yesterday = str(yesterday)
    y_from = yesterday + ' 6:00:00'
    y_to = yesterday + ' 16:00:00'

    for store in stores:

        # Current Store's Id
        s_id = str(store['store_id'])
        # Retrieving Today's Data
        store['t_tot_retail_price'], store['t_final_amount_paid'] = retrieve_data_by_date(s_id, t_from, t_to)
        # Retrieving Yesterday's Data
        store['y_tot_retail_price'], store['y_final_amount_paid'] = retrieve_data_by_date(s_id, y_from, y_to)

    # print(stores)

    return stores, dates


def retrieve_data_by_date(s_id, _from, _to):

    # Retrieving the Sum of the Total_Retailers_price for given Date for current store_id
        tmp = InvoiceTable.objects.filter(store_id=s_id, timestamp__range=(_from, _to)).aggregate(Sum('total_retailers_price'))
        # the above line returns a dict, so storing it in my data dict.
        tot_retail_price = tmp['total_retailers_price__sum']
        # Checking for No Data
        if tot_retail_price is None:
            tot_retail_price = 0

        # Retrieving the Sum of the Final_Amount_Paid for given Date for current store_id
        tmp = InvoiceTable.objects.filter(store_id=s_id, timestamp__range=(_from, _to)).aggregate(Sum('final_amount_paid'))
        # the above line returns a dict, so storing it in my data dict.
        final_amount_paid = tmp['final_amount_paid__sum']
        # Checking for No Data
        if final_amount_paid is None:
            final_amount_paid = 0

        return tot_retail_price, final_amount_paid



def last_7_days_data(s_id):

    data = {}
    dates = []
    lst_tot_retail_price = []
    lst_no_of_items = []
    lst_no_of_txns = []

    # loop to calculate last 7 Days data
    for i in range(7):

        # Dynamically Calculating the last 7 Days dates.
        days = datetime.date.today() - datetime.timedelta(days=i)
        # Adding dates in Format (7-Feb) to the dates list.
        dates.append(days.strftime("%d-%b"))
        days = str(days)
        # Conatenating the required timestamps
        _from = days + ' 6:00:00'
        _to = days + ' 16:00:00'

        # Retrieving the Sum of the Total_Retailers_price for last 7 days for current store_id
        tmp = InvoiceTable.objects.filter(store_id="3", timestamp__range=(_from, _to)).aggregate(Sum('total_retailers_price'))
        # the above line returns a dict, so storing it in my data dict.
        temp = tmp['total_retailers_price__sum']
        # Checking if the data for the current date exists or not.
        if temp is None:
            temp = 0
        lst_tot_retail_price.append( temp)
        
        # Retrieving the Sum of the Number_of_items sold last 7 days for current store_id
        tmp = BasketTable.objects.filter(store_id="3", timestamp__range=(_from, _to)).aggregate(Sum('number_of_items'))
        # the above line returns a dict, so storing it in my data dict.
        temp = tmp['number_of_items__sum']
        # Checking if the data for the current date exists or not.
        if temp is None:
            temp = 0
        lst_no_of_items.append( temp)
        
        # Retrieving the Count of the Total_Transactions made last 7 days from current store_id
        tmp= InvoiceTable.objects.filter(store_id="3", timestamp__range=(_from, _to)).count()
        # Checking if the data for the current date exists or not.
        if tmp is None:
            tmp = 0
        lst_no_of_txns.append( tmp)
        
    # print(lst_tot_retail_price)
    # print(dates)
    data['tot_retail_price'] = lst_tot_retail_price[::-1]
    data['lastDates'] = json.dumps(dates[::-1])
    data['no_of_items'] = lst_no_of_items[::-1]
    data['tot_txns'] = lst_no_of_txns[::-1]

    return data


def users_with_more_than_one_txn():

    # Getting all the users that have made a Txn.
    tot_users = InvoiceTable.objects.filter(store_id="3").values('user')
    # Extracting the individual Phone No's present in the Table.
    total_users = []
    for i in tot_users:
        total_users.append(i['user'])
    # Getting the Unique Users that have made a Txn.
    uniq_users = InvoiceTable.objects.filter(store_id="3").values('user').distinct()
    # Extracting the Individual Unique Phone No's Present in the Table.
    unique_users = []
    for i in uniq_users:
        unique_users.append(i['user'])
    # Calculating the Users with more than one Txn.
    approx_users = 0
    for i in unique_users:
        if total_users.count(i) > 1:
            approx_users += 1
    print(approx_users)


def retrieve_all_time_data(s_id):
    
    data = {}

    # Retrieving the Count of the Total_Transactions made from current store_id
    data['tot_txns'] = InvoiceTable.objects.filter(store_id="3").count()

    # Retrieving the Sum of the Number_of_items from current store_id
    tmp = BasketTable.objects.filter(store_id="3").aggregate(Sum('number_of_items'))
    # the above line returns a dict, so storing it in my data dict.
    data['tot_items_sold'] = tmp['number_of_items__sum']

    # Retrieving the Sum of the Total_Retailers_price for current store_id
    tmp = InvoiceTable.objects.filter(store_id="3").aggregate(Sum('total_retailers_price'))
    # the above line returns a dict, so storing it in my data dict.
    data['tot_retail_price'] = tmp['total_retailers_price__sum']

    data['unique_users'] = InvoiceTable.objects.filter(store_id="3").values('user').distinct().count()

    return data


def retrieve_yesterdays_data(s_id):
    
    data = {}

    yesterday = str(datetime.date.today() - datetime.timedelta(days=1))
    _from = yesterday + ' 6:00:00'
    _to = yesterday + ' 16:00:00'
    # Retrieving the Count of the Total_Transactions made yesterday from current store_id
    data['tot_txns'] = InvoiceTable.objects.filter(store_id="3", timestamp__range=(_from, _to)).count()

    # Retrieving the Sum of the Number_of_items sold yesterday from current store_id
    tmp = BasketTable.objects.filter(store_id="3", timestamp__range=(_from, _to)).aggregate(Sum('number_of_items'))
    # the above line returns a dict, so storing it in my data dict.
    tmp = tmp['number_of_items__sum']
    if tmp is None:
        tmp = 0
    data['tot_items_sold']  = tmp

    # Retrieving the Sum of the Total_Retailers_price for current store_id
    tmp = InvoiceTable.objects.filter(store_id="3", timestamp__range=(_from, _to)).aggregate(Sum('total_retailers_price'))
    # the above line returns a dict, so storing it in my data dict.
    tmp = tmp['total_retailers_price__sum']
    if tmp is None:
        tmp = 0
    data['tot_retail_price'] = tmp

    return data


def retrieve_top_products(s_id):
    
    data = {}

    # function to calculate the times_purchased for individual products in database for specific store_id and save them in database. 
    # calc_times_purchased(s_id)

    # list to hold products_times_purchased
    prod_t_p = []
    # list to hold top 5 products times_purchased
    top5_products = []
    # list to verify that only unique products get into the top 5 Products list
    check_top5 = [0] * 5

    # Retrieving the required columns from ProductDatabaseDesiFarms Table for specific store_id.
    products = list(ProductDatabaseDesiFarms.objects.filter(store_id="3").values('barcode', 'name_of_the_product', 'times_purchased'))
    
    # Extracting the Times_Purchased value from each products object and append it into product_times_purchased list
    for i in products:
        prod_t_p.append(i['times_purchased'])

    prod_t_p.sort(reverse=True)
    # calculating the length of the product_times_purchased
    ln = len(prod_t_p)
    ln_p = len(products)
    # extracting the last five elements of the list
    prod_t_p = prod_t_p[0:5]

    # going through the product_top_five list to identify the products details associated with the top5 products.
    # ensured no duplicate items are appended to the list.
    for i, v in enumerate(prod_t_p):
        for j in range(ln_p):
            if check_top5[i] == 0:
                if products[j]['times_purchased'] == v:
                    check_top5[i] = 1
                    tmp = products[j].copy()
                    top5_products.append(tmp)
                    products[j]['times_purchased'] = 0
                    break

    return top5_products


def calc_times_purchased(s_id):

    products_barcodes = ProductDatabaseDesiFarms.objects.filter(store_id="3").values('barcode')

    for b_code in products_barcodes:
        b = b_code['barcode']
        times_purchased = BasketTable.objects.filter(store_id="3", barcode=b).aggregate(Sum('number_of_items'))
        # print(b, times_purchased['number_of_items__sum'])
        product = ProductDatabaseDesiFarms.objects.get(store_id="3", barcode=b)
        
        times_purchased = times_purchased['number_of_items__sum']

        if times_purchased is not None:
            product.times_purchased = times_purchased
        else:
            product.times_purchased = 0
        product.save(update_fields=['times_purchased'])


@login_required
def show_profile(request):

    # getting the store_id of the current user
    user = request.user
    s_id = user.id
    data = {}
    print(s_id)
    # Retrieving the details of the Store from the Store_table for the specific store_id
    st = StoreTable.objects.filter(store_id="4")
    # the above line returns a QuerySet so looping through it to access each object's info
    for s in st:
        s_name = s.store_name
        s_addr = s.store_address
        s_city = s.store_city
        pin = s.pincode
        s_state = s.store_state
        s_country = s.store_country

        data = {'store_name':s_name, 'store_address':s_addr, 'store_city':s_city, 'pincode':pin, 'store_state':s_state, 'store_country':s_country}

    return render(request, 'BaseApp/profile.html', data)


@login_required
def myProd(request):

    # getting the store_id of the current user
    user = request.user
    s_id = user.id
    # Retrieving the details of the items stored in Database for specific store_id
    items = ProductDatabaseDesiFarms.objects.filter(store_id="1").values('barcode', 'brand_name', 'name_of_the_product', 'mrp', 'retailer_discount', 'retailer_price')

    return render(request, 'BaseApp/myprod.html', {'items_data':items})