package org.shelajev.webframeworks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import spark.Spark;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
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
      if(d.getName().indexOf(".mp3")+d.getName().indexOf(".ogg")>-2)
        rc.add(d);

    List<LocalFile> lf=new ArrayList<>();
    for(File f:rc)
      lf.add(new LocalFile(f,computer,user));

    return lf;
  }

  public static void main(String[] args) throws IOException {
    Spark.port(SERVER_PORT);
    //System.console().writer().printf("Analyse des répertoires");

    String computer=Main.getLocalAddress();
    if(args.length>2 && args[2].equals("extern"))computer=Main.getMyAddress();
    List<LocalFile> lf=scanFiles(args[1],args[0],computer+":"+SERVER_PORT);

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