import java.util.ArrayList;
import java.util.HashMap;

public class FuncVisitor extends FunctionsBaseVisitor<Integer> {

	private HashMap<String, Integer> funcs = new HashMap<String, Integer>(); //guarda função e quantidade de parametros
	private ArrayList<String> var = new ArrayList<String>(); // guarda variáveis
	private ArrayList<String> param = new ArrayList<String>(); // guarda parametros da função, para verificar se as variáveis estão no escopo dela

  @Override
  public Integer visitDecVar(FunctionsParser.DecVarContext ctx) {
  	if(!var.contains(ctx.ID().getText()) && funcs.get(ctx.ID().getText()) == null) {
  		var.add(ctx.ID().getText());
  	}else {
  		//ERRO já foi declarado
  		System.out.println("Symbol already declared: " + ctx.ID().getText());
  	}
		visit(ctx.expr());
		return 0;
  }

	public Integer visitDecFunc(FunctionsParser.DecFuncContext ctx){
		if(!var.contains(ctx.ID().getText()) && funcs.get(ctx.ID().getText()) == null) {
			param.clear();
			int n = visit(ctx.ids()); //guardar funções no hash com quantidade de parametros
			funcs.put(ctx.ID().getText(), n);
			visit(ctx.expr());
			param.clear();
		}else{
			System.out.println("Symbol already declared: " + ctx.ID().getText());
		}
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
   	if(var.contains(ctx.ID().getText())) {
		 	//ERRO bad use
		 	System.out.println("Bad used symbol: " + ctx.ID().getText());
   	}else if (funcs.get(ctx.ID().getText()) == null){
	 		//ERRO não foi declarado
	 		System.out.println("Symbol undeclared: " + ctx.ID().getText());
   	}else{
			int n = visit(ctx.values());
     	if(n != funcs.get(ctx.ID().getText())) {
     		//ERRO numero de argumentos errado
     		System.out.println("Bad argument count: " + ctx.ID().getText());
     	}
		}
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
		visit(ctx.muldiv());
  	visit(ctx.paren());
  	return 0;
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
		if(param.isEmpty() || !param.contains(ctx.ID().getText())){
			if(funcs.get(ctx.ID().getText()) != null){ // Se já tem uma função com o mesmo nome
				System.out.println("Bad used symbol: " + ctx.ID().getText());
			}else if(!var.contains(ctx.ID().getText())){ //Se variável recebe valor de outra variável não declarada
				System.out.println("Symbol undeclared: " + ctx.ID().getText());
			}
		}
		return 0;
	}

	public Integer visitParenFunc(FunctionsParser.ParenFuncContext ctx) {
		return visit(ctx.func());
	}

	public Integer visitArgvNum(FunctionsParser.ArgvNumContext ctx) {
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
