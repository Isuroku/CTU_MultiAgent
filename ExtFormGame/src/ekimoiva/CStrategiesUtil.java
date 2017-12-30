package ekimoiva;

public class CStrategiesUtil
{
    public String _agent;
    public String _bandit;
    public float _koef;
    public float _utility;
    private boolean _approach;

    CStrategiesUtil(String agent_strategy, String bandit_strategy, float utility, float koef, boolean approach)
    {
        _agent = agent_strategy;
        _bandit = bandit_strategy;
        _utility = utility;
        _koef = koef;
        _approach = approach;
    }

    public String GetAgentStrategy() { return _agent; }
    public String GetBanditStrategy() { return _bandit; }
    public float GetUtility() { return _utility * _koef; }
    public float GetMaxUtility() { return _utility; }
    public boolean IsApproched() { return _approach; }

    @Override
    public String toString()
    {
        return String.format("B: %s; A: %s; U: %f, %s", _bandit, _agent, GetUtility(), _approach ? "Approached" : "");
    }
}
