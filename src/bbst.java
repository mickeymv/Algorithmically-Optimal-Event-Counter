import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class bbst {

	public static void main(String[] args) {
		RedBlackTree tree = new RedBlackTree();
		if (0 < args.length) {

			String inputFileName = args[0];
			File nodesInputFile = new File(inputFileName);
			try {
				FileReader inputFil = new FileReader(nodesInputFile);
				BufferedReader in = new BufferedReader(inputFil);

				String s = in.readLine();

				int nodesCount = Integer.parseInt(s);
				RedBlackTree.TreeNode[] sortedNodesArray = new RedBlackTree.TreeNode[nodesCount];
				s = in.readLine();

				for (int i = 0; i < nodesCount; i++) {
					String nums[] = s.split(" ");
					int nodeID = Integer.parseInt(nums[0]);
					int nodeCount = Integer.parseInt(nums[1]);
					RedBlackTree.TreeNode node = tree.new TreeNode(nodeID, nodeCount);
					node.isRed = false;
					sortedNodesArray[i] = node;
					s = in.readLine();
				}

				tree.sortedArrayToRedBlackTree(sortedNodesArray, nodesCount);

				// create a scanner so we can read the command-line input
				Scanner scanner = new Scanner(System.in);
				s = scanner.nextLine();
				while (!"quit".equals(s)) {
					String commands[] = s.split(" ");
					String command = commands[0];

					switch (command) {
					case "increase":
						tree.increase(Integer.parseInt(commands[1]), Integer.parseInt(commands[2]));
						break;
					case "reduce":
						tree.reduce(Integer.parseInt(commands[1]), Integer.parseInt(commands[2]));
						break;
					case "count":
						tree.count(Integer.parseInt(commands[1]));
						break;
					case "inrange":
						tree.inRange(Integer.parseInt(commands[1]), Integer.parseInt(commands[2]));
						break;
					case "next":
						tree.next(Integer.parseInt(commands[1]), true);
						break;
					case "previous":
						tree.previous(Integer.parseInt(commands[1]), true);
						break;
					default:
						System.out.println("\nInvalid command: '" + command + "' ! Enter 'quit' to exit. ");
						break;
					}
					s = scanner.nextLine();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("\n Enter an input file name with the nodes in sorted order. \n");
	}

}
