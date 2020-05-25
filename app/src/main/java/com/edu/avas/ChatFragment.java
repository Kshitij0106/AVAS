package com.edu.avas;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.avas.Common.Connectivity;
import com.edu.avas.Model.Message;
import com.edu.avas.ViewHolder.MessageAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ChatFragment extends Fragment {
    private RecyclerView chatRecyclerView;
    private EditText typeHere;
    private ImageButton attachFiles, sendMessageButton;
    private Uri filepath;
    private ProgressDialog dialog;
    private StorageReference sRef;
    private DatabaseReference chatRef;
    private FirebaseAuth auth;
    private List<Message> messageList;
    private MessageAdapter adapter;
    private ValueEventListener seenListener;

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        Bundle bundle = this.getArguments();
        String teacherId = bundle.getString("tid");

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String studentId = user.getUid();

        sRef = FirebaseStorage.getInstance().getReference("Messages");
        chatRecyclerView = view.findViewById(R.id.chatRecyclerView);
        attachFiles = view.findViewById(R.id.attachFiles);
        typeHere = view.findViewById(R.id.typeHere);
        sendMessageButton = view.findViewById(R.id.sendMessageButton);
        sendMessageButton.setEnabled(false);

        openAttachDialog();
        msgSeenOrNot(studentId, teacherId);
        showMessage(studentId, teacherId);
        getMessage(studentId, teacherId);

        return view;
    }

    public void msgSeenOrNot(final String studentId, final String teacherId) {
        chatRef = FirebaseDatabase.getInstance().getReference("Conversations");
        seenListener = chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Message msg = data.getValue(Message.class);
                    if (msg.getReceiverId().equals(studentId) && msg.getSenderId().equals(teacherId)) {
                        HashMap<String, Object> status = new HashMap<>();
                        status.put("status", "Seen");
                        data.getRef().updateChildren(status);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void showMessage(final String studentId, final String teacherId) {
        if (Connectivity.isConnectedToInternet(getContext())) {
            messageList = new ArrayList<>();
            chatRef = FirebaseDatabase.getInstance().getReference("Conversations");
            chatRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    messageList.clear();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        final Message msg = data.getValue(Message.class);
                        if (msg.getSenderId().equals(studentId) && msg.getReceiverId().equals(teacherId)
                                || msg.getReceiverId().equals(studentId) && msg.getSenderId().equals(teacherId)) {
                            messageList.add(msg);
                        }

                        adapter = new MessageAdapter(getContext(), messageList);
                        adapter.setOnImageClicked(new MessageAdapter.onImageClickedListener() {
                            @Override
                            public void onImageClicked(int pos) {
                                Bundle bundle = new Bundle();
                                bundle.putString("url", messageList.get(pos).getMessage());
                                FullImageFragment fullImg = new FullImageFragment();
                                fullImg.setArguments(bundle);
                                getFragmentManager().beginTransaction().replace(R.id.mainPage, fullImg).addToBackStack(" ").commit();
                            }
                        });
                        chatRecyclerView.setHasFixedSize(true);
                        LinearLayoutManager llm = new LinearLayoutManager(getContext());
                        llm.scrollToPosition(messageList.size() - 1);
                        chatRecyclerView.setLayoutManager(llm);
                        chatRecyclerView.setAdapter(adapter);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    public void getMessage(final String senderId, final String receiverId) {
        typeHere.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                sendMessageButton.setEnabled(true);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sendMessageButton.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {
                sendMessageButton.setEnabled(true);
            }
        });
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = typeHere.getText().toString();
                if (!msg.equals(" ")) {
                    sendMessage(senderId, receiverId, msg, "text", "unSeen");
                } else if (msg.equals(" ")) {
                    Toast.makeText(getContext(), "Message is Blank", Toast.LENGTH_SHORT).show();
                }
                typeHere.setText(" ");
            }
        });
    }

    public void openAttachDialog() {
        attachFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Connectivity.isConnectedToInternet(getContext())) {
                    final Dialog attachDialog = new Dialog(getContext());
                    final TextView attachImage, attachPDF, attachWord;
                    attachDialog.setContentView(R.layout.attach_files_dialog);

                    attachImage = attachDialog.findViewById(R.id.attachImage);
                    attachPDF = attachDialog.findViewById(R.id.attachPDF);
                    attachWord = attachDialog.findViewById(R.id.attachWord);

                    attachImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectPic();
                            attachDialog.dismiss();
                        }
                    });
                    attachPDF.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectPDF();
                            attachDialog.dismiss();
                        }
                    });
                    attachWord.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectWord();
                            attachDialog.dismiss();
                        }
                    });
                    attachDialog.show();
                } else {
                    Toast.makeText(getContext(), "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void selectPic() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 101);
    }

    public void selectPDF() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 102);
    }

    public void selectWord() {
        Intent intent = new Intent();
        intent.setType("application/word");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 103);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            filepath = data.getData();
            if (requestCode == 101) {
                AlertDialog.Builder cnfrm = new AlertDialog.Builder(getContext());
                cnfrm.setCancelable(true);
                cnfrm.setMessage("Share This");
                cnfrm.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        uploadPic();
                    }
                }).setNegativeButton("Select Another", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectPic();
                    }
                });
                cnfrm.show();
            } else if (requestCode == 102) {
                AlertDialog.Builder cnfrm = new AlertDialog.Builder(getContext());
                cnfrm.setCancelable(true);
                cnfrm.setMessage("Share This");
                cnfrm.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        uploadPDF();
                    }
                }).setNegativeButton("Select Another", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectPDF();
                    }
                });
                cnfrm.show();
            } else if (requestCode == 103) {
                AlertDialog.Builder cnfrm = new AlertDialog.Builder(getContext());
                cnfrm.setCancelable(true);
                cnfrm.setMessage("Share This");
                cnfrm.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        uploadWord();
                    }
                }).setNegativeButton("Select Another", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectWord();
                    }
                });
                cnfrm.show();
            }
        } else {
            Toast.makeText(getContext(), "Try Again !", Toast.LENGTH_SHORT).show();
        }
    }

    public String getFileExtension(Uri filepath) {
        ContentResolver cr = getContext().getContentResolver();
        MimeTypeMap mp = MimeTypeMap.getSingleton();
        return mp.getExtensionFromMimeType(cr.getType(filepath));
    }

    public void uploadPic() {
        Bundle bundle = this.getArguments();
        final String teacherId = bundle.getString("tid");
        final FirebaseUser user = auth.getCurrentUser();
        if (Connectivity.isConnectedToInternet(getContext())) {
            if (filepath != null) {
                dialog = new ProgressDialog(getContext());
                dialog.setTitle("Sending Image");
                dialog.setCancelable(false);
                dialog.show();
                StorageReference ref = sRef.child("Images").child(System.currentTimeMillis() + "." + getFileExtension(filepath));
                ref.putFile(filepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        dialog.dismiss();
                        Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
                        while (!task.isSuccessful()) ;
                        Uri uri = task.getResult();
                        String url = uri.toString();
                        sendMessage(user.getUid(), teacherId, url, "image", "unSeen");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        double p = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        dialog.setMessage((int) p + "% Uploading");
                    }
                });
            }
        } else {
            Toast.makeText(getContext(), "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    public void uploadPDF() {
        Bundle bundle = this.getArguments();
        final String teacherId = bundle.getString("tid");
        final FirebaseUser user = auth.getCurrentUser();
        if (Connectivity.isConnectedToInternet(getContext())) {
            if (filepath != null) {
                dialog = new ProgressDialog(getContext());
                dialog.setCancelable(false);
                dialog.setTitle("Sending File");
                dialog.show();
                StorageReference ref = sRef.child("Docs").child(System.currentTimeMillis() + "." + "pdf");
                ref.putFile(filepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        dialog.dismiss();
                        Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
                        while (!task.isSuccessful()) ;
                        Uri uri = task.getResult();
                        String url = uri.toString();

                        sendMessage(user.getUid(), teacherId, url, "docs", "unSeen");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        double p = (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        dialog.setMessage((int) p + "% Uploading");
                        dialog.show();
                    }
                });
            }
        }
    }

    public void uploadWord() {
        Bundle bundle = this.getArguments();
        final String teacherId = bundle.getString("tid");
        final FirebaseUser user = auth.getCurrentUser();
        if (Connectivity.isConnectedToInternet(getContext())) {
            if (filepath != null) {
                dialog = new ProgressDialog(getContext());
                dialog.setTitle("Sending File");
                dialog.setCancelable(false);
                StorageReference ref = sRef.child("Docs").child(System.currentTimeMillis() + "." + "docx");
                ref.putFile(filepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        dialog.dismiss();
                        Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
                        while (!task.isSuccessful()) ;
                        Uri uri = task.getResult();
                        String url = uri.toString();

                        sendMessage(user.getUid(), teacherId, url, "docs", "unSeen");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        double d = (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        dialog.setMessage((int) d + "% Uploading");
                        dialog.show();
                    }
                });
            }
        } else {
            Toast.makeText(getContext(), "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    public void sendMessage(String senderId, String receiverId, String msg, String type, String status) {
        chatRef = FirebaseDatabase.getInstance().getReference("Conversations");

        if (Connectivity.isConnectedToInternet(getContext())) {
            HashMap<String, Object> message = new HashMap<>();
            message.put("senderId", senderId);
            message.put("receiverId", receiverId);
            message.put("message", msg);
            message.put("type", type);
            message.put("status", status);

            chatRef.push().setValue(message);
        } else {
            Toast.makeText(getContext(), "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        chatRef.removeEventListener(seenListener);
    }
}