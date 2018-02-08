package com.atgc.hd.entity;

import android.nfc.tech.MifareClassic;

import com.orhanobut.logger.Logger;

import java.io.IOException;

/**
 * <p>描述：Nfc卡块区数据
 * <p>作者：liangguokui 2018/2/5
 */
public class NfcBlock {
    private int sectorIndex;
    private int blockIndex;
    private byte[] blockBytes;

    public NfcBlock(int sectorIndex, int blockIndex) {
        this.sectorIndex = sectorIndex;
        this.blockIndex = blockIndex;
    }

    public void initBlockData(MifareClassic mfc) {
        try {
            byte[] originData = mfc.readBlock(sectorIndex * 4 + blockIndex);

            blockBytes = new byte[originData.length];

            System.arraycopy(originData, 0, blockBytes, 0, blockBytes.length);
        } catch (IOException e) {
            Logger.e(e, "初始化-块-区数据异常");
        }
    }

    public byte[] getBlockBytes() {
        return blockBytes;
    }
}
