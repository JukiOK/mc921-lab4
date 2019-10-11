import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class MyParser {
    public static void main(String[] args) throws Exception {
        // create a CharStream that reads from standard input
        CharStream input = CharStreams.fromStream(System.in);
        // create a lexer that feeds off of input CharStream
        FunctionsLexer lexer = new FunctionsLexer(input);
        // create a buffer of tokens pulled from the lexer
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        // create a parser that feeds off the tokens buffer
        FunctionsParser parser = new FunctionsParser(tokens);
        ParseTree tree = parser.source(); // begin parsing at prog rule
        FuncVisitor vis = new FuncVisitor();
        vis.visit(tree);
    }
}
