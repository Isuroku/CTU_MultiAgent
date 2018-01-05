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
	
    public MyQueenAgent(int agentId, int nAgents) {
		// Leave this method as it is...
    	super(agentId, nAgents);
		_map = new Map(nAgents);
		Vector2D pos = new Vector2D(agentId, 0);
		_map.AddAgentPos(agentId, pos);
	}

	@Override
	protected void start(int agentId, int nAgents) {
		// This method is called when the agent is initialized.
		// e.g., you can start sending messages: 
		//broadcast(new StringContent("Hello world"));

		if(getAgentId() == 0)
			return;

		Vector2D pos = _map.GetAgentPos(getAgentId());
		CMessageBase msg = new CMessagePositionOk(agentId, pos);
		sendMessage("0", msg.ToContent());
	}

	@Override
	protected void processMessages(List<Message> newMessages) {
		// This method is called whenever there are any new messages for the robot 
		
		// You can list the messages like this:
        for (Message message : newMessages) {
			CMessageBase msg = CMessageBase.CreateMessage(message);
			//System.out.println(getAgentId() + ": Received a message from " + message.getSender() + " with the content " + message.getContent().toString());
			System.out.println(msg.toString());
			switch(msg.MessageType())
			{
				case PositionOk: PositionOkHandler((CMessagePositionOk)msg); break;
				case AddNeighbour: AddNeighbourHandler((CMessageAddNeighbour)msg); break;
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
            Thread.sleep(1000);
        } catch (InterruptedException e) {}
		
	}

	private void PositionOkHandler(CMessagePositionOk msg)
	{
        _map.AddAgentPos(msg.GetSenderId(), msg.Position());
        CheckLocalView();
	}

	private void CheckLocalView()
    {

    }

    private void AddNeighbourHandler(CMessageAddNeighbour msg)
	{

	}

}