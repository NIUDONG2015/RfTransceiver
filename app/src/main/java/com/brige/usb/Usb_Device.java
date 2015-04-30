package com.brige.usb;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Handler;

import java.util.HashMap;
import java.util.Iterator;

public class Usb_Device //usb�豸��
{
	UsbManager usb_manager;               //usb����������
	UsbDevice stm32_device;				     //stm32�豸����
	UsbInterface usb_interface;					 //usb�ӿڶ���
	UsbDeviceConnection connection;		 //usb�豸���Ӷ���
	UsbEndpoint ep_out,ep_in;				 //���롢��� �˵� ����
    Usb_Thread usb_thread;             //usb��̨��Ϣ�߳�
	Handler usb_handle;                 //usb��Ϣhandler
	public boolean usb_state;                  //usb������״̬
	public boolean usb_permisson;              //����Ȩ��
	 
	
	
   public void  Usb_GetPermisson(Activity activity)  //��ȡusbȨ��
	{
	    //ע��㲥
		final PendingIntent mPermissionIntent = PendingIntent.getBroadcast(activity, 0, new Intent(ACTION_USB_PERMISSION), 0);
		IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
		activity.registerReceiver(mUsbReceiver, filter);
		usb_manager = (UsbManager)activity.getSystemService(Context.USB_SERVICE); //��ȡUSB����
		if (usb_manager == null)
		{
			usb_permisson=false;
			return ;
		}
		//�������е�usb�豸
				HashMap<String,UsbDevice> deviceList = usb_manager.getDeviceList();
				Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
				while (deviceIterator.hasNext()) 
				{
					UsbDevice device = deviceIterator.next();						
						stm32_device = device;			
				}	
				if(stm32_device==null)	
				{   
					usb_permisson=false;
					return ;//�Ҳ����豸
				}
		if(!usb_manager.hasPermission(stm32_device))//�ж��Ƿ���Ȩ�� ʹ��usb�豸
		{ 		
		     //û��Ȩ����ѯ���û��Ƿ�����Ȩ��			
		   usb_manager.requestPermission(stm32_device, mPermissionIntent); //�ô���ִ�к�ϵͳ����һ���Ի���  
		                                                  //ѯ���û��Ƿ�����������USB�豸��Ȩ��  
		} 
		else
		{
		   usb_permisson=true;
		   return;
		}
		
	}
	
	public boolean Usb_Connect( )// ����USB����
	{
			
					
		connection = usb_manager.openDevice(stm32_device);//��stm32��������    
		if(connection==null)
			return false;//����ʧ��
		if (stm32_device.getInterfaceCount() != 1) 	
			return false;
		usb_interface=stm32_device.getInterface(0); //��ȡ�ӿ�
		connection.claimInterface(usb_interface, true);//��ռ�ӿ�
		int cnt = usb_interface.getEndpointCount();  //��ȡ���ö˵����Ŀ
		if(cnt<1)
			return false;
		for (int index = 0; index < cnt; index++)  //�����˵� �ҵ�����˵������˵�
		{
			  UsbEndpoint ep = usb_interface.getEndpoint(index); //��ȡindex��ŵĶ˵�			 
			  if ((ep.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) && (ep.getDirection() == UsbConstants.USB_DIR_OUT))
				  ep_out = ep;    //�������ݶ˵�			  
			  
			  if ((ep.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) && (ep.getDirection() == UsbConstants.USB_DIR_IN)) 			  
				  ep_in = ep;    //�������ݶ˵�					
		}		
		usb_thread = new Usb_Thread(connection,ep_in,usb_handle);  //���� usb���ݽ����߳�
		usb_thread.start();
		usb_state= true;
		
		return true; //���ӳɹ�
	}
	
	boolean Usb_Disconnect() //usb�Ͽ�����
	{		
		if (usb_state) 
		{
		 connection.releaseInterface(usb_interface);   //�ͷŽӿ�
	     usb_thread.interrupt();		   //���� �߳�	
	     return true;
		}
		else
		return false;	
	}
	
	public boolean Usb_Transfer(byte buffer[],int length) //�������� ʹ��ʱ���ں�̨�߳�
	{
		 
		int cnt=connection.bulkTransfer(ep_out, buffer, length, 5000); //����˵� ������ ���峤�� ʱ��
		if(cnt>=0)
		return true;
		else 
		return false;		
	}
	
	
	boolean Usb_State()//��ȡUSB��״̬
	{
		return usb_state;
	}
	
	
	
	
	
	//�㲥��Ȩ
	private static final String ACTION_USB_PERMISSION =
		    "com.android.example.USB_PERMISSION";
	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver()
	{
		    //usb �豸���� ��Ȩ
		    public void onReceive(Context context, Intent intent)
		    {
		        String action = intent.getAction();
		        if (ACTION_USB_PERMISSION.equals(action)) 
		        {
		            synchronized (this) 
		            {
		            	UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
		                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false))
		                {
		                    if(device != null)	      
		                    {
		                       stm32_device = device;  
		                       usb_permisson=true;
		                    }
		                else
		                   { 
		                	  usb_permisson=false;
		                	  return;
		                   }
		               
		                }          
		                
		            }
		        }
		    }
	 };
	
   
}
