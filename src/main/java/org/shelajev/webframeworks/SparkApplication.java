package org.shelajev.webframeworks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import spark.Spark;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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

  public static List<String> listFilesForFolder(final File folder) {
    List<String> paths = new ArrayList<>();
    for (final File fileEntry : folder.listFiles()) {
      if (fileEntry.isDirectory()) {
        listFilesForFolder(fileEntry);
      } else {
        paths.add(fileEntry.getName());
      }
    }
    return paths;
  }

  public static byte[] getFile(String name) throws IOException {
    return Files.readAllBytes(Paths.get(DIRECTORY+name));
  }

  public static void main(String[] args) throws IOException {
    Spark.port(SERVER_PORT);
    List<String> rc = listFilesForFolder(new File(args[1]));
    String computer=Main.getMyAddress()+":"+SERVER_PORT;

    List<LocalFile> lf=new ArrayList<>();
    for(String s:rc)
      lf.add(new LocalFile(s,computer));

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

    Spark.get("/getfile", (request, response) -> {
      return Files.readAllBytes(request.attribute("id"));
    });
  }
}