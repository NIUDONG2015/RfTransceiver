package com.rftransceiver.datasets;

public class AudioData //��Ƶ������
{
	int size;  //���ݴ�С
	short[] realData;  //ʵʱ����
	byte[] encodeData;//�ѱ�������
	public void setSize(int size) //���ô�С 
	{
		this.size = size;  		
	}

	public void setRealData(short[] tempData)  //����ʵʱ���ݻ�����
	{
		  this.realData = tempData;  
		
	}
	
	public void setencodeData(byte[] tempData)  //���ñ������ݻ�����
	{
		  this.encodeData = tempData;  
		
	}


	public int getSize()
	{
		return this.size;
	}

	public short[] getRealData() 
	{
		 return realData;  
	}
	

	public byte[] getencodeData() 
	{
		 return encodeData;  
	}

}
