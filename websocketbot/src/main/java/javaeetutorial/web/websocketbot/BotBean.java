/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with the terms of the License at:
 * http://java.net/projects/javaeetutorial/pages/BerkeleyLicense
 */
package javaeetutorial.web.websocketbot;

import com.hankcs.hanlp.HanLP;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import static javaeetutorial.web.websocketbot.WeatherUtil.getWeekWeatherMap;
import javax.inject.Named;
import net.sf.json.JSONObject;

@Named
public class BotBean {
    /*Inject CityJSON class*/
//        @Inject
//        private CityJSON cityjson;

    /* Respond to a message from the chat */
    public String respond(String msg) throws IOException {

        String response;
        String location = null;
        String date = null;
        String week = null;
        int d = 0;
        System.out.println("msg:=" + msg);
        if (msg.contains("后天") && !msg.contains("大后天")) {
            date = "后天";
        }
        String nativeStr = HanLP.segment(msg).toString();
        nativeStr = nativeStr.substring(1, nativeStr.length() - 1);
        System.out.println("nativeStr:" + nativeStr);
        String[] str = nativeStr.split(", ");
        for (String s : str) {
            System.out.println("str=" + s);
            if (s.endsWith("/ns")) {
                s = s.substring(0, s.lastIndexOf("/"));
                System.out.println("/ns" + s);
                location = s;
                System.out.println("location:" + location);
            } else if (s.endsWith("/t")) {
                s = s.substring(0, s.lastIndexOf("/"));
                System.out.println("/t" + s);
                date = s;
                System.out.println("date" + date);
            } else if (s.endsWith("/m")) {
                s = s.substring(0, s.lastIndexOf("/"));
                System.out.println("/m" + s);
                week = s;
            }
        }
       //若用户输入没有城市地点，则默认查询为天津的天气
        if (location == null) {
            location = "天津";
        }
        //若用户输入中不包含查询时间
        if (date == null) {
            if (week == null) {
                date = "今天";
            } else {
                if (week.contains("一") || week.contains("1")) {
                    date = "今天";
                } else if (week.contains("二") || week.contains("2")) {
                    date = "明天";
                } else if (week.contains("三") || week.contains("3")) {
                    date = "后天";
                } else if (week.contains("四") || week.contains("4")) {
                    date = "大后天";
                } else if (week.contains("五") || week.contains("5")) {
                    date = "第五天";
                } else if (week.contains("六") || week.contains("6")) {
                    date = "第六天";
                } else {
                    date = "今天";
                }

            }

        }
        System.out.println("date=:" + date);
        /* Remove question marks */
        // msg = msg.toLowerCase().replaceAll("\\?", "");
//        if (msg.contains("西安")) {
        System.out.print("location:"+location);

        String sb = new CityJSON("D:\\city.json").getCode(location);
        JSONObject jsonData = JSONObject.fromObject(sb);
//        Object code = jsonData.get("code");
        String code = jsonData.getString("code");
        

        if (date.equals("今天")) {
            d = 1;
        } else if (date.equals("明天")) {
            d = 2;
        } else if (date.equals("后天")) {
            d = 3;
        } else if (date.equals("大后天")) {
            d = 4;
        } else if (date.equals("第五天")) {
            d = 5;
        } else {
            d = 6;
        }
        if (code.equals("0")) {
            response = "非常抱歉，您输入的地方范围太大，TT无法为您提供确切的天气状况，您能缩小一下范围么";
        } else {
            //测试获取一周天气
            List<Map<String, Object>> listData = getWeekWeatherMap(code);
            Map<String, Object> wMap = listData.get(d - 1);
            System.out.println("wMap....." + wMap.toString());
            System.out.println(wMap.get("city") + "\t" + wMap.get("date_y")
                    + "\t" + wMap.get("week") + "\t" + wMap.get("weather")
                    + "\t" + wMap.get("fx") + "\t" + wMap.get("fl")
                    + "\t" + wMap.get("temp") + "\t" + wMap.get("index_tr")
                    + "\t" + wMap.get("index_uv") + "\t" + wMap.get("index_d"));
            response = wMap.get("city") + "\t" + wMap.get("date_y")
                    + "\t" + wMap.get("week") + "\t" + wMap.get("weather")
                    + "\t" + wMap.get("fx") + "\t" + wMap.get("fl")
                    + "\t" + wMap.get("temp") + "\t" + wMap.get("index_tr")
                    + "\t" + wMap.get("index_uv") + "\t" + wMap.get("index_d");
            if (msg.contains("温度")) {
                response = "您查询的地方温度为：" + wMap.get("temp");
            } else if (msg.contains("雨") || msg.contains("雪")) {
                if(wMap.get("weather").toString().contains("雨")||wMap.get("weather").toString().contains("雪")){
                    response = "是的，您查询的天气为"+wMap.get("weather");
                }else{
                    response = "没有，您查询的天气为" + wMap.get("weather");
                }
                
            } else if (msg.contains("穿")) {
                response = "天气状况为" + wMap.get("weather")+"，温度为"+ wMap.get("temp") + ",适合" + wMap.get("index_d");
            } else if (msg.contains("防晒霜")) {
                response = "今天" + wMap.get("index_dx");
            } else if (msg.contains("运动")) {
                response = "今天" + wMap.get("index_dl");
            }else if (msg.contains("洗车")) {
                response = "今天" + wMap.get("index_dll");
            }

            try {
                Thread.sleep(1200);
            } catch (InterruptedException ex) {
            }

        }

        return response;
    }
}
//注意事项 若无地点 /ns 这默认为天津
//            
//            Map<String, Object> map = getTodayWeather1(code.toString());
//			System.out.println(map.get("city") + "\t" + map.get("temp")
//					+ "\t" + map.get("WD") + "\t" + map.get("WS")
//					+ "\t" + map.get("SD") + "\t" + map.get("time"));
//            response = map.get("city") + "\t温度：" + map.get("temp")
//					+ "\t风向：" + map.get("WD") + "\t风速：" + map.get("WS")
//					+ "\t湿度：" + map.get("SD") + "\t发布时间：" + map.get("time")+"点";