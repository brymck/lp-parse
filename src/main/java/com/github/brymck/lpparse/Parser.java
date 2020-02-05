package com.github.brymck.lpparse;

import static com.github.brymck.lpparse.ParserState.*;
import static com.github.brymck.lpparse.TokenType.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** @author Bryan McKelvey */
public class Parser {
  private @NotNull ParserState errorState(StateMachine stateMachine, Token token) {
    String message = "invalid token " + token.toString() + " for state " + stateMachine.getState();
    throw new RuntimeException(message);
  }

  private @NotNull ParserState processKeywordToken(StateMachine stateMachine, Token token) {
    @Nullable ParserState nextState = null;
    Keyword keyword = Keyword.valueOf(token.getValue());
    Problem problem = stateMachine.getProblem();
    switch (keyword) {
      case MAXIMIZE:
        problem.setObjective(new Statement("obj", new ArrayList<>()));
        return OBJECTIVE;
      case MINIMIZE:
        problem.setObjective(new Statement("obj", new ArrayList<>()));
        return OBJECTIVE;
      case BOUNDS:
        return BOUNDS_STATEMENT_NEW_LINE;
      case SUBJECT_TO:
        return SUBJECT_TO;
      case SEMI_CONTINUOUS:
        return SEMI_CONTINUOUS;
      case GENERALS:
        return GENERALS;
      case END:
        return END;
    }
    throw new IllegalStateException("unknown keyword: " + keyword);
  }

  private @NotNull ParserState processObjectiveLabel(StateMachine stateMachine, Token token) {
    Problem problem = stateMachine.getProblem();
    problem.getObjective().setLabel(token.getValue());
    return OBJECTIVE_STATEMENT;
  }

  private @NotNull ParserState processObjectiveStatementItem(
      StateMachine stateMachine, Token token) {
    Problem problem = stateMachine.getProblem();
    problem.getObjective().addItem(token.getValue());
    return stateMachine.getState();
  }

  private @NotNull ParserState processSubjectToLabel(StateMachine stateMachine, Token token) {
    Problem problem = stateMachine.getProblem();
    Statement statement = stateMachine.newStatement(token.getValue());
    problem.addSubjectTo(statement);
    return SUBJECT_TO_STATEMENT;
  }

  private @NotNull ParserState processSubjectToStatementItem(
      StateMachine stateMachine, Token token) {
    Problem problem = stateMachine.getProblem();
    problem.addItemToLatestSubjectTo(token.getValue());
    return stateMachine.getState();
  }

  private @NotNull ParserState processBoundsItemOnNewLine(StateMachine stateMachine, Token token) {
    Problem problem = stateMachine.getProblem();
    List<String> items = new ArrayList<>();
    items.add(token.getValue());
    Bounds bounds = new Bounds(items);
    problem.addBounds(bounds);
    return BOUNDS_STATEMENT;
  }

  private @NotNull ParserState processBoundsItem(StateMachine stateMachine, Token token) {
    Problem problem = stateMachine.getProblem();
    problem.addItemToLatestBounds(token.getValue());
    return BOUNDS_STATEMENT;
  }

  private @NotNull ParserState processBoundsLineBreak(StateMachine stateMachine, Token token) {
    return BOUNDS_STATEMENT_NEW_LINE;
  }

  private @NotNull ParserState processGeneralsVariable(StateMachine stateMachine, Token token) {
    Problem problem = stateMachine.getProblem();
    Variable variable = new Variable(token.getValue());
    problem.addGeneralsVariable(variable);
    return stateMachine.getState();
  }

  private @NotNull ParserState processSemiContinuousVariable(
      StateMachine stateMachine, Token token) {
    Problem problem = stateMachine.getProblem();
    Variable variable = new Variable(token.getValue());
    problem.addSemiContinuousVariable(variable);
    return stateMachine.getState();
  }

  private @NotNull ParserState noOp(StateMachine stateMachine, Token token) {
    return stateMachine.getState();
  }

  private @NotNull ParserState processToken(StateMachine stateMachine, Token token) {
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
          return processObjectiveStatementItem(stateMachine, token);
        } else if (tokenType == LINE_BREAK) {
          return noOp(stateMachine, token);
        }
        break;
      case BOUNDS:
        if (tokenType == LINE_BREAK) {
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
          return processSubjectToStatementItem(stateMachine, token);
        } else if (tokenType == LINE_BREAK) {
          return noOp(stateMachine, token);
        }
        break;
      case GENERALS:
        if (tokenType == KEYWORD) {
          return processKeywordToken(stateMachine, token);
        } else if (tokenType == VARIABLE) {
          return processGeneralsVariable(stateMachine, token);
        } else if (tokenType == LINE_BREAK) {
          return noOp(stateMachine, token);
        }
        break;
      case SEMI_CONTINUOUS:
        if (tokenType == KEYWORD) {
          return processKeywordToken(stateMachine, token);
        } else if (tokenType == VARIABLE) {
          return processSemiContinuousVariable(stateMachine, token);
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
  Problem parse(@NotNull InputStream inputStream) throws IOException {
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
