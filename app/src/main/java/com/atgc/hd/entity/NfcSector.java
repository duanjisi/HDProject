package com.atgc.hd.entity;

import android.nfc.tech.MifareClassic;
import android.util.SparseArray;

import com.orhanobut.logger.Logger;

import java.io.IOException;

/**
 * <p>描述：Nfc卡扇区数据（含4个块区数据）
 * <p>作者：liangguokui 2018/2/5
 */
public class NfcSector {
    // 扇区号
    private int sectorIndex;

    private byte[] sectorKeyA;
    private byte[] sectorKeyB;

    private SparseArray<NfcBlock> blocks;

    public NfcSector(int sectorIndex) {
        this.sectorIndex = sectorIndex;
        sectorKeyA = MifareClassic.KEY_DEFAULT;
        sectorKeyB = MifareClassic.KEY_DEFAULT;
        blocks = new SparseArray<>();
    }

    public void initSectorData(MifareClassic mfc) {
        try {
            if (mfc.authenticateSectorWithKeyA(sectorIndex, MifareClassic.KEY_DEFAULT)) {

                for (int i = 0; i < 4; i++) {
                    NfcBlock nfcBlock = new NfcBlock(sectorIndex, i);
                    nfcBlock.initBlockData(mfc);
                    blocks.append(i, nfcBlock);
                }
            }
        } catch (IOException e) {
            Logger.e(e, "初始化-扇-区数据异常");
        }
    }

    public NfcBlock getNfcBlock(int blockIndex) {
        return blocks.get(blockIndex);
    }

    public int getSectorIndex() {
        return sectorIndex;
    }

    public void setSectorIndex(int sectorIndex) {
        this.sectorIndex = sectorIndex;
    }

    public byte[] getSectorKeyA() {
        return sectorKeyA;
    }

    public void setSectorKeyA(byte[] sectorKeyA) {
        this.sectorKeyA = sectorKeyA;
    }

    public byte[] getSectorKeyB() {
        return sectorKeyB;
    }

    public void setSectorKeyB(byte[] sectorKeyB) {
        this.sectorKeyB = sectorKeyB;
    }

}
