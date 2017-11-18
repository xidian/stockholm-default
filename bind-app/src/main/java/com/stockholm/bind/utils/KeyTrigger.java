package com.stockholm.bind.utils;

import android.view.KeyEvent;

import com.stockholm.common.utils.WeakHandler;

import java.util.ArrayList;

import javax.inject.Inject;

public class KeyTrigger {

    public static final int KEY_EVENT_FACTORY = 0;
    public static final int KEY_EVENT_STABLE = 1;
    private static final int KEY_INTERVAL = 3000;

    private ArrayList<Integer> inputFactoryKeyList;
    private ArrayList<Integer> inputStableKeyList;
    private WeakHandler handler = new WeakHandler();
    private KeyTriggerCallBack keyTriggerCallBack;
    private int factoryKeyList[] = {KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_DPAD_UP
            , KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_DPAD_DOWN};
    private int stableKeyList[] = {KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_ENTER,
            KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_DPAD_DOWN};

    private final Runnable factoryTriggerRunnable = new Runnable() {
        @Override
        public void run() {
            inputFactoryKeyList.clear();
        }
    };

    private final Runnable stableTriggerRunnable = new Runnable() {
        @Override
        public void run() {
            inputStableKeyList.clear();
        }
    };

    @Inject
    public KeyTrigger() {
        inputFactoryKeyList = new ArrayList();
        inputStableKeyList = new ArrayList();
    }

    public void setKeyTriggerCallBack(KeyTriggerCallBack keyTriggerCallBack) {
        this.keyTriggerCallBack = keyTriggerCallBack;
    }

    public void input(int keyCode) {
        handler.removeCallbacks(factoryTriggerRunnable);
        handler.removeCallbacks(stableTriggerRunnable);
        if (keyCode == factoryKeyList[inputFactoryKeyList.size()]) {
            inputFactoryKeyList.add(keyCode);
            if (inputFactoryKeyList.size() >= factoryKeyList.length) {
                handler.post(factoryTriggerRunnable);
                keyTriggerCallBack.keyEvent(KEY_EVENT_FACTORY);
            } else {
                handler.postDelayed(factoryTriggerRunnable, KEY_INTERVAL);
            }
        } else {
            handler.post(factoryTriggerRunnable);
        }

        if (keyCode == stableKeyList[inputStableKeyList.size()]) {
            inputStableKeyList.add(keyCode);
            if (inputStableKeyList.size() >= stableKeyList.length) {
                handler.post(stableTriggerRunnable);
                keyTriggerCallBack.keyEvent(KEY_EVENT_STABLE);
            } else {
                handler.postDelayed(stableTriggerRunnable, KEY_INTERVAL);
            }
        } else {
            handler.post(stableTriggerRunnable);
        }

    }

    public interface KeyTriggerCallBack {
        void keyEvent(int keyEvent);
    }

}
