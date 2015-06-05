package javaeetutorial.web.websocketbot;


import java.io.BufferedReader;  
import java.io.File;  
import java.io.FileInputStream;
import java.io.FileWriter;  
import java.io.IOException;  
import java.io.InputStreamReader;
import java.util.ArrayList;  
import java.util.Map;  
import java.util.Set;  
import java.util.regex.Matcher;  
import java.util.regex.Pattern;  
import org.json.JSONArray;  
import org.json.JSONException;  
import org.json.JSONObject;  

//@Model
public class CityJSON {  
//@Inject 
//private CityCode cityutil;
//  
    private String path;  
    public CityJSON(String path){  
        this.path=path;         
    }  
      
    public void getCityCode(){  
        File file=new File(path);  
          
        if("".equals(file.getAbsolutePath())) return ;  
          
        JSONObject newCities=new JSONObject();  
        CityCode cityutil=new CityCode();  
        try {  
            //get Province list  
            Map<String,String> pro=cityutil.getProvinceList();  
            Set<String> pro_keys=pro.keySet();  
  
            ArrayList<JSONObject> proList=new ArrayList<JSONObject>();  
            for(String key : pro_keys){  
                System.out.println(pro.get(key));  
                //create province JSON  
                JSONObject proJson=new JSONObject();  
                //get the city's distracts  
                Map<String,String> city=cityutil.getCityZone(key);  
                Set<String> city_keys=city.keySet();  
  
                ArrayList<JSONObject> cityList=new ArrayList<JSONObject>();  
                for(String city_key : city_keys){  
                    System.out.println("   " +city.get(city_key));  
                    JSONObject cityJson=new JSONObject();  
                    Map<String,String> distract=cityutil.getCityZone(city_key);  
                    Set<String> distract_keys=distract.keySet();  
  
                    ArrayList<JSONObject> distractList=new ArrayList<JSONObject>();  
                    for(String distract_key : distract_keys){  
                        System.out.println("      " + distract.get(distract_key));  
                        JSONObject distractJson=new JSONObject();  
                        Map<String,String> zone=cityutil.getCityZone(distract_key);  
                        distractJson.put("name", distract.get(distract_key));  
                        distractJson.put("id", distract_key);  
                        distractJson.put("code", zone.get(distract_key));  
                        distractList.add(distractJson);  
                    }  
                    cityJson.put("name", city.get(city_key));  
                    cityJson.put("id", city_key);  
                    cityJson.put("zone", distractList.toArray());  
                    cityList.add(cityJson);  
                }  
                proJson.put("name", pro.get(key));  
                proJson.put("id", key);  
                proJson.put("zone", cityList.toArray());  
                proList.add(proJson);  
            }  
            newCities.put("name", "china");  
  
            newCities.put("zone", proList.toArray());  
              
            System.out.println(newCities.toString());  
            writeToFile(newCities.toString(),file);  
        } catch (JSONException  e) { 
        	System.out.println("JSONException111111"+"是在这出错的吗"+e.toString());
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        } catch (IOException e2) {  
            // TODO: handle exception  
            e2.printStackTrace();  
        }  
    }  
  
    private void writeToFile(String string, File file) throws IOException {  
        // TODO Auto-generated method stub  
        FileWriter fw=new FileWriter(file);  
        fw.write(string);  
        fw.close();  
    }  
      
    public String getCode(String city){  
        File file=new File(path);  
        Pattern p=Pattern.compile(city);
        try {          	
//            FileReader fr=new FileReader(file);  
//            BufferedReader br=new BufferedReader(fr);  
        	BufferedReader br = new BufferedReader(new InputStreamReader(
        	                      new FileInputStream(file), "UTF8"));
            StringBuilder sb=new StringBuilder();  
            String line=null;  
            while((line=br.readLine()) != null){  
                sb.append(line);  
               
            }  
//            System.out.println("sb="+sb);
            br.close();                
            JSONObject cityJson=new JSONObject(sb.toString());  
            JSONObject backJson=new JSONObject();  
            ArrayList<String> similarList=new ArrayList<String>();  
            //china zone array  
            JSONArray zones1=cityJson.getJSONArray("zone");             
            for(int i=0; i<zones1.length(); i++){  
                JSONObject obj2=zones1.getJSONObject(i);  
                //province zone array  
                JSONArray zones2=obj2.getJSONArray("zone");  
                ArrayList<String> list=new ArrayList<String>();  
                for(int j=0; j<zones2.length(); j++){  
                    JSONObject obj3=zones2.getJSONObject(j);                     
                    //distract zone array  
                    JSONArray zones3=obj3.getJSONArray("zone");  
                    for(int k=0; k<zones3.length(); k++){  
                        //concrete information  
                        JSONObject zone=zones3.getJSONObject(k);  
                        String name=zone.getString("name");                            
                        Matcher m=p.matcher(name);  
                        if(m.matches()) {  
                            backJson.put("name", zone.getString("name"));  
                            backJson.put("code", zone.getString("code"));  
                            backJson.put("status", "0");  
                            backJson.put("msg", "ok");  
                            return backJson.toString();  
                        }  
                        if(m.find()) similarList.add(zone.getString("name"));  
                    }  
                    list.add(obj3.getString("name"));  
                      
                    String name=obj3.getString("name");  
                    Matcher m=p.matcher(name);  
                    if(m.find()) similarList.add(obj3.getString("name"));  
                }  
                String name=obj2.getString("name");  
                Matcher m=p.matcher(name);  
                if(m.matches()) {  
                    backJson.put("name", city);  
                    backJson.put("code", "0");  
                    backJson.put("status", "1");  
                    backJson.put("msg", list.toString());  
                    
                    return backJson.toString();  
                }  
                  
                if(m.find()) similarList.add(obj2.getString("name"));  
            }  
              
            backJson.put("name", city);  
            backJson.put("code", "0");  
            backJson.put("status", "-1");  
            backJson.put("msg", similarList.toString());  
            return backJson.toString();  
              
        } catch (IOException  e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        } catch ( JSONException e) {  
        	
            // TODO: handle exception  
            e.printStackTrace();  
        }  
        
        return "{\"name\" : \" "+city+"\",\"code\" : \"0\",\"status\" : \"404\",\"msg\" : \"exveption\"}";
//          return "\"0\"";
    }  
} 