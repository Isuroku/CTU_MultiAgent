package ekimoiva;

import java.util.ArrayList;

public class PlaceGen
{
    int _max_value;
    int _digital_count;

    ArrayList<Integer> _current = new ArrayList<>();

    public PlaceGen(int max_value, int digital_count)
    {
        _max_value = max_value;
        _digital_count = digital_count;
        for(int i = 0; i < digital_count; i++)
            _current.add(0);
    }

    public Integer[] NextGen()
    {
        for(int i = _digital_count - 1; i >= 0; i--)
        {
            int val = _current.get(i);
            val++;
            int last_val = val;
            for(int j = i + 1, k = 1; j <= _digital_count - 1; j++, k++)
            {
                last_val = val + k;
                _current.set(j, last_val);
            }

            boolean norm = last_val < _max_value;
            if(norm)
            {
                _current.set(i, val);
                return _current.toArray(new Integer[_current.size()]);
            }
        }

        return null;
    }
}
