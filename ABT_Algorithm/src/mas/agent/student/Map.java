package mas.agent.student;

public class Map
{
    private int _size;

    private int[] _agents;

    public Map(int size)
    {
        _size = size;
        _agents = new int[_size * _size];
        for(int i = 0; i < _agents.length; i++)
            _agents[i] = -1;
    }

    int GetSize() { return _size; }

    private int CoordToIndex(Vector2D pos)
    {
        int index = pos.y  * _size + pos.x;
        if(index < 0 || index >= _size * _size)
            throw new RuntimeException( String.format("Wrong pos %s", pos) );
        return index;
    }

    private Vector2D IndexToCoord(int index)
    {
        int y = index / _size;
        int x = index - y * _size;
        return new Vector2D(x, y);
    }

    void AddAgentPos(int agentId, Vector2D position)
    {
        RemoveAgent(agentId);
        int index = CoordToIndex(position);
        _agents[index] = agentId;
    }

    private void RemoveAgent(int agentId)
    {
        for(int i = 0; i < _agents.length; i++)
            if(_agents[i] == agentId)
                _agents[i] = -1;
    }

    boolean IsGoodPlace(Vector2D pos)
    {
        Vector2D v = new Vector2D(pos.x, 0);
        for(; v.y < _size; v.y++)
        {
            if(_agents[CoordToIndex(v)] > -1)
                return false;
        }

        v.Load(pos);
        for(; v.x >=0 && v.y >= 0; v.x--, v.y--)
        {
            if(_agents[CoordToIndex(v)] > -1)
                return false;
        }

        v.Load(pos);
        for(; v.x < _size && v.y < _size; v.x++, v.y++)
        {
            if(_agents[CoordToIndex(v)] > -1)
                return false;
        }

        v.Load(pos);
        for(; v.x >= 0 && v.y < _size; v.x--, v.y++)
        {
            if(_agents[CoordToIndex(v)] > -1)
                return false;
        }

        v.Load(pos);
        for(; v.x < _size && v.y >= 0; v.x++, v.y--)
        {
            if(_agents[CoordToIndex(v)] > -1)
                return false;
        }

        return true;
    }
}

