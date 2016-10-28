package util;

import android.content.Context;
import android.os.Environment;

/**
 * 创建时间：2/7/16
 * 项目名称：newNaviDemo
 *
 * @author zhilong Huang
 * @email 765608464@qq.com
 * 类说明：
 */
public class OffLineMapUtils {
	/**
	 * 获取map 缓存和读取目录
	 */
	public static String getSdCacheDir(Context context) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			java.io.File fExternalStorageDirectory = Environment
					.getExternalStorageDirectory();
			java.io.File autonaviDir = new java.io.File(
					fExternalStorageDirectory, "amapsdk");
			boolean result = false;
			if (!autonaviDir.exists()) {
				result = autonaviDir.mkdir();
			}
			java.io.File minimapDir = new java.io.File(autonaviDir,
					"offlineMap");
			if (!minimapDir.exists()) {
				result = minimapDir.mkdir();
			}
			return minimapDir.toString() + "/";
		} else {
			return "";
		}
	}
}
