package ekimoiva;

public class TwoNames
{
    String _full_name;
    boolean _agent;
    int _index;

    public TwoNames(String full_name, boolean agent, int index)
    {
        _full_name = full_name;
        _agent = agent;
        _index = index;
    }

    public String FullName()
    {
        return _full_name;
    }

    public String ShortName()
    {
        return String.format("%ss%d", _agent ? "A": "B", _index);
    }

    public int GetIndex()
    {
        return _index;
    }
}
