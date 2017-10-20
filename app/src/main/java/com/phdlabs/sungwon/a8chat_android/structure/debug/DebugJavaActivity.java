package com.phdlabs.sungwon.a8chat_android.structure.debug;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.phdlabs.sungwon.a8chat_android.structure.core.CoreActivity;

import java.net.URISyntaxException;

/**
 * Created by SungWon on 10/19/2017.
 */

public class DebugJavaActivity extends CoreActivity{

    private Socket mSocket;

    {
        try{
            mSocket = IO.socket("http://chat.socket.io");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected int layoutId() {
        return 0;
    }

    @Override
    protected int contentContainerId() {
        return 0;
    }
}
