package com.example.semesterproject.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.semesterproject.R;
import com.example.semesterproject.adapter.BestFoodsAdapter;
import com.example.semesterproject.adapter.CategoryAdapter;
import com.example.semesterproject.databinding.ActivityMainBinding;
import com.example.semesterproject.domain.Category;
import com.example.semesterproject.domain.Foods;
import com.example.semesterproject.domain.Location;
import com.example.semesterproject.domain.Price;
import com.example.semesterproject.domain.Time;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends BaseActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initLocation();
        initTime();
        initPrice();
        initBestFood();
        initCategory();
        setVariable();
    }

    private void setVariable() {
        binding.logoutBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });
        binding.searchBtn.setOnClickListener(v -> {
            String text = binding.searchEdt.getText().toString();

            if (!text.isEmpty()) {
                Intent intent = new Intent(MainActivity.this, ListFoodsActivity.class);
                intent.putExtra("text", text);
                intent.putExtra("isSearch", true);
                startActivity(intent);
            }
        });

        binding.cartBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CartActivity.class)));

        binding.supportBtn.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, MainActivity2.class)));
    }

    private void initBestFood() {
        DatabaseReference myRef = database.getReference("Foods");
        binding.progressBarBestFood.setVisibility(View.VISIBLE);
        ArrayList<Foods> list = new ArrayList<>();
        Query query = myRef.orderByChild("BestFood").equalTo(true);

        if (isNetworkAvailable()) {
            ArrayList<Foods> finalList = list;
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot issue : snapshot.getChildren()) {
                            finalList.add(issue.getValue(Foods.class));
                        }
                        if (finalList.size() > 0) {
                            binding.bestFoodView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                            RecyclerView.Adapter adapter = new BestFoodsAdapter(finalList);
                            binding.bestFoodView.setAdapter(adapter);
                            saveDataToSharedPreferences("best_foods", finalList); // Save to SharedPreferences
                        }
                        binding.progressBarBestFood.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        } else {
            list = getDataFromSharedPreferences("best_foods", Foods.class); // Load from SharedPreferences
            if (list.size() > 0) {
                binding.bestFoodView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                RecyclerView.Adapter adapter = new BestFoodsAdapter(list);
                binding.bestFoodView.setAdapter(adapter);
                binding.progressBarBestFood.setVisibility(View.GONE);
            }
        }
    }

    private void initCategory() {
        DatabaseReference myRef = database.getReference("Category");
        binding.progressBarCategory.setVisibility(View.VISIBLE);
        ArrayList<Category> list = new ArrayList<>();

        if (isNetworkAvailable()) {
            ArrayList<Category> finalList = list;
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot issue : snapshot.getChildren()) {
                            finalList.add(issue.getValue(Category.class));
                        }
                        if (finalList.size() > 0) {
                            binding.categoryView.setLayoutManager(new GridLayoutManager(MainActivity.this, 4));
                            RecyclerView.Adapter adapter = new CategoryAdapter(finalList);
                            binding.categoryView.setAdapter(adapter);
                            saveDataToSharedPreferences("categories", finalList); // Save to SharedPreferences
                        }
                        binding.progressBarCategory.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        } else {
            list = getDataFromSharedPreferences("categories", Category.class); // Load from SharedPreferences
            if (list.size() > 0) {
                binding.categoryView.setLayoutManager(new GridLayoutManager(MainActivity.this, 4));
                RecyclerView.Adapter adapter = new CategoryAdapter(list);
                binding.categoryView.setAdapter(adapter);
                binding.progressBarCategory.setVisibility(View.GONE);
            }
        }
    }

    private void initLocation() {
        DatabaseReference myRef = database.getReference("Location");
        ArrayList<Location> list = new ArrayList<>();

        if (isNetworkAvailable()) {
            ArrayList<Location> finalList = list;
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot issue : snapshot.getChildren()) {
                            finalList.add(issue.getValue(Location.class));
                        }
                        ArrayAdapter<Location> adapter = new ArrayAdapter<>(MainActivity.this, R.layout.sp_item, finalList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        binding.locationSp.setAdapter(adapter);
                        saveDataToSharedPreferences("locations", finalList); // Save to SharedPreferences
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        } else {
            list = getDataFromSharedPreferences("locations", Location.class); // Load from SharedPreferences
            if (list.size() > 0) {
                ArrayAdapter<Location> adapter = new ArrayAdapter<>(MainActivity.this, R.layout.sp_item, list);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.locationSp.setAdapter(adapter);
            }
        }
    }

    private void initTime() {
        DatabaseReference myRef = database.getReference("Time");
        ArrayList<Time> list = new ArrayList<>();

        if (isNetworkAvailable()) {
            ArrayList<Time> finalList = list;
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot issue : snapshot.getChildren()) {
                            finalList.add(issue.getValue(Time.class));
                        }
                        ArrayAdapter<Time> adapter = new ArrayAdapter<>(MainActivity.this, R.layout.sp_item, finalList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        binding.timeSp.setAdapter(adapter);
                        saveDataToSharedPreferences("times", finalList); // Save to SharedPreferences
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        } else {
            list = getDataFromSharedPreferences("times", Time.class); // Load from SharedPreferences
            if (list.size() > 0) {
                ArrayAdapter<Time> adapter = new ArrayAdapter<>(MainActivity.this, R.layout.sp_item, list);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.timeSp.setAdapter(adapter);
            }
        }
    }

    private void initPrice() {
        DatabaseReference myRef = database.getReference("Price");
        ArrayList<Price> list = new ArrayList<>();

        if (isNetworkAvailable()) {
            ArrayList<Price> finalList = list;
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot issue : snapshot.getChildren()) {
                            finalList.add(issue.getValue(Price.class));
                        }
                        ArrayAdapter<Price> adapter = new ArrayAdapter<>(MainActivity.this, R.layout.sp_item, finalList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        binding.priceSp.setAdapter(adapter);
                        saveDataToSharedPreferences("prices", finalList); // Save to SharedPreferences
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        } else {
            list = getDataFromSharedPreferences("prices", Price.class); // Load from SharedPreferences
            if (list.size() > 0) {
                ArrayAdapter<Price> adapter = new ArrayAdapter<>(MainActivity.this, R.layout.sp_item, list);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.priceSp.setAdapter(adapter);
            }
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    private void saveDataToSharedPreferences(String key, ArrayList<?> data) {
        SharedPreferences sharedPreferences = getSharedPreferences("app_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(data);
        editor.putString(key, json);
        editor.apply();
    }

    private <T> ArrayList<T> getDataFromSharedPreferences(String key, Class<T> clazz) {
        SharedPreferences sharedPreferences = getSharedPreferences("app_data", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(key, null);
        Type type = TypeToken.getParameterized(ArrayList.class, clazz).getType();
        return gson.fromJson(json, type);
    }
}
