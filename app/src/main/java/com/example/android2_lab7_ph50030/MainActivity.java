package com.example.android2_lab7_ph50030;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android2_lab7_ph50030.adapter.ToDoAdapter;
import com.example.android2_lab7_ph50030.models.Todo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private Button btnAdd;
    private EditText edtTitle,edtContent,edtDate,edtType;
    private RecyclerView recyclerView;
    private ToDoAdapter adapter;
    private List<Todo> listTodo;
    private FirebaseFirestore database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
        String[] dataType={"Khó","Trung bình","Dễ"};
        listTodo=new ArrayList<>();
        adapter=new ToDoAdapter(listTodo,this);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        //kết nối firebase
        database=FirebaseFirestore.getInstance();
        ListenFirebaseFireStore();
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strTitle=edtTitle.getText().toString().trim();
                String strContent=edtContent.getText().toString().trim();
                String strDate=edtDate.getText().toString().trim();
                String type=edtType.getText().toString().trim();
                //tạo id ngẫu nhiên
                String id= UUID.randomUUID().toString();
                Todo todo=new Todo(id,strTitle,strContent,strDate,type,0);
                HashMap<String, Object> mapTodo=todo.convertHashMap();
                if (!strTitle.isEmpty()&&!strContent.isEmpty()&&!strDate.isEmpty()&&!type.isEmpty()){
                    database.collection("TODO").document(id)
                            .set(mapTodo)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(MainActivity.this, "Thêm công việc thành công", Toast.LENGTH_SHORT).show();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MainActivity.this, "Thêm công việc thất bại", Toast.LENGTH_SHORT).show();
                                }
                            });
                     }
                else {
                    Toast.makeText(MainActivity.this, "Bạn cần nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void ListenFirebaseFireStore(){
        database.collection("TODO").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error!=null){
                    Log.e("TAG","Listen failed",error);
                    return;
                }
                if (value!=null){
                    for (DocumentChange dc:value.getDocumentChanges()){
                        switch (dc.getType()){
                            case ADDED: //thêm
                                dc.getDocument().toObject(Todo.class);
                                listTodo.add(dc.getDocument().toObject(Todo.class));
                                adapter.notifyItemInserted(listTodo.size()-1);
                                break;
                            case MODIFIED:    //update
                                Todo updateTodo=dc.getDocument().toObject(Todo.class);
                                if (dc.getOldIndex()==dc.getNewIndex()){
                                    listTodo.set(dc.getOldIndex(),updateTodo);
                                    adapter.notifyItemChanged(dc.getOldIndex());

                                }
                                else {
                                    listTodo.remove(dc.getOldIndex());
                                    listTodo.add(updateTodo);
                                    adapter.notifyItemMoved(dc.getOldIndex(), dc.getNewIndex());
                                }
                                break;
                            case REMOVED:
                                dc.getDocument().toObject(Todo.class);
                                listTodo.remove(dc.getOldIndex());
                                adapter.notifyItemRemoved(dc.getOldIndex());
                                break;
                        }
                    }
                }
            }
        });




    }
    public void init(){
        btnAdd=findViewById(R.id.btnAdd);
        edtTitle=findViewById(R.id.edtTitle);
        edtContent=findViewById(R.id.edtContent);
        edtDate=findViewById(R.id.edtDate);
        edtType=findViewById(R.id.edtType);
        recyclerView=findViewById(R.id.recycleview);
    }
}