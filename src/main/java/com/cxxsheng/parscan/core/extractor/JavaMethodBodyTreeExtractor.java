package com.cxxsheng.parscan.core.extractor;

import com.cxxsheng.parscan.antlr.exception.JavaASTExtractorException;
import com.cxxsheng.parscan.antlr.parser.JavaParser;
import com.cxxsheng.parscan.core.Coordinate;
import com.cxxsheng.parscan.core.data.ConditionalBlock;
import com.cxxsheng.parscan.core.data.ExpressionOrBlockList;
import com.cxxsheng.parscan.core.data.ForBlock;
import com.cxxsheng.parscan.core.data.Statement;
import com.cxxsheng.parscan.core.data.SynchronizedBlock;
import com.cxxsheng.parscan.core.data.WhileBlock;
import com.cxxsheng.parscan.core.data.unit.Expression;
import com.cxxsheng.parscan.core.data.unit.ExpressionListWithPrevs;

import java.nio.file.Path;
import java.util.List;

import com.cxxsheng.parscan.core.iterator.ASTParsingException;
import org.apache.log4j.Logger;

public class JavaMethodBodyTreeExtractor {


  private final static org.apache.log4j.Logger LOG = Logger.getLogger(JavaMethodBodyTreeExtractor.class);
      //trace the param
      //private final String traceParamName;

      //private final FunctionImp imp ;

      //public Stack<ExpressionOrBlockList> domainStack = new Stack<>();
      private final Path fileName;

      private final CommonExtractor extractor;
      public JavaMethodBodyTreeExtractor(Path fileName, CommonExtractor extractor) {
          //this.traceParamName = params;
          //this.imp = imp;
          this.fileName = fileName;
          this.extractor = extractor;
      }



      /*****************************************************************

      localTypeDeclaration
        : classOrInterfaceModifier*
        (classDeclaration | interfaceDeclaration)                         X fixme unhandled innerclass declaration or interface
        | ';'
      ;
       *****************************************************************/

      public static ExpressionOrBlockList parseLocalTypeDeclaration(
                                                                    JavaParser.LocalTypeDeclarationContext localTypeDeclaration){
          throw new JavaASTExtractorException("unhandled innerclass declaration or interface", localTypeDeclaration);
      }



      /*****************************************************************

      localVariableDeclaration
        : variableModifier* typeType variableDeclarators                   √
        ;
       IT WILL IGNORE TYPE AND VAR WITH NO INITIALIZER (eg. int a;)
       *****************************************************************/

      public List<ExpressionListWithPrevs> parseLocalVariableDeclaration(JavaParser.LocalVariableDeclarationContext localVariableDeclaration){
            //this is useful? WE DO NOT DEED TO RECORD TYPE
            JavaParser.TypeTypeContext typeTypeContext = localVariableDeclaration.typeType();

            return  extractor.parseVariableDeclarators(localVariableDeclaration.variableDeclarators());
        }


      /*****************************************************************
      Statement means a block which contains multi-blocks, and also represent
      an statement like if-else statement or while statement.
      statement
        : blockLabel=block                                                                √
        | ASSERT expression (':' expression)? ';'                                         X fixme unfinished
        | IF parExpression statement (ELSE statement)?                                    √
        | FOR '(' forControl ')' statement                                                X fixme unfinished
        | WHILE parExpression statement                                                   √
        | DO statement WHILE parExpression ';'                                            √
        | TRY block (catchClause+ finallyBlock? | finallyBlock)                           X fixme unfinished
        | TRY resourceSpecification block catchClause* finallyBlock?                      X fixme unfinished
        | SWITCH parExpression '{' switchBlockStatementGroup* switchLabel* '}'            X fixme unfinished
        | SYNCHRONIZED parExpression block                                                √
        | RETURN expression? ';'                                                          √
        | THROW expression ';'                                                            √
        | BREAK IDENTIFIER? ';'                                                           X fixme IDENTIFIER unfinished
        | CONTINUE IDENTIFIER? ';'                                                        X fixme IDENTIFIER unfinished
        | SEMI                                                                            √
        | statementExpression=expression ';'                                              √
        | identifierLabel=IDENTIFIER ':' statement                                        X fixme unhandled
      ;
      *****************************************************************/

