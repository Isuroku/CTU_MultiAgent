package mas.agent.student;

public class CMessageSolved extends CMessageBase
{
    public CMessageSolved(int sender_id)
    {
        super(sender_id);
    }

    public CMessageSolved(int sender_id, int pos, boolean result)
    {
        super(sender_id);
        _pos = pos;
        _result = result;
    }

    @Override
    public EMessageType MessageType() { return EMessageType.Solved; }

    @Override
    public boolean Init(String inMsgBody)
    {
        String[] arr = inMsgBody.split(";");
        if(arr.length != 2)
            return false;

        _pos = Integer.parseInt(arr[0]);
        int value = Integer.parseInt(arr[1]);
        _result = value > 0;
        return true;
    }

    @Override
    public String GetBodyCoding()
    {
        return String.format("%d;%d", _pos, _result ? 1 : 0);
    }

    @Override
    public String toString() { return super.toString() + String.format("solved %s, pos %d", _result, _pos); }

    private int _pos;
    public int Position() { return _pos; }

    private boolean _result;
    public boolean Result() { return _result; }
}
