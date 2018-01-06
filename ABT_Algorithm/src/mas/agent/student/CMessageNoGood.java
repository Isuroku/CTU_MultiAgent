package mas.agent.student;

public class CMessageNoGood extends CMessageBase
{
    public CMessageNoGood(int sender_id)
    {
        super(sender_id);
    }

    @Override
    public EMessageType MessageType() { return EMessageType.NoGood; }

    @Override
    public boolean Init(String inMsgBody) { return true; }

    @Override
    public String GetBodyCoding() { return ""; }

}