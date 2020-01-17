//package mixertest;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

public class MixerTest {

    public static boolean stopCapture = false;
    public static TargetDataLine microphoneLine;

    public static final AudioFormat FORMAT = new AudioFormat(44100, 16, 2, true, true);
    public static void main_(String[] args) throws UnsupportedEncodingException {
        DataLine.Info lineInfo = new DataLine.Info(TargetDataLine.class, MixerTest.FORMAT);
        Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
        System.out.println("----------------Mixers Available----------------");
        for (int i = 0; i < mixerInfos.length; i++) {
            System.out.println(new String(mixerInfos[i].toString().getBytes("Windows-1252"), "Windows-1251"));
        }
        System.out.println("----------------------Mixers Supporting Line----------------------");
        for (int i = 0; i < mixerInfos.length; i++) {
            Mixer m = AudioSystem.getMixer(mixerInfos[i]);
            if (m.isLineSupported(lineInfo)) {
                System.out.println(i + " " + new String(mixerInfos[i].toString().getBytes("Windows-1252"), "Windows-1251")); //Узнаём списки портов на которые можно посылать звук
            }
        }
        try {
            Mixer mixerS = AudioSystem.getMixer(mixerInfos[4]); //Здесь пишем номер устройства на которое будем посылать звук
            microphoneLine = (TargetDataLine) mixerS.getLine(lineInfo);
            microphoneLine.open(MixerTest.FORMAT);
            microphoneLine.start();
            Thread captureThread = new Thread(new CaptureThread());
            captureThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    static class CaptureThread extends Thread{
        public  void run(){
            OutputStream out = new ByteArrayOutputStream();
            byte buffer[] = new byte[8192];
            try{
                while(!stopCapture){
                    int cnt = microphoneLine.read(buffer, 0, buffer.length);
                    if(cnt>0){
                        for(int i=0;i<cnt;){
                            int firstSample = (buffer[i]) << 8 | buffer[i+1];
                            int secondSample = (buffer[i+2]) << 8 | buffer[i+3];
                            System.out.println(firstSample + " - " + secondSample);
                            i = i+4;
                        }
                    }
                }
            }catch(Exception e){
                System.out.println(e);
                System.exit(0);
            }
        }
    }
}
