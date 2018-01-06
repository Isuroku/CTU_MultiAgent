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
    private boolean[] _closed;
    private EAgentState _state;
    private boolean _debug_output;
	
    public MyQueenAgent(int agentId, int nAgents) {
		// Leave this method as it is...
    	super(agentId, nAgents);
		_map = new Map(nAgents);
        _pos = -1;
        _closed = new boolean[nAgents];
        ClearClosed();
        _state = EAgentState.Search;
        _debug_output = false;
	}

	private void ClearClosed()
    {
        for(int i = 0; i < _closed.length; i++)
            _closed[i] = false;
    }

    private Vector2D GetPosition() { return new Vector2D(_pos, getAgentId()); }

	@Override
	protected void start(int agentId, int nAgents) {
		// This method is called when the agent is initialized.
		// e.g., you can start sending messages: 
		//broadcast(new StringContent("Hello world"));

//		if(getAgentId() != 0)
//			return;
//
//        Step();

        CheckLocalView(false);
	}

	@Override
	protected void processMessages(List<Message> newMessages) {
		// This method is called whenever there are any new messages for the robot 

        if(_state != EAgentState.Search)
            return;
		// You can list the messages like this:
        for (Message message : newMessages) {
			CMessageBase msg = CMessageBase.CreateMessage(message);
			//System.out.println(getAgentId() + ": Received a message from " + message.getSender() + " with the content " + message.getContent().toString());
            if(_debug_output)
			    System.out.println(String.format("%d[%d] received: %s", getAgentId(), _pos, msg.toString()));

			switch(msg.MessageType())
			{
				case PositionOk: PositionOkHandler((CMessagePositionOk)msg); break;
                case NoGood: NoGoodHandler((CMessageNoGood)msg); break;
                case Solved: SolvedHandler((CMessageSolved)msg); break;
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

    private void SendMessage(int receiver, CMessageBase msg)
    {
        if(_state != EAgentState.Search)
            return;

        if(receiver > -1)
        {
            //System.out.println(String.format("Send to %d: %s", receiver, msg.toString()));
            sendMessage(String.format("%d", receiver), msg.ToContent());
        }
        else
        {
            //System.out.println(String.format("Send to all: %s", msg.toString()));
            broadcast(msg.ToContent());
        }
    }

    private int FindCircle()
    {
        int counter = 0;
        Vector2D pos = GetPosition();
        if(pos.x == -1)
            pos.x = 0;

        while(counter < _map.GetSize() && (!_map.IsGoodPlace(pos) || _closed[pos.x]))
        {
            counter++;
            pos.x++;
            if(pos.x >= _map.GetSize())
                pos.x = 0;
        }

        if(counter < _map.GetSize())
            return pos.x;
        return -1;
    }

	private void PositionOkHandler(CMessagePositionOk msg)
	{
        _map.AddAgentPos(msg.GetSenderId(), msg.Position());
        ClearClosed();
        CheckLocalView(false);
	}

	private void CheckLocalView(boolean need_send_pos)
    {
        int new_pos = FindCircle();
        if(new_pos == -1)
            BackTrack();
        else
        {
            if(new_pos != _pos)
            {
                _pos = new_pos;
                if(_debug_output)
                    System.out.println(String.format("%d takes %s", getAgentId(), _pos));
                SendPosition();
            }
            else
            {
                if(need_send_pos)
                    SendPosition();
                if(_debug_output)
                    System.out.println(String.format("%d saves pos %s", getAgentId(), _pos));
            }

            if(getAgentId() == nAgents() - 1)
            {
                if(_map.CheckSolve(getAgentId(), GetPosition()))
                {
                    notifySolutionFound(_pos);

                    Vector2D[] poses = _map.GetAgentPositions();

                    for(int i = 0; i < poses.length; i++)
                        SendMessage(poses[i].y, new CMessageSolved(getAgentId(), poses[i].x, true));

                    _state = EAgentState.Solved;
                }
            }
        }
    }

    private void BackTrack()
    {
        if(getAgentId() == 0)
        {
            notifySolutionDoesNotExist();
            for(int i = 1; i < nAgents(); i++)
                SendMessage(i, new CMessageSolved(getAgentId(), i, false));
            _state = EAgentState.NoSolution;
            return;
        }

        _pos = -1;
        Vector2D agent_id_pos = _map.RemoveLowestAgent();
        if(agent_id_pos.x > -1)
        {
            if(_debug_output)
                System.out.println(String.format("%d[%d] remove %d[%d]", getAgentId(), _pos, agent_id_pos.x, agent_id_pos.y));

            CMessageBase no_good_msg = new CMessageNoGood(getAgentId(), agent_id_pos.y);
            SendMessage(agent_id_pos.x, no_good_msg);

            CheckLocalView(false);
        }
        else if(_debug_output)
            System.out.println(String.format("%d: empty map", getAgentId()));
    }

    private void SendPosition()
    {
        Vector2D pos = GetPosition();
        CMessageBase msg = new CMessagePositionOk(getAgentId(), pos);

        for(int i = getAgentId() + 1; i < nAgents(); i++)
            SendMessage(i, msg);
    }

    private void NoGoodHandler(CMessageNoGood msg)
    {
        _closed[msg.Position()] = true;
        if(_debug_output)
            System.out.println(String.format("%d[%d] closed %d", getAgentId(), _pos, msg.Position()));
        CheckLocalView(true);
    }

    private void SolvedHandler(CMessageSolved msg)
    {
        if(msg.Result())
        {
            _pos = msg.Position();
            notifySolutionFound(_pos);
            _state = EAgentState.Solved;
        }
        else
        {
            notifySolutionDoesNotExist();
            _state = EAgentState.NoSolution;
        }
    }
}