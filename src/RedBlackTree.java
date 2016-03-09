/**
 * 
 * @author Mickey Vellukunnel
 * 
 *         Implement an event counter using Red-Black tree.
 */
public class RedBlackTree {

	TreeNode root;

	private static final boolean RED = true;
	private static final boolean BLACK = false;

	private class TreeNode {
		int key; // the ID.
		int count; // number of active events with the given ID.
		int subtreeCount; // count of number of treeNodes in the subtree rooted
							// at this node. minimum = 1 (the node itself)
		TreeNode parent, leftChild, rightChild;
		boolean isRed; // Also the color of the node. By default it's RED (true)

		TreeNode(int key, int count) {
			this.key = key;
			this.count = count;
			this.subtreeCount = 1;
			this.isRed = RED; //true
		}
	}

	void insert(int key, int count) {
		TreeNode newNode = new TreeNode(key, count);
		if (root != null) {
			TreeNode parent = null, tempNode = root;
			while (tempNode != null) {
				parent = tempNode;
				if (key <= tempNode.key) {
					tempNode = tempNode.leftChild;
				} else {
					tempNode = tempNode.rightChild;
				}
			}
			if (key <= parent.key) {
				parent.leftChild = newNode;
			} else {
				parent.rightChild = newNode;
			}
		} else {
			root = newNode;
		}
	}

	void printTree() {
		System.out.println("\n");
		recursivelyPrintTree(root, "");
		System.out.println("\n");
	}

	/*
	 * Print the nodes of the tree in an in-order fashion.
	 */
	private void recursivelyPrintTree(TreeNode node, String indentDots) {
		if (node != null) {
			recursivelyPrintTree(node.rightChild, indentDots + ".");
			System.out.println(indentDots + node.key + " " + node.isRed +"\n");
			recursivelyPrintTree(node.leftChild, indentDots + ".");
		}
	}

	public static void main(String[] args) {
		int[] list = { 60, 20, 75, 10, 85, 100, 80, 35, 5, 18, 2, 4, 3 };
		RedBlackTree tree = new RedBlackTree();
		for (int i : list) {
			tree.insert(i, 1);
			System.out.println("\nThe tree after insertion of " + i);
			tree.printTree();
		}
	}
}
