package com.stockholm.bind.wifi;


import android.os.Handler;
import android.os.Message;

import com.stockholm.api.socket.AcceptorConfig;
import com.stockholm.api.socket.AcceptorManager;
import com.stockholm.api.socket.SessionManager;
import com.stockholm.bind.Constant;
import com.stockholm.common.utils.StockholmLogger;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import javax.inject.Inject;

public class WiFiHelper {

    private static final String TAG = "WiFiHelper";

    private static final int MSG_RECEIVE_MESSAGE = 1;
    private static final int MSG_CONNECT = 2;

    private APManager apManager;

    private AcceptorManager acceptorManager;
    private WiFiListener listener;
    private IoSession ioSession;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_RECEIVE_MESSAGE:
                    listener.onMessageReceive(msg.obj.toString());
                    break;
                case MSG_CONNECT:
                    listener.onConnect((Long) msg.obj);
                    break;
                default:
            }
        }
    };

    @Inject
    public WiFiHelper(APManager apManager) {
        this.apManager = apManager;
    }

    public void init(WiFiListener listener) {
        this.listener = listener;
    }

    public void createAP() {
        boolean ap = apManager.createAP();
        StockholmLogger.d(TAG, "create ap:" + ap);
    }

    public void openWiFi() {
        boolean open = apManager.openWifi();
        StockholmLogger.d(TAG, "open wifi:" + open);
    }

    public boolean isWiFiEnable() {
        return apManager.isWifiEnable();
    }

    public void disableAP() {
        apManager.disableAp();
    }

    public void start() {
        new Thread(() -> {
            try {
                AcceptorConfig acceptorConfig = new AcceptorConfig.Builder()
                    .setPort(Constant.DEFAULT_SERVER_TCP_PORT)
                    .setIoHandler(new IoHandlerAdapter() {

                        @Override
                        public void sessionCreated(IoSession session) throws Exception {
                            super.sessionCreated(session);
                            StockholmLogger.d(TAG, "session created. " + session.getId());
                            handler.obtainMessage(MSG_CONNECT, session.getId()).sendToTarget();
                        }

                        @Override
                        public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
                            super.sessionIdle(session, status);
                            StockholmLogger.d(TAG, "session idle.");
                        }

                        @Override
                        public void sessionOpened(IoSession session) throws Exception {
                            super.sessionOpened(session);
                            StockholmLogger.d(TAG, "session opened.");
                        }

                        @Override
                        public void sessionClosed(IoSession session) throws Exception {
                            super.sessionClosed(session);
                            StockholmLogger.d(TAG, "session closed.");
                        }

                        @Override
                        public void messageReceived(IoSession session, Object message) throws Exception {
                            StockholmLogger.d(TAG, "receive:" + message);
                            ioSession = session;
                            handler.obtainMessage(MSG_RECEIVE_MESSAGE, message.toString()).sendToTarget();
                        }
                    }).builder();
                acceptorManager = new AcceptorManager(acceptorConfig);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void sendMessage(WifiMessage message) {
        StockholmLogger.d(TAG, "send message:" + message);
        SessionManager.getInstance().writeToServer(message.toString());
    }

    public IoSession getIoSession() {
        return ioSession;
    }

    public void release() {
        StockholmLogger.d(TAG, "release");
        if (acceptorManager != null) {
            acceptorManager.disConnect();
        }
        SessionManager.getInstance().closeSession();
        disableAP();
    }

    public void disconnectCurrentNetwork() {
        apManager.disconnectCurrentNetwork();
    }

    public interface WiFiListener {
        void onConnect(long sessionId);
        void onMessageReceive(String message);
    }
}
