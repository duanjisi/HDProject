package com.atgc.hd.entity;

import android.nfc.tech.MifareClassic;
import android.util.SparseArray;

import com.orhanobut.logger.Logger;

import java.io.IOException;

/**
 * <p>描述：Nfc卡数据（16个扇区、64个块区）
 * <p>作者：liangguokui 2018/2/5
 */
public class NfcCard {
    private SparseArray<NfcSector> sectors;
    private int sectorCount;
    private int blockCount;

    public NfcCard() {
        sectors = new SparseArray<>();
    }

    public void initCardData(MifareClassic mfc) {
        sectorCount = mfc.getSectorCount();
        blockCount = mfc.getBlockCount();

        for (int i = 0; i < sectorCount; i++) {
            NfcSector sector = new NfcSector(i);
            sector.initSectorData(mfc);
            sectors.append(i, sector);
        }
    }

    public byte[] getBlockBytes(int sectorIndex, int blockIndex) {
        NfcSector sector = sectors.get(sectorIndex);
        if (sector == null) {
            return null;
        }
        NfcBlock block = sector.getNfcBlock(blockIndex);
        if (block == null) {
            return null;
        }
        return block.getBlockBytes();
    }

    public NfcSector getSector(int index) {
        return sectors.get(index);
    }

    public int getSectorCount() {
        return sectorCount;
    }

    public int getBlockCount() {
        return blockCount;
    }

    public String fastCheck(MifareClassic mfc) {
        NfcCard nfcCard = new NfcCard();
        nfcCard.initCardData(mfc);

        StringBuilder builder = new StringBuilder();
        builder.append("\n扇区总数: ");
        builder.append(mfc.getSectorCount());
        builder.append("\n块区总数: ");
        builder.append(mfc.getBlockCount());
        builder.append("\n卡片大小: ");
        builder.append(mfc.getSize());

        for (int i = 0; i < mfc.getSectorCount(); i++) {
            builder.append(readSector(mfc, i));
        }

        Logger.e(builder.toString());

        return builder.toString();
    }

    private String readSector(MifareClassic mfc, int sectorNum) {
        try {
            if (mfc.authenticateSectorWithKeyA(sectorNum, MifareClassic.KEY_DEFAULT)) {
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < 4; i++) {
                    String cardNo = readBlock(mfc, sectorNum * 4 + i);
                    builder.append("\n扇区" + sectorNum);
                    builder.append(" 块区" + i);
                    builder.append(" ：");
                    builder.append(cardNo);
                }

                return builder.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "\n读取" + sectorNum + "扇区异常";
    }

    private String readBlock(MifareClassic mfc, int blockNum) {
        try {
            byte[] data1 = mfc.readBlock(blockNum);
            byte[] copy1 = new byte[data1.length];
            System.arraycopy(data1, 0, copy1, 0, copy1.length);
            String temp = toHexString(copy1);
            return temp;
        } catch (IOException e) {
//            e.printStackTrace();
        }
        return "读取块区异常";
    }

    public static String toHexString(byte[] byteArray) {
        if (byteArray == null || byteArray.length < 1) {
            return "";
        }
        int size = byteArray.length;
        final StringBuilder hexString = new StringBuilder(2 * size);
        for (int i = 0; i < size; i++) {
            if ((byteArray[i] & 0xff) < 0x10)//
                hexString.append("0");
            hexString.append(Integer.toHexString(0xFF & byteArray[i]));
            if (i != (byteArray.length - 1))
                hexString.append("");
        }
        return hexString.toString().toUpperCase();
    }
}
