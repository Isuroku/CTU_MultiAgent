package mas.agent.student;

public class CMessageSolved extends CMessageBase
{
    public CMessageSolved(int sender_id)
    {
        super(sender_id);
    }

    @Override
    public EMessageType MessageType() { return EMessageType.Solved; }

    @Override
    public boolean Init(String inMsgBody) { return true; }

    @Override
    public String GetBodyCoding() { return ""; }

}
