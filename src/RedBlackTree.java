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
		boolean isRed; // Also the color of the node. By default (by using the
						// constructor) it's RED (true)

		TreeNode(int key, int count) {
			this.key = key;
			this.count = count;
			this.subtreeCount = 1;
			this.isRed = RED; // true
		}
	}

	/*
	 * Binary search tree insert.
	 */
	void insert(int key, int count) {
		TreeNode newNode = new TreeNode(key, count);
		if (root != null) {
			TreeNode parent = null, tempNode = root;
			while (tempNode != null) {
				parent = tempNode;
				if (key < tempNode.key) {
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
		// insert1(newNode);
	}

	/*
	 * Binary search tree delete after finding the node with the given key.
	 */
	void delete(int key) {
		if (root != null) {
			// First find the node to delete
			TreeNode node = root;
			while (node != null && node.key != key) {
				if (key < node.key) {
					node = node.leftChild;
				} else {
					node = node.rightChild;
				}
			}
			if (node != null) { // node isn't null implies we've found the node
								// to delete.
				deleteNode(node);
			}
		}
	}

	/*
	 * deletes the given node.
	 */
	void deleteNode(TreeNode node) {
		if (node != null) {
			if (node.leftChild == null && node.rightChild == null) {
				// CASE 1: No children: if there are no children, delete the
				// node directly.
				deleteNodeReferences(node);
			} else if (node.leftChild != null && node.rightChild != null) {
				// CASE 2: 2 children: If the node has two children replace
				// node with its predecessor, and delete the predecessor
				// recursively.
				TreeNode predecessor = predecessor(node);
				replaceNode(node, predecessor);
				deleteNode(predecessor);
			}
			// CASE 3: Deletion of node with one child. call
			// delete of red black tree IF the node being deleted is a black
			// node.
			// (if it's red, then no RBT properties are violated)
			else if (node.rightChild != null) {
				boolean moreFixesRequired = false;
				if (node.isRed == BLACK) {
					moreFixesRequired = !deleteFix1(node);
				}
				// CASE 3.1: 1 child: if the node only has rightChild, replace
				// node's parent
				// link to its child.
				if (node.parent == null) {
					root = node.rightChild;
				} else if (node.parent.rightChild == node) {
					node.parent.rightChild = node.rightChild;
				} else {
					node.parent.leftChild = node.rightChild;
				}
			} else {
				boolean moreFixesRequired = false;
				if (node.isRed == BLACK) {
					moreFixesRequired = !deleteFix1(node);
				}
				// CASE 3.2: 2 child: if the node only has leftChild, replace
				// node's parent
				// link to its child.
				if (node.parent == null) {
					root = node.leftChild;
				} else if (node.parent.rightChild == node) {
					node.parent.rightChild = node.leftChild;
				} else {
					node.parent.leftChild = node.leftChild;
				}
			}
		}
	}

	/*
	 * If the node to be deleted is black with ONE child, and the child is red,
	 * simply repaint the child black.
	 */
	boolean deleteFix1(TreeNode node) {
		if (node.isRed == BLACK && node.rightChild != null && node.rightChild.isRed == RED) {
			node.rightChild.isRed = BLACK;
			return true;
		} else if (node.isRed == BLACK && node.leftChild != null && node.leftChild.isRed == RED) {
			node.leftChild.isRed = BLACK;
			return true;
		}
		return false;
	}

	/*
	 * "Deletes" a node by removing all references to it and setting the parent
	 * reference to null;
	 */
	void deleteNodeReferences(TreeNode node) {
		if (node != null) {
			if (node == root) {
				root = null;
			} else {
				if (node.parent.leftChild == node) {
					node.parent.leftChild = null;
				} else {
					node.parent.rightChild = null;
				}
			}
		}
	}

	/*
	 * Copy all the contents of one node to another.
	 */
	void replaceNode(TreeNode replaceeNode, TreeNode replacerNode) {
		replaceeNode.key = replacerNode.key;
		replaceeNode.count = replacerNode.count;
	}

	/*
	 * Returns the successor of the node, i.e. the left-most child in it's right
	 * subtree.
	 */
	TreeNode successor(TreeNode node) {
		TreeNode successor = null;
		if (node != null) {
			successor = node.rightChild;
			while (successor != null && successor.leftChild != null) {
				successor = successor.leftChild;
			}
		}
		return successor;
	}

	/*
	 * Returns the predecessor of the node, i.e. the right-most child in it's
	 * left subtree.
	 */
	TreeNode predecessor(TreeNode node) {
		TreeNode predecessor = null;
		if (node != null) {
			predecessor = node.leftChild;
			while (predecessor != null && predecessor.rightChild != null) {
				predecessor = predecessor.rightChild;
			}
		}
		return predecessor;
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

	TreeNode sibling(TreeNode node) {
		if (node != null && node.parent != null) {
			if (node == node.parent.rightChild) {
				return node.parent.leftChild;
			} else {
				return node.parent.rightChild;
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
	 * Print the nodes of the tree in an in-order looking (left child towards
	 * the bottom and right child towards the top) fashion.
	 */
	private void recursivelyPrintTree(TreeNode node, String indentDots) {
		if (node != null) {
			recursivelyPrintTree(node.rightChild, indentDots + ".");
			// System.out.println(indentDots + node.key + " " + node.isRed +
			// "\n");
			System.out.println(indentDots + node.key + "\n");
			recursivelyPrintTree(node.leftChild, indentDots + ".");
		}
	}

	public static void main(String[] args) {
		int[] list = { 60, 20, 75, 10, 85, 100, 80, 35, 5, 18, 2, 4, 3, 64, 105, 46, 29, 61 };
		RedBlackTree tree = new RedBlackTree();
		for (int i : list) {
			tree.insert(i, 1);
			System.out.println("\nThe tree after insertion of " + i);
			tree.printTree();
		}
		int[] delList = { 60, 20, 105, 85, 80, 10 };
		for (int i : delList) {
			tree.delete(i);
			System.out.println("\nThe tree after deletion of " + i);
			tree.printTree();
		}
	}
}
