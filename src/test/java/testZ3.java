import com.microsoft.z3.ArrayExpr;
import com.microsoft.z3.ArraySort;
import com.microsoft.z3.BitVecExpr;
import com.microsoft.z3.BitVecNum;
import com.microsoft.z3.BitVecSort;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntNum;
import com.microsoft.z3.Sort;

public class testZ3 {
  public static void main(String[] args) {
    Context ctx = new Context();
    BitVecExpr b = ctx.mkBVConst("a",8);
    int size = b.getSortSize();

    IntNum num = ctx.mkInt(0);
    IntNum num_one = ctx.mkInt(1);

    ArrayExpr arrayExpr = ctx.mkArrayConst("ret",ctx.getIntSort(), b.getSort());



    ctx.mkStore(arrayExpr,num,b);
    Expr expr = ctx.mkAdd(num, num_one);
    ctx.mkStore(arrayExpr,expr,b);
    System.out.println(arrayExpr.getSort());

    int a = 0;
     a^=3;
  }
}
