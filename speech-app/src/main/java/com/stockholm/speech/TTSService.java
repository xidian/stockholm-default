package com.stockholm.speech;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.stockholm.common.speech.SpeechAction;
import com.stockholm.common.utils.StockholmLogger;


public class TTSService extends Service {
    private static final String TAG = "TTSService";

    private long speakId = -1;
    private String sourceDirPath;

    private SpeechSynthesizerListener listener = new SpeechSynthesizerListener() {
        @Override
        public void onSynthesizeStart(String utteranceId) {
            StockholmLogger.d(TAG, "onSynthesizeStart: ");
        }

        @Override
        public void onSynthesizeDataArrived(String utteranceId, byte[] bytes, int i) {
            StockholmLogger.d(TAG, "onSynthesizeDataArrived: ");
        }

        @Override
        public void onSynthesizeFinish(String utteranceId) {
            StockholmLogger.d(TAG, "onSynthesizeFinish: ");
        }

        @Override
        public void onSpeechStart(String utteranceId) {
            StockholmLogger.d(TAG, "onSpeechStart: ");
        }

        @Override
        public void onSpeechProgressChanged(String utteranceId, int progress) {
            StockholmLogger.d(TAG, "onSpeechProgressChanged: " + progress);
        }

        @Override
        public void onSpeechFinish(String utteranceId) {
            StockholmLogger.d(TAG, "onSpeechFinish: ");
            sendFinishBroadcast();
        }

        @Override
        public void onError(String utteranceId, SpeechError speechError) {
            StockholmLogger.e(TAG, "onError: " + speechError.toString());
            sendFinishBroadcast();
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        StockholmLogger.d(TAG, "TTSService onStartCommand");

        if (intent == null) {
            return START_STICKY_COMPATIBILITY;
        }

        String content = intent.getStringExtra(TTSConstant.TTS_CONTENT);
        speakId = intent.getLongExtra(TTSConstant.TTS_SPEAK_ID, -1);
        StockholmLogger.d(TAG, "speech content: " + content + "--speakId: " + speakId);
        if (!TextUtils.isEmpty(content)) {
            if (!TextUtils.isEmpty(content)) {
                SpeechSynthesizer.getInstance().stop();
            }
            int result = SpeechSynthesizer.getInstance().speak(content);
            if (result < 0) StockholmLogger.d(TAG, "onStartCommand error: " + result);
        }

        return START_STICKY_COMPATIBILITY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        StockholmLogger.d(TAG, "TTS onCreate: ");
        sourceDirPath = getExternalFilesDir(null).getPath();
        initSpeech();
    }

    private void initSpeech() {
        SpeechSynthesizer speechSynthesizer = SpeechSynthesizer.getInstance();
        speechSynthesizer.setContext(this);
        speechSynthesizer.setSpeechSynthesizerListener(listener);
        // 文本模型文件路径 (离线引擎使用)
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, sourceDirPath + "/"
                + FileConstant.TEXT_MODEL_NAME);
        // 声学模型文件路径 (离线引擎使用)
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, sourceDirPath + "/"
                + FileConstant.SPEECH_FEMALE_MODEL_NAME);
        speechSynthesizer.setAppId("10052601");
        speechSynthesizer.setApiKey("zB73nzR7IIEsdOZtBIgir9fc", "0f305a7ee240c85767ea4f5390ae2913");
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0");
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);

        speechSynthesizer.initTts(TtsMode.MIX);

        // 必须在初始化之后加载离线英文资源（提供离线英文合成功能）
        int result = speechSynthesizer.loadEnglishModel(sourceDirPath + "/" + FileConstant.ENGLISH_TEXT_MODEL_NAME,
                sourceDirPath + "/" + FileConstant.ENGLISH_SPEECH_FEMALE_MODEL_NAME);
        StockholmLogger.d(TAG, "initSpeech: loadEnglishModel result: " + result);
    }

    private void sendFinishBroadcast() {
        StockholmLogger.i(TAG, "end speakId: " + speakId);
        Intent intent = new Intent(SpeechAction.ACTION_TTS_SPEAK_FINISH);
        intent.putExtra(SpeechAction.KEY_TTS_SPEAK_ID, speakId);
        getApplicationContext().sendBroadcast(intent);
        speakId = -1;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (SpeechSynthesizer.getInstance() != null) {
            SpeechSynthesizer.getInstance().stop();
            SpeechSynthesizer.getInstance().release();
        }
    }

}