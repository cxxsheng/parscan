import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @Test
    void handleOneFile() {
        Main.init();
        boolean reuslt = Main.handleOneFile(Paths.get("/home/cs/Downloads/newoutput/_1.1.6_Apkpure.apk/sources/com/playstore/installer/Device.java"));
        System.out.println(reuslt);
    }
}