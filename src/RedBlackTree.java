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
			this.isRed = RED; // true
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
			newNode.parent = parent;
		} else {
			root = newNode;
		}
		insert1(newNode);
	}

	TreeNode grandparent(TreeNode node) {
		if (node != null && node.parent != null && node.parent.parent != null) {
			return node.parent.parent;
		} else {
			return null;
		}
	}

	TreeNode uncle(TreeNode node) {
		if (node != null && node.parent != null && node.parent.parent != null) {
			if (node.parent == node.parent.parent.rightChild) {
				return node.parent.parent.leftChild;
			} else {
				return node.parent.parent.rightChild;
			}
		} else {
			return null;
		}
	}

	/*
	 * Case 1 of red-black tree insertion, node inserted is the first node, if
	 * so make it black.
	 */
	void insert1(TreeNode node) {
		System.out.println("\nInside insert1 inserting " + node.key);
		if (node != null) {
			if (node.parent == null) {
				node.isRed = BLACK;
			} else {
				insert2(node);
			}
		}
	}

	/*
	 * Case 2 of red-black tree insertion, parent is black, do nothing.
	 */
	void insert2(TreeNode node) {
		System.out.println("\nInside insert2 inserting " + node.key);
		if (node.parent.isRed == BLACK) {
			return;
		} else {
			insert3(node);
		}
	}

	/*
	 * Case 3 of red-black tree insertion, (parent is red and) uncle is also
	 * red. Then change parent and uncle to black, grandparent to red and
	 * recurse on grandparent.
	 */
	void insert3(TreeNode node) {
		System.out.println("\nInside insert3 inserting " + node.key);
		TreeNode uncle = uncle(node);
		if (uncle != null && uncle.isRed == RED) {
			node.parent.isRed = BLACK;
			uncle.isRed = BLACK;
			TreeNode grandparent = grandparent(node);
			grandparent.isRed = RED;
			insert1(grandparent);
		} else {
			insert4(node);
		}
	}

	/*
	 * Case 4 of red-black tree insertion, (parent is red, uncle is black)
	 * newNode is an inside child of grandparent, i.e. newNode is either the
	 * right child of grandparent's leftChild, or newNode is the left child of
	 * grandparent's rightChild, then rotate left / right respectively.
	 */
	void insert4(TreeNode node) {
		System.out.println("\nInside insert4 inserting " + node.key);
		TreeNode grandparent = grandparent(node);
		TreeNode parent = node.parent;
		if (grandparent.leftChild == parent && parent.rightChild == node) {
			// left-rotate
			parent.rightChild = node.leftChild;
			parent.parent = node;
			node.leftChild = parent;
			node.parent = grandparent;
			grandparent.leftChild = node;
			node = node.leftChild;
		} else if (grandparent.rightChild == parent && parent.leftChild == node) {
			// right-rotate
			parent.leftChild = node.rightChild;
			parent.parent = node;
			node.rightChild = parent;
			node.parent = grandparent;
			grandparent.rightChild = node;
			node = node.rightChild;
		}
		insert5(node);
	}

	/*
	 * Case 5 of red-black tree insertion, newNode is an outside child of
	 * grandparent, newNode is either the right child of grandparent's
	 * rightChild, or newNode is the left child of grandparent's leftChild, then
	 * rotate right / left respectively, then paint .
	 */
	void insert5(TreeNode node) {
		System.out.println("\nInside insert5 inserting " + node.key);
		TreeNode grandparent = grandparent(node);
		TreeNode parent = node.parent;
		parent.isRed = BLACK;
		grandparent.isRed = RED;
		if (parent.rightChild == node) {
			leftRotate(grandparent);
		} else {
			rightRotate(grandparent);
		}
	}

	void leftRotate(TreeNode node) {
		if (node != null && node.rightChild != null) {
			TreeNode rightChild = node.rightChild, grandparent = node.parent;
			node.rightChild = rightChild.leftChild;
			node.parent = rightChild;
			rightChild.leftChild = node;
			rightChild.parent = grandparent;
			if (grandparent != null) {
				if (node == grandparent.leftChild) {
					grandparent.leftChild = rightChild;
				} else {
					grandparent.rightChild = rightChild;
				}
			}
		}
	}

	void rightRotate(TreeNode node) {
		if (node != null && node.leftChild != null) {
			TreeNode leftChild = node.leftChild, grandparent = node.parent;
			node.leftChild = leftChild.rightChild;
			node.parent = leftChild;
			leftChild.rightChild = node;
			leftChild.parent = grandparent;
			if (grandparent != null) {
				if (node == grandparent.leftChild) {
					grandparent.leftChild = leftChild;
				} else {
					grandparent.rightChild = leftChild;
				}
			}
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
			System.out.println(indentDots + node.key + " " + node.isRed + "\n");
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
