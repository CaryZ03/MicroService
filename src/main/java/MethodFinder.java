import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

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