package org.shelajev.webframeworks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bitlet.weupnp.GatewayDevice;
import org.bitlet.weupnp.GatewayDiscover;
import org.bitlet.weupnp.PortMappingEntry;
import org.xml.sax.SAXException;
import spark.Spark;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * Created by shelajev on 17/02/16.
 */
//test :http://localhost:9090/mail={
public class SparkApplication {
  private static final int SERVER_PORT = 4444;
  private static final String DOMAIN = "https://shifumixweb.appspot.com";
  //private static final String DOMAIN = "http://localhost:8080";
  private static final String DIRECTORY = "/home/pi/workspace/Mails";
  public static final String API_ROOT = "/_ah/api/shifumix/v1";
  private static final int WAIT_TIME = 3;

  private static Logger logger = Logger.getLogger(String.valueOf(SparkApplication.class));

  public static List<File> listFilesForFolder(final File folder) {
    List<File> paths = new ArrayList<>();
    for (final File fileEntry : folder.listFiles()) {
      if (fileEntry.isDirectory()) {
        listFilesForFolder(fileEntry);
      } else {
        paths.add(fileEntry);
      }
    }
    return paths;
  }

  public static byte[] getFile(String name) throws IOException {
    return Files.readAllBytes(Paths.get(DIRECTORY+name));
  }

  public static List<LocalFile> scanFiles(String root,String user,String computer){
    ArrayList<File> directory=new ArrayList<>();
    Main.list_directory(root,directory);

    List<File> rc = new ArrayList<>();
    for(File d:directory)
      if(d.getName().endsWith(".mp3") || d.getName().endsWith(".ogg"))
        rc.add(d);

    List<LocalFile> lf=new ArrayList<>();
    for(File f:rc)
      lf.add(new LocalFile(f,computer,user));

    return lf;
  }


  //https://api.discogs.com/database/search?artist=Nirvana&release_title=Smell+like+teen+spirit&key=kOwTlKAydAoLEnuMuXcN&secret=dFbewQxZmfOZNPSvqRUMeKRivQXpwQYH
  public void getCovert(List<LocalFile> lf) throws IOException {
    List<LocalFile> rc=new ArrayList<>();
    for(LocalFile f:lf){
      String query=f.getTitle()+"+"+f.getArtist();
      String key="kOwTlKAydAoLEnuMuXcN";
      String secret="dFbewQxZmfOZNPSvqRUMeKRivQXpwQYH";
      for(int i=0;i<20;i++)query=query.replace(" ","+");
      String result=Main.get("https://api.discogs.com/database/search?title="+query+"&key="+key+"&secret="+secret);
      JsonObject obj=new Gson().fromJson(result,JsonObject.class);
      f.setPicture(obj.getAsJsonArray("results").get(0).getAsJsonObject().get("thumb").getAsString());
    }
  }

  public static Boolean openPort(int port,InetAddress localAddr) throws ParserConfigurationException, SAXException, IOException, InterruptedException {
    GatewayDiscover discover = new GatewayDiscover();
    discover.discover();
    GatewayDevice d = discover.getValidGateway();
    if (null != d) {
      PortMappingEntry portMapping = new PortMappingEntry();
      if (!d.getSpecificPortMappingEntry(port,"TCP",portMapping)) {
        logger.info("Port was already mapped. Aborting test.");
        return true;
      } else {
        logger.info("Sending port mapping request");
        if (!d.addPortMapping(port, port ,localAddr.getHostAddress(),"TCP","test")) {
          logger.info("Port mapping attempt failed");
          logger.info("Test FAILED");
        } else {
          Thread.sleep(1000*WAIT_TIME);
          d.deletePortMapping(port ,"TCP");

          logger.info("Port mapping removed");
          logger.info("Test SUCCESSFUL");
        }
      }
    } else {
      logger.info("No valid gateway device found.");
    }
    return false;
  }


  public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException, InterruptedException {
    logger.info("Starting weupnp");

    InetAddress serverAddr=Main.getLocalAddress();
    if(args.length>2 && args[2].equals("extern") && openPort(SERVER_PORT,serverAddr))serverAddr=Main.getMyAddress();

    Spark.port(SERVER_PORT);

    List<LocalFile> lf=scanFiles(args[1],args[0],serverAddr+":"+SERVER_PORT);

    final GsonBuilder builder = new GsonBuilder();
    final Gson gson = builder.create();

    String body=gson.toJson(new localFiles(lf));
    String url=DOMAIN + API_ROOT + "/uploadfiles?user="+args[0];
    Main.post(url,body);

    //Recherche de la musique

    Spark.post("/setPath", "application/json", (request, response) -> {
      String s = "";
      return s;
    });

    //http://localhost:4444/getfile/D:_Users_U016272_Music_AAA_005. Ronan Keating - Let Me Love You.mp3
    Spark.get("/getfile/:path", (request, response) -> {
      String path=request.params(":path");
      for(int i=0;i<10;i++)path=path.replace("_","/");
      Path p= FileSystems.getDefault().getPath(path);
      byte[] a=null;
      try{
        a=Files.readAllBytes(p);
      } catch (Exception e){
        return null;
      }
      response.raw();
      response.type("audio/mpeg");
      return a;
    });
  }
}