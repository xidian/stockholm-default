package com.stockholm.speech.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.baidu.tts.client.SpeechSynthesizer;
import com.stockholm.common.speech.SpeechAction;
import com.stockholm.common.utils.StockholmLogger;
import com.stockholm.speech.TTSConstant;
import com.stockholm.speech.TTSService;


public class SpeechContentReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(SpeechAction.ACTION_TTS_SPEAK)) {
            String content = intent.getStringExtra(SpeechAction.KEY_TTS_SPEAK_MESSAGE);
            long speakId = intent.getLongExtra(SpeechAction.KEY_TTS_SPEAK_ID, -1);
            StockholmLogger.d("SpeechContentReceiver", "receive speakId: " + speakId);
            Intent serviceIntent = new Intent(context, TTSService.class);
            serviceIntent.putExtra(TTSConstant.TTS_CONTENT, content);
            serviceIntent.putExtra(TTSConstant.TTS_SPEAK_ID, speakId);
            context.startService(serviceIntent);
        } else if (intent.getAction().equals(SpeechAction.ACTION_TTS_STOP)) {
            SpeechSynthesizer.getInstance().stop();
        }
    }

}