package repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import model.BTree;
import model.FileInfo;
import model.ServerError;
import util.Constants;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.rmi.ServerException;

public class FolderRepository {
    private BTree<FileInfo> bTree;

    public FolderRepository(String path, String method) throws IOException {
        this.connect(Constants.SERVER + "/folder?path=" + URLEncoder.encode(path, StandardCharsets.UTF_8), method);
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
                this.bTree = new Gson().fromJson(reader, new TypeToken<BTree<FileInfo>>(){}.getType());
            }
        }
    }

    public BTree<FileInfo> getBTree() {
        return bTree;
    }
}
