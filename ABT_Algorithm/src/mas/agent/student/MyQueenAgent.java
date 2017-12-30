package mas.agent.student;

import java.util.List;

import mas.agent.MASQueenAgent;
import cz.agents.alite.communication.Message;

/**
 * This example agent illustrates the usage API available in the MASQueenAgent class.
 */
public class MyQueenAgent extends MASQueenAgent {
	
    public MyQueenAgent(int agentId, int nAgents) {
		// Leave this method as it is...
    	super(agentId, nAgents);
	}

	@Override
	protected void start(int agentId, int nAgents) {
		// This method is called when the agent is initialized.
		// e.g., you can start sending messages: 
		broadcast(new StringContent("Hello world"));
	}

	@Override
	protected void processMessages(List<Message> newMessages) {
		// This method is called whenever there are any new messages for the robot 
		
		// You can list the messages like this:
        for (Message message : newMessages) {
            System.out.println(getAgentId() + ": Received a message from " + message.getSender() + " with the content " + message.getContent().toString());
        }
        
        // This is how you send a message to the agent "2":
        // sendMessage("2", new StringContent("Hello world"));

        // This is how you broadcast a message:
        // broadcast(new StringContent("Hello world"));

        // This is how you notify us that all the agents are finished with computing and successfully found a consistent solution.
        // Each agent must call this method. The first parameter represent the index of the column, 
        // where the agent(queen) stands within the found solution. The columns are indexed 0,..,(nAgents-1).
        notifySolutionFound(0 /* i.e. first column */);

        // In the case there is no valid solution, at least one agent should call the following method:
        // notifySolutionDoesNotExist();

        // You can slow down the search process like this:
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {}
		
	}
}