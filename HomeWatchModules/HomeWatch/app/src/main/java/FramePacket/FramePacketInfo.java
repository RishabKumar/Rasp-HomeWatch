package FramePacket;

import java.io.Serializable;

/**
 * Created by Rishabh on 16-12-2015.
 */
public class FramePacketInfo implements Serializable {

    private byte[] bytes;

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public FramePacketInfo(byte[] bytes) {
        this.bytes = bytes;
    }

}
