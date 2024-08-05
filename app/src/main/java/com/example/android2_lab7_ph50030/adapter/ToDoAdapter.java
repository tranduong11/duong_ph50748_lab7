package com.example.android2_lab7_ph50030.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android2_lab7_ph50030.R;
import com.example.android2_lab7_ph50030.models.Todo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ToDaViewHolder>{

    private List<Todo> list;
    private Context context;
    private EditText edtTitle,edtContent,edtDate,edtType;
    private Button btnUpdate,btnCancel;
    private Dialog dialog;

    private FirebaseFirestore database;

    public ToDoAdapter(List<Todo> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ToDaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ToDaViewHolder(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.layout_item_todolist,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ToDaViewHolder holder, int position) {
        Todo todo=list.get(position);
        holder.tvContent.setText(todo.getContent());
        holder.tvDate.setText(todo.getDate());
        if (todo.getStatus()==1){
            holder.cbStatus.setChecked(true);
            holder.tvContent.setPaintFlags(holder.tvContent.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else {
            holder.cbStatus.setChecked(false);
            holder.tvContent.setPaintFlags(holder.tvContent.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
        holder.imgUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog=new Dialog(view.getContext());
                dialog.setContentView(R.layout.activity_custom_dialog);
                init(todo);
                clickDialog(todo);
                dialog.show();
            }
        });
        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database=FirebaseFirestore.getInstance();
                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                builder.setTitle("Thông báo");
                builder.setMessage("Bạn có chắc chắn muốn xoá không");
                builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        database.collection("TODO").document(todo.getId())
                                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(context, "Xoá thành công", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e("Loi","zz"+e);
                                    }
                                });
                        dialogInterface.dismiss();
                    }
                });
                builder.setNegativeButton("Huỷ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog dialog1=builder.create();
                dialog1.show();
            }
        });
        holder.cbStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                database=FirebaseFirestore.getInstance();
                boolean check=holder.cbStatus.isChecked();
                int value=check ? 1 : 0;
                database.collection("TODO").document(todo.getId())
                        .update("status",value).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(context, "Cập nhật trạng thái công việc thành công", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("loi","message"+e);
                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public void init(Todo todo){
        edtTitle=dialog.findViewById(R.id.edtTitle_dialog);
        edtContent=dialog.findViewById(R.id.edtContent_dialog);
        edtDate=dialog.findViewById(R.id.edtDate_dialog);
        edtType=dialog.findViewById(R.id.edtType_dialog);
        btnCancel=dialog.findViewById(R.id.btnCancel_Dialog);
        btnUpdate=dialog.findViewById(R.id.btnUpdate_dialog);
        edtTitle.setText(todo.getTitle());
        edtType.setText(todo.getType());
        edtContent.setText(todo.getContent());
        edtDate.setText(todo.getDate());

    }
    private void clickDialog(Todo todo){
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strTitle=edtTitle.getText().toString().trim();
                String strContent=edtContent.getText().toString().trim();
                String strDate=edtDate.getText().toString().trim();
                String strtype=edtType.getText().toString().trim();
                if(!strTitle.isEmpty()&&!strContent.isEmpty()&&!strDate.isEmpty()&&!strtype.isEmpty()){
                    todo.setTitle(strTitle);
                    todo.setContent(strContent);
                    todo.setDate(strDate);
                    todo.setType(strtype);
                    database=FirebaseFirestore.getInstance();
                    database.collection("TODO").document(todo.getId())
                            .update(todo.convertHashMap())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                                    Log.e("Loi","onFailure"+e);
                                }
                            });
                }
                else {
                    Toast.makeText(context, "Bạn cần nhập đầy đủ thông tin ", Toast.LENGTH_SHORT).show();

                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    public class ToDaViewHolder extends RecyclerView.ViewHolder {
        TextView tvContent,tvDate;
        CheckBox cbStatus;
        ImageView imgUpdate;
        ImageView imgDelete;
        public ToDaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContent=itemView.findViewById(R.id.tvContent);
            tvDate=itemView.findViewById(R.id.tvDate);
            imgDelete=itemView.findViewById(R.id.img_delete);
            imgUpdate=itemView.findViewById(R.id.img_update);
            cbStatus=itemView.findViewById(R.id.chkStatus);
        }
    }
}
