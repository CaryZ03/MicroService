package tool;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/*
 * 用于查找指定方法是否存在于Java文件中
 * 使用方法: MethodFinder.containsMethod(cu, "public void test()")
 */
public class MethodFinder extends VoidVisitorAdapter<Void> {
    private String targetName;
    private boolean found = false;

    public static boolean containsMethod(CompilationUnit cu, String methodPattern) {
        MethodFinder finder = new MethodFinder();
        finder.targetName = methodPattern.toLowerCase();
        finder.visit(cu, null);
        return finder.found;
    }

    @Override
    public void visit(MethodDeclaration md, Void arg) {
        super.visit(md, arg);
        if (md.getNameAsString().toLowerCase().contains(targetName)) {
            found = true;
        }
    }
}