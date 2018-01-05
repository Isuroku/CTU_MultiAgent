package mas.agent.student;

public class Map
{
    int _size;

    private int[] _agents;

    public Map(int size)
    {
        _size = size;
        _agents = new int[_size * _size];
        for(int i = 0; i < _agents.length; i++)
            _agents[i] = -1;
    }

    private int CoordToIndex(int x, int y) { return y  * _size + x; }
    private int CoordToIndex(Vector2D pos) { return pos.y  * _size + pos.x;}

    private Vector2D IndexToCoord(int index)
    {
        int y = index / _size;
        int x = index - y * _size;
        return new Vector2D(x, y);
    }

    public void AddAgentPos(int agentId, Vector2D position)
    {
        int index = CoordToIndex(position);
        _agents[index] = agentId;
    }

    public void RemoveAgent(int agentId)
    {
        for(int i = 0; i < _agents.length; i++)
            if(_agents[i] == agentId)
                _agents[i] = -1;
    }

    public Vector2D GetAgentPos(int agentId)
    {
        for(int i = 0; i < _agents.length; i++)
            if(_agents[i] == agentId)
                return IndexToCoord(i);
        return null;
    }
}

