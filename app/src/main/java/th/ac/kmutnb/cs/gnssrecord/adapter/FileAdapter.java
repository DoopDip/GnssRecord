package th.ac.kmutnb.cs.gnssrecord.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;
import java.util.List;

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
        final File file = fileList.get(position);
        holder.textViewName.setText(file.getName());
        holder.textViewDateTime.setText(getFileDateTime(file.getName()));
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Click -> File: " + file.getName());
                Context context = v.getContext();
                new MaterialDialog.Builder(context)
                        .title(file.getName())
                        .items(new String[]{
                                context.getString(R.string.open),
                                context.getString(R.string.share),
                                context.getString(R.string.delete),
                        })
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                Log.i(TAG, "Dialog which: " + which);
                                //TODO
                                if (which == 0) {
                                    Log.i(TAG, "Open file" + file.getPath());
                                    if (file.exists()) {
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setType("thisis/sonotreal");
                                        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.getPath()));
                                        view.getContext().startActivity(Intent.createChooser(intent, "Share File " + file.getName()));
                                    }
                                } else if (which == 1) {
                                    Log.i(TAG, "Share file " + file.getPath());
                                    if (file.exists()) {
                                        Intent intent = new Intent(Intent.ACTION_SEND)
                                                .setDataAndType(Uri.parse("file://" + file.getPath()), "file/*");
                                        view.getContext().startActivity(Intent.createChooser(intent, "Share File " + file.getName()));
                                    }
                                } else if (which == 2) {
                                    Log.i(TAG, "Delete file" + file.getPath());
                                    if (file.delete()) {
                                        Log.i(TAG, "Delete complete");
                                        fileList.remove(holder.getAdapterPosition());
                                        notifyItemRemoved(holder.getAdapterPosition());
                                    }
                                }
                            }
                        })
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    static class FileHolder extends RecyclerView.ViewHolder {

        TextView textViewName;
        TextView textViewDateTime;
        RelativeLayout relativeLayout;

        FileHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.adapter_file_name);
            textViewDateTime = itemView.findViewById(R.id.adapter_file_dateTime);
            relativeLayout = itemView.findViewById(R.id.adapter_file);
        }
    }

    private String getFileDateTime(String name) {
        return name.substring(2, 6) + "/" +
                name.substring(6, 8) + "/" +
                name.substring(8, 10) + " " +
                name.substring(10, 12) + ":" +
                name.substring(12, 14);
    }
}
