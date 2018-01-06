package mas.agent.student;

public class CMessageNoGood extends CMessageBase
{
    public CMessageNoGood(int sender_id)
    {
        super(sender_id);
    }

    public CMessageNoGood(int sender_id, int pos)
    {
        super(sender_id);
        _pos = pos;
    }

    @Override
    public EMessageType MessageType() { return EMessageType.NoGood; }

    @Override
    public boolean Init(String inMsgBody)
    {
        _pos = Integer.parseInt(inMsgBody);
        return true;
    }

    @Override
    public String GetBodyCoding()
    {
        return String.format("%d", _pos);
    }

    @Override
    public String toString() { return super.toString() + String.format("%d", _pos); }

    private int _pos;

    public int Position() { return _pos; }
}