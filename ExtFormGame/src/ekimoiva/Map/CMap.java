package ekimoiva.Map;

import ekimoiva.Rect2D;
import ekimoiva.Vector2D;

public class CMap
{
    private Rect2D _map_rect;
    private CMapCell[] _cells;

    public CMap(int width, int height)
    {
        _map_rect = new Rect2D(0, width, 0, height);

        _cells = new CMapCell[MapWidth() * MapHeight()];
        for(int i = 0; i < _cells.length; ++i)
        {
            Vector2D p = IndexToCoord(i);
            _cells[i] = new CMapCell(p, CellType.Empty);
        }
    }

    public Rect2D GetMapRect() { return _map_rect; }
    public int MapWidth() { return _map_rect.Width(); }
    public int MapHeight() { return _map_rect.Height(); }

    private int CoordToIndex(int x, int y) { return y  * MapWidth() + x; }
    private int CoordToIndex(Vector2D pos) { return pos.y  * MapWidth() + pos.x; }

    private Vector2D IndexToCoord(int index)
    {
        int y = index / MapWidth();
        int x = index - y * MapWidth();
        return new Vector2D(x, y);
    }

    public void SetCellType(int x, int y, char input_char)
    {
        CellType t = CellType.Empty;
        switch(input_char)
        {
            case '#': t = CellType.Obstacle; break;
            case 'G': t = CellType.Gold; break;
            case 'E': t = CellType.Danger; break;
            case 'D': t = CellType.Exit; break;
        }

        if(t != CellType.Empty)
            GetCell(x, y).SetType(t);
    }

    public boolean IsPassableCell(Vector2D inCoord)
    {
        CMapCell c = GetCell(inCoord);
        return c.IsPassable();
    }

    public CMapCell GetCell(Vector2D inCoord)
    {
        int index = CoordToIndex(inCoord);

        if(index < 0 || index >= _cells.length)
            throw new RuntimeException( String.format("Incorrect coord %s!", inCoord));

        return _cells[index];
    }

    private CMapCell GetCell(int x, int y)
    {
        int index = CoordToIndex(x, y);

        if(index < 0 || index >= _cells.length)
            throw new RuntimeException( String.format("Incorrect coord %d - %d!", x, y));

        return _cells[index];
    }
}
