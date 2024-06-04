import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cxxsheng.parscan.antlr.parser.JavaLexer;
import com.cxxsheng.parscan.antlr.parser.JavaParser;
import com.cxxsheng.parscan.light.LightClassListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class LightScanner {

    private final static Logger LOG = LoggerFactory.getLogger(LightScanner.class);

    private static void handleOneFile(Path path) throws IOException {
        JavaLexer lexer = new JavaLexer(CharStreams.fromPath(path));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JavaParser parser = new JavaParser(tokens);
        JavaParser.CompilationUnitContext tree = parser.compilationUnit();

        ParseTreeWalker walker = new ParseTreeWalker();
        LightClassListener listener = new LightClassListener();

        walker.walk(listener, tree);

    }
    public static void main(String[] args) {
        // handleTest();
        if (args.length <=1 ){
            LOG.error("please give an input file");
            return;
        }

        String giveJsonFile = args[0];
        String outputFile = args[1];
        OutputStream fos = null;
        try {
            fos = Files.newOutputStream(Paths.get(outputFile));
            String json = FileUtils.readFileToString(new File(giveJsonFile));
            JSONObject obj = JSON.parseObject(json);
            for(String key :obj.keySet()){
                LOG.info("handing " + key + " ...");
                fos.write(("handing " + key + " ...\n").getBytes());

                JSONArray path_array = obj.getJSONArray(key);
                for (int i = 0; i < path_array.size(); i ++){
                    //coveint for debug
                    String path = path_array.getString(i);
                    StringBuilder sb = new StringBuilder("parsing " + path + " ...");
                    LOG.info(sb.toString());
                    fos.write((sb.toString()+"\n").getBytes());
                    try {
                        handleOneFile(Paths.get(path));
                        sb = new StringBuilder("finished "+path);
                        fos.write((sb.toString()+"\n").getBytes());
                        LOG.info(sb.toString());
                    }catch (Exception e){
                        StringWriter sw = new StringWriter();
                        e.printStackTrace(new PrintWriter(sw));
                        fos.write((sw.toString()+"\n").getBytes());
                    }

                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }






    }



}
