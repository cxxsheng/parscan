import com.cxxsheng.parscan.Logger;
import com.cxxsheng.parscan.core.iterator.ParcelMismatchException;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {
    String [] target = {
            "b.b.b.a.b",
            "net.sqlcipher.CursorWindow",
            "com.google.android.exoplayer2.c4.v",
            "com.fundevs.app.mediaconverter.d.i1",
            "com.google.android.exoplayer2.f4.n.d$b",
            "com.google.android.exoplayer2.source.hls.s$b",
            "com.google.android.exoplayer2.scheduler.a",
            "c.j.a.a",
            "com.fundevs.app.mediaconverter.a2.q.n.r.f",
            "com.fundevs.app.mediaconverter.a2.q.b",
            "com.google.android.gms.common.internal.BinderWrapper",
            "com.google.android.exoplayer2.c4.v$b",
            "com.fundevs.app.mediaconverter.a2.c",
            "com.fundevs.app.mediaconverter.a2.e",
            "com.google.android.exoplayer2.f4.a",
            "com.google.android.exoplayer2.offline.e",
            "com.google.android.exoplayer2.offline.b",
            "com.fundevs.app.mediaconverter.d.c",
            "com.google.android.gms.internal.ads.zzm",
            "com.google.android.gms.internal.ads.zzn",
            "com.google.android.gms.internal.gtm.zzcr",
            "com.google.firebase.iid.o0",
            "com.google.android.gms.internal.ads.zzavh",
            "com.google.android.gms.internal.ads.zzapk",
            "com.google.android.gms.internal.ads.zzapj",
            "com.google.android.gms.internal.ads.zzanm",
            "com.google.android.gms.internal.ads.zzaru",
            "com.google.android.gms.internal.ads.zzaiv",
            "com.inmobi.media.bc",
    };
    @Test
    void handleOneFile() {
        Main.init();
        for (String packageName : target){
            Logger.log("handing"+ packageName);
            String packageName_ = packageName.replace(".","/");
            String[] pp = packageName_.split("\\$");
            packageName_ = pp[0]+ ".java";
            try {
                boolean reuslt = Main.handleOneFile(Paths.get("/home/cs/Downloads/newoutput/Video MP3 Converter_2.6.7_Apkpure.apk/sources/" + packageName_));
                Logger.log(""+reuslt);

            }catch (ParcelMismatchException e){
                StringWriter sw = new StringWriter();

                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                String exceptionString = sw.toString();
                Logger.log(""+ exceptionString);

            }catch (Exception e){
                StringWriter sw = new StringWriter();

                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                String exceptionString = sw.toString();
                Logger.log(""+ exceptionString);

            }
        }
    }
}