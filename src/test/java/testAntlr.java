import com.cxxsheng.parscan.antlr.JavaScanListener;
import com.cxxsheng.parscan.antlr.parser.JavaLexer;
import com.cxxsheng.parscan.antlr.parser.JavaParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.pattern.ParseTreeMatch;
import org.antlr.v4.runtime.tree.pattern.ParseTreePattern;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class testAntlr {
  public static void main(String[] args) throws IOException {

    Path cp = Paths.get("src", "test", "resources", "JavaDemo", "GateKeeperResponse.java");

    JavaLexer lexer = new JavaLexer(CharStreams.fromPath(cp));
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    JavaParser parser = new JavaParser(tokens);

    ParseTree tree = parser.compilationUnit();

    //parExpression
    //    : '(' expression ')'
    //    ;
    ParseTreePattern p = parser.compileParseTreePattern("(<expression>==<expression>)", JavaParser.RULE_parExpression);

    List<ParseTreeMatch> matches = p.findAll(tree, "//parExpression");

    for (ParseTreeMatch match : matches) {
      System.out.println("\nMATCH:");
      //System.out.printf(" - IDENTIFIER: %s\n", match.get("IDENTIFIER").getText());
      System.out.printf(" - expression: %s\n", match.get("expression").getText());

    }

  }
}
