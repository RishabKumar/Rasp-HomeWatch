/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FramePacket;

import java.io.Serializable;

/**
 *
 * @author Rishabh
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
