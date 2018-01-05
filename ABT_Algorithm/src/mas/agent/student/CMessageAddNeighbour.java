package mas.agent.student;

public class CMessageAddNeighbour extends CMessageBase
{
    public CMessageAddNeighbour(int sender_id)
    {
        super(sender_id);
    }

    @Override
    public EMessageType MessageType() { return EMessageType.AddNeighbour; }

    @Override
    public boolean Init(String inMsgBody) { return true; }

    @Override
    public String GetBodyCoding() { return ""; }

}
