package be.mobiledatacaptator.dao;

import java.io.File;
import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;

public interface IMdcDao {

	public String getFilecontent(String path) throws IOException;

	public List<String> getAllFilesFromPathWithExtension(String path, String extension, Boolean returnExtension)
			throws Exception;

	public boolean existsFile(String path) throws Exception;

	public void delete(String path) throws Exception;

	public void saveFile(String path, File file) throws Exception;

	public void saveStringToFile(String path, String string) throws Exception;

	public void appendStringToFile(String path, String string) throws Exception;

	public Bitmap getBitmapFromFile(String path) throws Exception;
	
	public void dumpToSd()throws Exception;

}
