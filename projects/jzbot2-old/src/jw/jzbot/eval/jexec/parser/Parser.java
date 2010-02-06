/* This file was generated by SableCC (http://www.sablecc.org/). */

package jw.jzbot.eval.jexec.parser;

import jw.jzbot.eval.jexec.lexer.*;
import jw.jzbot.eval.jexec.node.*;
import jw.jzbot.eval.jexec.analysis.*;
import java.util.*;

import java.io.DataInputStream;
import java.io.BufferedInputStream;
import java.io.IOException;

@SuppressWarnings("nls")
public class Parser
{
    public final Analysis ignoredTokens = new AnalysisAdapter();

    protected ArrayList nodeList;

    private final Lexer lexer;
    private final ListIterator stack = new LinkedList().listIterator();
    private int last_pos;
    private int last_line;
    private Token last_token;
    private final TokenIndex converter = new TokenIndex();
    private final int[] action = new int[2];

    private final static int SHIFT = 0;
    private final static int REDUCE = 1;
    private final static int ACCEPT = 2;
    private final static int ERROR = 3;

    public Parser(@SuppressWarnings("hiding") Lexer lexer)
    {
        this.lexer = lexer;
    }

    protected void filter() throws ParserException, LexerException, IOException
    {
        // Empty body
    }

    private void push(int numstate, ArrayList listNode, boolean hidden) throws ParserException, LexerException, IOException
    {
        this.nodeList = listNode;

        if(!hidden)
        {
            filter();
        }

        if(!this.stack.hasNext())
        {
            this.stack.add(new State(numstate, this.nodeList));
            return;
        }

        State s = (State) this.stack.next();
        s.state = numstate;
        s.nodes = this.nodeList;
    }

    private int goTo(int index)
    {
        int state = state();
        int low = 1;
        int high = gotoTable[index].length - 1;
        int value = gotoTable[index][0][1];

        while(low <= high)
        {
            int middle = (low + high) / 2;

            if(state < gotoTable[index][middle][0])
            {
                high = middle - 1;
            }
            else if(state > gotoTable[index][middle][0])
            {
                low = middle + 1;
            }
            else
            {
                value = gotoTable[index][middle][1];
                break;
            }
        }

        return value;
    }

    private int state()
    {
        State s = (State) this.stack.previous();
        this.stack.next();
        return s.state;
    }

    private ArrayList pop()
    {
        return ((State) this.stack.previous()).nodes;
    }

    private int index(Switchable token)
    {
        this.converter.index = -1;
        token.apply(this.converter);
        return this.converter.index;
    }

