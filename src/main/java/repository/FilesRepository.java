package repository;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.FileInfo;
import model.HttpMultipart;
import model.ServerError;
import util.Constants;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.rmi.ServerException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FilesRepository {
    private ArrayList<FileInfo> fileInfoList;
    private String message;

    public FilesRepository(String path, String method) throws IOException {
        this.connect(Constants.SERVER + "/file?path=" + URLEncoder.encode(path, StandardCharsets.UTF_8), method);
    }

    private void connect(String urlS, String method) throws IOException {
        URL url = new URL(urlS);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);

        if (connection.getResponseCode() == 400 || connection.getResponseCode() == 500) {
            try (InputStreamReader errorSteam = new InputStreamReader(connection.getErrorStream())) {
                ServerError serverError = new Gson().fromJson(errorSteam, ServerError.class);
                throw new ServerException(serverError.getMessage());
            }
        } else {
            try (InputStreamReader reader = new InputStreamReader(connection.getInputStream())) {
                if (method.equals("POST")) {
                    this.message = new Gson().fromJson(reader, String.class);
                } else {
                    this.fileInfoList = new Gson().fromJson(reader, new TypeToken<ArrayList<FileInfo>>() {}.getType());
                }
            }
        }
    }

    public ArrayList<FileInfo> getFileInfoList() {
        return fileInfoList;
    }


    public static boolean uploadFile(File file, String serverCurPath, String charset) {
        try {
            // Set header
            Map<String, String> headers = new HashMap<>();
            //headers.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36");
            HttpMultipart multipart = new HttpMultipart("http://localhost:8080/FileUploader/file", "utf-8", headers);
            // Add form field
            multipart.addFormField(serverCurPath, "path");
            multipart.addFormField(charset, "charset");
            // Add file
            multipart.addFilePart("img", file);
            // Print result
            multipart.finish();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void downloadFile(String path, String name, String fileOutPath) throws IOException {
        URL url = new URL(Constants.SERVER + "/file?path=" + URLEncoder.encode(path, StandardCharsets.UTF_8) + "&name=" + URLEncoder.encode(name, StandardCharsets.UTF_8));
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        int responseCode = urlConn.getResponseCode();
        if (responseCode == 500){
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getErrorStream()))){
                throw new IOException("Server returned non-OK status: " + responseCode + " " + reader.readLine());
            }
        }
        try(InputStream in = urlConn.getInputStream(); OutputStream out = new FileOutputStream(fileOutPath)) {
            in.transferTo(out);
        }
    }



}

