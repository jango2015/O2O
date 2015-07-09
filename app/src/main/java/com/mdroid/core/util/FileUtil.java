package com.mdroid.core.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.http.util.CharArrayBuffer;

import so.contacts.hub.businessbean.ContactBackup;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.Thumbnails;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.mdroid.core.image.cache.Utils;

/**
 * 文件操作类
 * 
 * @author William.cheng
 * @version 创建时间：2011-10-18 上午9:53:05
 */
public class FileUtil {
	private static String[] PICTURE_EXT = new String[] { ".png", ".jpg",
			".jpeg", ".gif" };

	/**
	 * 扫描文件夹。
	 * 
	 * @author William.cheng
	 * @version 创建时间：2011-10-18 上午10:13:18
	 * @param path
	 *            路径
	 * @param time
	 *            文件创建时间
	 * @return 返回创建时间大于time的文件集合
	 */
	public static ArrayList<File[]> scan(String path, long time) {
		File file = new File(path);
		if (!file.exists() || file.isFile()) {
			return null;
		}
		ArrayList<File[]> files = new ArrayList<File[]>();
		scanf(file, files, time);
		return files;
	}

	private static void scanf(File file, List<File[]> files, final long time) {
		File[] pictureFiles = file.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				String fileName = pathname.getName().toLowerCase();
				boolean isPicture = false;
				for (int i = 0, len = PICTURE_EXT.length; i < len; i++) {
					if (fileName.endsWith(PICTURE_EXT[i])) {
						isPicture = true;
						break;
					}
				}
				return !fileName.startsWith(".")
						&& (isPicture && pathname.isFile())
						&& pathname.lastModified() > time;
			}
		});
		if (pictureFiles != null && pictureFiles.length > 0) {
			files.add(pictureFiles);
		}

		File[] dirFiles = file.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				String fileName = pathname.getName().toLowerCase();
				return !fileName.startsWith(".") && pathname.isDirectory()
						&& pathname.list().length > 0;
			}
		});
		if (dirFiles != null) {
			for (int i = 0, len = dirFiles.length; i < len; i++) {
				scanf(dirFiles[i], files, time);
			}
		}
	}

	/**
	 * 格式化文件大小
	 * 
	 * @author William.cheng
	 * @version 创建时间：2011-11-11 下午2:52:55
	 * @param size
	 *            文件大小
	 * @return 文件大小字符串
	 */
	public static String sizeFormat(long size) {
		String suffix;
		double express = size;
		if (size >= 1024) {
			suffix = "K";
			size = size * 100 / 1024;
			if (size >= 102400) {
				suffix = "M";
				size /= 1024;
			}
			if (size >= 102400) {
				suffix = "G";
				size /= 1024;
			}
			express = size / 100d;
		} else {
			suffix = "B";
		}
		// 最大精确到小数点后1位
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(1);
		return nf.format(express) + suffix;
	}

	private static boolean checkFsWritable() {
		// Create a temporary file to see whether a volume is really writeable.
		// It's important not to put it in the root directory which may have a
		// limit on the number of files.
		String directoryName = Environment.getExternalStorageDirectory()
				.toString() + "/contactshub";
		File directory = new File(directoryName);
		if (!directory.isDirectory()) {
			if (!directory.mkdirs()) {
				return false;
			}
		}
		return directory.canWrite();
	}

	public static boolean quickHasStorage() {
		return Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState());
	}

	public static boolean hasStorage() {
		return hasStorage(true);
	}

	/**
	 * 判断是否有sdcard
	 * 
	 * @param requireWriteAccess
	 *            是否需要写入数据
	 * @return
	 */
	public static boolean hasStorage(boolean requireWriteAccess) {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			if (requireWriteAccess) {
				boolean writable = checkFsWritable();
				return writable;
			} else {
				return true;
			}
		} else if (!requireWriteAccess
				&& Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		}
		return false;
	}

	/**
	 * 获取sdcard路径
	 * 
	 * @author William.cheng
	 * @version 创建时间：2011-12-17 上午11:46:44
	 * @return sdcard存在返回路径，不存在返回null
	 */
	public static String getSDcardPath() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			File sdcardDir = Environment.getExternalStorageDirectory();
			return sdcardDir.getPath();
		}
		return null;
	}

	/**
	 * 复制src文件到dst文件。如果dst文件不存在，则创建它
	 * 
	 * @author William.cheng
	 * @version 创建时间：2011-12-27 下午5:32:48
	 * @param src
	 * @param dst
	 * @throws IOException
	 */
	public static void Copy(File src, File dst) throws IOException {
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);
		copyStream(in, out);
	}

	private static void copyStream(InputStream in, OutputStream out)
			throws IOException {
		// Transfer bytes from in to out
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}

	/**
	 * 获取图片的缩略图
	 * 
	 * @author William.cheng
	 * @version 创建时间：2011-11-17 上午10:17:31
	 * @param activity
	 * @param path
	 * @return
	 */
	public synchronized static Bitmap getThumbnail(Activity activity,
			String path) {
		ContentResolver resolver = activity.getContentResolver();
		Cursor cursor = activity.managedQuery(
				Images.Media.EXTERNAL_CONTENT_URI,
				new String[] { Images.Media._ID }, Images.Media.DATA
						+ "=? COLLATE NOCASE", new String[] { path },
				Images.Media.DEFAULT_SORT_ORDER);
		long origId = -1;
		if (cursor != null && cursor.moveToNext()) {
			origId = cursor.getLong(0);
		}
		Bitmap bitmap = null;
		if (origId != -1) {
			try {
				bitmap = Thumbnails.getThumbnail(resolver, origId,
						Thumbnails.MICRO_KIND, null);
			} catch (Throwable e) {
				// Ignore
			}
		}
		if (cursor != null) {
			cursor.close();
		}
		return bitmap;
	}

	public static boolean save(InputStream is, String path) {
		String temFile = path + ".tmp";
		OutputStream outStream = null;
		try {
			outStream = new FileOutputStream(temFile);
			final byte[] buffer = new byte[8192];
			while (true) {
				final int size = is.read(buffer);
				if (size <= 0) {
					break;
				}
				outStream.write(buffer, 0, size);
			}
			outStream.flush();
			File file = new File(temFile);
			file.renameTo(new File(path));
			return true;
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} finally {
			new File(temFile).delete();
			try {
				if (outStream != null)
					outStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/*
	 * 通过相机回传图片的文件名
	 */
	public static String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'IMG'_yyyyMMdd_HHmmss");
		return dateFormat.format(date) + ".jpg";
	}

	/**
	 * 获得指定文件的byte数组
	 */
	public static byte[] toByteArray(File file) {
		byte[] buffer = null;
		try {
			FileInputStream fis = new FileInputStream(file);
			ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
			byte[] b = new byte[1000];
			int n;
			while ((n = fis.read(b)) != -1) {
				bos.write(b, 0, n);
			}
			fis.close();
			bos.close();
			buffer = bos.toByteArray();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer;
	}

	public static String toString(Context context, File file)
			throws IOException {
		if (!file.exists()) {
			throw new RuntimeException(file + ": file not found");
		}

		if (!file.isFile()) {
			throw new RuntimeException(file + ": not a file");
		}

		if (!file.canRead()) {
			throw new RuntimeException(file + ": file not readable");
		}

		long longLength = file.length();
		int length = (int) longLength;
		if (length != longLength) {
			throw new RuntimeException(file + ": file too long");
		}

		InputStream instream = new FileInputStream(file);
		if (context != null
				&& file.getAbsolutePath().startsWith(
						context.getCacheDir().getAbsolutePath())) {
			instream = context.openFileInput(file.getName());
		} else {
			instream = new FileInputStream(file);
		}
		int i = 4096;
		Reader reader = new InputStreamReader(instream, "UTF-8");
		CharArrayBuffer buffer = new CharArrayBuffer(i);
		try {
			char[] tmp = new char[1024];
			int l;
			while ((l = reader.read(tmp)) != -1) {
				buffer.append(tmp, 0, l);
			}
		} finally {
			reader.close();
		}
		return buffer.toString();
	}

	public static void write(Context context, File file, String content) {
		if (TextUtils.isEmpty(content)) {
			return;
		}
		write(context, file, content.getBytes());
	}

	public static void write(Context context, File file, byte[] bytes) {
		FileOutputStream fout = null;
		try {
			if (context != null
					&& file.getAbsolutePath().startsWith(
							context.getCacheDir().getAbsolutePath())) {
				fout = context.openFileOutput(file.getName(),
				        Context.MODE_PRIVATE);
			} else {
				fout = new FileOutputStream(file);
			}
			fout.write(bytes);
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} finally {
			if (fout != null) {
				try {
					fout.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static File getBackupFile(Context context, String filename) {
		final String backupPath = Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())
				|| !Utils.isExternalStorageRemovable() ? Environment
				.getExternalStorageDirectory() + "/contactshub/save/" : context
				.getCacheDir().getAbsolutePath();
		File backupDir = new File(backupPath);
		if (!backupDir.exists()) {
			backupDir.mkdirs();
		}

		return new File(backupDir, filename);
	}
	
	public static File getUploadFile(Context context) {
		final String backupPath = Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())
				|| !Utils.isExternalStorageRemovable() ? Environment
				.getExternalStorageDirectory() + "/contactshub/upload/"
				: context.getCacheDir().getAbsolutePath();
		File backupDir = new File(backupPath);
		if (!backupDir.exists()) {
			backupDir.mkdirs();
		}

		return new File(backupDir, System.currentTimeMillis() + ".jpg");
	}
	
	public static Set<String> getLastBackupSet(Context mContext, String filename) {
        File backupFile = FileUtil.getBackupFile(mContext, filename);

        Set<String> backup = null;
        try {
            String b = FileUtil.toString(mContext, backupFile);
            if (b != null) {
                ContactBackup contactBackup = new Gson().fromJson(b,
                        ContactBackup.class);
                if (contactBackup != null) {
                    backup = contactBackup.backup;
                }
            }
        } catch (IOException e) {
        } catch (Exception e) {
        }

        if (backup == null) {
            backup = new TreeSet<String>();
        }
        return backup;
    }

    /**
     * 备份已上报的号码摘要，用于处理下次上报联系人时，若已上报则不再上报 [功能说明]
     * @author lh
     * @param datas
     *            已上报的号码摘要 [version] Create at 2013-6-1 下午12:04:33
     */
    public static void backup(Context mContext, Set<String> backup, String filename) {
        File backupFile = FileUtil.getBackupFile(mContext, filename);
        
        ContactBackup contactBackup = new ContactBackup();
        contactBackup.backup = backup;

        String content = new Gson().toJson(contactBackup);

        write(mContext, backupFile, content);
    }

	public static void deleteBackupFile(Context context, String filename) {
		File backup = getBackupFile(context, filename);
		if (backup != null && backup.exists()) {
			backup.delete();
		}
	}

	public static String getFileExtension(String filename) {
		String extension = ".png";
		if (!TextUtils.isEmpty(filename)) {
			int index = filename.lastIndexOf(".");
			extension = filename.substring(index + 1);
			if ("jpg".equalsIgnoreCase(extension)
					|| "jpeg".equalsIgnoreCase(extension)
					|| "png".equalsIgnoreCase(extension)
					|| "gif".equalsIgnoreCase(extension)) {
				extension = "." + extension;
			} else {
				extension = ".png";
			}
		}
		return extension;
	}

}
