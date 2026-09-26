package com.example.semesterproject.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.semesterproject.R;
import com.example.semesterproject.adapter.FoodListAdapter;
import com.example.semesterproject.databinding.ActivityListFoodsBinding;
import com.example.semesterproject.domain.Foods;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ListFoodsActivity extends BaseActivity {
    ActivityListFoodsBinding binding;
    private RecyclerView.Adapter adapterListFood;
    private int categoryId;
    private String categoryName;
    private String searchTxt;
    private boolean isSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityListFoodsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getIntentExtra();
        initList();
        setVariable();
    }

    private void setVariable() {
        binding.backBtn.setOnClickListener(v -> finish());
    }

    private void initList() {
        DatabaseReference myRef = database.getReference("Foods");
        binding.progressBar.setVisibility(View.VISIBLE);
        ArrayList<Foods> list = new ArrayList<>();
        Query query;

        if (isSearch) {
            query = myRef.orderByChild("Title").startAt(searchTxt).endAt(searchTxt + '\uf8ff');
        } else {
            query = myRef.orderByChild("CategoryId").equalTo(categoryId);
        }

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
                            binding.foodListView.setLayoutManager(new GridLayoutManager(ListFoodsActivity.this, 2));
                            adapterListFood = new FoodListAdapter(finalList);
                            binding.foodListView.setAdapter(adapterListFood);
                            saveDataToSharedPreferences("foods_" + categoryId, finalList); // Save to SharedPreferences
                        }
                        binding.progressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        } else {
            list = getDataFromSharedPreferences("foods_" + categoryId, Foods.class); // Load from SharedPreferences
            if (list.size() > 0) {
                binding.foodListView.setLayoutManager(new GridLayoutManager(ListFoodsActivity.this, 2));
                adapterListFood = new FoodListAdapter(list);
                binding.foodListView.setAdapter(adapterListFood);
                binding.progressBar.setVisibility(View.GONE);
            }
        }
    }

    private void getIntentExtra() {
        categoryId = getIntent().getIntExtra("CategoryId", 0);
        categoryName = getIntent().getStringExtra("CategoryName");
        searchTxt = getIntent().getStringExtra("text");
        isSearch = getIntent().getBooleanExtra("isSearch", false);

        binding.titleTxt.setText(categoryName);
    }

    private boolean isNetworkAvailable() {
        // Check network availability here
        return true; // Placeholder for actual network check
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