    @SuppressWarnings("unchecked")
    public Start parse() throws ParserException, LexerException, IOException
    {
        push(0, null, true);
        List<Node> ign = null;
        while(true)
        {
            while(index(this.lexer.peek()) == -1)
            {
                if(ign == null)
                {
                    ign = new LinkedList<Node>();
                }

                ign.add(this.lexer.next());
            }

            if(ign != null)
            {
                this.ignoredTokens.setIn(this.lexer.peek(), ign);
                ign = null;
            }

            this.last_pos = this.lexer.peek().getPos();
            this.last_line = this.lexer.peek().getLine();
            this.last_token = this.lexer.peek();

            int index = index(this.lexer.peek());
            this.action[0] = Parser.actionTable[state()][0][1];
            this.action[1] = Parser.actionTable[state()][0][2];

            int low = 1;
            int high = Parser.actionTable[state()].length - 1;

            while(low <= high)
            {
                int middle = (low + high) / 2;

                if(index < Parser.actionTable[state()][middle][0])
                {
                    high = middle - 1;
                }
                else if(index > Parser.actionTable[state()][middle][0])
                {
                    low = middle + 1;
                }
                else
                {
                    this.action[0] = Parser.actionTable[state()][middle][1];
                    this.action[1] = Parser.actionTable[state()][middle][2];
                    break;
                }
            }

            switch(this.action[0])
            {
                case SHIFT:
		    {
		        ArrayList list = new ArrayList();
		        list.add(this.lexer.next());
                        push(this.action[1], list, false);
                    }
		    break;
                case REDUCE:
                    switch(this.action[1])
                    {
                    case 0: /* reduce AExpr */
		    {
			ArrayList list = new0();
			push(goTo(0), list, false);
		    }
		    break;
                    case 1: /* reduce ANextAddp */
		    {
			ArrayList list = new1();
			push(goTo(1), list, false);
		    }
		    break;
                    case 2: /* reduce AInAddp */
		    {
			ArrayList list = new2();
			push(goTo(1), list, false);
		    }
		    break;
                    case 3: /* reduce ANextSubp */
		    {
			ArrayList list = new3();
			push(goTo(2), list, false);
		    }
		    break;
                    case 4: /* reduce AInSubp */
		    {
			ArrayList list = new4();
			push(goTo(2), list, false);
		    }
		    break;
                    case 5: /* reduce ANextMulp */
		    {
			ArrayList list = new5();
			push(goTo(3), list, false);
		    }
		    break;
                    case 6: /* reduce AInMulp */
		    {
			ArrayList list = new6();
			push(goTo(3), list, false);
		    }
		    break;
                    case 7: /* reduce ANextDivp */
		    {
			ArrayList list = new7();
			push(goTo(4), list, false);
		    }
		    break;
                    case 8: /* reduce AInDivp */
		    {
			ArrayList list = new8();
			push(goTo(4), list, false);
		    }
		    break;
                    case 9: /* reduce ANextUnmp */
		    {
			ArrayList list = new9();
			push(goTo(5), list, false);
		    }
		    break;
                    case 10: /* reduce AInUnmp */
		    {
			ArrayList list = new10();
			push(goTo(5), list, false);
		    }
		    break;
                    case 11: /* reduce ANextNmep */
		    {
			ArrayList list = new11();
			push(goTo(6), list, false);
		    }
		    break;
                    case 12: /* reduce APreNmep */
		    {
			ArrayList list = new12();
			push(goTo(6), list, false);
		    }
		    break;
                    case 13: /* reduce AInNmep */
		    {
			ArrayList list = new13();
			push(goTo(6), list, false);
		    }
		    break;
                    case 14: /* reduce AVarNmep */
		    {
			ArrayList list = new14();
			push(goTo(6), list, false);
		    }
		    break;
                    case 15: /* reduce APostNmep */
		    {
			ArrayList list = new15();
			push(goTo(6), list, false);
		    }
		    break;
                    case 16: /* reduce ANumberTerm */
		    {
			ArrayList list = new16();
			push(goTo(7), list, false);
		    }
		    break;
                    case 17: /* reduce AParensTerm */
		    {
			ArrayList list = new17();
			push(goTo(7), list, false);
		    }
		    break;
                    }
                    break;
                case ACCEPT:
                    {
                        EOF node2 = (EOF) this.lexer.next();
                        PExpr node1 = (PExpr) pop().get(0);
                        Start node = new Start(node1, node2);
                        return node;
                    }
                case ERROR:
                    throw new ParserException(this.last_token,
                        "[" + this.last_line + "," + this.last_pos + "] " +
                        Parser.errorMessages[Parser.errors[this.action[1]]]);
            }
        }
    }



    @SuppressWarnings("unchecked")
    ArrayList new0() /* reduce AExpr */
    {
        @SuppressWarnings("hiding") ArrayList nodeList = new ArrayList();

        @SuppressWarnings("unused") ArrayList nodeArrayList1 = pop();
        PExpr pexprNode1;
        {
            // Block
        PAddp paddpNode2;
        paddpNode2 = (PAddp)nodeArrayList1.get(0);

        pexprNode1 = new AExpr(paddpNode2);
        }
	nodeList.add(pexprNode1);
        return nodeList;
    }



    @SuppressWarnings("unchecked")
    ArrayList new1() /* reduce ANextAddp */
    {
        @SuppressWarnings("hiding") ArrayList nodeList = new ArrayList();

        @SuppressWarnings("unused") ArrayList nodeArrayList1 = pop();
        PAddp paddpNode1;
        {
            // Block
        PSubp psubpNode2;
        psubpNode2 = (PSubp)nodeArrayList1.get(0);

        paddpNode1 = new ANextAddp(psubpNode2);
        }
	nodeList.add(paddpNode1);
        return nodeList;
    }



