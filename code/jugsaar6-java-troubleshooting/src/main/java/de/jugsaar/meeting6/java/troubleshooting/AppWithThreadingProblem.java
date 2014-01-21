package de.jugsaar.meeting6.java.troubleshooting;

public class AppWithThreadingProblem {
	public static void main(String[] args) throws Exception {
		for (int i = 1; i <= 100_000; i++)
			new Thread(new SomeListener(), "Th-" + i).start();
		Thread.sleep(10000L);
	}

	static class SomeListener implements Runnable {
		public void run() {
			try {
				System.out.println(Thread.currentThread().getName());
				Thread.sleep(10000L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
