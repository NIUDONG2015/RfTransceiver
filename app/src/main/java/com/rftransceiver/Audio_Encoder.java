package com.rftransceiver;

import com.audio.Speex;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

//PCM���ݱ�����
//һ�������޸ı���
public class Audio_Encoder implements Runnable
{
	   private static Audio_Encoder encoder;  //������ʵ��
	    private boolean isEncoding = false; 	  
	   
	    private List<AudioData> dataList = null;// �������
	   private Speex coder;
	    public static Audio_Encoder getInstance() 
	    {  
	        if (encoder == null)
	        {  
	            encoder = new Audio_Encoder();  
	        }  
	        return encoder;  
	    }  
	  
	    private Audio_Encoder() //���캯��
	    {  
	    	//���߳���ͬ������
	        dataList = Collections.synchronizedList(new LinkedList<AudioData>());  
	    }  
	  
	    public void addData(short[] data, int size) //�������
	    {
	        AudioData rawData = new AudioData();
	        rawData.setSize(size);  
	        short[] tempData = new short[size];  
	        System.arraycopy(data, 0, tempData, 0, size);  
	        rawData.setRealData(tempData);  
	        dataList.add(rawData);  
	    }  
	  
	    // ��ʼ����  
	    public void startEncoding()
	    {  
	        if (isEncoding)
	        {  	          
	            return;  
	        }  
	        new Thread(this).start();  
	    }  
	  
	    // ����  
	    public void stopEncoding() 
	    {  
	        this.isEncoding = false;  
	    }  
	  
	    public void run() 
	    {  
	        // ���������Ͷ�  
	        AudioSender sender = new AudioSender();
	        sender.startSending();  
	  
	        int encodeSize = 0;  
	        byte[] encodedData;
	  
	        // ��ʼ��������  
	        try 
	        {
	        	coder=new Speex();
	        	coder.init();	         
	       
			} 
	        catch (Exception e) 
	        {
				 isEncoding=false;
				 return;
			}

	        isEncoding = true;  
	        while (isEncoding)
	        {  
	        	//�������ݴ���
	            if (dataList.size() == 0)
	            {  
	                try 
	                {  
	                    Thread.sleep(20);  
	                } catch (InterruptedException e) 
	                {  
	                    e.printStackTrace();  
	                }  
	                continue;  
	            }  
	            if (isEncoding)
	            {
	                AudioData rawData = dataList.remove(0);  //ȡ������������
	                encodedData = new byte[rawData.getSize()];  
	                encodeSize=coder.encode(rawData.getRealData(),0,encodedData, rawData.getSize());
	                 
	                if (encodeSize > 0) 
	                {  
	                    sender.addData(encodedData, encodeSize);  
	                    //encodedData = new byte[encodedData.length];
	                }
	            }  
	        }  
	      //ֹͣ����
	        sender.stopSending();  
	    }  
}
