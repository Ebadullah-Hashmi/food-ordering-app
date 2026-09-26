package com.example.semesterproject.Activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.semesterproject.R;
import com.example.semesterproject.adapter.MessageAdapter;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MainActivity2 extends AppCompatActivity {

    RecyclerView recyclerView;
    EditText message;
    ImageView send;
    List<MessageModel> list;
    MessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        recyclerView = findViewById(R.id.recyclerView);
        message = findViewById(R.id.messageTxt);
        send = findViewById(R.id.sende);

        list = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new MessageAdapter(list);
        recyclerView.setAdapter(adapter);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String question = message.getText().toString();

                if (question.isEmpty()) {
                    Toast.makeText(MainActivity2.this, "Write something", Toast.LENGTH_SHORT).show();
                } else {
                    hideKeyboard(view);
                    addToChat(question, MessageModel.SENT_BY_ME);
                    message.setText("");
                    callModel(question);
                }
            }
        });
    }

    private void callModel(String promptText) {
        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash", "AIzaSyDUBHtbJcwi0jDC0NZLR8d-dr8vVyMKt3E");
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);
        Content content = new Content.Builder()
                .addText(promptText + " ")
                .build();

        CompletableFuture.supplyAsync(() -> {
            try {
                return model.generateContent(content).get(); // Blocking call to get the result
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).thenAccept(result -> {
            String resultText = result.getText();
            runOnUiThread(() -> addToChat(resultText, MessageModel.SENT_BY_BOT));
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            runOnUiThread(() -> addToChat("Failed to load: " + throwable.getMessage(), MessageModel.SENT_BY_BOT));
            return null;
        });
    }

    private void addToChat(String text, String sentBy) {
        runOnUiThread(() -> {
            list.add(new MessageModel(text, sentBy));
            adapter.notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(adapter.getItemCount());
        });
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
