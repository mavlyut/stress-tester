package parser;

import generators.*;

import static generators.AbstractGBinaryExpression.*;
import static generators.AbstractGUnaryExpression.*;

public class ExpressionParser {
    private int bal = 0;

    public ExpressionParser() {
    }

    public GExpression parseExpression(CharIterator it, Variables vars) throws ParserException {
        bal = 0;
        GExpression ans = parse0(it, vars);
        if (bal > 0) {
            throw it.exception("Extra open bracket");
        }
        return ans;
    }

    private GExpression parse0(CharIterator it, Variables vars) throws ParserException {
        GExpression ans = parse1(it, vars);
        while (true) {
            if (it.take("min")) {
                ans = new GMin(ans, parse1(it, vars));
            } else if (it.take("max")) {
                ans = new GMax(ans, parse1(it, vars));
            } else {
                break;
            }
        }
        return ans;
    }

    private GExpression parse1(CharIterator it, Variables vars) throws ParserException {
        GExpression ans = parse2(it, vars);
        while (true) {
            if (it.take("+")) {
                ans = new GAdd(ans, parse2(it, vars));
            } else if (it.take("-")) {
                ans = new GSubtract(ans, parse2(it, vars));
            } else {
                break;
            }
        }
        return ans;
    }

    private GExpression parse2(CharIterator it, Variables vars) throws ParserException {
        GExpression ans = parse3(it, vars);
        while (true) {
            if (it.take("*")) {
                ans = new GMultiply(ans, parse3(it, vars));
            } else if (it.take("/")) {
                ans = new GDivide(ans, parse3(it, vars));
            } else {
                break;
            }
        }
        return ans;
    }

    private GExpression parse3(CharIterator it, Variables vars) throws ParserException {
        if (it.take("abs")) {
            return new GAbs(parse3(it, vars));
        } else if (it.take("-")) {
            if (it.between('0', '9')) {
                it.remove();
                return new GIntConst(it.nextIInt());
            }
            return new GNegate(parse3(it, vars));
        }
        return parse4(it, vars);
    }

    private GExpression parse4(CharIterator it, Variables vars) throws ParserException {
        if (it.take("(")) {
            bal++;
            GExpression ans = parse0(it, vars);
            it.expect(")");
            bal--;
            return ans;
        }
        String name = it.nextWord();
        if (!name.isBlank()) {
            return new GVar(name);
        }
        return new GIntConst(it.nextIInt());
    }
}
