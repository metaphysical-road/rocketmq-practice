package com.rocketmq.cloud.youxia.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class JsonObjectUtil {
    public static List<Map<String,String>> parse(Map<String,Object> mapData) {
        List<Map<String, String>> itemList = new CopyOnWriteArrayList<>();
        if (null != mapData && mapData.size() > 0) {
            Iterator<String> iterator = mapData.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                if (key.equals("alerts")) {
                    JSONArray alerts = (JSONArray) mapData.get("alerts");
                    Iterator<Object> jsonObjectIterator = alerts.stream().iterator();
                    while (jsonObjectIterator.hasNext()) {
                        JSONObject item = (JSONObject) jsonObjectIterator.next();
                        Map<String, String> alarmMessageMap = new HashMap<>();
                        parse(item, alarmMessageMap);
                        itemList.add(alarmMessageMap);
                    }
                }
            }
        }
        return itemList;
    }

    private static void parse(JSONObject jsonObject,Map<String,String> alarmMessageMap){
        Iterator<String> childItemIterator=jsonObject.keySet().iterator();
        while (childItemIterator.hasNext()){
            String childItemKey=childItemIterator.next();
            if(jsonObject.get(childItemKey) instanceof JSONObject){
                JSONObject recursion=(JSONObject)jsonObject.get(childItemKey);
                parse(recursion,alarmMessageMap);
            }else {
                String childItemValue=(String) jsonObject.get(childItemKey);
                //System.out.println("childItemKey:"+childItemKey+" childItemValue:"+childItemValue);
                alarmMessageMap.put(childItemKey,childItemValue);
            }
        }
    }
}
