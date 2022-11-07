package com.example.ct_harshitha_android;

import androidx.annotation.NonNull;
import  androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.clevertap.android.sdk.CTInboxListener;
import com.clevertap.android.sdk.CTInboxStyleConfig;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.displayunits.DisplayUnitListener;
import com.clevertap.android.sdk.displayunits.model.CleverTapDisplayUnit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements CTInboxListener{

    CleverTapAPI clevertapDefaultInstance;
    Location location;
    LocationManager locationManager;
    String provider;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //CT Instance
        clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());

        //Basic Location
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);
        checkLocationPermission();

        //CT Location
        clevertapDefaultInstance.enableDeviceNetworkInfoReporting(true);
        clevertapDefaultInstance.setLocation(location);

        //Notification Channel
        CleverTapAPI.createNotificationChannel(getApplicationContext(),"Test12","Harshitha-Android","Android Test", NotificationManager.IMPORTANCE_MAX,true);
        clevertapDefaultInstance.setDebugLevel(3);

        //Initializing inbox
        if (clevertapDefaultInstance != null) {
            //Set the Notification Inbox Listener
            clevertapDefaultInstance.setCTNotificationInboxListener(this);
            //Initialize the inbox and wait for callbacks on overridden methods
            clevertapDefaultInstance.initializeInbox();
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        Button userProfileButton = (Button) findViewById(R.id.button1);
        userProfileButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                createUser();
                Toast.makeText(getApplicationContext(), "User profile created", Toast.LENGTH_LONG).show();
            }
        });

        Button profilePushButton = (Button) findViewById(R.id.button3);
        profilePushButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                updateUser();
                Toast.makeText(getApplicationContext(), "User profile updated", Toast.LENGTH_LONG).show();
            }
        });

        Button becomingCustomerButton = (Button) findViewById(R.id.customer);
        becomingCustomerButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                becomingCustomer();
                Toast.makeText(getApplicationContext(), "Transaction Completed", Toast.LENGTH_LONG).show();
            }
        });

        inboxDidInitialize();

        Button addToCartButton = (Button) findViewById(R.id.addToCart_button);
        addToCartButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                addToCart();
                Toast.makeText(getApplicationContext(), "Items added to cart", Toast.LENGTH_LONG).show();
            }
        });

        Button viewProductButton = (Button) findViewById(R.id.viewProduct_button);
        viewProductButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                viewProduct();
                Toast.makeText(getApplicationContext(), "Product Viewed", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void createUser()
    {
        //OnUserLogin
        HashMap<String, Object> profileOnuserLogin = new HashMap<String, Object>();
        /*profileOnuserLogin.put("Name", "CTTest 1");    // String
        profileOnuserLogin.put("Identity", 1);      // String or number
        profileOnuserLogin.put("Email", "ct126396@gmail.com"); // Email address of the user
        profileOnuserLogin.put("Phone", "+917338080340");
        //profileOnuserLogin.put("Gender", "F");             // Can be either M or F
        //Optional
        profileOnuserLogin.put("MSG-sms", false);          // Disable SMS notifications
        profileOnuserLogin.put("MSG-whatsapp", false);      // Enable WhatsApp notifications*/
        profileOnuserLogin.put("Identity", 2);      // String or number
        profileOnuserLogin.put("Email", "tstharsh@gmail.com"); // Email address of the user
        profileOnuserLogin.put("Phone", "+917338080340");
        profileOnuserLogin.put("MSG-sms", true);          // Disable SMS notifications
        profileOnuserLogin.put("MSG-whatsapp", true);
        clevertapDefaultInstance.onUserLogin(profileOnuserLogin);
        clevertapDefaultInstance.pushEvent("User Profile Created");
    }

    private void updateUser()
    {
        clevertapDefaultInstance.setDebugLevel(3);
        String date_string = "02-11-1996";
        //Instantiating the SimpleDateFormat class
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        //Parsing the given String to Date object
        Date date = null;
        try {
            date = formatter.parse(date_string);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        location= clevertapDefaultInstance.getLocation();
        //Profile Update
        HashMap<String, Object> profilePush = new HashMap<String, Object>();
        profilePush.put("DOB",date);
        profilePush.put("Location",location);
        clevertapDefaultInstance.pushProfile(profilePush);
        clevertapDefaultInstance.setLocation(location);
        clevertapDefaultInstance.pushEvent("Profile Updated");
    }

    private void becomingCustomer()
    {
        HashMap<String, Object> chargeDetails = new HashMap<String, Object>();
        chargeDetails.put("Amount", 2000);
        chargeDetails.put("Payment Mode", "Credit card");
        chargeDetails.put("Charged ID", 24052027);

        HashMap<String, Object> item1 = new HashMap<String, Object>();
        item1.put("Product category", "books");
        item1.put("Book name", "Girl who drank the moon");
        item1.put("Quantity", 2);

        HashMap<String, Object> item2 = new HashMap<String, Object>();
        item2.put("Product category", "Shoes");
        item2.put("Shoes name", "Nik");
        item2.put("Quantity", 1);

        HashMap<String, Object> item3 = new HashMap<String, Object>();
        item3.put("Product category", "Shirts");
        item3.put("Brand name", "tex");
        item3.put("Quantity", 2);

        ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();
        items.add(item1);
        items.add(item2);
        items.add(item3);

        clevertapDefaultInstance.pushChargedEvent(chargeDetails, items);

    }

    private void addToCart()
    {
        HashMap<String, Object> item1 = new HashMap<String, Object>();
        item1.put("Product category", "books");
        item1.put("Book name", "Girl who drank the moon");
        item1.put("Quantity", 2);
        item1.put("Product ID",12);

        clevertapDefaultInstance.pushEvent("Added to Cart", item1);
    }

    private void viewProduct()
    {
        HashMap<String, Object> prodViewedAction = new HashMap<String, Object>();
        prodViewedAction.put("Product Name", "Casio Watch");
        prodViewedAction.put("Category", "Mens Accessories");
        prodViewedAction.put("Price", 15000);
        prodViewedAction.put("Date", new java.util.Date());

        clevertapDefaultInstance.pushEvent("Product viewed", prodViewedAction);
    }
    //App Inbox
    @Override
    public void inboxDidInitialize()
    {
        Button yourInboxButton = (Button) findViewById(R.id.inbox_button);
        yourInboxButton.setOnClickListener(v -> {
            ArrayList<String> tabs = new ArrayList<>();
            tabs.add("Promotions");
            tabs.add("Offers");//We support upto 2 tabs only. Additional tabs will be ignored

            CTInboxStyleConfig styleConfig = new CTInboxStyleConfig();
            styleConfig.setFirstTabTitle("First Tab");
            styleConfig.setTabs(tabs);//Do not use this if you don't want to use tabs
            styleConfig.setTabBackgroundColor("#FF0000");
            styleConfig.setSelectedTabIndicatorColor("#0000FF");
            styleConfig.setSelectedTabColor("#0000FF");
            styleConfig.setUnselectedTabColor("#FFFFFF");
            styleConfig.setBackButtonColor("#FF0000");
            styleConfig.setNavBarTitleColor("#FF0000");
            styleConfig.setNavBarTitle("MY INBOX");
            styleConfig.setNavBarColor("#FFFFFF");
            styleConfig.setInboxBackgroundColor("#ADD8E6");
            if (clevertapDefaultInstance != null) {
                clevertapDefaultInstance.showAppInbox(styleConfig); //With Tabs
            }
            //ct.showAppInbox();//Opens Activity with default style configs
        });
    }

    @Override
    public void inboxMessagesDidUpdate()
    {

    }

    //Location
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

}