package th.ac.kmutnb.cs.gnssrecord.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;
import th.ac.kmutnb.cs.gnssrecord.BuildConfig;
import th.ac.kmutnb.cs.gnssrecord.R;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileHolder> {

    private static final String TAG = FileAdapter.class.getSimpleName();

    private List<File> fileList;

    public FileAdapter(List<File> fileList) {
        this.fileList = fileList;
    }

    @Override
    public FileHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FileHolder(LayoutInflater
                .from(parent.getContext()).inflate(R.layout.adapter_file, parent, false));
    }

    @Override
    public void onBindViewHolder(final FileHolder holder, int position) {
        File file = fileList.get(position);
        holder.textViewName.setText(file.getName());
        holder.textViewDateTime.setText(fileDateTime(file.getName()));
        holder.textViewVer.setText(fileVersion(file.getName()));
        holder.relativeLayout.setOnClickListener(v -> {
            Log.i(TAG, "Click -> File: " + file.getName());
            Context context = v.getContext();
            new MaterialDialog.Builder(context)
                    .title(file.getName())
                    .items(new String[]{
                            context.getString(R.string.open),
                            context.getString(R.string.upload),
                            context.getString(R.string.share),
                            context.getString(R.string.delete)
                    })
                    .itemsCallback((dialog, view, which, text) -> {
                        Log.i(TAG, "Dialog which: " + which);
                        if (which == 0) {
                            Log.i(TAG, "Open file" + file.getPath());
                            if (file.exists()) {
                                Uri uri = FileProvider.getUriForFile(context,
                                        BuildConfig.APPLICATION_ID + ".provider",
                                        file);
                                Intent intent = new Intent(Intent.ACTION_VIEW)
                                        .setDataAndType(uri, "text/plain");
                                view.getContext().startActivity(Intent.createChooser(intent, "Open with"));

                            }
                        } else if (which == 1) {
                            Log.i(TAG, "Upload file: " + file.getName());
                            if (file.exists()) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if (user != null) {
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                                    reference.child(user.getUid()).orderByChild("name").equalTo(file.getName())
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()) {
                                                        Snackbar.make(holder.relativeLayout,
                                                                "[ " + file.getName() + " ] " + context.getString(R.string.already_exists),
                                                                Snackbar.LENGTH_SHORT
                                                        ).show();
                                                    } else {
                                                        FirebaseStorage storage = FirebaseStorage.getInstance();
                                                        StorageReference riversRef = storage.getReference().child(user.getUid() + "/" + file.getName());
                                                        UploadTask uploadTask = riversRef.putFile(Uri.fromFile(file));
                                                        uploadTask.addOnFailureListener(exception -> {
                                                            Log.i(TAG, "Upload " + file.getName() + ": Failure");
                                                            Snackbar.make(holder.relativeLayout,
                                                                    context.getString(R.string.upload) + " [ " + file.getName() + " ] " + context.getString(R.string.failed),
                                                                    Snackbar.LENGTH_SHORT
                                                            ).show();
                                                        }).addOnSuccessListener(taskSnapshot -> {
                                                            Log.i(TAG, "Upload " + file.getName() + ": Success");
                                                            reference.child(user.getUid()).push().child("name").setValue(file.getName());
                                                            Snackbar.make(holder.relativeLayout,
                                                                    context.getString(R.string.upload) + " [ " + file.getName() + " ] " + context.getString(R.string.successfully),
                                                                    Snackbar.LENGTH_SHORT
                                                            ).show();
                                                        });
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                }
                            }
                        } else if (which == 2) {
                            Log.i(TAG, "Share file " + file.getPath());
                            if (file.exists()) {
                                Intent intent = new Intent(Intent.ACTION_SEND)
                                        .setType("text/*")
                                        .putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.getPath()));
                                view.getContext().startActivity(Intent.createChooser(intent, "Share file"));
                            }
                        } else if (which == 3) {
                            Log.i(TAG, "Delete file" + file.getPath());
                            if (file.delete()) {
                                Log.i(TAG, "Delete complete");
                                fileList.remove(holder.getAdapterPosition());
                                notifyItemRemoved(holder.getAdapterPosition());
                            }
                        }
                    })
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    static class FileHolder extends RecyclerView.ViewHolder {

        TextView textViewName;
        TextView textViewDateTime;
        TextView textViewVer;
        RelativeLayout relativeLayout;

        FileHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.adapter_file_name);
            textViewDateTime = itemView.findViewById(R.id.adapter_file_dateTime);
            textViewVer = itemView.findViewById(R.id.adapter_file_ver);
            relativeLayout = itemView.findViewById(R.id.adapter_file);
        }
    }

    private String fileDateTime(String name) {
        return name.substring(6, 8) + "/" +
                name.substring(8, 10) + "/" +
                name.substring(2, 6) + " " +
                name.substring(10, 12) + ":" +
                name.substring(12, 14);
    }

    private String fileVersion(String name) {
        return "0".equals(name.substring(16, 17)) ? "v2.11" : "v3.03";
    }
}
