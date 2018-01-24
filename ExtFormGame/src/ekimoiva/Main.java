package ekimoiva;

import ekimoiva.Map.CMap;
import ekimoiva.Map.CMapCell;

import ilog.concert.*;
import ilog.cplex.IloCplex;

import javafx.util.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main
{
    static ArrayList<String> _ex1 = CreateExample1();
    static ArrayList<String> _ex2 = CreateExample2();
    static ArrayList<String> _ex3 = CreateExample3();
    static int _line_count = 0;

    static ArrayList<String> CreateExample1()
    {
        ArrayList<String> lst = new ArrayList<>();
        lst.add("7");
        lst.add("9");
        lst.add("#########");
        lst.add("#G-----E#");
        lst.add("#-#####-#");
        lst.add("#S--E--D#");
        lst.add("#-#####-#");
        lst.add("#G-----E#");
        lst.add("#########");
        lst.add("2");
        lst.add("0.5");
        return lst;
    }

    static ArrayList<String> CreateExample2()
    {
        ArrayList<String> lst = new ArrayList<>();
        lst.add("7");
        lst.add("9");
        lst.add("#########");
        lst.add("#G-E---E#");
        lst.add("#-#####-#");
        lst.add("#S-E--ED#");
        lst.add("#-#####-#");
        lst.add("#G-E---E#");
        lst.add("#########");
        lst.add("2");
        lst.add("0.6");
        return lst;
    }

    static ArrayList<String> CreateExample3()
    {
        ArrayList<String> lst = new ArrayList<>();
        lst.add("7");
        lst.add("9");
        lst.add("#########");
        lst.add("#E----EG#");
        lst.add("#-##-##E#");
        lst.add("#S##-##-#");
        lst.add("#-##-##-#");
        lst.add("#E-----D#");
        lst.add("#########");
        lst.add("2");
        lst.add("0.5");
        return lst;
    }

    static String ReadLineFromExample1()
    {
        if(_line_count >= _ex1.size())
            return "";

        String s = _ex1.get(_line_count);
        _line_count++;
        return s;
    }

    static String ReadLineFromExample2()
    {
        if(_line_count >= _ex2.size())
            return "";

        String s = _ex2.get(_line_count);
        _line_count++;
        return s;
    }

    static String ReadLineFromExample3()
    {
        if(_line_count >= _ex3.size())
            return "";

        String s = _ex3.get(_line_count);
        _line_count++;
        return s;
    }

    static String ReadLineFromStd() throws IOException
    {
        StringBuilder sb = new StringBuilder();

        int ch = System.in.read();

        while (ch != -1 && ch != '\n')
        {
            sb.append((char)ch);
            ch = System.in.read();
        }

        return sb.toString();
    }

    static String[] _ex_file;
    private static String ReadAll(String filePath)
    {
        String content = "";
        try
        {
            content = new String ( Files.readAllBytes( Paths.get(filePath) ) );
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return content;
    }

    private static String ReadLineFromFile(String filePath)
    {
        if(_ex_file == null)
        {
            String txt = ReadAll(filePath);
            _ex_file = txt.split(System.lineSeparator());
        }

        if(_line_count >= _ex_file.length)
            return "";

        String s = _ex_file[_line_count];
        _line_count++;
        return s;
    }

    static String ReadLine(String[] args) throws IOException
    {
        String line;
        if(args.length > 0 && "1".equals(args[0]))
            line = ReadLineFromExample1();
        else if(args.length > 0 && "2".equals(args[0]))
            line = ReadLineFromExample2();
        else if(args.length > 0 && "3".equals(args[0]))
            line = ReadLineFromExample3();
        else
            line = ReadLineFromFile(args[0]);
        return line;
    }

    public static void main(String[] args) throws IOException, IloException
    {
        //SolveTest();

        String line = ReadLineFromFile(args[0]);

        int line_count = -2;

        int map_h = 0;
        int map_w = 0;

        CMap map = null;

        int bandit_count = 0;
        float kill_probability = 0;
        Vector2D start_pos = null;

        ArrayList<Vector2D> dangerous_places = new ArrayList<>();

        while(!line.isEmpty())
        {
            System.out.printf("line: %s\n", line);

            if(line_count == -2)
                map_h = Integer.parseInt(line);
            else if(line_count == -1)
            {
                map_w = Integer.parseInt(line);
                map = new CMap(map_w, map_h);
            }
            else if(line_count < map_h)
            {
                char[] chars = line.toCharArray();
                for(int i = 0; i < chars.length; i++)
                {
                    char t = chars[i];
                    if(t == 'S')
                        start_pos = new Vector2D(i, line_count);
                    else
                    {
                        map.SetCellType(i, line_count, t);
                        if(t == 'E')
                            dangerous_places.add(new Vector2D(i, line_count));
                    }
                }
            }
            else if(line_count < map_h + 1)
                bandit_count = Integer.parseInt(line);
            else if(line_count < map_h + 2)
                kill_probability = Float.parseFloat(line);

            line = ReadLine(args);
            line_count++;
        }

        System.out.println();
        System.out.println("Search a utility of a strategy combination.");

        ArrayList<CNode> game_nodes = new ArrayList<>();

        HashMap constrains_agent = new HashMap<String, ConstrainExpression>();
        HashMap names_bandit = new HashMap<String, TwoNames>();

        PlaceGen gen = new PlaceGen(dangerous_places.size(), bandit_count);
        Integer[] places = gen.NextGen();
        while(places != null)
        {
            Vector2D[] bandit_set = new Vector2D[places.length];

            StringBuilder sb = new StringBuilder("set(");

            for(int i = 0; i < places.length; i++)
            {
                Vector2D pos = dangerous_places.get(places[i]);
                bandit_set[i] = pos;
                if(i == 0)
                    sb.append(pos);
                else
                    sb.append(", " + pos);
            }
            sb.append(")");

            CNode bn = new CNode(ENodeType.Bandit, start_pos, null, sb.toString(), bandit_set);
            game_nodes.add(bn);
            CreateTree(bn, map, kill_probability, dangerous_places);

            //bn.PrintPath("");
            ArrayList<CStrategiesUtil> utils = new ArrayList<>();
            bn.CollectUtils("", "", 1, 0, utils);

            for(CStrategiesUtil u: utils )
            {
                String as = u.GetAgentStrategy();
                ConstrainExpression agent_expr = (ConstrainExpression) constrains_agent.get(as);
                if(agent_expr == null)
                {
                    agent_expr = new ConstrainExpression(as, true, constrains_agent.size(), u.IsApproched(), u.GetMaxUtility());
                    constrains_agent.put(as, agent_expr);
                }

                String bs = u.GetBanditStrategy();
                TwoNames bandit_names = (TwoNames) names_bandit.get(bs);
                if(bandit_names == null)
                {
                    bandit_names = new TwoNames(bs, false, names_bandit.size());
                    names_bandit.put(bs, bandit_names);
                }

                agent_expr.AddTerm(new ConstrainTerm(bandit_names, u.GetUtility()));

                System.out.println(u);
            }
            System.out.println();

            places = gen.NextGen();
        }

        System.out.println("Short names for Bandit strategies:");
        for(Object entry: names_bandit.entrySet())
        {
            HashMap.Entry<String, TwoNames> e = (HashMap.Entry<String, TwoNames>)entry;
            TwoNames value = e.getValue();
            System.out.println(value.ShortName() + ": " + value.FullName());
        }

        System.out.println();
        System.out.println("Agent strategies:");
        ArrayList<ConstrainExpression> expressions = new ArrayList<>();
        for (Object entry: constrains_agent.entrySet())
        {
            HashMap.Entry<String, ConstrainExpression> e = (HashMap.Entry<String, ConstrainExpression>)entry;

            ConstrainExpression value = e.getValue();
            if(value.IsApproched())
            {
                expressions.add(value);

                //System.out.println(value.Names().ShortName() + ": " + value.Names().FullName());
                System.out.println(value.GetExpression(false, true));
            }
        }

        int koef_count = names_bandit.size();

        double[] koefs = new double[koef_count];
        for(int k = 0; k < koef_count; k++)
        {
            koefs[k] = 1;
        }

        double[] constrains_results = new double[expressions.size()];
        double[][] constrains_koef = new double[expressions.size()][koef_count];
        for(int e = 0; e < expressions.size(); e++)
        {
            ConstrainExpression expr = expressions.get(e);
            for(int k = 0; k < koef_count; k++)
            {
                constrains_koef[e][k] = expr.GetKoef(k);
            }

            constrains_results[e] = 1;
        }

        solveModel(koefs, true, constrains_koef, constrains_results);
    }

    static void CreateTree(CNode root, CMap map, float kill_probability, ArrayList<Vector2D> dangerous_places) throws IloException
    {
        Stack<CNode> node_stack = new Stack<>();
        node_stack.push(root);

        boolean need_jump = true;

        while(!node_stack.empty())
        {
            CNode node = node_stack.pop();

            Vector2D pos = node.GetPos();

            Pair<Character, Vector2D>[] nposes = pos.GetNeighbours(map.GetMapRect());
            for(Pair<Character, Vector2D> p: nposes)
            {
                if(!node.CellUsed(p.getValue()))
                {
                    CMapCell cell = map.GetCell(p.getValue());
                    if(cell.IsPassable())
                    {
                        switch(cell.GetCellType())
                        {
                            case Exit:
                            {
                                CNode child = new CNode(ENodeType.Agent, p.getValue(), node, p.getKey() + "E", true);
                                child.AddPayoff(10);
                                node.AddChild(child);
                            }
                            break;
                            case Danger:
                            {
                                CNode child = new CNode(ENodeType.Agent, p.getValue(), node, p.getKey());
                                node.AddChild(child);

                                Vector2D[] bandit_set = node.GetBanditSet();

                                if(IsContains(p.getValue(), bandit_set))
                                {
                                    CNode killed = new CNode(ENodeType.Nature, p.getValue(), child, 'K', kill_probability);
                                    killed.AddPayoff(0);
                                    child.AddChild(killed);

                                    CNode missed = new CNode(ENodeType.Nature, p.getValue(), child, 'M', 1 - kill_probability);
                                    child.AddChild(missed);
                                    node_stack.push(missed);
                                }
                                else if(need_jump)
                                {
                                    CNode no_jump = new CNode(ENodeType.Bandit, p.getValue(), child, "no jump");
                                    child.AddChild(no_jump);
                                    node_stack.push(no_jump);

                                    for(int b = 0; b < bandit_set.length; b++)
                                        for(int pl = 0; pl < dangerous_places.size(); pl++)
                                        {
                                            Vector2D v = dangerous_places.get(pl);

                                            if(!p.getValue().equals(v) && !IsContains(v, bandit_set))
                                            {
                                                Vector2D[] new_bandit_set = Arrays.copyOf(bandit_set, bandit_set.length);
                                                new_bandit_set[b] = v;

                                                String s = String.format("jump(%s-%s)", bandit_set[b], v);
                                                CNode jump = new CNode(ENodeType.Bandit, p.getValue(), child, s, new_bandit_set);
                                                child.AddChild(jump);
                                                node_stack.push(jump);
                                            }
                                        }
                                }
                                else
                                {
                                    node_stack.push(child);
                                }

                                need_jump = false;
                            }
                            break;
                            case Gold:
                            {
                                CNode child = new CNode(ENodeType.Agent, p.getValue(), node, p.getKey() + "(g)");
                                child.AddPayoff(1);
                                node.AddChild(child);
                                node_stack.push(child);
                            }
                            break;
                            case Empty:
                            {
                                CNode child = new CNode(ENodeType.Agent, p.getValue(), node, p.getKey());
                                node.AddChild(child);
                                node_stack.push(child);
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    static boolean IsContains(Vector2D pos, Vector2D[] places)
    {
        for(int b2 = 0; b2 < places.length; b2++)
            if(places[b2].equals(pos))
                return true;
        return false;
    }

    static void SolveTest()
    {
        //https://www.youtube.com/watch?v=oA86HCkCg5k
        double[] koefs = {41, 35, 96};
        double[][] constrains_koef = {{2, 3, 7}, {1, 1, 0}, {5, 3, 0}, {0.6, 0.25, 1}};
        double[] constrains_results = {1250, 250, 900, 232.5};


        solveModel(koefs, true, constrains_koef, constrains_results);
    }

    public static void solveModel(double[] koefs, boolean x_non_negative, double[][] constrains_koef, double[] constrains_results)
    {
        try
        {
            IloCplex model = new IloCplex();

            IloNumVar[] x = new IloNumVar[koefs.length];

            if(x_non_negative)
            {
                for(int i = 0; i < koefs.length; ++i)
                    x[i] = model.numVar(0, Double.MAX_VALUE);
            }

            IloLinearNumExpr obj = model.linearNumExpr();
            for(int i = 0; i < koefs.length; ++i)
                obj.addTerm(koefs[i], x[i]);

            //model.addMinimize(obj);
            model.addMaximize(obj);

            for(int i =0; i < constrains_koef.length; i++)
            {
                IloLinearNumExpr constrain = model.linearNumExpr();
                for(int j = 0; j < koefs.length; ++j)
                    constrain.addTerm(constrains_koef[i][j], x[j]);
                model.addGe(constrain, constrains_results[i]);
                //model.addLe(constrain, constrains_results[i]);
            }

            boolean solved = model.solve();
            if(solved)
            {
                double obj_value = model.getObjValue();
                double z = 1 / obj_value;
                System.out.println(String.format("Game Result = %f ", z));

                for(int i = 0; i < x.length; ++i)
                {
                    double mv = model.getValue(x[i]);
                    double p = mv * z;
                    System.out.println(String.format("Bandit strategy probability [%d] = %f", i, p));
                }
            }
            else
            {
                System.out.println("Not solved.");
            }
        }
        catch(IloException ex)
        {
            ex.printStackTrace();
        }
    }
}
