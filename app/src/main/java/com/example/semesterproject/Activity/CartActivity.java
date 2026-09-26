package com.example.semesterproject.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.semesterproject.R;
import com.example.semesterproject.adapter.CartAdapter;
import com.example.semesterproject.databinding.ActivityCartBinding;
import com.example.semesterproject.helper.ManagmentCart;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class CartActivity extends BaseActivity {

    private ActivityCartBinding binding;
    private RecyclerView.Adapter adapter;
    private ManagmentCart managmentCart;
    private double tax;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        managmentCart = new ManagmentCart(this);

        setVariable();
        calculateCart();
        initList();
        loadCartData();
    }

    private void initList() {
        if (managmentCart.getListCart().isEmpty()) {
            binding.emptyTxt.setVisibility(View.VISIBLE);
            binding.scrollViewcart.setVisibility(View.GONE);
        } else {
            binding.emptyTxt.setVisibility(View.GONE);
            binding.scrollViewcart.setVisibility(View.VISIBLE);
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.cardView.setLayoutManager(linearLayoutManager);
        adapter = new CartAdapter(managmentCart.getListCart(), this, () -> calculateCart());

        binding.cardView.setAdapter(adapter);
    }

    private void calculateCart() {
        double percentTax = 0.05;
        double delivery = 10;

        tax = Math.round(managmentCart.getTotalFee() * percentTax * 100.0) / 100;

        double total = Math.round((managmentCart.getTotalFee() + tax + delivery) * 100) / 100;
        double itemTotal = Math.round(managmentCart.getTotalFee() * 100) / 100;

        binding.totalFee.setText("$" + itemTotal);
        binding.taxTxt.setText("$" + tax);
        binding.deliveryTxt.setText("$" + delivery);
        binding.totalTxt.setText("$" + total);

        // Save cart data to SharedPreferences
        saveCartData(itemTotal, tax, delivery, total);
    }

    private void saveCartData(double itemTotal, double tax, double delivery, double total) {
        SharedPreferences sharedPreferences = getSharedPreferences("app_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();

        // Save the values as a JSON string
        editor.putFloat("itemTotal", (float) itemTotal);
        editor.putFloat("tax", (float) tax);
        editor.putFloat("delivery", (float) delivery);
        editor.putFloat("total", (float) total);

        editor.apply();
    }

    private void loadCartData() {
        SharedPreferences sharedPreferences = getSharedPreferences("app_data", MODE_PRIVATE);

        // Retrieve the saved values
        float itemTotal = sharedPreferences.getFloat("itemTotal", 0);
        float tax = sharedPreferences.getFloat("tax", 0);
        float delivery = sharedPreferences.getFloat("delivery", 0);
        float total = sharedPreferences.getFloat("total", 0);

        // Set the retrieved values to the UI
        binding.totalFee.setText("$" + itemTotal);
        binding.taxTxt.setText("$" + tax);
        binding.deliveryTxt.setText("$" + delivery);
        binding.totalTxt.setText("$" + total);
    }

    private void setVariable() {
        binding.backBtn.setOnClickListener(v -> finish());
    }

    // Network check method
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    // Save data to SharedPreferences using Gson
    private void saveDataToSharedPreferences(String key, ArrayList<?> data) {
        SharedPreferences sharedPreferences = getSharedPreferences("app_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(data);
        editor.putString(key, json);
        editor.apply();
    }

    // Retrieve data from SharedPreferences using Gson
    private <T> ArrayList<T> getDataFromSharedPreferences(String key, Class<T> clazz) {
        SharedPreferences sharedPreferences = getSharedPreferences("app_data", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(key, null);
        Type type = TypeToken.getParameterized(ArrayList.class, clazz).getType();
        return gson.fromJson(json, type);
    }
}
