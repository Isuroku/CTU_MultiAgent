package mas.agent.student;

import java.util.List;

import mas.agent.MASQueenAgent;
import cz.agents.alite.communication.Message;

/**
 * This example agent illustrates the usage API available in the MASQueenAgent class.
 */
public class MyQueenAgent extends MASQueenAgent
{
	private Map _map;
	private int _pos;
	
    public MyQueenAgent(int agentId, int nAgents) {
		// Leave this method as it is...
    	super(agentId, nAgents);
		_map = new Map(nAgents);
        _pos = 0;
	}

    private Vector2D GetPosition() { return new Vector2D(_pos, getAgentId()); }

	@Override
	protected void start(int agentId, int nAgents) {
		// This method is called when the agent is initialized.
		// e.g., you can start sending messages: 
		//broadcast(new StringContent("Hello world"));

		if(getAgentId() != 0)
			return;

        Step();
	}

	@Override
	protected void processMessages(List<Message> newMessages) {
		// This method is called whenever there are any new messages for the robot 
		
		// You can list the messages like this:
        for (Message message : newMessages) {
			CMessageBase msg = CMessageBase.CreateMessage(message);
			//System.out.println(getAgentId() + ": Received a message from " + message.getSender() + " with the content " + message.getContent().toString());
			System.out.println(String.format("Agent %d: %s", getAgentId(), msg.toString()));
			switch(msg.MessageType())
			{
				case PositionOk: PositionOkHandler((CMessagePositionOk)msg); break;
                case NoGood: NoGoodHandler(); break;
                case Solved: notifySolutionFound(_pos); break;
			}
        }
        
        // This is how you send a message to the agent "2":
        // sendMessage("2", new StringContent("Hello world"));

        // This is how you broadcast a message:
        // broadcast(new StringContent("Hello world"));

        // This is how you notify us that all the agents are finished with computing and successfully found a consistent solution.
        // Each agent must call this method. The first parameter represent the index of the column, 
        // where the agent(queen) stands within the found solution. The columns are indexed 0,..,(nAgents-1).
        //notifySolutionFound(0 /* i.e. first column */);

        // In the case there is no valid solution, at least one agent should call the following method:
        // notifySolutionDoesNotExist();

        // You can slow down the search process like this:
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {}
		
	}

    int FindForward()
    {
        if(_pos == -1)
            _pos = 0;
        Vector2D pos = GetPosition();
        while(pos.x < _map.GetSize() && !_map.IsGoodPlace(pos))
            pos.x++;
        if(pos.x < _map.GetSize())
            return pos.x;
        return -1;
    }

	private void PositionOkHandler(CMessagePositionOk msg)
	{
        _map.AddAgentPos(msg.GetSenderId(), msg.Position());
        if(msg.GetSenderId() == getAgentId() - 1)
            Step();
	}

    private void Step()
    {
        _pos = FindForward();
        if(_pos == -1)
            SendNoGood();
        else
            SendPosition();
    }

    void SendPosition()
    {
        if(getAgentId() == nAgents() - 1)
        {
            notifySolutionFound(_pos);
            broadcast((new CMessageSolved(getAgentId())).ToContent());
            return;
        }

        Vector2D pos = GetPosition();
        CMessageBase msg = new CMessagePositionOk(getAgentId(), pos);
        for(int i = getAgentId() + 1; i < nAgents(); i++)
            sendMessage(String.format("%d", i), msg.ToContent());
    }

	private void SendNoGood()
    {
        if(getAgentId() == 0)
        {
            notifySolutionDoesNotExist();
            return;
        }

        CMessageBase nogood_msg = new CMessageNoGood(getAgentId());
        sendMessage(String.format("%d", getAgentId() - 1), nogood_msg.ToContent());
    }

    private void NoGoodHandler()
    {
        _pos++;
        Step();
    }
}