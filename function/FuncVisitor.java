import java.util.ArrayList;
import java.util.HashMap;

public class FuncVisitor extends FunctionsBaseVisitor<Integer> {

	private HashMap<String, Integer> funcs = new HashMap<String, Integer>(); //guarda função e quantidade de parametros
	private ArrayList<String> var = new ArrayList<String>(); // guarda variáveis
	private ArrayList<String> param = new ArrayList<String>(); // guarda parametros da função, para verificar se as variáveis estão no escopo dela
	private int lastRegister = 0;
	private String code = "";
	private String init = "init {\n";
	private ArrayList<Integer>  params =  new ArrayList<Integer>();
	private boolean isCode = true;


	@Override
	public Integer visitProgram(FunctionsParser.ProgramContext ctx) {
		visit(ctx.root());
		init += "}";
		System.out.println("OKK\n");
		// File file = new File("code.ll"); // Specify the filename
		return 0;
	}
  @Override
  public Integer visitDecVar(FunctionsParser.DecVarContext ctx) {
  	// if(!var.contains(ctx.ID().getText()) && funcs.get(ctx.ID().getText()) == null) {
  	// 	var.add(ctx.ID().getText());
  	// }else {
  	// 	//ERRO já foi declarado
  	// 	System.out.println("Symbol already declared: " + ctx.ID().getText());
  	// }
		// visit(ctx.expr());
		// return 0;
		code += "@" + ctx.ID().getText() + " = global i32 0\n";
		isCode = false;
		int valuereg = visit(ctx.expr());
		// init += ctx.ID().getText() + "= " + String.valueOf(valuereg);
		isCode = true;
		System.out.println("code" + code);
		System.out.println(init);
		return 0;
  }

	public Integer visitDecFunc(FunctionsParser.DecFuncContext ctx){
		init += "define i32 @" + ctx.ID().getText() + "(i32";
		// if(!var.contains(ctx.ID().getText()) && funcs.get(ctx.ID().getText()) == null) {
		// 	param.clear();
		// 	int n = visit(ctx.ids()); //guardar funções no hash com quantidade de parametros
		// 	funcs.put(ctx.ID().getText(), n);
		// 	visit(ctx.expr());
		// 	param.clear();
		// }else{
		// 	System.out.println("Symbol already declared: " + ctx.ID().getText());
		// }
		// visit(ctx.ids());
		isCode = false;
		visit(ctx.expr());
		isCode = true;
		System.out.println("code" + code);
		System.out.println(init);
		return 0;
	}

	public Integer visitParamIds(FunctionsParser.ParamIdsContext ctx){
		return visit(ctx.ids(0)) + visit(ctx.ids(1));
	}

	public Integer visitParamId(FunctionsParser.ParamIdContext ctx){
		param.add(ctx.ID().getText());
		return 1;
	}

  public Integer visitFunction(FunctionsParser.FunctionContext ctx) {
		params = new ArrayList<Integer>();
		visit(ctx.values());
		// code += 'call f' (kdjafkjdslajflasdjflkasd);
		// res
		return 0;
  }


  public Integer visitExprSoma(FunctionsParser.ExprSomaContext ctx){
  	visit(ctx.expr());
  	visit(ctx.muldiv());
  	return 0;
  } //visit children vai descer na árvore visitando os filhos

	public Integer visitExprSub(FunctionsParser.ExprSubContext ctx){
		visit(ctx.expr());
  	visit(ctx.muldiv());
  	return 0;
	} //visit children vai descer na árvore visitando os filhos

	public Integer visitExprMuldiv(FunctionsParser.ExprMuldivContext ctx){
		return visit(ctx.muldiv());
	} //visit children vai descer na árvore visitando os filhos

	public Integer visitMuldivMul(FunctionsParser.MuldivMulContext ctx){
		int leftreg = visit(ctx.muldiv());
  	int rightreg = visit(ctx.paren());
		int resreg = ++lastRegister;
		String instr = "%" + resreg + " = " + "mul i32 "+ leftreg + ", " + rightreg + "\n";
		if(isCode){
			code += instr;
		}else{
			init += instr;
		}
  	return resreg;
	} //visit children vai descer na árvore visitando os filhos

	public Integer visitMuldivDiv(FunctionsParser.MuldivDivContext ctx){
    visit(ctx.muldiv());
  	visit(ctx.paren());
    return 0;
	} //visit children vai descer na árvore visitando os filhos

	public Integer visitMuldivParen(FunctionsParser.MuldivParenContext ctx){
		return visit(ctx.paren());
	} //visit children vai descer na árvore visitando os filhos

	public Integer visitParenID(FunctionsParser.ParenIDContext ctx){
		return 0;
	}

	public Integer visitParenFunc(FunctionsParser.ParenFuncContext ctx) {
		return visit(ctx.func());
	}

	public Integer visitParenNum(FunctionsParser.ParenNumContext ctx){
		return Integer.parseInt(ctx.NUM().getText());
	}

	public Integer visitArgvNum(FunctionsParser.ArgvNumContext ctx) {
		if(isCode)
			params.add(Integer.parseInt(ctx.NUM().getText()));
		return 1;
	}

	public Integer visitArgvId(FunctionsParser.ArgvIdContext ctx) {
		String id = ctx.ID().getText();
		if(!param.contains(id) && !var.contains(id)) {
			//ERRO argumento inválido
			System.out.println("Bad used symbol: " + ctx.ID().getText());
		}
		return 1;
	}

	public Integer visitArgvFunc(FunctionsParser.ArgvFuncContext ctx) {
		return visit(ctx.values(0)) + visit(ctx.values(1));
	}

}
