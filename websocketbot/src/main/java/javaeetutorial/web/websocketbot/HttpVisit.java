package javaeetutorial.web.websocketbot;

import java.io.BufferedReader;  
import java.io.DataOutputStream;  
import java.io.File;  
import java.io.FileInputStream;  
import java.io.IOException;  
import java.io.InputStreamReader;  
import java.io.OutputStream;  
import java.io.PrintWriter;  
import java.net.HttpURLConnection;  
import java.net.URL;  
import javax.enterprise.inject.Model;
import javax.inject.Named;
  
  
//@Model
public class HttpVisit {  
  
    String multipart_form_data = "multipart/form-data";     
    String twoHyphens = "----------------------------";     
    String boundary = "7dd1721b6034a";    // 数据分隔符      
    String lineEnd = System.getProperty("line.separator");    // The value is "\r\n" in Windows.      
      
      
    private HttpURLConnection getConnection(String url ,String method)   
            throws IOException{  
        URL u=new URL(url);  
        HttpURLConnection conn=(HttpURLConnection) u.openConnection();  
        conn.setRequestMethod(method);    
        conn.setDoInput(true);    
        conn.setDoOutput(true);    
        return conn;    
    }  
      
    private String httpVisit(String url, String method, String content){  
          
        try {  
            HttpURLConnection conn=getConnection(url, method);  
            if("POST".equals(method) && content != null ){  
                PrintWriter out = new PrintWriter(conn.getOutputStream());  
                out.print(content);  
                out.flush();  
                out.close();  
            }  
            else if("GET".equals(method)){  
                conn.connect();  
            }  
              
              
            BufferedReader in = new BufferedReader(  
                    new InputStreamReader(conn.getInputStream(), "UTF-8"));  
            String line,result="";  
            while ((line = in.readLine())!= null)  
            {  
                result += "\n" + line;  
            }  
            return result;  
        } catch (IOException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
//            LogUtil.logWarn(getClass(), "httpVisit",e.toString() + "URL: " + url);  
        }catch (Exception e) {  
            // TODO: handle exception  
            e.printStackTrace();  
//            LogUtil.logWarn(getClass(), "httpVisit",e.toString() + "URL: " + url);  
        }  
        return null;  
    }  
    public String httpGet(String url){  
        return httpVisit(url, "GET", null);  
    }  
      
    public String httpPost(String url, String content){  
        return httpVisit(url, "POST", content);  
    }  
      
    public String httpUpload(String url, String f){  
        try {  
            File file = new File(f);  
            HttpURLConnection conn=getConnection(url, "POST");  
            conn.setRequestProperty("Connection", "Keep-Alive");  
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; Trident/6.0)");  
            conn.setRequestProperty("Accept-Encoding", "gzip, deflate");  
            conn.setRequestProperty("Cache-Control", "no-cache");  
            //conn.setRequestProperty("Content-Length", 188 + file.length() + "");  
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary="+ twoHyphens + boundary +"");  
            //conn.setUseCaches(false);  
              
            StringBuilder split = new StringBuilder();     
            split.append(twoHyphens + boundary + lineEnd);     
            split.append("Content-Disposition: form-data; name=\"media\"; filename=\"" + file.getName()+ "\"" + lineEnd);     
            split.append("Content-Type: image/jpeg" + lineEnd);     
            split.append(lineEnd);     
              
            OutputStream out=new DataOutputStream(conn.getOutputStream());  
            FileInputStream fs=new FileInputStream(file);  
            byte[] bytes=new byte[1024*1024];  
            int len=-1;  
            out.write(split.toString().getBytes());  
            while((len=fs.read(bytes)) != -1){  
                out.write(bytes, 0, len);  
            }  
            out.write(lineEnd.getBytes());  
              
            split = new StringBuilder();   
            split.append(twoHyphens + boundary + "--" +lineEnd);   
//          split.append("Content-Disposition: form-data; name=\" wxupload\"" + lineEnd);     
//          split.append(lineEnd);     
//          split.append("submit" + lineEnd);  
//          split.append(twoHyphens + boundary + lineEnd);   
            split.append(lineEnd);    
              
            out.write(split.toString().getBytes());  
              
            fs.close();  
            out.flush();  
            out.close();  
              
            BufferedReader in = new BufferedReader(  
                    new InputStreamReader(conn.getInputStream()));  
            String line,result="";  
            while ((line = in.readLine())!= null)  
            {  
                result += "\n" + line;  
            }  
            return result;  
  
        } catch (IOException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
//            LogUtil.logWarn(getClass(), "httpUpload",e.getMessage());  
        }  
        return null;  
    }  
}  