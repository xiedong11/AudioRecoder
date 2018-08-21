package com.tfedu.record.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;

/**
 * *****************************************
 *
 * @文件名称 : FileUtils.java
 * @创建时间 : 2013-1-27 下午02:35:09
 * @文件描述 : 文件工具类
 * *****************************************
 */
public class FileUtils {

    /**
     * @param filePath
     * @Title: deleteFile
     * @Description: TODO删除文件方法
     */
    public static void deleteFile(String filePath) {
        File file = new File(filePath);
        SecurityManager checker = new SecurityManager();
        checker.checkDelete(file.toString());
        if (file.exists()) { // 判断文件是否存在
            if (file.isFile()) { // 判断是否是文件
                renameToDelete(file);
            } else if (file.isDirectory()) { // 否则如果它是一个目录
                File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
                if (files != null) {
                    for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                        deleteFileSub(files[i]); // 把每个文件 用这个方法进行迭代
                    }
                }
            }
        } else {
            System.out.println("所删除的文件不存在！" + '\n');
        }
    }

    private static void deleteFileSub(File file) {
        SecurityManager checker = new SecurityManager();
        checker.checkDelete(file.toString());
        if (file.exists()) { // 判断文件是否存在
            if (file.isFile()) { // 判断是否是文件
                renameToDelete(file);
            } else if (file.isDirectory()) { // 否则如果它是一个目录
                File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
                if (files != null) {
                    for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                        deleteFileSub(files[i]); // 把每个文件 用这个方法进行迭代
                    }
                }
                renameToDelete(file);
            }
            renameToDelete(file);
        } else {
            System.out.println("所删除的文件不存在！" + '\n');
        }
    }

    public static void renameToDelete(File file) {
        File mFile = new File(file.getAbsolutePath() + System.currentTimeMillis());
        file.renameTo(mFile);
        mFile.delete();
    }

    /**
     * @param filename
     * @return
     * @Title: Exists
     * @Description: TODO判断文件是否存在
     */
    public static boolean Exists(String filename) {
        if (filename == null || "".equals(filename)) {
            return false;
        }
        File file = new File(filename);
        return file.isFile() && file.exists();
    }

    /**
     * 写文本文件 在Android系统中，文件保存在 /data/data/PACKAGE_NAME/files 目录下
     *
     * @param context
     * @param msg
     */
    public static void write(Context context, String fileName, String content) {
        if (content == null)
            content = "";

        try {
            FileOutputStream fos = context.openFileOutput(fileName,
                    Context.MODE_PRIVATE);
            fos.write(content.getBytes());

            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取文本文件
     *
     * @param context
     * @param fileName
     * @return
     */
    public static String read(Context context, String fileName) {
        try {
            FileInputStream in = context.openFileInput(fileName);
            return readInStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 读取本地文本文件
     *
     * @param context
     * @param fileName
     * @return
     */
    public static String readLocal(String filePath) {
        String filetext = "";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(filePath), "gbk"));
            String Line = br.readLine();
            while (Line != null) {
                filetext += Line;
                Line = br.readLine();
            }
            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return filetext;
    }

    private static String readInStream(FileInputStream inStream) {
        try {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[512];
            int length = -1;
            while ((length = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, length);
            }

            outStream.close();
            inStream.close();
            return outStream.toString();
        } catch (IOException e) {
            Log.i("FileTest", e.getMessage());
        }
        return null;
    }

    public static File createFile(String folderPath, String fileName) {
        File destDir = new File(folderPath);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        return new File(folderPath, fileName + fileName);
    }

    public static void createFile(String filePath) {
        File file = new File(filePath);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 向手机写图片
     *
     * @param buffer
     * @param folder
     * @param fileName
     * @return
     */
    public static void writeFile(byte[] buffer, String filePath) {
        File file = new File(filePath);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            out.write(buffer);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据文件绝对路径获取文件名
     *
     * @param filePath
     * @return
     */
    public static String getFileName(String filePath) {
        if (StringUtils.isEmpty(filePath))
            return "";
        return filePath.substring(filePath.lastIndexOf(File.separator) + 1);
    }

    /**
     * 根据文件的绝对路径获取文件名但不包含扩展名
     *
     * @param filePath
     * @return
     */
    public static String getFileNameNoFormat(String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            return "";
        }
        int point = filePath.lastIndexOf('.');
        return filePath.substring(filePath.lastIndexOf(File.separator) + 1,
                point);
    }

    /**
     * 获取文件扩展名
     *
     * @param fileName
     * @return
     */
    public static String getFileFormat(String fileName) {
        if (StringUtils.isEmpty(fileName))
            return "";

        int point = fileName.lastIndexOf('.');
        return fileName.substring(point + 1);
    }

    /**
     * 获取文件大小
     *
     * @param filePath
     * @return
     */
    public static long getFileSize(String filePath) {
        long size = 0;

        File file = new File(filePath);
        if (file != null && file.exists()) {
            size = file.length();
        }
        return size;
    }

    /**
     * 获取文件大小
     *
     * @param size 字节
     * @return
     */
    public static String getFileSize(long size) {
        if (size <= 0)
            return "0";
        java.text.DecimalFormat df = new java.text.DecimalFormat("##.##");
        float temp = (float) size / 1024;
        if (temp >= 1024) {
            return df.format(temp / 1024) + "M";
        } else {
            return df.format(temp) + "K";
        }
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return B/KB/MB/GB
     */
    public static String formatFileSize(long fileS) {
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

    /**
     * 获取目录文件大小
     *
     * @param dir
     * @return
     */
    public static long getDirSize(File dir) {
        if (dir == null) {
            return 0;
        }
        if (dir != null && !dir.isDirectory()) {
            return 0;
        }
        long dirSize = 0;
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file != null && file.isFile()) {
                    dirSize += file.length();
                } else if (file != null && file.isDirectory()) {
                    dirSize += file.length();
                    dirSize += getDirSize(file); // 递归调用继续统计
                }
            }
        }
        return dirSize;
    }

    /**
     * 获取目录文件个数
     *
     * @return
     */
    public long getFileList(File dir) {
        long count = 0;
        File[] files = dir.listFiles();
        if (files != null) {
            count = files.length;
            for (File file : files) {
                if (file.isDirectory()) {
                    count = count + getFileList(file);// 递归
                    count--;
                }
            }
        }
        return count;
    }

    public static byte[] toBytes(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int ch;
        while ((ch = in.read()) != -1) {
            out.write(ch);
        }
        byte buffer[] = out.toByteArray();
        out.close();
        return buffer;
    }

    /**
     * 检查文件是否存在
     *
     * @param name
     * @return
     */
    public static boolean checkFileExists(String name) {
        boolean status;
        if (!name.equals("")) {
            File path = Environment.getExternalStorageDirectory();
            File newPath = new File(path.toString() + name);
            status = newPath.exists();
        } else {
            status = false;
        }
        return status;

    }

    /**
     * 计算SD卡的剩余空间
     *
     * @return 返回-1，说明没有安装sd卡
     */
    public static long getFreeDiskSpace() {
        String status = Environment.getExternalStorageState();
        long freeSpace = 0;
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            try {
                File path = Environment.getExternalStorageDirectory();
                StatFs stat = new StatFs(path.getPath());
                long blockSize = stat.getBlockSize();
                long availableBlocks = stat.getAvailableBlocks();
                freeSpace = availableBlocks * blockSize / 1024;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return -1;
        }
        return (freeSpace);
    }

    /**
     * 新建目录
     *
     * @param directoryPath
     * @return
     */
    public static boolean createDirectory(String directoryPath) {
        boolean status = false;
        if (directoryPath != null && !directoryPath.equals("")) {
            File newPath = new File(directoryPath);
            if (!newPath.exists()) {
                status = newPath.mkdirs();
                status = true;
            }
        } else
            status = false;
        return status;
    }

    /**
     * 检查是否安装SD卡
     *
     * @return
     */
    public static boolean checkSdCard() {
        String sDCardStatus = Environment.getExternalStorageState();
        boolean status;
        if (sDCardStatus.equals(Environment.MEDIA_MOUNTED)) {
            status = true;
        } else
            status = false;
        return status;
    }

    /**
     * @throws
     * @Title: dirHasFile
     * @Description:判断目录下是否有文件
     * @param:
     * @return: boolean
     */
    public static boolean dirHasFile(String dirPath) {
        if (dirPath == null || "".equals(dirPath)) {
            return false;
        }
        File dirFile = new File(dirPath);
        if (dirFile != null && dirFile.exists() && dirFile.listFiles() != null && dirFile.listFiles().length > 0) {
            return true;
        }
        return false;
    }

    /**
     * @param fileDir目录路径
     * @param fileName文件名称
     * @Title: createDirAndFile
     * @Description: TODO创建目录和文件
     */
    public static void createDirAndFile(String fileDir, String fileName) {
        if (fileDir != null && !"".equals(fileDir)) {
            File dirFile = new File(fileDir);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }
        }
        if (fileName != null && !"".equals(fileName)) {
            File file = new File(fileDir, fileName);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @param context
     * @param appdir
     * @param fileName
     * @param inStream
     * @Title: copyAssetsFile
     * @Description: TODO拷贝assets里的文件到本地
     */
    public static void copyAssetsFile(Context context, String appdir,
                                      String fileName, InputStream inStream) {
        File file = new File(appdir, fileName);
        createDirAndFile(appdir, fileName);
        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (outStream != null) {
                    outStream.close();
                }
                if (inStream != null) {
                    inStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param headDir
     * @param user_id
     * @return
     * @Title: getHeadName
     * @Description: TODO获取本地头像的文件名
     */
    public static String getHeadName(String headDir, String user_id) {
        File headFile = new File(headDir);
        if (headFile != null && headFile.exists()) {
            File[] files = headFile.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    File file = files[i];
                    if (file != null && file.isFile()) {
                        String filename = file.getName();
                        if (filename != null && filename.startsWith(user_id)) {
                            return filename;
                        }
                    }
                }
            }
        }
        return user_id;
    }

    /**
     * @param headDir
     * @param user_id
     * @Title: deleteHeadIcon
     * @Description: TODO删除本地头像文件
     */
    public static void deleteHeadIcon(String headDir, String user_id) {
        File headFile = new File(headDir);
        if (headFile != null && headFile.exists()) {
            File[] files = headFile.listFiles();
            if (files != null) {


                for (int i = 0; i < files.length; i++) {
                    File file = files[i];
                    if (file != null && file.isFile()) {
                        String filename = file.getName();
                        if (filename != null && filename.startsWith(user_id)) {
                            file.delete();
                        }
                    }
                }
            }
        }
    }

    /**
     * 使用文件通道的方式复制文件
     *
     * @param s 源文件
     * @param t 复制到的新文件
     */
    public static void fileChannelCopy(File s, File t) {
        FileInputStream fi = null;
        FileOutputStream fo = null;
        FileChannel in = null;
        FileChannel out = null;
        try {
            fi = new FileInputStream(s);
            fo = new FileOutputStream(t);
            if (fi != null && fo != null) {
                in = fi.getChannel();// 得到对应的文件通道
                out = fo.getChannel();// 得到对应的文件通道
            }
            if (in != null) {
                in.transferTo(0, in.size(), out);// 连接两个通道，并且从in通道读取，然后写入out通道
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fi != null) {
                    fi.close();
                }
                if (in != null) {
                    in.close();
                }
                if (fo != null) {
                    fo.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 读取表情配置文件
     *
     * @param context
     * @return
     */
    public static JSONArray getEmojiFile(Context context) {
        try {
            //			List<String> list = new ArrayList<String>();
            //			InputStream in = context.getResources().getAssets().open("emoji");
            //			BufferedReader br = new BufferedReader(
            //					new InputStreamReader(in, "UTF-8"));
            //			String str = null;
            //			while ((str = br.readLine()) != null) {
            //				list.add(str);
            //			}
            //
            //			return list;
            //	String json = "[{\"name\": \"smile\",\"src\": \"1f604.png\"},{\"name\": \"smiley\",\"src\": \"1f603.png\"},{\"name\": \"grinning\",\"src\": \"1f600.png\"},{\"name\": \"blush\",\"src\": \"1f60a.png\"},{\"name\": \"relaxed\",\"src\": \"263a.png\"},{\"name\": \"wink\",\"src\": \"1f609.png\"},{\"name\": \"heart_eyes\",\"src\": \"1f60d.png\"},{\"name\": \"kissing_heart\",\"src\": \"1f618.png\"},{\"name\": \"kissing_closed_eyes\",\"src\": \"1f61a.png\"},{\"name\": \"kissing\",\"src\": \"1f617.png\"},{\"name\": \"kissing_smiling_eyes\",\"src\": \"1f619.png\"},{\"name\": \"stuck_out_tongue_winking_eye\",\"src\": \"1f61c.png\"},{\"name\": \"stuck_out_tongue_closed_eyes\",\"src\": \"1f61d.png\"},{\"name\": \"stuck_out_tongue\",\"src\": \"1f61b.png\"},{\"name\": \"flushed\",\"src\": \"1f633.png\"},{\"name\": \"grin\",\"src\": \"1f601.png\"},{\"name\": \"pensive\",\"src\": \"1f614.png\"},{\"name\": \"relieved\",\"src\": \"1f60c.png\"},{\"name\": \"unamused\",\"src\": \"1f612.png\"},{\"name\": \"disappointed\",\"src\": \"1f61e.png\"},{\"name\": \"persevere\",\"src\": \"1f623.png\"},{\"name\": \"cry\",\"src\": \"1f622.png\"},{\"name\": \"joy\",\"src\": \"1f602.png\"},{\"name\": \"sob\",\"src\": \"1f62d.png\"},{\"name\": \"sleepy\",\"src\": \"1f62a.png\"},{\"name\": \"disappointed_relieved\",\"src\": \"1f625.png\"},{\"name\": \"cold_sweat\",\"src\": \"1f630.png\"},{\"name\": \"sweat_smile\",\"src\": \"1f605.png\"},{\"name\": \"sweat\",\"src\": \"1f613.png\"},{\"name\": \"weary\",\"src\": \"1f629.png\"},{\"name\": \"tired_face\",\"src\": \"1f62b.png\"},{\"name\": \"fearful\",\"src\": \"1f628.png\"},{\"name\": \"scream\",\"src\": \"1f631.png\"},{\"name\": \"angry\",\"src\": \"1f620.png\"},{\"name\": \"rage\",\"src\": \"1f621.png\"},{\"name\": \"triumph\",\"src\": \"1f624.png\"},{\"name\": \"confounded\",\"src\": \"1f616.png\"},{\"name\": \"laughing\",\"src\": \"1f606.png\"},{\"name\": \"yum\",\"src\": \"1f60b.png\"},{\"name\": \"mask\",\"src\": \"1f637.png\"},{\"name\": \"sunglasses\",\"src\": \"1f60e.png\"},{\"name\": \"sleeping\",\"src\": \"1f634.png\"},{\"name\": \"dizzy_face\",\"src\": \"1f635.png\"},{\"name\": \"astonished\",\"src\": \"1f632.png\"},{\"name\": \"worried\",\"src\": \"1f61f.png\"},{\"name\": \"frowning\",\"src\": \"1f626.png\"},{\"name\": \"anguished\",\"src\": \"1f627.png\"},{\"name\": \"smiling_imp\",\"src\": \"1f608.png\"},{\"name\": \"imp\",\"src\": \"1f47f.png\"},{\"name\": \"open_mouth\",\"src\": \"1f62e.png\"},{\"name\": \"grimacing\",\"src\": \"1f62c.png\"},{\"name\": \"neutral_face\",\"src\": \"1f610.png\"},{\"name\": \"confused\",\"src\": \"1f615.png\"},{\"name\": \"hushed\",\"src\": \"1f62f.png\"},{\"name\": \"no_mouth\",\"src\": \"1f636.png\"},{\"name\": \"innocent\",\"src\": \"1f607.png\"},{\"name\": \"smirk\",\"src\": \"1f60f.png\"},{\"name\": \"expressionless\",\"src\": \"1f611.png\"},{\"name\": \"man_with_gua_pi_mao\",\"src\": \"1f472.png\"},{\"name\": \"man_with_turban\",\"src\": \"1f473.png\"},{\"name\": \"cop\",\"src\": \"1f46e.png\"},{\"name\": \"construction_worker\",\"src\": \"1f477.png\"},{\"name\": \"guardsman\",\"src\": \"1f482.png\"},{\"name\": \"baby\",\"src\": \"1f476.png\"},{\"name\": \"boy\",\"src\": \"1f466.png\"},{\"name\": \"girl\",\"src\": \"1f467.png\"},{\"name\": \"man\",\"src\": \"1f468.png\"},{\"name\": \"woman\",\"src\": \"1f469.png\"},{\"name\": \"older_man\",\"src\": \"1f474.png\"},{\"name\": \"older_woman\",\"src\": \"1f475.png\"},{\"name\": \"person_with_blond_hair\",\"src\": \"1f471.png\"},{\"name\": \"angel\",\"src\": \"1f47c.png\"},{\"name\": \"princess\",\"src\": \"1f478.png\"},{\"name\": \"smiley_cat\",\"src\": \"1f63a.png\"},{\"name\": \"smile_cat\",\"src\": \"1f638.png\"},{\"name\": \"heart_eyes_cat\",\"src\": \"1f63b.png\"},{\"name\": \"kissing_cat\",\"src\": \"1f63d.png\"},{\"name\": \"smirk_cat\",\"src\": \"1f63c.png\"},{\"name\": \"scream_cat\",\"src\": \"1f640.png\"},{\"name\": \"crying_cat_face\",\"src\": \"1f63f.png\"},{\"name\": \"joy_cat\",\"src\": \"1f639.png\"},{\"name\": \"pouting_cat\",\"src\": \"1f63e.png\"},{\"name\": \"japanese_ogre\",\"src\": \"1f479.png\"},{\"name\": \"japanese_goblin\",\"src\": \"1f47a.png\"},{\"name\": \"see_no_evil\",\"src\": \"1f648.png\"},{\"name\": \"hear_no_evil\",\"src\": \"1f649.png\"},{\"name\": \"speak_no_evil\",\"src\": \"1f64a.png\"},{\"name\": \"skull\",\"src\": \"1f480.png\"},{\"name\": \"alien\",\"src\": \"1f47d.png\"},{\"name\": \"hankey\",\"src\": \"1f4a9.png\"},{\"name\": \"fire\",\"src\": \"1f525.png\"},{\"name\": \"sparkles\",\"src\": \"2728.png\"},{\"name\": \"star2\",\"src\": \"1f31f.png\"},{\"name\": \"dizzy\",\"src\": \"1f4ab.png\"},{\"name\": \"boom\",\"src\": \"1f4a5.png\"},{\"name\": \"anger\",\"src\": \"1f4a2.png\"},{\"name\": \"sweat_drops\",\"src\": \"1f4a6.png\"},{\"name\": \"droplet\",\"src\": \"1f4a7.png\"},{\"name\": \"zzz\",\"src\": \"1f4a4.png\"},{\"name\": \"dash\",\"src\": \"1f4a8.png\"},{\"name\": \"ear\",\"src\": \"1f442.png\"},{\"name\": \"eyes\",\"src\": \"1f440.png\"},{\"name\": \"nose\",\"src\": \"1f443.png\"},{\"name\": \"tongue\",\"src\": \"1f445.png\"},{\"name\": \"lips\",\"src\": \"1f444.png\"},{\"name\": \"+1\",\"src\": \"1f44d.png\"},{\"name\": \"-1\",\"src\": \"1f44e.png\"},{\"name\": \"ok_hand\",\"src\": \"1f44c.png\"},{\"name\": \"facepunch\",\"src\": \"1f44a.png\"},{\"name\": \"fist\",\"src\": \"270a.png\"},{\"name\": \"v\",\"src\": \"270c.png\"},{\"name\": \"wave\",\"src\": \"1f44b.png\"},{\"name\": \"hand\",\"src\": \"270b.png\"},{\"name\": \"open_hands\",\"src\": \"1f450.png\"},{\"name\": \"point_up_2\",\"src\": \"1f446.png\"},{\"name\": \"point_down\",\"src\": \"1f447.png\"},{\"name\": \"point_right\",\"src\": \"1f449.png\"},{\"name\": \"point_left\",\"src\": \"1f448.png\"},{\"name\": \"raised_hands\",\"src\": \"1f64c.png\"},{\"name\": \"pray\",\"src\": \"1f64f.png\"},{\"name\": \"point_up\",\"src\": \"261d.png\"},{\"name\": \"clap\",\"src\": \"1f44f.png\"},{\"name\": \"muscle\",\"src\": \"1f4aa.png\"},{\"name\": \"walking\",\"src\": \"1f6b6.png\"},{\"name\": \"runner\",\"src\": \"1f3c3.png\"},{\"name\": \"dancer\",\"src\": \"1f483.png\"},{\"name\": \"couple\",\"src\": \"1f46b.png\"},{\"name\": \"family\",\"src\": \"1f46a.png\"},{\"name\": \"two_men_holding_hands\",\"src\": \"1f46c.png\"},{\"name\": \"two_women_holding_hands\",\"src\": \"1f46d.png\"},{\"name\": \"couplekiss\",\"src\": \"1f48f.png\"},{\"name\": \"couple_with_heart\",\"src\": \"1f491.png\"},{\"name\": \"dancers\",\"src\": \"1f46f.png\"},{\"name\": \"ok_woman\",\"src\": \"1f646.png\"},{\"name\": \"no_good\",\"src\": \"1f645.png\"},{\"name\": \"information_desk_person\",\"src\": \"1f481.png\"},{\"name\": \"raising_hand\",\"src\": \"1f64b.png\"},{\"name\": \"massage\",\"src\": \"1f486.png\"},{\"name\": \"haircut\",\"src\": \"1f487.png\"},{\"name\": \"nail_care\",\"src\": \"1f485.png\"},{\"name\": \"bride_with_veil\",\"src\": \"1f470.png\"},{\"name\": \"person_with_pouting_face\",\"src\": \"1f64e.png\"},{\"name\": \"person_frowning\",\"src\": \"1f64d.png\"},{\"name\": \"bow\",\"src\": \"1f647.png\"},{\"name\": \"tophat\",\"src\": \"1f3a9.png\"},{\"name\": \"crown\",\"src\": \"1f451.png\"},{\"name\": \"womans_hat\",\"src\": \"1f452.png\"},{\"name\": \"athletic_shoe\",\"src\": \"1f45f.png\"},{\"name\": \"mans_shoe\",\"src\": \"1f45e.png\"},{\"name\": \"sandal\",\"src\": \"1f461.png\"},{\"name\": \"high_heel\",\"src\": \"1f460.png\"},{\"name\": \"boot\",\"src\": \"1f462.png\"},{\"name\": \"shirt\",\"src\": \"1f455.png\"},{\"name\": \"necktie\",\"src\": \"1f454.png\"},{\"name\": \"womans_clothes\",\"src\": \"1f45a.png\"},{\"name\": \"dress\",\"src\": \"1f457.png\"},{\"name\": \"running_shirt_with_sash\",\"src\": \"1f3bd.png\"},{\"name\": \"jeans\",\"src\": \"1f456.png\"},{\"name\": \"kimono\",\"src\": \"1f458.png\"},{\"name\": \"bikini\",\"src\": \"1f459.png\"},{\"name\": \"briefcase\",\"src\": \"1f4bc.png\"},{\"name\": \"handbag\",\"src\": \"1f45c.png\"},{\"name\": \"pouch\",\"src\": \"1f45d.png\"},{\"name\": \"purse\",\"src\": \"1f45b.png\"},{\"name\": \"eyeglasses\",\"src\": \"1f453.png\"},{\"name\": \"ribbon\",\"src\": \"1f380.png\"},{\"name\": \"closed_umbrella\",\"src\": \"1f302.png\"},{\"name\": \"lipstick\",\"src\": \"1f484.png\"},{\"name\": \"yellow_heart\",\"src\": \"1f49b.png\"},{\"name\": \"blue_heart\",\"src\": \"1f499.png\"},{\"name\": \"purple_heart\",\"src\": \"1f49c.png\"},{\"name\": \"green_heart\",\"src\": \"1f49a.png\"},{\"name\": \"heart\",\"src\": \"2764.png\"},{\"name\": \"broken_heart\",\"src\": \"1f494.png\"},{\"name\": \"heartpulse\",\"src\": \"1f497.png\"},{\"name\": \"heartbeat\",\"src\": \"1f493.png\"},{\"name\": \"two_hearts\",\"src\": \"1f495.png\"},{\"name\": \"sparkling_heart\",\"src\": \"1f496.png\"},{\"name\": \"revolving_hearts\",\"src\": \"1f49e.png\"},{\"name\": \"cupid\",\"src\": \"1f498.png\"},{\"name\": \"love_letter\",\"src\": \"1f48c.png\"},{\"name\": \"kiss\",\"src\": \"1f48b.png\"},{\"name\": \"ring\",\"src\": \"1f48d.png\"},{\"name\": \"gem\",\"src\": \"1f48e.png\"},{\"name\": \"bust_in_silhouette\",\"src\": \"1f464.png\"},{\"name\": \"busts_in_silhouette\",\"src\": \"1f465.png\"},{\"name\": \"speech_balloon\",\"src\": \"1f4ac.png\"},{\"name\": \"footprints\",\"src\": \"1f463.png\"},{\"name\": \"thought_balloon\",\"src\": \"1f4ad.png\"}]";
            String json = "[{\"name\": \"微笑\",\"src\": \"1f604.png\"},{\"name\": \"笑脸\",\"src\": \"1f603.png\"},{\"name\": \"笑嘻嘻\",\"src\": \"1f600.png\"},{\"name\": \"脸红\",\"src\": \"1f60a.png\"},{\"name\": \"轻松\",\"src\": \"263a.png\"},{\"name\": \"眼色\",\"src\": \"1f609.png\"},{\"name\": \"喜爱\",\"src\": \"1f60d.png\"},{\"name\": \"飞吻\",\"src\": \"1f618.png\"},{\"name\": \"闭眼轻吻\",\"src\": \"1f61a.png\"},{\"name\": \"接吻\",\"src\": \"1f617.png\"},{\"name\": \"笑眼轻吻\",\"src\": \"1f619.png\"},{\"name\": \"鬼脸\",\"src\": \"1f61c.png\"},{\"name\": \"吐舌头挤眼\",\"src\": \"1f61d.png\"},{\"name\": \"吐舌头\",\"src\": \"1f61b.png\"},{\"name\": \"脸红\",\"src\": \"1f633.png\"},{\"name\": \"眉开眼笑\",\"src\": \"1f601.png\"},{\"name\": \"沉思\",\"src\": \"1f614.png\"},{\"name\": \"松口气\",\"src\": \"1f60c.png\"},{\"name\": \"垂头丧气\",\"src\": \"1f612.png\"},{\"name\": \"失望\",\"src\": \"1f61e.png\"},{\"name\": \"坚持不懈\",\"src\": \"1f623.png\"},{\"name\": \"哭\",\"src\": \"1f622.png\"},{\"name\": \"喜悦\",\"src\": \"1f602.png\"},{\"name\": \"呜咽\",\"src\": \"1f62d.png\"},{\"name\": \"困\",\"src\": \"1f62a.png\"},{\"name\": \"冷汗\",\"src\": \"1f625.png\"},{\"name\": \"冷汗\",\"src\": \"1f630.png\"},{\"name\": \"尴尬\",\"src\": \"1f605.png\"},{\"name\": \"汗\",\"src\": \"1f613.png\"},{\"name\": \"厌倦\",\"src\": \"1f629.png\"},{\"name\": \"倦容\",\"src\": \"1f62b.png\"},{\"name\": \"可怕\",\"src\": \"1f628.png\"},{\"name\": \"呐喊\",\"src\": \"1f631.png\"},{\"name\": \"生气\",\"src\": \"1f620.png\"},{\"name\": \"愤怒\",\"src\": \"1f621.png\"},{\"name\": \"胜利\",\"src\": \"1f624.png\"},{\"name\": \"混淆\",\"src\": \"1f616.png\"},{\"name\": \"笑\",\"src\": \"1f606.png\"},{\"name\": \"百胜\",\"src\": \"1f60b.png\"},{\"name\": \"面具\",\"src\": \"1f637.png\"},{\"name\": \"太阳镜\",\"src\": \"1f60e.png\"},{\"name\": \"沉睡\",\"src\": \"1f634.png\"},{\"name\": \"晕\",\"src\": \"1f635.png\"},{\"name\": \"吃惊\",\"src\": \"1f632.png\"},{\"name\": \"担心\",\"src\": \"1f61f.png\"},{\"name\": \"皱眉\",\"src\": \"1f626.png\"},{\"name\": \"苦恼\",\"src\": \"1f627.png\"},{\"name\": \"魔鬼微笑\",\"src\": \"1f608.png\"},{\"name\": \"小鬼\",\"src\": \"1f47f.png\"},{\"name\": \"张口\",\"src\": \"1f62e.png\"},{\"name\": \"鬼脸\",\"src\": \"1f62c.png\"},{\"name\": \"中性脸\",\"src\": \"1f610.png\"},{\"name\": \"糊涂\",\"src\": \"1f615.png\"},{\"name\": \"肃静\",\"src\": \"1f62f.png\"},{\"name\": \"闭嘴\",\"src\": \"1f636.png\"},{\"name\": \"无辜\",\"src\": \"1f607.png\"},{\"name\": \"傻笑\",\"src\": \"1f60f.png\"},{\"name\": \"面无表情\",\"src\": \"1f611.png\"},{\"name\": \"瓜皮帽男\",\"src\": \"1f472.png\"},{\"name\": \"头巾男\",\"src\": \"1f473.png\"},{\"name\": \"警察\",\"src\": \"1f46e.png\"},{\"name\": \"建筑工人\",\"src\": \"1f477.png\"},{\"name\": \"卫兵\",\"src\": \"1f482.png\"},{\"name\": \"宝贝\",\"src\": \"1f476.png\"},{\"name\": \"男孩\",\"src\": \"1f466.png\"},{\"name\": \"女孩\",\"src\": \"1f467.png\"},{\"name\": \"男人\",\"src\": \"1f468.png\"},{\"name\": \"女人\",\"src\": \"1f469.png\"},{\"name\": \"老年男人\",\"src\": \"1f474.png\"},{\"name\": \"老年女人\",\"src\": \"1f475.png\"},{\"name\": \"金发人\",\"src\": \"1f471.png\"},{\"name\": \"天使\",\"src\": \"1f47c.png\"},{\"name\": \"公主\",\"src\": \"1f478.png\"},{\"name\": \"笑眼猫咪\",\"src\": \"1f63a.png\"},{\"name\": \"笑脸猫\",\"src\": \"1f638.png\"},{\"name\": \"花痴猫\",\"src\": \"1f63b.png\"},{\"name\": \"闭眼轻吻猫咪\",\"src\": \"1f63d.png\"},{\"name\": \"奸诈猫\",\"src\": \"1f63c.png\"},{\"name\": \"呐喊猫咪\",\"src\": \"1f640.png\"},{\"name\": \"哭脸猫咪\",\"src\": \"1f63f.png\"},{\"name\": \"开心猫咪\",\"src\": \"1f639.png\"},{\"name\": \"凶\",\"src\": \"1f63e.png\"},{\"name\": \"红鬼面\",\"src\": \"1f479.png\"},{\"name\": \"长鼻鬼面\",\"src\": \"1f47a.png\"},{\"name\": \"捂眼睛\",\"src\": \"1f648.png\"},{\"name\": \"捂耳朵\",\"src\": \"1f649.png\"},{\"name\": \"捂嘴巴\",\"src\": \"1f64a.png\"},{\"name\": \"骷髅\",\"src\": \"1f480.png\"},{\"name\": \"外星人\",\"src\": \"1f47d.png\"},{\"name\": \"汉基\",\"src\": \"1f4a9.png\"},{\"name\": \"火\",\"src\": \"1f525.png\"},{\"name\": \"火花\",\"src\": \"2728.png\"},{\"name\": \"闪星星\",\"src\": \"1f31f.png\"},{\"name\": \"晕\",\"src\": \"1f4ab.png\"},{\"name\": \"潮\",\"src\": \"1f4a5.png\"},{\"name\": \"愤怒\",\"src\": \"1f4a2.png\"},{\"name\": \"大汗\",\"src\": \"1f4a6.png\"},{\"name\": \"滴\",\"src\": \"1f4a7.png\"},{\"name\": \"zzz\",\"src\": \"1f4a4.png\"},{\"name\": \"冲刺\",\"src\": \"1f4a8.png\"},{\"name\": \"耳朵\",\"src\": \"1f442.png\"},{\"name\": \"眼睛\",\"src\": \"1f440.png\"},{\"name\": \"鼻子\",\"src\": \"1f443.png\"},{\"name\": \"舌头\",\"src\": \"1f445.png\"},{\"name\": \"唇\",\"src\": \"1f444.png\"},{\"name\": \"+1\",\"src\": \"1f44d.png\"},{\"name\": \"-1\",\"src\": \"1f44e.png\"},{\"name\": \"OK\",\"src\": \"1f44c.png\"},{\"name\": \"撞脸\",\"src\": \"1f44a.png\"},{\"name\": \"碰拳\",\"src\": \"270a.png\"},{\"name\": \"耶\",\"src\": \"270c.png\"},{\"name\": \"超人\",\"src\": \"1f44b.png\"},{\"name\": \"手\",\"src\": \"270b.png\"},{\"name\": \"张开手\",\"src\": \"1f450.png\"},{\"name\": \"向上指\",\"src\": \"1f446.png\"},{\"name\": \"向下指\",\"src\": \"1f447.png\"},{\"name\": \"向右指\",\"src\": \"1f449.png\"},{\"name\": \"向左指\",\"src\": \"1f448.png\"},{\"name\": \"举手\",\"src\": \"1f64c.png\"},{\"name\": \"祈祷\",\"src\": \"1f64f.png\"},{\"name\": \"向上\",\"src\": \"261d.png\"},{\"name\": \"拍\",\"src\": \"1f44f.png\"},{\"name\": \"肌肉\",\"src\": \"1f4aa.png\"},{\"name\": \"散步\",\"src\": \"1f6b6.png\"},{\"name\": \"跑步\",\"src\": \"1f3c3.png\"},{\"name\": \"跳舞\",\"src\": \"1f483.png\"},{\"name\": \"情侣\",\"src\": \"1f46b.png\"},{\"name\": \"家庭\",\"src\": \"1f46a.png\"},{\"name\": \"握手\",\"src\": \"1f46c.png\"},{\"name\": \"握手\",\"src\": \"1f46d.png\"},{\"name\": \"接吻\",\"src\": \"1f48f.png\"},{\"name\": \"心心相印\",\"src\": \"1f491.png\"},{\"name\": \"跳舞\",\"src\": \"1f46f.png\"},{\"name\": \"好的\",\"src\": \"1f646.png\"},{\"name\": \"不好\",\"src\": \"1f645.png\"},{\"name\": \"公告人\",\"src\": \"1f481.png\"},{\"name\": \"举手\",\"src\": \"1f64b.png\"},{\"name\": \"按摩\",\"src\": \"1f486.png\"},{\"name\": \"理发\",\"src\": \"1f487.png\"},{\"name\": \"涂指甲\",\"src\": \"1f485.png\"},{\"name\": \"面纱新娘\",\"src\": \"1f470.png\"},{\"name\": \"噘嘴\",\"src\": \"1f64e.png\"},{\"name\": \"皱眉\",\"src\": \"1f64d.png\"},{\"name\": \"弓\",\"src\": \"1f647.png\"},{\"name\": \"顶帽\",\"src\": \"1f3a9.png\"},{\"name\": \"皇冠\",\"src\": \"1f451.png\"},{\"name\": \"女帽\",\"src\": \"1f452.png\"},{\"name\": \"健身鞋\",\"src\": \"1f45f.png\"},{\"name\": \"男鞋\",\"src\": \"1f45e.png\"},{\"name\": \"凉鞋\",\"src\": \"1f461.png\"},{\"name\": \"高跟鞋\",\"src\": \"1f460.png\"},{\"name\": \"靴子\",\"src\": \"1f462.png\"},{\"name\": \"衬衫\",\"src\": \"1f455.png\"},{\"name\": \"男士衬衫\",\"src\": \"1f454.png\"},{\"name\": \"女士短袖衬衫\",\"src\": \"1f45a.png\"},{\"name\": \"礼服\",\"src\": \"1f457.png\"},{\"name\": \"跑步衫带肩带\",\"src\": \"1f3bd.png\"},{\"name\": \"牛仔裤\",\"src\": \"1f456.png\"},{\"name\": \"和服\",\"src\": \"1f458.png\"},{\"name\": \"比基尼\",\"src\": \"1f459.png\"},{\"name\": \"公文包\",\"src\": \"1f4bc.png\"},{\"name\": \"手袋\",\"src\": \"1f45c.png\"},{\"name\": \"袋子\",\"src\": \"1f45d.png\"},{\"name\": \"钱包\",\"src\": \"1f45b.png\"},{\"name\": \"眼镜\",\"src\": \"1f453.png\"},{\"name\": \"丝带\",\"src\": \"1f380.png\"},{\"name\": \"伞\",\"src\": \"1f302.png\"},{\"name\": \"口红\",\"src\": \"1f484.png\"},{\"name\": \"黄心\",\"src\": \"1f49b.png\"},{\"name\": \"蓝心\",\"src\": \"1f499.png\"},{\"name\": \"紫心\",\"src\": \"1f49c.png\"},{\"name\": \"绿心\",\"src\": \"1f49a.png\"},{\"name\": \"心\",\"src\": \"2764.png\"},{\"name\": \"心碎\",\"src\": \"1f494.png\"},{\"name\": \"心跳\",\"src\": \"1f497.png\"},{\"name\": \"心跳加速\",\"src\": \"1f493.png\"},{\"name\": \"爱你\",\"src\": \"1f495.png\"},{\"name\": \"倾心\",\"src\": \"1f496.png\"},{\"name\": \"动心\",\"src\": \"1f49e.png\"},{\"name\": \"丘比特\",\"src\": \"1f498.png\"},{\"name\": \"情书\",\"src\": \"1f48c.png\"},{\"name\": \"吻\",\"src\": \"1f48b.png\"},{\"name\": \"戒指\",\"src\": \"1f48d.png\"},{\"name\": \"宝石\",\"src\": \"1f48e.png\"},{\"name\": \"管理员\",\"src\": \"1f464.png\"},{\"name\": \"管理员们\",\"src\": \"1f465.png\"},{\"name\": \"会话\",\"src\": \"1f4ac.png\"},{\"name\": \"脚印\",\"src\": \"1f463.png\"},{\"name\": \"思考中\",\"src\": \"1f4ad.png\"}]";
            JSONArray ja = new JSONArray(json);
            return ja;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents"
                .equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents"
                .equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents"
                .equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content"
                .equals(uri.getAuthority());
    }

    @TargetApi(19)
    public static String getImageAbsolutePath(Activity context, Uri imageUri) {
        if (context == null || imageUri == null)
            return null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT
                && DocumentsContract.isDocumentUri(context, imageUri)) {
            if (isExternalStorageDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }
            } else if (isDownloadsDocument(imageUri)) {
                String id = DocumentsContract.getDocumentId(imageUri);
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        } // MediaStore (and general)
        else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(imageUri))
                return imageUri.getLastPathSegment();
            return getDataColumn(context, imageUri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
            return imageUri.getPath();
        }
        return null;
    }

    public static void compressPicture(String srcPath) {
        if (!FileUtils.Exists(srcPath)) {
            return;
        }
        FileOutputStream fos = null;
        BitmapFactory.Options op = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        op.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, op);
        op.inJustDecodeBounds = false;
        // 缩放图片的尺寸
        float w = op.outWidth;
        float h = op.outHeight;
        float hh = 600f;
        float ww = 900f;
        // 最长宽度或高度1024
        float be = 1.0f;
        if (w > h && w > ww) {
            be = (float) (w / ww);
        } else if (w < h && h > hh) {
            be = (float) (h / hh);
        }
        if (be <= 0) {
            be = 1.0f;
        }
        op.inSampleSize = (int) be;// 设置缩放比例,这个数字越大,图片大小越小.
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, op);
        int desWidth = (int) (w / be);
        int desHeight = (int) (h / be);
        bitmap = Bitmap.createScaledBitmap(bitmap, desWidth, desHeight, true);
        try {
            fos = new FileOutputStream(srcPath);
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }

    public static void openAudioFile(Context context, String filePath) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(new File(filePath));
        intent.setDataAndType(uri, "audio/*");
        context.startActivity(intent);
    }

    public static void saveBitmap(String filePath, Bitmap bitmap) {
        if (bitmap != null) {
            File f = new File(filePath);
            if (f.exists()) {
                f.delete();
            }
            try {
                FileOutputStream out = new FileOutputStream(f);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bitmap.recycle();
        }
    }

    /**
     * @param fromFile 被复制的文件
     * @param toFile   复制的目录文件
     * @param rewrite  是否重新创建文件
     *                 <p>
     *                 <p>文件的复制操作方法
     */
    public static void copyfile(File fromFile, File toFile, Boolean rewrite) {
        if (!fromFile.exists()) {
            return;
        }
        if (!fromFile.isFile()) {
            return;
        }
        if (!fromFile.canRead()) {
            return;
        }
        if (!toFile.getParentFile().exists()) {
            toFile.getParentFile().mkdirs();
        }
        if (toFile.exists() && rewrite) {
            toFile.delete();
        }
        try {
            FileInputStream fosfrom = new FileInputStream(fromFile);
            FileOutputStream fosto = new FileOutputStream(toFile);

            byte[] bt = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c);
            }
            //关闭输入、输出流
            fosfrom.close();
            fosto.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void write(byte[] data, String filePath) {
        try {
            if (data != null) {
                FileOutputStream fos = new FileOutputStream(filePath);
                fos.write(data);
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] read(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            try {
                FileInputStream inStream = new FileInputStream(filePath);
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length = -1;
                while ((length = inStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, length);
                }
                outStream.close();
                inStream.close();
                return outStream.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
