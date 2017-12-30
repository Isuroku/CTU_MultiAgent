package ekimoiva.Map;

import ekimoiva.Vector2D;

public class CMapCell //implements Comparable<CMapCell>
{
    private Vector2D _pos;
    private CellType _type;

    CMapCell(Vector2D inPos, CellType inType)
    {
        _pos = inPos;
        _type = inType;
    }

    public boolean IsPassable() { return _type != CellType.Obstacle; }
    public CellType GetCellType() { return _type; }
    public void SetType(CellType inType) { _type = inType; }

    @Override
    public String toString()
    {
        return String.format("%s: %s", _pos, _type);
    }

    //@Override
    //public int compareTo(CMapCell o)
    //{
    //    return WaveLength - o.WaveLength;
    //}
}