      public ExpressionOrBlockList parseStatement(JavaParser.StatementContext statement){
        Coordinate x = Coordinate.createFromCtx(statement);

        if (statement.block()!=null){
               return parseBlock(statement.block());
          }

          if (statement.ASSERT()!=null){
                //here may have some problem
                LOG.info("Have an assert at " + Coordinate.createFromCtx(statement));
                ExpressionListWithPrevs e = extractor.parseExpression(statement.expression(0));
                Statement s = new Statement(x, Statement.ASSERT_STATEMENT ,e);
                return s.wrapToList();
          }

          //If statement
          if (statement.IF()!=null){
                ExpressionListWithPrevs el = extractor.parseExpression(statement.parExpression().expression());
                ConditionalBlock b = new ConditionalBlock(x, el, parseStatement( statement.statement(0)));

                if (statement.ELSE() != null){
                    b.initElseBlock(parseStatement(statement.statement(1)));

                }
                return b.wrapToList();
          }


          /*
          for statement

          //fixme unfinished
          */
          if (statement.FOR()!=null){

              JavaParser.ForControlContext forControl = statement.forControl();


              /*

              forControl
              : enhancedForControl
                | forInit? ';' expression? ';' forUpdate=ExpressionListWithPrevs?
              ;
              */

              //for each like for (element:iterator)
              List<ExpressionListWithPrevs> equals = null;
              if (forControl.enhancedForControl()!=null){
                    //throw new ASTParsingException("enhancedForControl unsupported");
                  return new ConditionalBlock(x, null, parseBlock(statement.statement().get(0).block())).wrapToList();
              }
              else {
                 equals = parseLocalVariableDeclaration(forControl.forInit().localVariableDeclaration());
                 JavaParser.ExpressionListContext exp = (forControl.forInit().expressionList());
                 if (exp!=null){
                     for (JavaParser.ExpressionContext c :exp.expression()){
                         equals.add(extractor.parseExpression(c));
                     }
                 }
              }

              //ForBlock forBlock = new ForBlock(x, parseBlock(statement.statement().get(0).block()));
              //covert for block to if block
              ConditionalBlock block = new ConditionalBlock(x, extractor.parseExpression(forControl.expression()), parseBlock(statement.statement().get(0).block()),equals);
              return block.wrapToList();
          }

          //do-while statement
          if (statement.DO() !=null ){
            WhileBlock whileBlock = new WhileBlock(x, extractor.parseExpression(statement.expression(0)), true, parseBlock(statement.block()));
            return whileBlock.wrapToList();
          }

          //while statement
          if (statement.WHILE()!=null){
              WhileBlock whileBlock = new WhileBlock(x, extractor.parseExpression(statement.expression(0)), false, parseBlock(statement.block()));
              return whileBlock.wrapToList();
          }


          //synchronized statement
          if (statement.SYNCHRONIZED()!=null){

              SynchronizedBlock b = new SynchronizedBlock(x, extractor.parseExpression(statement.expression(0)), parseBlock(statement.block()));
              return b.wrapToList();
          }
          //return statement
          if (statement.RETURN()!=null){
            Statement rt = new Statement(x, Statement.RETURN_STATEMENT, extractor.parseExpression(statement.expression(0)));
            return rt.wrapToList();
          }

          //Throw statement
          if (statement.THROW()!=null){
            Statement rt = new Statement(x, Statement.THROW_STATEMENT, extractor.parseExpression(statement.expression(0)));
            return rt.wrapToList();
          }


          if (statement.BREAK()!=null){
              //ignore label
              Statement rt = new Statement(x, Statement.BREAK_STATEMENT,null);
              return rt.wrapToList();
          }

          if (statement.CONTINUE()!=null){
              //ignore label
              Statement rt = new Statement(x, Statement.CONTINUE_STATEMENT,null);
              return rt.wrapToList();
          }

          //statementExpression
          if (statement.statementExpression!=null)
          {
            ExpressionListWithPrevs elp = extractor.parseExpression(statement.statementExpression);
            ExpressionOrBlockList ebl = ExpressionOrBlockList.InitEmptyInstance();
            return ebl.add(elp.toExpressionList());
          }

          //SEMI empty body
          if (statement.SEMI()!=null){
              return null;
          }

//          throw new JavaASTExtractorException("unreachable code ", statement);

            return ExpressionOrBlockList.InitEmptyInstance();


      }

        /*****************************************************************

        blockStatement
          : localVariableDeclaration ';'                                  √
          | statement                                                     √
          | localTypeDeclaration                                          √
        ;
       *****************************************************************/
      public ExpressionOrBlockList parseBlockStatement(JavaParser.BlockStatementContext blockStatement){
          JavaParser.LocalTypeDeclarationContext localVariableDeclaration =  blockStatement.localTypeDeclaration();
          if (localVariableDeclaration != null){
           return parseLocalTypeDeclaration(localVariableDeclaration);
          }

          JavaParser.LocalVariableDeclarationContext localVariableDeclarationContext = blockStatement.localVariableDeclaration();
          if (localVariableDeclarationContext!=null)
          {
            ExpressionOrBlockList retEBL = ExpressionOrBlockList.InitEmptyInstance();
            List<ExpressionListWithPrevs> elps = parseLocalVariableDeclaration(localVariableDeclarationContext);
            for (ExpressionListWithPrevs elp : elps){
              retEBL = retEBL.add(elp.toExpressionList());
            }
            return retEBL;
          }

          //it is son
          JavaParser.StatementContext statement = blockStatement.statement();
          if (statement!=null){
             return parseStatement(statement);

          }

          throw new JavaASTExtractorException("cannot handle type, unreachable code", blockStatement);
      }

      /*****************************************************************
       block
       : '{' blockStatement* '}'                                        √
       ;
       *****************************************************************/
      public ExpressionOrBlockList parseBlock(JavaParser.BlockContext block){

          ExpressionOrBlockList blockDomain = ExpressionOrBlockList.InitEmptyInstance();
            if (block == null){
                return ExpressionOrBlockList.InitEmptyInstance();
            }
          List<JavaParser.BlockStatementContext> blockStatements = block.blockStatement();
          for (JavaParser.BlockStatementContext blockStatement: blockStatements){
             blockDomain = blockDomain.combine(parseBlockStatement(blockStatement));
          }
          return blockDomain;

      }


      /*****************************************************************

         methodBody                                                       √
         : block
         | ';'
         ;
       ****************************************************************
       * @return*/
      public ExpressionOrBlockList parseMethodBody(JavaParser.MethodBodyContext methodBodyContext){
          JavaParser.BlockContext block = methodBodyContext.block();
          if (block!=null)
            return parseBlock(block);
          return ExpressionOrBlockList.InitEmptyInstance();
      }



}