    @SuppressWarnings("unchecked")
    ArrayList new2() /* reduce AInAddp */
    {
        @SuppressWarnings("hiding") ArrayList nodeList = new ArrayList();

        @SuppressWarnings("unused") ArrayList nodeArrayList3 = pop();
        @SuppressWarnings("unused") ArrayList nodeArrayList2 = pop();
        @SuppressWarnings("unused") ArrayList nodeArrayList1 = pop();
        PAddp paddpNode1;
        {
            // Block
        PAddp paddpNode2;
        TPlus tplusNode3;
        PSubp psubpNode4;
        paddpNode2 = (PAddp)nodeArrayList1.get(0);
        tplusNode3 = (TPlus)nodeArrayList2.get(0);
        psubpNode4 = (PSubp)nodeArrayList3.get(0);

        paddpNode1 = new AInAddp(paddpNode2, tplusNode3, psubpNode4);
        }
	nodeList.add(paddpNode1);
        return nodeList;
    }



    @SuppressWarnings("unchecked")
    ArrayList new3() /* reduce ANextSubp */
    {
        @SuppressWarnings("hiding") ArrayList nodeList = new ArrayList();

        @SuppressWarnings("unused") ArrayList nodeArrayList1 = pop();
        PSubp psubpNode1;
        {
            // Block
        PMulp pmulpNode2;
        pmulpNode2 = (PMulp)nodeArrayList1.get(0);

        psubpNode1 = new ANextSubp(pmulpNode2);
        }
	nodeList.add(psubpNode1);
        return nodeList;
    }



    @SuppressWarnings("unchecked")
    ArrayList new4() /* reduce AInSubp */
    {
        @SuppressWarnings("hiding") ArrayList nodeList = new ArrayList();

        @SuppressWarnings("unused") ArrayList nodeArrayList3 = pop();
        @SuppressWarnings("unused") ArrayList nodeArrayList2 = pop();
        @SuppressWarnings("unused") ArrayList nodeArrayList1 = pop();
        PSubp psubpNode1;
        {
            // Block
        PSubp psubpNode2;
        TMinus tminusNode3;
        PMulp pmulpNode4;
        psubpNode2 = (PSubp)nodeArrayList1.get(0);
        tminusNode3 = (TMinus)nodeArrayList2.get(0);
        pmulpNode4 = (PMulp)nodeArrayList3.get(0);

        psubpNode1 = new AInSubp(psubpNode2, tminusNode3, pmulpNode4);
        }
	nodeList.add(psubpNode1);
        return nodeList;
    }



    @SuppressWarnings("unchecked")
    ArrayList new5() /* reduce ANextMulp */
    {
        @SuppressWarnings("hiding") ArrayList nodeList = new ArrayList();

        @SuppressWarnings("unused") ArrayList nodeArrayList1 = pop();
        PMulp pmulpNode1;
        {
            // Block
        PDivp pdivpNode2;
        pdivpNode2 = (PDivp)nodeArrayList1.get(0);

        pmulpNode1 = new ANextMulp(pdivpNode2);
        }
	nodeList.add(pmulpNode1);
        return nodeList;
    }



    @SuppressWarnings("unchecked")
    ArrayList new6() /* reduce AInMulp */
    {
        @SuppressWarnings("hiding") ArrayList nodeList = new ArrayList();

        @SuppressWarnings("unused") ArrayList nodeArrayList3 = pop();
        @SuppressWarnings("unused") ArrayList nodeArrayList2 = pop();
        @SuppressWarnings("unused") ArrayList nodeArrayList1 = pop();
        PMulp pmulpNode1;
        {
            // Block
        PMulp pmulpNode2;
        TMult tmultNode3;
        PDivp pdivpNode4;
        pmulpNode2 = (PMulp)nodeArrayList1.get(0);
        tmultNode3 = (TMult)nodeArrayList2.get(0);
        pdivpNode4 = (PDivp)nodeArrayList3.get(0);

        pmulpNode1 = new AInMulp(pmulpNode2, tmultNode3, pdivpNode4);
        }
	nodeList.add(pmulpNode1);
        return nodeList;
    }



    @SuppressWarnings("unchecked")
    ArrayList new7() /* reduce ANextDivp */
    {
        @SuppressWarnings("hiding") ArrayList nodeList = new ArrayList();

        @SuppressWarnings("unused") ArrayList nodeArrayList1 = pop();
        PDivp pdivpNode1;
        {
            // Block
        PUnmp punmpNode2;
        punmpNode2 = (PUnmp)nodeArrayList1.get(0);

        pdivpNode1 = new ANextDivp(punmpNode2);
        }
	nodeList.add(pdivpNode1);
        return nodeList;
    }



