//package info.privateblog;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class TestSound extends Thread    {
    private TargetDataLine        m_line;
    private AudioFileFormat.Type    m_targetType;
    private AudioInputStream    m_audioInputStream;
    private File            m_outputFile;

    public TestSound(TargetDataLine m_line, Type m_targetType,  File m_outputFile) {
        this.m_line = m_line;
        this.m_targetType = m_targetType;
        this.m_audioInputStream = new AudioInputStream(m_line);
        this.m_outputFile = m_outputFile;
    }

    public void start() {
        m_line.start();
        super.start();
    }

    public void stopRecording() {
        m_line.stop();
        m_line.close();
    }


    public void run() {
        try {
            AudioSystem.write(
                    m_audioInputStream,
                    m_targetType,
                    m_outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String args[]) {
        File outputFile = new File("c:\\Users\\yboikov\\audiorec.wav");

        AudioFormat    audioFormat = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                44100.0F, 16, 2, 4, 44100.0F, false);

        DataLine.Info    info = new DataLine.Info(TargetDataLine.class, audioFormat);
        TargetDataLine    targetDataLine = null;
        try {
            targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
            targetDataLine.open(audioFormat);
        } catch (LineUnavailableException e) {
            System.out.println("unable to get a recording line");
            e.printStackTrace();
            System.exit(1);
        }

        AudioFileFormat.Type    targetType = AudioFileFormat.Type.WAVE;

        TestSound j = new TestSound(targetDataLine, targetType, outputFile);

        j.start();

        System.out.println("Recording started.");

        try {
            Thread.currentThread().sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        j.stopRecording();

        System.out.println("Recording stopped.");
    }
}