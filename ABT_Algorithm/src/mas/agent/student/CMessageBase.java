package mas.agent.student;

import cz.agents.alite.communication.Message;

public abstract class CMessageBase
{
    private final int _sender_id;

    public CMessageBase(int SenderId)
    {
        _sender_id = SenderId;
    }

    public int GetSenderId() { return _sender_id; }
    public String GetSender() { return String.format("%d", _sender_id); }

    @Override
    public String toString() { return String.format("Msg %s from Agent %d. ", MessageType(), _sender_id); }

    public abstract EMessageType MessageType();
    protected abstract boolean Init(String inMsgBody);

    public static CMessageBase CreateMessage(Message message)
    {
        int id = Integer.parseInt(message.getSender());
        return CreateMessage(id, ((StringContent)message.getContent()).content);
    }

    public static CMessageBase CreateMessage(int SenderId, String inMsgStr)
    {
        for(EMessageType mt : EMessageType.values())
        {
            String header = mt.toString();
            if(inMsgStr.startsWith(header))
            {
                String body = inMsgStr.substring(header.length() + 1);
                return CreateMessage(SenderId, mt, body);
            }
        }
        throw new IllegalArgumentException(String.format("SenderId %d, inMsgStr %s", SenderId, inMsgStr));
    }

    private static CMessageBase CreateMessage(int SenderId, EMessageType inMsgType, String inMsgBody)
    {
        CMessageBase msg = null;
        switch(inMsgType)
        {
            case PositionOk: msg = new CMessagePositionOk(SenderId); break;
            case AddNeighbour: msg = new CMessageAddNeighbour(SenderId); break;
//            case LetPass: msg = new CMessageLetPass(SenderId); break;
//            case REnv: msg = new CMessageRefreshEnvironment(SenderId); break;
//            case ChangeState: msg = new CMessageChangeState(SenderId); break;
//            case TakeGold: msg = new CMessageTakeGold(SenderId); break;
//            case GoldPicked: msg = new CMessageGoldPicked(SenderId); break;
//            case PickUp: msg = new CMessagePickUp(SenderId); break;
        }

        if(msg != null && msg.Init(inMsgBody))
            return msg;

        throw new IllegalArgumentException(String.format("Can't CreateMessage: type %s, body %s", inMsgBody, inMsgType));
    }

    public StringContent ToContent()
    {
        String str = String.format("%s:%s", MessageType(), GetBodyCoding());
        return new StringContent(str);
    }

    protected abstract String GetBodyCoding();
}
