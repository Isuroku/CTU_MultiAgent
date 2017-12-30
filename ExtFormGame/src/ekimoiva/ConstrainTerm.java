package ekimoiva;

public class ConstrainTerm
{
    TwoNames _names;
    float _payoff;

    public ConstrainTerm(String full_name, boolean agent, int index, float payoff)
    {
        this(new TwoNames(full_name, agent, index), payoff);
    }

    public ConstrainTerm(TwoNames names, float payoff)
    {
        _names = names;
        _payoff = payoff;
    }

    public String toString(boolean inShortForm)
    {
        return String.format("%f * %s", _payoff, inShortForm ? _names.ShortName() : _names.FullName());
    }

    int GetIndex()
    {
        return _names.GetIndex();
    }

    float GetPayoff()
    {
        return _payoff;
    }
}
