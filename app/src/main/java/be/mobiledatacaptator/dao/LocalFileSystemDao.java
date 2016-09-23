package be.mobiledatacaptator.dao;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by rvanloon on 21/09/2016.
 * Files are stored on the local file system
 * <p>
 * the path in the methods is always relative to a 'root'
 * (Was originaly for dropbox, and the root was set in the account)
 * So now, for each path, the root has to be added.
 */

public class LocalFileSystemDao implements IMdcDao {

    private String rootPath = Environment.getExternalStorageDirectory().getPath();

    private String getFullPath(String path) {
        int numberSlashes = 0;
        numberSlashes += rootPath.endsWith("/") ? 1 : 0;
        numberSlashes += path.startsWith("/") ? 1 : 0;

        if (numberSlashes == 0) {
            return rootPath + "/" + path;
        } else if (numberSlashes == 2) {
            return rootPath.substring(0, rootPath.length() - 1) + path;
        } else {
            return rootPath + path;
        }
    }

    @Override
    public String getFilecontent(String path) throws IOException {
        File file = new File(getFullPath(path));
        StringBuilder builder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String str;
        while ((str = bufferedReader.readLine()) != null) {
            builder.append(str);
        }
        return builder.toString();
    }

    @Override
    public List<String> getAllFilesFromPathWithExtension(String path, String extension, Boolean returnExtension) throws Exception {
        List<String> folderContent = new ArrayList<>();
        File[] rawFiles = new File(path).listFiles();
        for (File f : rawFiles) {
            String fileName = f.getName();
            if (fileName.endsWith(extension)) {
                fileName = returnExtension ? fileName : fileName.substring(0, fileName.length() - extension.length());
                folderContent.add(fileName);
            }
        }
        return folderContent;
    }

    @Override
    public boolean existsFile(String path) throws Exception {
        return new File(path).exists();
    }

    @Override
    public void delete(String path) throws Exception {
        File file = new File(path);
        file.delete();
    }

    @Override
    public void saveFile(String path, File file) throws Exception {
        File outFile  = new File(path);
        outFile.createNewFile();
        InputStream inputStream = new FileInputStream(file);
        OutputStream outputStream = new FileOutputStream(outFile);

        byte[] buf = new byte[1024];
        int len;
        while ((len = inputStream.read(buf))>0){
            outputStream.write(buf,0,len);
        }
        inputStream.close();
        outputStream.close();
    }

    @Override
    public void saveStringToFile(String path, String string) throws Exception {
        File file = new File(path);
        file.createNewFile();
        FileOutputStream fileOutputStream = new FileOutputStream(file,false);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
        outputStreamWriter.write(string);
        outputStreamWriter.flush();
        outputStreamWriter.close();
        fileOutputStream.close();
    }

    @Override
    public void appendStringToFile(String path, String string) throws Exception {
        File file = new File(path);
        file.createNewFile();
        FileOutputStream fileOutputStream = new FileOutputStream(file,true);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
        outputStreamWriter.append(string);
        outputStreamWriter.flush();
        outputStreamWriter.close();
        fileOutputStream.close();
    }

    @Override
    public Bitmap getBitmapFromFile(String path) throws Exception {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        return bitmap;
    }

    @Override
    public void dumpToSd() throws Exception {

    }
}
