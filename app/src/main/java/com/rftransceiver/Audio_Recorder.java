package com.rftransceiver;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

//¼��PCM������
//��Ҫ����Ȩ�� <uses-permission android:name="android.permission.RECORD_AUDIO" />
//��Ҫ�޸�Audio_Sender��ķ������ݲ���
//һ�������޸ı���
public class Audio_Recorder  implements Runnable
{

    private volatile boolean isRecording = false;   //¼����־
    private AudioRecord audioRecord;    
    
    //¼�Ʋ���ѡ��
    private static final int audioSource = MediaRecorder.AudioSource.MIC;  
    private static final int sampleRate = 8000;   //ȡ����8000hz
    private static final int channelConfig = AudioFormat.CHANNEL_IN_MONO;  //������ 
    private static final int audioFormat = AudioFormat.ENCODING_PCM_16BIT;    //16λ
    private static final int BUFFER_FRAME_SIZE =160;  
    private int audioBufSize = 0;  
    
    private short[] samples;// ������  
    private int bufferRead = 0;// ��recorder�ж�ȡ��samples�Ĵ�С  
  
    private int bufferSize = 0;// samples�Ĵ�С  
    
    
    
  
    public void startRecording()
    {  
        bufferSize = BUFFER_FRAME_SIZE;  
       //��ȡ��С������
        audioBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig,  
                audioFormat);  
        if (audioBufSize == AudioRecord.ERROR_BAD_VALUE) 
        {  
            //do something
            return;  
        }  
        //��ʼ������������
        samples = new short[audioBufSize];  
        // ��ʼ��recorder  
        if (null == audioRecord)
        {  
            audioRecord = new AudioRecord(audioSource, sampleRate,  
                    channelConfig, audioFormat, audioBufSize);  
        }  
        //�����߳��е�run����
        new Thread(this).start();  
    }  
    
    public void run() 
    {
        //start the encoder
        Audio_Encoder encoder = Audio_Encoder.getInstance();
        encoder.startEncoding();     
        try 
        {
        	  audioRecord.startRecording();  
		}
        catch (IllegalStateException e) 
        {
        	this.isRecording = false; 
        	return;
		}
      
  
        this.isRecording = true;  
        while (isRecording) 
        {  
            bufferRead = audioRecord.read(samples, 0, bufferSize);  
            if (bufferRead > 0) 
            {  
                // add the data to the encoder
                encoder.addData(samples, bufferRead);
            }
            try
            {  
                Thread.sleep(20);  
            } catch (InterruptedException e) 
            {  
                e.printStackTrace();  
            }  
        }  
        audioRecord.stop();
        encoder.stopEncoding();
    }
    
    public void stopRecording() 
    {  
        this.isRecording = false;  
    }  
  
    public boolean isRecording()
    {  
        return isRecording;  
    }  
    
}
