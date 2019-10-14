import java.util.ArrayList;
import java.util.HashMap;
import java.io.FileWriter;
import java.io.IOException;

public class FuncVisitor extends FunctionsBaseVisitor < Integer > {

    private HashMap < String,
    Integer > funcs = new HashMap < String,
    Integer > (); //guarda função e quantidade de parametros
    private ArrayList < String >
    var = new ArrayList < String > (); // guarda variáveis
    private ArrayList < String > param = new ArrayList < String > (); // guarda parametros da função, para verificar se as variáveis estão no escopo dela
    private int lastRegister = 0, lastInitRegister = 0;
    private String code = "";
    private String init = "define void @init(){\n";
    private ArrayList < Integer > args = new ArrayList < Integer > ();
    private boolean isCode = true;

		public Integer writeFile(String value) throws IOException{
			try (FileWriter file = new FileWriter("code.ll")){
				file.write(value);
				file.close();
			}
			return 0;
		}
    @Override
    public Integer visitProgram(FunctionsParser.ProgramContext ctx){
        visit(ctx.root());
        init += "ret void \n}";
				String program = code + init;
				try{
					writeFile(program);
				}catch(Exception e){
					e.printStackTrace();
				}
        return 0;
    }
    @Override
    public Integer visitDecVar(FunctionsParser.DecVarContext ctx) {
        var.add(ctx.ID().getText());
        code += "@" + ctx.ID().getText() + " = global i32 0\n";
        isCode = false;
				lastRegister = lastInitRegister;
        int valuereg = visit(ctx.expr());
				lastInitRegister = lastRegister;
        init += "store i32 %" + valuereg + ", i32* @" + ctx.ID().getText() + "\n";
        isCode = true;
        return 0;
    }

    public Integer visitDecFunc(FunctionsParser.DecFuncContext ctx) {
        code += "define i32 @" + ctx.ID().getText() + "(";
        param.clear();
				lastRegister = 0;
        visit(ctx.ids());
        for (int i = 0; i < param.size() - 1; i++) {
            code += "i32 %" + param.get(i) + ", ";
        }
        code += "i32 %" + param.get(param.size() - 1) + "){\n";
        isCode = true;
        int resreg = visit(ctx.expr());
        code += "ret i32 %" + resreg + "\n" + "}\n";
				param.clear();
  			return 0;
    }

    public Integer visitParamIds(FunctionsParser.ParamIdsContext ctx) {
        return visit(ctx.ids(0)) + visit(ctx.ids(1));
    }

    public Integer visitParamId(FunctionsParser.ParamIdContext ctx) {
        param.add(ctx.ID().getText());
        return 1;
    }

    public Integer visitFunction(FunctionsParser.FunctionContext ctx) {
        args.clear();
        visit(ctx.values());
				int resreg = ++lastRegister;
        String instr = "%" + resreg + " = call i32 @" + ctx.ID().getText() + "(";
        for(int i = 0; i < args.size(); i++){
            if(i != args.size() -1){
                instr += "i32 %" + args.get(i) + ", ";
            }else{
                instr += "i32 %" + args.get(i) + ")\n";
            }
        }
        if(isCode){
            code += instr;
        }else{
            init += instr;
        }
				args.clear();
        return resreg;
    }


    public Integer visitExprSoma(FunctionsParser.ExprSomaContext ctx) {
        int leftreg = visit(ctx.expr());
        int rightreg = visit(ctx.muldiv());
        int resreg = ++lastRegister;
        String instr = "%" + resreg + " = " + "add i32 %" + leftreg + ", %" + rightreg + "\n";
        if (isCode) {
            code += instr;
        } else {
            init += instr;
        }
        return resreg;
    } //visit children vai descer na árvore visitando os filhos

    public Integer visitExprSub(FunctionsParser.ExprSubContext ctx) {
        int leftreg = visit(ctx.expr());
        int rightreg = visit(ctx.muldiv());
        int resreg = ++lastRegister;
        String instr = "%" + resreg + " = " + "sub i32 %" + leftreg + ", %" + rightreg + "\n";
        if (isCode) {
            code += instr;
        } else {
            init += instr;
        }
        return resreg;
    } //visit children vai descer na árvore visitando os filhos

    public Integer visitExprMuldiv(FunctionsParser.ExprMuldivContext ctx) {
        return visit(ctx.muldiv());
    } //visit children vai descer na árvore visitando os filhos

    public Integer visitMuldivMul(FunctionsParser.MuldivMulContext ctx) {
        int leftreg = visit(ctx.muldiv());
        int rightreg = visit(ctx.paren());
        int resreg = ++lastRegister;
        String instr = "%" + resreg + " = " + "mul i32 %" + leftreg + ", %" + rightreg + "\n";
        if (isCode) {
            code += instr;
        } else {
            init += instr;
        }
        return resreg;
    } //visit children vai descer na árvore visitando os filhos

    public Integer visitMuldivDiv(FunctionsParser.MuldivDivContext ctx) {
        int leftreg = visit(ctx.muldiv());
        int rightreg = visit(ctx.paren());
        int resreg = ++lastRegister;
        String instr = "%" + resreg + " = " + "sdiv i32 %" + leftreg + ", %" + rightreg + "\n";
        if (isCode) {
            code += instr;
        } else {
            init += instr;
        }
        return resreg;
    } //visit children vai descer na árvore visitando os filhos

    public Integer visitMuldivParen(FunctionsParser.MuldivParenContext ctx) {
        return visit(ctx.paren());
    } //visit children vai descer na árvore visitando os filhos

    public Integer visitParenID(FunctionsParser.ParenIDContext ctx) {
        int resreg = ++lastRegister;
        String instr = "%" + resreg + " = ";
        String id = ctx.ID().getText();
        if (param.contains(id)) {
            instr += "add i32 %" + id + ", 0\n";
        }else if (var.contains(id)) {
            instr += "load i32, i32* @" + id + "\n";
        }
        if (isCode) {
            code += instr;
        } else {
            init += instr;
        }
        return resreg;
    }

    public Integer visitParenFunc(FunctionsParser.ParenFuncContext ctx) {
        return visit(ctx.func());
    }

    public Integer visitParenNum(FunctionsParser.ParenNumContext ctx) {
        int resreg = ++lastRegister;
        String instr = "%" + resreg + " = " + "add i32 " + ctx.NUM().getText() + ", 0\n";
        args.add(resreg);
        if (isCode) {
            code += instr;
        } else {
            init += instr;
        }
        return resreg;
    }

    public Integer visitParenParen(FunctionsParser.ParenParenContext ctx){
      return visit(ctx.expr());
    }

    public Integer visitArgvNum(FunctionsParser.ArgvNumContext ctx) {
        int resreg = ++lastRegister;
        String instr = "%" + resreg + " = " + "add i32 " + ctx.NUM().getText() + ", 0\n";
        args.add(resreg);
        if (isCode) {
            code += instr;
        } else {
            init += instr;
        }
        return 1;
    }

    public Integer visitArgvId(FunctionsParser.ArgvIdContext ctx) {
        String id = ctx.ID().getText();
        int resreg = ++lastRegister;
        String instr = "%" + resreg + " = ";
        if (args.contains(id)) {
            instr += "add i32 %" + id + ", 0\n";
        }else if (var.contains(id)) {
            instr += "load i32, i32* @" + id + "\n";
        }
        if (isCode) {
            code += instr;
        } else {
            init += instr;
        }
        args.add(resreg);
        return 1;
    }

    public Integer visitArgvFunc(FunctionsParser.ArgvFuncContext ctx) {
        return visit(ctx.values(0)) + visit(ctx.values(1));
    }
}
