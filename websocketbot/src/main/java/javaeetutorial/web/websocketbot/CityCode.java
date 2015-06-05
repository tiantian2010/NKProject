package javaeetutorial.web.websocketbot;

import java.util.ArrayList;  
import java.util.HashMap;  
import java.util.Map;  
import java.util.Set;  


//@Model
public class CityCode {  
//    @Inject
//    private HttpVisit httpvisit;
  
    private final String BASE_URL="http://m.weather.com.cn/data5/city";  
    private final String SUFFIX=".xml";  
      
    private final int threadsSize=10;  
      
    private int[] status=new int[threadsSize];  
    public CityCode(){}  
      
    private synchronized int getStatus(){  
        for(int i=0 ; i<status.length ; i++){  
            if(status[i] == 0) {  
                status[i]=-1;  
                return i;  
            }  
        }  
        return -1;  
    }  
      
    private synchronized void setStatus(int i){  
        if(i > 0 && i < status.length){  
            status[i]=0;  
        }  
    }  
      
    public Map<String,String> getCityZone(String citycode){  
        return getCode(BASE_URL + citycode + SUFFIX);  
    }  
      
    public Map<String,String> getProvinceList(){  
        return getCode(BASE_URL + SUFFIX);  
    }  
      
    public Map<String,String> getCityList(boolean threads){  
        Map<String, String> pro_map=getProvinceList();  
        if(threads) return getCodeMulti(pro_map);  
        Map<String, String> map=new HashMap<String ,String>();  
        Set<String> keys=pro_map.keySet();  
        for(String key : keys){  
            Map<String, String> t_map=getCode(BASE_URL + key + SUFFIX);  
            if(t_map != null) map.putAll(t_map);  
        }  
        return map;  
    }  
      
      
    public Map<String,String> getDistrictList(boolean threads){  
        Map<String,String> city_map=getCityList(threads);  
        if(threads) return getCodeMulti(city_map);  
                  
        Map<String, String> map=new HashMap<String ,String>();  
        Set<String> citys=city_map.keySet();  
        for(String city : citys){  
            Map<String, String> t_map=getCode(BASE_URL + city + SUFFIX);  
            if(t_map != null) map.putAll(t_map);  
        }  
        return map;  
    }  
      
    public Map<String,String> getAreaCodeList(boolean threads){  
        Map<String,String> district_map=getDistrictList(threads);  
        if(threads) return getCodeMulti(district_map);  
          
        Map<String, String> map=new HashMap<String ,String>();  
        Set<String> districts=district_map.keySet();  
        for(String district : districts){  
            Map<String, String> t_map=getCode(BASE_URL + district + SUFFIX);  
            if(t_map != null) map.putAll(t_map);  
        }  
        return map;  
    }  
      
    private Map<String,String> getCode(String url){  
        Map<String, String> map=new HashMap<String ,String>();  
        String content=new HttpVisit().httpGet(url);  
        if(content == null) return map;  
        content = content.replace('\n', '\0').trim();  
        String pro_1[]=content.split(",");  
        for(String tmp : pro_1){  
            String pro_2[]=tmp.split("\\|");  
            if(pro_2.length == 2 && !"".equals(pro_2[0])){  
                map.put(pro_2[0], pro_2[1]);  
            }  
        }  
        return map;  
    }  
      
    private Map<String,String> getCodeMulti(Map<String, String> argMap){  
        Map<String, String> pro_map=argMap;  
        ArrayList<Map<String, String>> list=new ArrayList<Map<String, String>>(threadsSize);  
        for(int i=0; i<threadsSize; i++){  
            list.add(new HashMap<String ,String>());  
        }  
          
        Map<String, String> map=new HashMap<String ,String>();  
        Set<String> keys=pro_map.keySet();  
        for(String key : keys){  
            while(true) {   
                int index=getStatus();  
                if(index > -1) {  
                    new Thread(new HttpThread(BASE_URL + key + SUFFIX, list.get(index), index)).start();  
                    break;  
                }  
                try {  
                    Thread.sleep(1000);  
                } catch (InterruptedException e) {  
                    // TODO Auto-generated catch block  
                    e.printStackTrace();  
//                    LogUtil.logWarn(CityCode.class, "getCityListNulti", e.toString());  
                }  
                  
            }  
              
        }  
          
          
        for(Map<String, String> tmap : list){  
            if(tmap != null) map.putAll(tmap);  
        }  
          
        return map;  
    }  
      
    private class HttpThread implements Runnable{  
  
        private String url;  
        private Map<String, String> map;  
        private int flag;  
        public HttpThread(String url, Map<String, String> map, int flag){  
            this.url=url;  
            this.map=map;  
            this.flag=flag;  
        }  
        @Override  
        public void run() {  
            // TODO Auto-generated method stub  
            map.putAll(getCode(url));  
            setStatus(flag);  
        }  
          
    }  
      
}  