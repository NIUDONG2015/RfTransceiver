package com.rftransceiver.util;

import com.rftransceiver.activity.MainActivity;
import com.rftransceiver.datasets.ConversationData;

import java.util.ArrayList;

/**
 * ����洢�Ѿ���ʾ�ڽ�����ȴ��Ҫ��������Ӳ������Ϣ����
 * Created by wuyang on 2015/8/6.
 */
public class MessageCacheUtil {
    /**
     * δ������Ϣ���ı�����
     */
    private ArrayList<String> cacheContentList;
    /**
     * δ������Ϣ��ConversationData
     */
    private ArrayList<ConversationData> cacheDataList;
    /**
     * ��������Ϣ���ı�����
     */
    private ArrayList<String> unCheckContentList;
    /**
     * ��������Ϣ��ConversationData
     */
    private ArrayList<ConversationData> unCheckDataList;

    public MessageCacheUtil(){
        cacheContentList=new ArrayList<String>();
        cacheDataList = new ArrayList<ConversationData>();
        unCheckContentList = new ArrayList<String>();
        unCheckDataList = new ArrayList<ConversationData>();
    }

    public ArrayList<String> getCacheContentList(){
        return cacheContentList;
    }

    public ArrayList<ConversationData> getUnCheckDataList(){
        return unCheckDataList;
    }

    public ArrayList<String> getUnCheckContentList(){
        return unCheckContentList;
    }

    public ArrayList<ConversationData> getCacheDataList(){
        return cacheDataList;
    }

    public void addCache(String content,ConversationData data){
        cacheContentList.add(content);
        cacheDataList.add(data);
        unCheckContentList.add(content);
        unCheckDataList.add(data);
    }

    /**
     * �����͵����ݷ���ʧ�ܣ���Ϊδ���͵����ݣ������ݴ�unCheck�б���ɾ��
     */
    public void checkMessage(){
        unCheckContentList.remove(0);
        unCheckDataList.remove(0);
    }
    /**
     * �����͵����ݷ��ͳɹ��������ݴ�cache�б��Լ�unCheck�б���ɾ��
     */
    public void removeCache(ConversationData data){
        cacheContentList.remove(cacheDataList.indexOf(data));
        cacheDataList.remove(data);
        unCheckContentList.remove(unCheckDataList.indexOf(data));
        unCheckDataList.remove(data);
    }

    public int getUnCheckNum(){
        return unCheckContentList.size();
    }

    public int getCacheNum(){
        return cacheContentList.size();
    }
}
