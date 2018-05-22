package th.ac.kmutnb.cs.gnssrecord;

import android.content.pm.ActivityInfo;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import th.ac.kmutnb.cs.gnssrecord.adapter.FileAdapter;

public class FileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        RecyclerView recyclerView = findViewById(R.id.file_recyclerView);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView.setAdapter(new FileAdapter(getFilesRinex()));
    }

    private List<File> getFilesRinex() {
        File parentDir = new File(Environment.getExternalStorageDirectory(), getString(R.string.app_name) + "_Rinex");
        ArrayList<File> listFiles = new ArrayList<>();
        File[] files = parentDir.listFiles();
        for (File file : files) {
            if (!file.isDirectory()) {
                listFiles.add(file);
            }
        }
        return listFiles;
    }
}
