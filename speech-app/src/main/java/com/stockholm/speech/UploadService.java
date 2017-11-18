package com.stockholm.speech;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UploadService extends IntentService {

    public UploadService() {
        super("UploadService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) return;
        List<String> files = getVoiceFiles();
        //do upload here
    }

    private List<String> getVoiceFiles() {
        List<String> voiceFiles = new ArrayList<>();
        String path = getExternalFilesDir(null) + File.separator + "msc/asr";
        File file = new File(path);
        File[] files = file.listFiles();
        if (files != null) {
            for (File voice : files) {
                if (voice.exists() && voice.getPath().toLowerCase().endsWith(".pcm"))
                    voiceFiles.add(voice.getPath());
            }
        }
        return voiceFiles;
    }

}