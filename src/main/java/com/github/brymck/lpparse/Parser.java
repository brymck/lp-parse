package com.github.brymck.lpparse;

import static com.github.brymck.lpparse.ParserState.*;
import static com.github.brymck.lpparse.TokenType.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/** @author Bryan McKelvey */
public class Parser {
  private @NotNull ParserState errorState(
      @NotNull StateMachine stateMachine, @NotNull Token token) {
    String message = "invalid token " + token.toString() + " for state " + stateMachine.getState();
    throw new RuntimeException(message);
  }

  private @NotNull ParserState processKeywordToken(
      @NotNull StateMachine stateMachine, @NotNull Token token) {
    Keyword keyword = Keyword.valueOf(token.getValue());
    Problem problem = stateMachine.getProblem();
    Objective objective;
    Statement statement;
    List<Variable> variables;
    switch (keyword) {
      case MAXIMIZE:
        statement = stateMachine.newStatement();
        objective = new Objective(Sense.MAXIMIZE, statement);
        problem.setObjective(objective);
        return OBJECTIVE;
      case MINIMIZE:
        statement = stateMachine.newStatement();
        objective = new Objective(Sense.MINIMIZE, statement);
        problem.setObjective(objective);
        return OBJECTIVE;
      case BOUNDS:
        return BOUNDS_STATEMENT_NEW_LINE;
      case SUBJECT_TO:
        return SUBJECT_TO;
      case SEMI_CONTINUOUS:
        variables = stateMachine.newVariables();
        problem.setSemiContinuous(variables);
        return SEMI_CONTINUOUS;
      case GENERALS:
        variables = stateMachine.newVariables();
        problem.setGenerals(variables);
        return GENERALS;
      case END:
        return END;
    }
    throw new IllegalStateException("unknown keyword: " + keyword);
  }

  private @NotNull ParserState processObjectiveLabel(
      @NotNull StateMachine stateMachine, @NotNull Token token) {
    stateMachine.getStatement().setLabel(token.getValue());
    return OBJECTIVE_STATEMENT;
  }

  private @NotNull ParserState processStatementItem(
      @NotNull StateMachine stateMachine, @NotNull Token token) {
    stateMachine.getStatement().addItem(token.getValue());
    return stateMachine.getState();
  }

  private @NotNull ParserState processSubjectToLabel(
      @NotNull StateMachine stateMachine, @NotNull Token token) {
    Problem problem = stateMachine.getProblem();
    Statement statement = stateMachine.newStatement();
    statement.setLabel(token.getValue());
    problem.addSubjectTo(statement);
    return SUBJECT_TO_STATEMENT;
  }

  private @NotNull ParserState processBoundsItemOnNewLine(
      @NotNull StateMachine stateMachine, @NotNull Token token) {
    Problem problem = stateMachine.getProblem();
    List<String> items = new ArrayList<>();
    items.add(token.getValue());
    Bounds bounds = new Bounds(items);
    problem.addBounds(bounds);
    return BOUNDS_STATEMENT;
  }

  private @NotNull ParserState processBoundsItem(
      @NotNull StateMachine stateMachine, @NotNull Token token) {
    Problem problem = stateMachine.getProblem();
    problem.addItemToLatestBounds(token.getValue());
    return BOUNDS_STATEMENT;
  }

  private @NotNull ParserState processBoundsLineBreak(
      @NotNull StateMachine stateMachine, @NotNull Token token) {
    return BOUNDS_STATEMENT_NEW_LINE;
  }

  private @NotNull ParserState processVariable(
      @NotNull StateMachine stateMachine, @NotNull Token token) {
    Variable variable = new Variable(token.getValue());
    stateMachine.getVariables().add(variable);
    return stateMachine.getState();
  }

  private @NotNull ParserState noOp(@NotNull StateMachine stateMachine, @NotNull Token token) {
    return stateMachine.getState();
  }

  private @NotNull ParserState processToken(
      @NotNull StateMachine stateMachine, @NotNull Token token) {
    TokenType tokenType = token.getType();
    switch (stateMachine.getState()) {
      case START:
        if (tokenType == KEYWORD) {
          return processKeywordToken(stateMachine, token);
        } else if (tokenType == LINE_BREAK) {
          return noOp(stateMachine, token);
        }
        break;
      case OBJECTIVE:
        if (tokenType == LABEL) {
          return processObjectiveLabel(stateMachine, token);
        } else if (tokenType == LINE_BREAK) {
          return noOp(stateMachine, token);
        }
        break;
      case OBJECTIVE_STATEMENT:
        if (tokenType == KEYWORD) {
          return processKeywordToken(stateMachine, token);
        } else if (tokenType == VARIABLE) {
          return processStatementItem(stateMachine, token);
        } else if (tokenType == LINE_BREAK) {
          return noOp(stateMachine, token);
        }
        break;
      case BOUNDS_STATEMENT:
        if (tokenType == VARIABLE) {
          return processBoundsItem(stateMachine, token);
        } else if (tokenType == LINE_BREAK) {
          return processBoundsLineBreak(stateMachine, token);
        }
        break;
      case BOUNDS_STATEMENT_NEW_LINE:
        if (tokenType == KEYWORD) {
          return processKeywordToken(stateMachine, token);
        } else if (tokenType == VARIABLE) {
          return processBoundsItemOnNewLine(stateMachine, token);
        } else if (tokenType == LINE_BREAK) {
          return processBoundsLineBreak(stateMachine, token);
        }
        break;
      case SUBJECT_TO:
        if (tokenType == LABEL) {
          return processSubjectToLabel(stateMachine, token);
        } else if (tokenType == LINE_BREAK) {
          return noOp(stateMachine, token);
        }
        break;
      case SUBJECT_TO_STATEMENT:
        if (tokenType == KEYWORD) {
          return processKeywordToken(stateMachine, token);
        } else if (tokenType == LABEL) {
          return processSubjectToLabel(stateMachine, token);
        } else if (tokenType == VARIABLE) {
          return processStatementItem(stateMachine, token);
        } else if (tokenType == LINE_BREAK) {
          return noOp(stateMachine, token);
        }
        break;
      case GENERALS:
      case SEMI_CONTINUOUS:
        if (tokenType == KEYWORD) {
          return processKeywordToken(stateMachine, token);
        } else if (tokenType == VARIABLE) {
          return processVariable(stateMachine, token);
        } else if (tokenType == LINE_BREAK) {
          return noOp(stateMachine, token);
        }
        break;
      case END:
        if (tokenType == LINE_BREAK) {
          return noOp(stateMachine, token);
        }
        break;
    }
    return errorState(stateMachine, token);
  }

  @NotNull
  Problem parse(@NotNull InputStream inputStream) {
    StateMachine stateMachine = new StateMachine();
    Lexer lexer = new Lexer(inputStream);
    lexer.forEach(
        (Token token) -> {
          ParserState nextState = processToken(stateMachine, token);
          stateMachine.setState(nextState);
        });
    return stateMachine.getProblem();
  }
}
