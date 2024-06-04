import com.cxxsheng.parscan.core.AntlrCore;
import com.cxxsheng.parscan.core.data.FunctionImp;
import com.cxxsheng.parscan.core.data.JavaClass;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class Experiment {
    public static void main(String[] args) throws IOException {
        if (args.length <= 0)
        {
            System.out.println("nonon0no");
            return;
        }

        if (args.length == 1)
        {
            Object ret = testOneDir(args[0]);
        }else {
            if(args[1].equals("sb")){
                Object ret = testOneDirWithHeap(args[0]);
            }
        }


    }

    public static List<JavaClass> testOneDir(String testDir) throws IOException {
        long startTime = System.currentTimeMillis();

        List<JavaClass> javaClasses = new ArrayList<>();
        System.out.println(testDir);
        Path directory = Paths.get(testDir);

        // 遍历目录下的所有文件

        Files.walk(directory)
                .filter(Files::isRegularFile) // 过滤出普通文件
                .forEach(path -> {
                    JavaClass javaClass = null;
                    try {
                        javaClass = new AntlrCore(path).parse(null);
                        javaClasses.add(javaClass);

                    } catch (Exception e) {
                        throw new RuntimeException(e);

                    }
                }); // 打印文件路径
        System.out.println(javaClasses.size());
        long stopTime = System.currentTimeMillis();
        long excutionTime = stopTime-startTime;

        System.out.println("执行毫秒数"+ excutionTime);
        return javaClasses;
    }


    public static List<JavaClass> testOneDirWithHeap(String testDir) throws IOException {


        List javaClasses = new ArrayList<>();
        System.out.println(testDir);
        Path directory = Paths.get(testDir);
        AtomicLong total = new AtomicLong();
        // 遍历目录下的所有文件
        Files.walk(directory)
                .filter(Files::isRegularFile) // 过滤出普通文件
                .forEach(path -> {
                    JavaClass javaClass = null;
                    try {
                        javaClass = new AntlrCore(path).parse(null);

                        for (FunctionImp method : javaClass.getMethods()){
                            total.addAndGet(method.getBody().getLength());
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);

                    }
                }); // 打印文件路径
       // System.out.println(javaClasses.size());
        System.out.println("使用内存"+  total.longValue());
        return javaClasses;
    }
}
