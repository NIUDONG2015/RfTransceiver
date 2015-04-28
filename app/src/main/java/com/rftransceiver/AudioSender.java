package com.rftransceiver;


import android.util.Log;

import com.datasets.MyDataQueue;


//���ͱ���������
//��ȫ��ʼ��ͨ�� �Լ����ʹ���
public class AudioSender  implements Runnable
{
	private boolean isSendering = false;  
	private MyDataQueue dataQueue;
    public static int num = 1;

    public static TestSoundWithBluetooth sendListener = null;

    public static volatile boolean end = false;
	
	 public AudioSender() 
	 {
         //��ʼ������ͨ��
         dataQueue = MyDataQueue.getInstance(MyDataQueue.DataType.Sound_Decoder);
	 }
	
	public void startSending() 
	{
		 new Thread(this).start(); 
	
	}

	//��������������ͻ�����
	public void addData(byte[] data, int size) 
	{
		    AudioData encodedData = new AudioData();
	        encodedData.setSize(size);  
            encodedData.setencodeData(data);
	        dataQueue.add(encodedData);  //����ѱ�������
	}

	
	//ֹͣ����
	public void stopSending() 
	{
		this.isSendering = false;  
	}
	
	 //�̲߳���
	public void run() 
	 {
            StringBuilder sb = new StringBuilder();
            byte[] temp = new byte[64];
	        this.isSendering = true;
            int index = 0; //63�ֽڼ�����
            int sum = 0;   //��������
	        while (isSendering)
	        {
                AudioData sendData = (AudioData)dataQueue.get();
                if(sendData == null) {
                    //�˴����ԼӸ���ʱ������ȡ������Ϊ�յĴ���
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }else {
                    for(int i = 0;i<sendData.getSize();i++) {
                        temp[index++] = sendData.getencodeData()[i];
                        if(index == 64) {
                            //temp���������Է���
                            if(sendListener != null) {
                                for(byte b : temp)  {
                                    sb.append(String.format("%#x ",b));
                                }
                                Log.e("send the " + (++sum) + "th pakage is ",sb.toString());
                                sb.delete(0,sb.length());
                                sendListener.sendSound(temp,index);
                            }
                            //���½���������0��׼���´�װ��
                            index = 0;
                        }else {
                            if(dataQueue.getSize() == 0 && end) {
                                for(byte b : temp)  {
                                    sb.append(String.format("%#x ",b));
                                }
                                Log.e("send the " + (++sum) + "th pakage(may be the last) is ",sb.toString());
                                sb.delete(0,sb.length());
                                sum = 0;
                                if(sendListener != null) {
                                    sendListener.sendSound(temp,index);
                                }
                                end = false;
                                isSendering = false;
                            }
                        }
                    }
                }
	        }
	 }

    //���Է��ͽӿ�
    public interface TestSoundWithBluetooth {
        void sendSound(Object data,int size);
    }

}
