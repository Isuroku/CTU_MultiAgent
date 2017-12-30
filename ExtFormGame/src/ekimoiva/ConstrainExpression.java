package ekimoiva;

import java.util.ArrayList;

public class ConstrainExpression
{
    TwoNames _names;
    private boolean _approach = false;
    private float _max_utility;

    ArrayList<ConstrainTerm> _terms = new ArrayList<>();

    public ConstrainExpression(String full_name, boolean agent, int index, boolean approach, float max_utility)
    {
        _names = new TwoNames(full_name, agent, index);
        _approach = approach;
        _max_utility = max_utility;
    }

    public float GetMaxUtility() { return _max_utility; }

    public boolean IsApproched() { return _approach; }

    public void AddTerm(ConstrainTerm term)
    {
        _terms.add(term);
    }

    public TwoNames Names()
    {
        return _names;
    }

    public String GetExpression(boolean short_l, boolean short_r)
    {
        StringBuilder sb = new StringBuilder(String.format("%s", short_l ? _names.ShortName() : _names.FullName()));

        boolean first = true;
        for(ConstrainTerm ct: _terms)
        {
            if(first)
            {
                sb.append(" : " + ct.toString(short_r));
                first = false;
            }
            else
                sb.append(" + " + ct.toString(short_r));
        }

        return sb.toString();
    }

    public float GetKoef(int inExpressionIndex)
    {
        for(ConstrainTerm t: _terms)
        {
            if(t.GetIndex() == inExpressionIndex)
                return t.GetPayoff();
        }
        return 0;
    }
}
