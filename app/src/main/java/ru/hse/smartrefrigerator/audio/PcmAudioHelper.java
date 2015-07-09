package ru.hse.smartrefrigerator.audio;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by michaelborisov on 09.07.15.
 */
public class PcmAudioHelper {

    /**
     * Converts a pcm encoded raw audio stream to a wav file.
     *
     * @param af
     * @param rawSource
     * @param wavTarget
     * @throws IOException
     */
    public static void convertRawToWav(WavAudioFormat af, File rawSource, File wavTarget) throws IOException {
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(wavTarget));
        dos.write(new RiffHeaderData(af, 0).asByteArray());
        DataInputStream dis = new DataInputStream(new FileInputStream(rawSource));
        byte[] buffer = new byte[4096];
        int i;
        int total = 0;
        while ((i = dis.read(buffer)) != -1) {
            total += i;
            dos.write(buffer, 0, i);
        }
        dos.close();
        modifyRiffSizeData(wavTarget, total);
    }

    /**
     * Modifies the size information in a wav file header.
     *
     * @param wavFile a wav file
     * @param size    size to replace the header.
     * @throws IOException if an error occurs whule accesing the data.
     */
    static void modifyRiffSizeData(File wavFile, int size) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(wavFile, "rw");
        raf.seek(RiffHeaderData.RIFF_CHUNK_SIZE_INDEX);
        raf.write(Bytes.toByteArray(size + 36, false));
        raf.seek(RiffHeaderData.RIFF_SUBCHUNK2_SIZE_INDEX);
        raf.write(Bytes.toByteArray(size, false));
        raf.close();
    }




}
