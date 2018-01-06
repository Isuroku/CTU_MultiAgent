package mas.agent.student;

import java.util.ArrayList;

public class Map
{
    private int _size;

    private int[] _agents;

    private int _agent_count;

    public Map(int size)
    {
        _size = size;
        _agent_count = 0;
        _agents = new int[_size * _size];
        for(int i = 0; i < _agents.length; i++)
            _agents[i] = -1;
    }

    int GetSize() { return _size; }
    int GetAgentCount() { return _agent_count; }

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
        _agent_count++;
    }

    private void RemoveAgent(int agentId)
    {
        for(int i = 0; i < _agents.length; i++)
            if(_agents[i] == agentId)
            {
                _agent_count--;
                _agents[i] = -1;
            }
    }

    boolean IsGoodPlace(Vector2D pos)
    {
        Vector2D v = new Vector2D(pos.x, 0);
        for(; v.y < _size; v.y++)
        {
            if(_agents[CoordToIndex(v)] > -1)
                return false;
        }

        v.Set(pos);
        for(; v.x >=0 && v.y >= 0; v.x--, v.y--)
        {
            if(_agents[CoordToIndex(v)] > -1)
                return false;
        }

        v.Set(pos);
        for(; v.x < _size && v.y < _size; v.x++, v.y++)
        {
            if(_agents[CoordToIndex(v)] > -1)
                return false;
        }

        v.Set(pos);
        for(; v.x >= 0 && v.y < _size; v.x--, v.y++)
        {
            if(_agents[CoordToIndex(v)] > -1)
                return false;
        }

        v.Set(pos);
        for(; v.x < _size && v.y >= 0; v.x++, v.y--)
        {
            if(_agents[CoordToIndex(v)] > -1)
                return false;
        }

        return true;
    }

    Vector2D RemoveLowestAgent()
    {
        int id = -1;
        int pos = -1;
        for(int i = 0; i < _agents.length; i++)
        {
            if(_agents[i] > id)
            {
                id = _agents[i];
                pos = i;
            }
        }

        if(pos >= 0)
        {
            _agents[pos] = -1;
            pos = IndexToCoord(pos).x;
            _agent_count--;
        }

        return new Vector2D(id, pos);
    }

    boolean CheckSolve(int agentId, Vector2D pos)
    {
        AddAgentPos(agentId, pos);
        if(_agent_count < _size)
        {
            RemoveAgent(agentId);
            return false;
        }

        boolean res = CheckSolve();
        RemoveAgent(agentId);
        return res;
    }

    private boolean CheckSolve()
    {
        int sum;
        Vector2D v = new Vector2D(0, 0);
        for(; v.x < _size; v.x++)
        {
            sum = 0;
            v.y = 0;
            for(; v.y < _size; v.y++)
            {
                if(_agents[CoordToIndex(v)] > -1)
                {
                    sum++;
                    if(sum > 1)
                        return false;
                }
            }
        }

        for(int i = 0; i < _size - 1; i++)
        {
            v.Set(i, 0);
            sum = 0;
            for(; v.x < _size && v.y < _size; v.x++, v.y++)
            {
                if(_agents[CoordToIndex(v)] > -1)
                {
                    sum++;
                    if(sum > 1)
                        return false;
                }
            }

            if(i != 0)
            {
                v.Set(0, i);
                sum = 0;
                for(; v.x < _size && v.y < _size; v.x++, v.y++)
                {
                    if(_agents[CoordToIndex(v)] > -1)
                    {
                        sum++;
                        if(sum > 1)
                            return false;
                    }
                }
            }
        }

        for(int x = _size - 1, y = 0; x > 0 && y < _size; x--, y++)
        {
            v.Set(x, 0);
            sum = 0;
            for(; v.x >= 0 && v.y < _size; v.x--, v.y++)
            {
                if(_agents[CoordToIndex(v)] > -1)
                {
                    sum++;
                    if(sum > 1)
                        return false;
                }
            }

            if(y != 0)
            {
                v.Set(_size - 1, y);
                sum = 0;
                for(; v.x >= 0 && v.y < _size; v.x--, v.y++)
                {
                    if(_agents[CoordToIndex(v)] > -1)
                    {
                        sum++;
                        if(sum > 1)
                            return false;
                    }
                }
            }
        }


        return true;
    }

    Vector2D[] GetAgentPositions()
    {
        ArrayList<Vector2D> arr = new ArrayList<>();
        for(int i = 0; i < _agents.length; i++)
        {
            if(_agents[i] > -1)
            {
                Vector2D pos = IndexToCoord(i);
                arr.add(pos);
            }
        }
        return arr.toArray(new Vector2D[arr.size()]);
    }
}

