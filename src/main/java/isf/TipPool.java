package isf;

import java.util.EmptyStackException;
import java.util.Stack;

import jota.dto.response.GetTransactionsToApproveResponse;

public class TipPool extends Thread {

	private static Stack<GetTransactionsToApproveResponse> gttars = new Stack<GetTransactionsToApproveResponse>();
	private static int gttarsLimit;
	
	private static final TimeBomb GTTA_BOMB = new TimeBomb("requesting transactions to approve (tips)", 10) {
		@Override
		void onCall() {
			gttars.push(NodeManager.getTransactionsToApprove());
		}
	};
	
	@Override
	public void run() {
		gttarsLimit = Configs.getInt(P.THREADS_TIP_POOL_SIZE);
		while(true) {
			if(gttars.size() < gttarsLimit)
				GTTA_BOMB.call(10);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static GetTransactionsToApproveResponse getTransactionsToApprove() {
		try {
			return gttars.pop();
		} catch(EmptyStackException e) {
			return null;
		}
	}
	
	public static int gttarsQueueSize() {
		return gttars.size();
	}
	
	public static int gttarsLimit() {
		return gttarsLimit;
	}
}