    @SuppressWarnings("unchecked")
    ArrayList new8() /* reduce AInDivp */
    {
        @SuppressWarnings("hiding") ArrayList nodeList = new ArrayList();

        @SuppressWarnings("unused") ArrayList nodeArrayList3 = pop();
        @SuppressWarnings("unused") ArrayList nodeArrayList2 = pop();
        @SuppressWarnings("unused") ArrayList nodeArrayList1 = pop();
        PDivp pdivpNode1;
        {
            // Block
        PDivp pdivpNode2;
        TDiv tdivNode3;
        PUnmp punmpNode4;
        pdivpNode2 = (PDivp)nodeArrayList1.get(0);
        tdivNode3 = (TDiv)nodeArrayList2.get(0);
        punmpNode4 = (PUnmp)nodeArrayList3.get(0);

        pdivpNode1 = new AInDivp(pdivpNode2, tdivNode3, punmpNode4);
        }
	nodeList.add(pdivpNode1);
        return nodeList;
    }



    @SuppressWarnings("unchecked")
    ArrayList new9() /* reduce ANextUnmp */
    {
        @SuppressWarnings("hiding") ArrayList nodeList = new ArrayList();

        @SuppressWarnings("unused") ArrayList nodeArrayList1 = pop();
        PUnmp punmpNode1;
        {
            // Block
        PNmep pnmepNode2;
        pnmepNode2 = (PNmep)nodeArrayList1.get(0);

        punmpNode1 = new ANextUnmp(pnmepNode2);
        }
	nodeList.add(punmpNode1);
        return nodeList;
    }



    @SuppressWarnings("unchecked")
    ArrayList new10() /* reduce AInUnmp */
    {
        @SuppressWarnings("hiding") ArrayList nodeList = new ArrayList();

        @SuppressWarnings("unused") ArrayList nodeArrayList2 = pop();
        @SuppressWarnings("unused") ArrayList nodeArrayList1 = pop();
        PUnmp punmpNode1;
        {
            // Block
        TMinus tminusNode2;
        PNmep pnmepNode3;
        tminusNode2 = (TMinus)nodeArrayList1.get(0);
        pnmepNode3 = (PNmep)nodeArrayList2.get(0);

        punmpNode1 = new AInUnmp(tminusNode2, pnmepNode3);
        }
	nodeList.add(punmpNode1);
        return nodeList;
    }



    @SuppressWarnings("unchecked")
    ArrayList new11() /* reduce ANextNmep */
    {
        @SuppressWarnings("hiding") ArrayList nodeList = new ArrayList();

        @SuppressWarnings("unused") ArrayList nodeArrayList1 = pop();
        PNmep pnmepNode1;
        {
            // Block
        PTerm ptermNode2;
        ptermNode2 = (PTerm)nodeArrayList1.get(0);

        pnmepNode1 = new ANextNmep(ptermNode2);
        }
	nodeList.add(pnmepNode1);
        return nodeList;
    }



    @SuppressWarnings("unchecked")
    ArrayList new12() /* reduce APreNmep */
    {
        @SuppressWarnings("hiding") ArrayList nodeList = new ArrayList();

        @SuppressWarnings("unused") ArrayList nodeArrayList2 = pop();
        @SuppressWarnings("unused") ArrayList nodeArrayList1 = pop();
        PNmep pnmepNode1;
        {
            // Block
        TName tnameNode2;
        PTerm ptermNode3;
        tnameNode2 = (TName)nodeArrayList1.get(0);
        ptermNode3 = (PTerm)nodeArrayList2.get(0);

        pnmepNode1 = new APreNmep(tnameNode2, ptermNode3);
        }
	nodeList.add(pnmepNode1);
        return nodeList;
    }



    @SuppressWarnings("unchecked")
    ArrayList new13() /* reduce AInNmep */
    {
        @SuppressWarnings("hiding") ArrayList nodeList = new ArrayList();

        @SuppressWarnings("unused") ArrayList nodeArrayList3 = pop();
        @SuppressWarnings("unused") ArrayList nodeArrayList2 = pop();
        @SuppressWarnings("unused") ArrayList nodeArrayList1 = pop();
        PNmep pnmepNode1;
        {
            // Block
        PNmep pnmepNode2;
        TName tnameNode3;
        PTerm ptermNode4;
        pnmepNode2 = (PNmep)nodeArrayList1.get(0);
        tnameNode3 = (TName)nodeArrayList2.get(0);
        ptermNode4 = (PTerm)nodeArrayList3.get(0);

        pnmepNode1 = new AInNmep(pnmepNode2, tnameNode3, ptermNode4);
        }
	nodeList.add(pnmepNode1);
        return nodeList;
    }



