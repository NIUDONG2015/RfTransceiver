package com.brige.usb;

import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.os.Handler;
import android.os.Message;

import com.rftransceiver.datasets.DataPack;

//usb��Ϣ�̺߳�̨��
public class Usb_Thread extends Thread
{
	UsbEndpoint epIn; //usb stm32�豸���ֻ�������˵�
	UsbDeviceConnection connection; //USB�豸����
	private Handler usbHandler;     // handler���������̵߳�����
	
	public Usb_Thread(UsbDeviceConnection connection,UsbEndpoint epIn,Handler msgHandler) 
	{   //���캯�������mmInStream��msgHandler����
		this.epIn = epIn;
		this.connection = connection;
		this.usbHandler = msgHandler;
	}
	
	public void run()
	{
		byte[] InBuffer = new byte[64];           //���� ������,1�δ��� 8���ֽ�    
		int length = InBuffer.length;
		int timeout = 5000;
		while (!Thread.interrupted()) {                             
			//����bulk����
			int cnt = connection.bulkTransfer(epIn, InBuffer, length, timeout);   
			if ( cnt < 0) 
			{						//û�н��յ����ݣ������ѭ��
				continue;
			}	
						    
		    Message msg = new Message();          //����һ����Ϣ,���������
		    msg.what = 0x1234; //�̱߳�ʶ
		    DataPack temPack=new DataPack();
		    temPack.Inbuffer=InBuffer;
		    temPack.length=cnt;
		    msg.obj=temPack;		 
		    usbHandler.sendMessage(msg);          //ͨ��handler������Ϣ
		 
		}
	}
}
