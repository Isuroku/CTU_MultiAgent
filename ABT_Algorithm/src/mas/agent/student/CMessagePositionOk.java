package mas.agent.student;

public class CMessagePositionOk extends CMessageBase
{
    public CMessagePositionOk(int SenderId)
    {
        super(SenderId);
    }

    public CMessagePositionOk(int sender_id, Vector2D pos)
    {
        super(sender_id);
        _pos = pos;
    }


    @Override
    public EMessageType MessageType() { return EMessageType.PositionOk; }

    @Override
    protected boolean Init(String inMsgBody)
    {
        _pos = Vector2D.Parse(inMsgBody);
        return true;
    }

    @Override
    protected String GetBodyCoding()
    {
        return String.format("%s", _pos);
    }

    @Override
    public String toString() { return super.toString() + String.format("%s", _pos); }


    private Vector2D _pos;

    public Vector2D Position() { return _pos; }

}