    @SuppressWarnings("unchecked")
    ArrayList new14() /* reduce AVarNmep */
    {
        @SuppressWarnings("hiding") ArrayList nodeList = new ArrayList();

        @SuppressWarnings("unused") ArrayList nodeArrayList1 = pop();
        PNmep pnmepNode1;
        {
            // Block
        TName tnameNode2;
        tnameNode2 = (TName)nodeArrayList1.get(0);

        pnmepNode1 = new AVarNmep(tnameNode2);
        }
	nodeList.add(pnmepNode1);
        return nodeList;
    }



    @SuppressWarnings("unchecked")
    ArrayList new15() /* reduce APostNmep */
    {
        @SuppressWarnings("hiding") ArrayList nodeList = new ArrayList();

        @SuppressWarnings("unused") ArrayList nodeArrayList2 = pop();
        @SuppressWarnings("unused") ArrayList nodeArrayList1 = pop();
        PNmep pnmepNode1;
        {
            // Block
        PNmep pnmepNode2;
        TName tnameNode3;
        pnmepNode2 = (PNmep)nodeArrayList1.get(0);
        tnameNode3 = (TName)nodeArrayList2.get(0);

        pnmepNode1 = new APostNmep(pnmepNode2, tnameNode3);
        }
	nodeList.add(pnmepNode1);
        return nodeList;
    }



    @SuppressWarnings("unchecked")
    ArrayList new16() /* reduce ANumberTerm */
    {
        @SuppressWarnings("hiding") ArrayList nodeList = new ArrayList();

        @SuppressWarnings("unused") ArrayList nodeArrayList1 = pop();
        PTerm ptermNode1;
        {
            // Block
        TNumber tnumberNode2;
        tnumberNode2 = (TNumber)nodeArrayList1.get(0);

        ptermNode1 = new ANumberTerm(tnumberNode2);
        }
	nodeList.add(ptermNode1);
        return nodeList;
    }



    @SuppressWarnings("unchecked")
    ArrayList new17() /* reduce AParensTerm */
    {
        @SuppressWarnings("hiding") ArrayList nodeList = new ArrayList();

        @SuppressWarnings("unused") ArrayList nodeArrayList3 = pop();
        @SuppressWarnings("unused") ArrayList nodeArrayList2 = pop();
        @SuppressWarnings("unused") ArrayList nodeArrayList1 = pop();
        PTerm ptermNode1;
        {
            // Block
        TLPar tlparNode2;
        PExpr pexprNode3;
        TRPar trparNode4;
        tlparNode2 = (TLPar)nodeArrayList1.get(0);
        pexprNode3 = (PExpr)nodeArrayList2.get(0);
        trparNode4 = (TRPar)nodeArrayList3.get(0);

        ptermNode1 = new AParensTerm(tlparNode2, pexprNode3, trparNode4);
        }
	nodeList.add(ptermNode1);
        return nodeList;
    }



