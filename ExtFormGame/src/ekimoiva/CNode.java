package ekimoiva;

import java.util.ArrayList;

public class CNode
{
    private Vector2D _pos;
    private CNode _parent;
    private ArrayList<CNode> _childs = new ArrayList<>();
    private String _strategy;
    private ENodeType _agent_type;
    private int _payoff = 0;
    private float _payoff_k;

    private Vector2D[] _bandit_set;

    private boolean _approach = false;

    public CNode(ENodeType agent_type, Vector2D pos, CNode parent, char step, float payoff_k)
    {
        _agent_type = agent_type;
        _pos = pos;
        _parent = parent;

        if(_strategy == null)
            _strategy = new String(new char[] {step});
        else
            _strategy += " " + step;

        _bandit_set = null;

        _payoff_k = payoff_k;
    }

    public CNode(ENodeType agent_type, Vector2D pos, CNode parent, char step)
    {
        this(agent_type, pos, parent, step, 1);
    }

    public CNode(ENodeType agent_type, Vector2D pos, CNode parent, String step)
    {
        this(agent_type, pos, parent, step, null);
    }

    public CNode(ENodeType agent_type, Vector2D pos, CNode parent, String step, boolean approach)
    {
        this(agent_type, pos, parent, step, null);
        _approach = approach;
    }

    public CNode(ENodeType agent_type, Vector2D pos, CNode parent, String step, Vector2D[] bandit_set)
    {
        _agent_type = agent_type;
        _pos = pos;
        _parent = parent;

        if(_strategy == null)
            _strategy = step;
        else
            _strategy += " " + step;

        _bandit_set = bandit_set;

        _payoff_k = 1;
    }

    @Override
    public String toString()
    {
        return String.format("%s: pos %s, _strategy %s", _agent_type, _pos, _strategy);
    }

    public boolean CellUsed(Vector2D pos)
    {
        if(pos.equals(_pos))
            return true;

        if(_parent != null)
            return _parent.CellUsed(pos);
        return false;
    }

    public Vector2D GetPos() { return _pos; }

    public void AddChild(CNode child)
    {
        _childs.add(child);
    }

    public void AddPayoff(int value)
    {
        _payoff = value;
    }

    public Vector2D[] GetBanditSet()
    {
        if(_bandit_set == null)
            return _parent.GetBanditSet();
        return _bandit_set;
    }

    public void PrintTree(int intend)
    {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < intend; i++)
            sb.append(' ');
        sb.append(toString());

        System.out.println(sb.toString());
        for(CNode n: _childs)
            n.PrintTree(intend + 4);
    }

    public void PrintPath(String prev_path)
    {
        if(_childs.isEmpty())
        {
            System.out.println(prev_path + _strategy);
            return;
        }

        for(CNode n: _childs)
            n.PrintPath(prev_path + _strategy);
    }

    public void CollectUtils(String prev_bandit, String prev_agent, float prev_koef, int prev_payoff, ArrayList<CStrategiesUtil> outUtils)
    {
        String bandit = prev_bandit;
        String agent = prev_agent;

        float koef = prev_koef * _payoff_k;
        int payoff = prev_payoff + _payoff;

        if(_agent_type == ENodeType.Agent)
            agent += _strategy;
        if(_agent_type == ENodeType.Bandit)
            bandit += " " + _strategy;

        if(_childs.isEmpty())
        {
            CStrategiesUtil u = new CStrategiesUtil(agent, bandit, payoff, koef, _approach);
            outUtils.add(u);
            return;
        }


        for(CNode n: _childs)
            n.CollectUtils(bandit, agent, koef, payoff, outUtils);
    }
}

