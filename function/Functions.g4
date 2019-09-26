grammar Functions;  

source: root EOF;

root: root root
   | dec
   ;

dec: 'var' ID '=' expr ';' #decVar
   | 'func' ID '(' ids ')' expr ';' #decFunc
   ;

expr: expr '+' muldiv #exprSoma
    | expr '-' muldiv #exprSub
    | muldiv	      #exprMuldiv
    ;

muldiv: muldiv '*' paren #muldivMul
      | muldiv '/' paren #muldivDiv
      | paren		 #muldivParen
      ;

func: ID '(' values ')' #function ;

values: values ',' values #argvFunc
   | NUM #argvNum
   | ID #argvId
   ;

ids: ids ',' ids #paramIds
   | ID  #paramId
   ;

paren: ID	#parenID
   | NUM	#parenNum
   | func	#parenFunc
   | '(' expr ')' #parenParen
   ;


ID: [a-zA-Z_][a-zA-Z0-9_]*;
NUM : [0-9]+;
SPACE: [ \t\n]+ -> skip ;