    private static int[][][] actionTable;
/*      {
			{{-1, ERROR, 0}, {0, SHIFT, 1}, {2, SHIFT, 2}, {6, SHIFT, 3}, {8, SHIFT, 4}, },
			{{-1, REDUCE, 16}, },
			{{-1, ERROR, 2}, {0, SHIFT, 1}, {6, SHIFT, 3}, {8, SHIFT, 4}, },
			{{-1, ERROR, 3}, {0, SHIFT, 1}, {2, SHIFT, 2}, {6, SHIFT, 3}, {8, SHIFT, 4}, },
			{{-1, REDUCE, 14}, {0, SHIFT, 1}, {6, SHIFT, 3}, },
			{{-1, ERROR, 5}, {9, ACCEPT, -1}, },
			{{-1, REDUCE, 0}, {1, SHIFT, 16}, },
			{{-1, REDUCE, 1}, {2, SHIFT, 17}, },
			{{-1, REDUCE, 3}, {3, SHIFT, 18}, },
			{{-1, REDUCE, 5}, {4, SHIFT, 19}, },
			{{-1, REDUCE, 7}, },
			{{-1, REDUCE, 9}, {8, SHIFT, 20}, },
			{{-1, REDUCE, 11}, },
			{{-1, REDUCE, 10}, {8, SHIFT, 20}, },
			{{-1, ERROR, 14}, {7, SHIFT, 21}, },
			{{-1, REDUCE, 12}, },
			{{-1, ERROR, 16}, {0, SHIFT, 1}, {2, SHIFT, 2}, {6, SHIFT, 3}, {8, SHIFT, 4}, },
			{{-1, ERROR, 17}, {0, SHIFT, 1}, {2, SHIFT, 2}, {6, SHIFT, 3}, {8, SHIFT, 4}, },
			{{-1, ERROR, 18}, {0, SHIFT, 1}, {2, SHIFT, 2}, {6, SHIFT, 3}, {8, SHIFT, 4}, },
			{{-1, ERROR, 19}, {0, SHIFT, 1}, {2, SHIFT, 2}, {6, SHIFT, 3}, {8, SHIFT, 4}, },
			{{-1, REDUCE, 15}, {0, SHIFT, 1}, {6, SHIFT, 3}, },
			{{-1, REDUCE, 17}, },
			{{-1, REDUCE, 2}, {2, SHIFT, 17}, },
			{{-1, REDUCE, 4}, {3, SHIFT, 18}, },
			{{-1, REDUCE, 6}, {4, SHIFT, 19}, },
			{{-1, REDUCE, 8}, },
			{{-1, REDUCE, 13}, },
        };*/
    private static int[][][] gotoTable;
/*      {
			{{-1, 5}, {3, 14}, },
			{{-1, 6}, },
			{{-1, 7}, {16, 22}, },
			{{-1, 8}, {17, 23}, },
			{{-1, 9}, {18, 24}, },
			{{-1, 10}, {19, 25}, },
			{{-1, 11}, {2, 13}, },
			{{-1, 12}, {4, 15}, {20, 26}, },
        };*/
    private static String[] errorMessages;
/*      {
			"expecting: number, '-', '(', name",
			"expecting: '+', '-', '*', '/', ')', name, EOF",
			"expecting: number, '(', name",
			"expecting: number, '+', '-', '*', '/', '(', ')', name, EOF",
			"expecting: EOF",
			"expecting: '+', ')', EOF",
			"expecting: '+', '-', ')', EOF",
			"expecting: '+', '-', '*', ')', EOF",
			"expecting: '+', '-', '*', '/', ')', EOF",
			"expecting: ')'",
        };*/
    private static int[] errors;
/*      {
			0, 1, 2, 0, 3, 4, 5, 6, 7, 8, 8, 1, 1, 1, 9, 1, 0, 0, 0, 0, 3, 1, 6, 7, 8, 8, 1, 
        };*/

    static 
    {
        try
        {
            DataInputStream s = new DataInputStream(
                new BufferedInputStream(
                Parser.class.getResourceAsStream("parser.dat")));

            // read actionTable
            int length = s.readInt();
            Parser.actionTable = new int[length][][];
            for(int i = 0; i < Parser.actionTable.length; i++)
            {
                length = s.readInt();
                Parser.actionTable[i] = new int[length][3];
                for(int j = 0; j < Parser.actionTable[i].length; j++)
                {
                for(int k = 0; k < 3; k++)
                {
                    Parser.actionTable[i][j][k] = s.readInt();
                }
                }
            }

            // read gotoTable
            length = s.readInt();
            gotoTable = new int[length][][];
            for(int i = 0; i < gotoTable.length; i++)
            {
                length = s.readInt();
                gotoTable[i] = new int[length][2];
                for(int j = 0; j < gotoTable[i].length; j++)
                {
                for(int k = 0; k < 2; k++)
                {
                    gotoTable[i][j][k] = s.readInt();
                }
                }
            }

            // read errorMessages
            length = s.readInt();
            errorMessages = new String[length];
            for(int i = 0; i < errorMessages.length; i++)
            {
                length = s.readInt();
                StringBuffer buffer = new StringBuffer();

                for(int j = 0; j < length; j++)
                {
                buffer.append(s.readChar());
                }
                errorMessages[i] = buffer.toString();
            }

            // read errors
            length = s.readInt();
            errors = new int[length];
            for(int i = 0; i < errors.length; i++)
            {
                errors[i] = s.readInt();
            }

            s.close();
        }
        catch(Exception e)
        {
            throw new RuntimeException("The file \"parser.dat\" is either missing or corrupted.");
        }
    }
}
