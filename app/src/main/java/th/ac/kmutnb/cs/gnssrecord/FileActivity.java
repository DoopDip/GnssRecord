package th.ac.kmutnb.cs.gnssrecord;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import th.ac.kmutnb.cs.gnssrecord.adapter.FileAdapter;

public class FileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        TextView textViewEmpty = findViewById(R.id.file_empty);
        RecyclerView recyclerView = findViewById(R.id.file_recyclerView);

        List<File> files = getFilesRinex();
        if (!files.isEmpty()) {
            textViewEmpty.setVisibility(View.INVISIBLE);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
            recyclerView.setAdapter(new FileAdapter(files));
        } else
            textViewEmpty.setVisibility(View.VISIBLE);
    }

    private List<File> getFilesRinex() {
        File[] files = new File(Environment.getExternalStorageDirectory(), getString(R.string.app_name) + "_Rinex")
                .listFiles();
        return Arrays.stream(files)
                .filter(file -> !file.isDirectory())
                .collect(Collectors.toList());
    }
}
