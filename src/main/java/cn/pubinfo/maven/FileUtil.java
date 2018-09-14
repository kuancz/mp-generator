package cn.pubinfo.maven;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * @author kuancz
 * @date 2018/9/14
 */
class FileUtil {
    private static Set<String> set = new HashSet<>();

    /** 寻找指定的文件夹下， 是否有目标文件*/
    private static void listFile(File file){
        if (file != null){
            if (!file.isDirectory()){
                set.add(file.getName());
            }else{
                File[] sonList = file.listFiles();
                if (sonList!=null){
                    for (File son:sonList){
                        if (!son.isDirectory()){
                            set.add(son.getName());
                        }else{
                            listFile(son);
                        }
                    }
                }
            }
        }
    }

    static boolean isFileNotExist(String path, String name){
        File file = new File(path);
        listFile(file);
        return !set.contains(name);
    }

}
