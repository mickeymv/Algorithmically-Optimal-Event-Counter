import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

/**
 * 
 * @author Mickey Vellukunnel
 * 
 *         Implement an Event counter using Red-Black tree.
 * 
 *         Each event has two fields: ID and count, where count is the number of
 *         active events with the given ID. The event counter stores only those
 *         ID’s whose count is > 0. Once a count drops below 1, that ID is
 *         removed.
 * 
 *         The required functions are; Increase, Reduce, Count, InRange, Next
 *         and Previous.
 */
public class RedBlackTree {

	// Root of the RedBlackTree Event Counter
	TreeNode root;

	private static final boolean RED = true;
	private static final boolean BLACK = false;

	// Variables which store the IDs of the max and min Events (to facilitate
	// range operations) (Initialized to -1 to indicate it's not set. This is a
	// safe assumption because IDs are assumed to be positive integers)
	private int treeMinimum = -1;
	private int treeMaximum = -1;

	// Structure of each node (which represents an "Event") in the RedBlackTree
	// Event Counter
	public class TreeNode {
		int key; // the ID.
		int count; // number of active events with the given ID.
		int subtreeCount; // total count of all events' counts in the subtree
							// rooted
							// at this node. minimum = this.count (the count of
							// the
							// node itself)
		/*
		 * NOTE: This "subtreeCount" is an augmented order statistic variable
		 * which helps to support the inRange operation in O(log n) time. This
		 * variable is updated in constant time whenever an operation which
		 * changes the tree structure relevant to this node (i.e. the subtree
		 * below it) changes, i.e. whenver an insert, delete, increase or reduce
		 * operation takes place.
		 */
		// subtreeCount = leftChild.subtreeCount + rightChild.subtreeCount +
		// this.count
		TreeNode parent, leftChild, rightChild; // pointers to the parent and
												// children nodes.
		boolean isRed; // Also the color of the node. By default (by using the
						// constructor) it's RED (true) (since during insert
						// it's RED by default)

		TreeNode(int key, int count) {
			this.key = key;
			this.count = count;
			this.subtreeCount = count; // when adding to the leaf, subtree count
										// is count itself
			this.isRed = RED; // true. Since when inserting, leaf nodes are RED
								// by default.
		}
	}

	/*
	 * RedBlack Tree constructor to initialize the RedBlack tree from an
	 * ascending sorted array of n TreeNodes (Events) in O(n) time.
	 * 
	 * This is done by calling a recursive function sortedArrayToRBBST().
	 * 
	 */
	RedBlackTree(TreeNode arrOfTreeNodesInAscendingSortedOrder[], int totalNumberOfNodesInSortedArray) {
		treeMinimum = arrOfTreeNodesInAscendingSortedOrder[0].key;
		treeMaximum = arrOfTreeNodesInAscendingSortedOrder[totalNumberOfNodesInSortedArray - 1].key;
		root = sortedArrayToRBBST(arrOfTreeNodesInAscendingSortedOrder, 0, totalNumberOfNodesInSortedArray - 1, 0,
				log2(totalNumberOfNodesInSortedArray));
	}

	public RedBlackTree() {
		// TODO Auto-generated constructor stub
	}

	/*
	 * Increase the count of the event theID by m. If theID is not present,
	 * insert it. Print the count of theID after the addition. Maintain the
	 * value of the augmented variable subTreeCount as well. Time complexity:
	 * O(log n).
	 */
	void increase(int theIDofEvent, int countIncreaseBy) {
		TreeNode theEvent = findNode(theIDofEvent);
		if (theEvent != null) {
			theEvent.count += countIncreaseBy;
			theEvent.subtreeCount += countIncreaseBy;
			TreeNode temp = theEvent.parent;
			// Increase the subtreeCounts up the tree (till root) by the
			// increased amount. Time complexity: O(log n)
			while (temp != null) {
				temp.subtreeCount += countIncreaseBy;
				temp = temp.parent;
			}
		} else {
			insert(theIDofEvent, countIncreaseBy);
			theEvent = findNode(theIDofEvent);
		}
		System.out.println(theEvent.count);
	}

	/*
	 * Decrease the count of theID by m. If theID’s count becomes less than or
	 * equal to 0, remove theID from the counter. Print the count of theID after
	 * the deletion, or 0 if theID is removed or not present. Maintain the value
	 * of the augmented variable subTreeCount as well. Time complexity: O(log
	 * n).
	 */
	void reduce(int theIDofEvent, int decreaseCountBy) {
		TreeNode theEvent = findNode(theIDofEvent);
		if (theEvent != null) {
			if (theEvent.count <= decreaseCountBy) {
				int countOfDeletedEvent = theEvent.count;
				delete(theIDofEvent);
				TreeNode temp = theEvent.parent;
				// Decrease the subtreeCounts up the tree till the root by the
				// decreased
				// amount (count of deleted event). Time complexity: O(log n)
				while (temp != null) {
					temp.subtreeCount -= countOfDeletedEvent;
					temp = temp.parent;
				}
				// ID is removed because count became zero or less. Print zero.
				System.out.println(0);
			} else {
				theEvent.count -= decreaseCountBy;
				theEvent.subtreeCount -= decreaseCountBy;
				TreeNode temp = theEvent.parent;
				// Decrease the subtreeCounts up the tree till the root by the
				// decreased
				// amount. Time complexity: O(log n)
				while (temp != null) {
					temp.subtreeCount -= decreaseCountBy;
					temp = temp.parent;
				}
				System.out.println(theEvent.count);
			}
		} else {
			// theID is not present. Print zero.
			System.out.println(0);
		}
	}

	/*
	 * Print the count of theID. If not present, print 0. Time complexity: O(log
	 * n).
	 */
	void count(int theIDofEvent) {
		TreeNode theEvent = findNode(theIDofEvent);
		if (theEvent != null) {
			System.out.println(theEvent.count);
		} else {
			System.out.println(0);
		}
	}

	/*
	 * Print the ID and the count of the event with the lowest ID that is
	 * greater that theID. Print “0 0”, if there is no next ID. Time complexity:
	 * O(log n).
	 */
	TreeNode next(int theIDofEvent, boolean shouldPrint) {
		TreeNode theEvent = findNode(theIDofEvent);
		if (theEvent != null) {
			TreeNode successorOfEvent = successor(theEvent);
			if (successorOfEvent != null) {
				if (shouldPrint) {
					System.out.println(successorOfEvent.key + " " + successorOfEvent.count);
				}
				return successorOfEvent;
			} else {
				// if there is no "successor" for the node, find the next
				// greatest number by going up the tree. Time complexity: O(log
				// n)
				TreeNode temp = theEvent.parent;
				boolean found = false;
				while (temp != null) {
					if (temp.key > theIDofEvent) {
						if (shouldPrint) {
							System.out.println(temp.key + " " + temp.count);
						}
						found = true;
						break;
					}
					temp = temp.parent;
				}
				if (!found) {
					if (shouldPrint) {
						System.out.println("0 0");
					}
					return null;
				} else {
					return temp;
				}
			}
		} else {
			// the ID is not present, so find the next fit (used in inRange
			// queries)
			if (theIDofEvent > treeMaximum) {
				if (shouldPrint) {
					System.out.println("0 0");
				}
				return null;
			}
			int bestFitForLeftRange = treeMinimum;
			TreeNode leftNode = null;
			if (theIDofEvent <= treeMinimum) {
				leftNode = findNode(treeMinimum);
			} else {
				while (!(theIDofEvent <= bestFitForLeftRange)) {
					leftNode = next(bestFitForLeftRange, false);
					bestFitForLeftRange = leftNode.key;
				}
			}
			if (shouldPrint) {
				if (leftNode == null) {
					System.out.println("0 0");
				} else {
					System.out.println(leftNode.key + " " + leftNode.count);
				}
			}
			return leftNode;
		}
	}

	/*
	 * Print the ID and the count of the event with the greatest key that is
	 * less that theID. Print “0 0”, if there is no previous ID. Time
	 * complexity: O(log n).
	 */
	TreeNode previous(int theIDofEvent, boolean shouldPrint) {
		TreeNode theEvent = findNode(theIDofEvent);
		if (theEvent != null) {
			TreeNode predecessorOfEvent = predecessor(theEvent);
			if (predecessorOfEvent != null) {
				if (shouldPrint) {
					System.out.println(predecessorOfEvent.key + " " + predecessorOfEvent.count);
				}
				return predecessorOfEvent;
			} else {
				// if there is no "predecessor" for the node, find the next
				// smallest number by going up the tree. Time complexity: O(log
				// n)
				TreeNode temp = theEvent.parent;
				boolean found = false;
				while (temp != null) {
					if (temp.key < theIDofEvent) {
						if (shouldPrint) {
							System.out.println(temp.key + " " + temp.count);
						}
						found = true;
						break;
					}
					temp = temp.parent;
				}
				if (!found) {
					if (shouldPrint) {
						System.out.println("0 0");
					}
					return null;
				} else {
					return temp;
				}
			}
		} else {
			// the ID is not present, so find the next fit (used in inRange
			// queries)
			if (theIDofEvent < treeMinimum) {
				if (shouldPrint) {
					System.out.println("0 0");
				}
				return null;
			}
			int bestFitForRightRange = treeMaximum;
			TreeNode rightNode = null;
			if (theIDofEvent >= treeMaximum) {
				rightNode = findNode(treeMaximum);
			} else {
				while (!(theIDofEvent >= bestFitForRightRange)) {
					rightNode = previous(bestFitForRightRange, false);
					bestFitForRightRange = rightNode.key;
				}
			}
			if (shouldPrint) {
				if (rightNode == null) {
					System.out.println("0 0");
				} else {
					System.out.println(rightNode.key + " " + rightNode.count);
				}
			}
			return rightNode;
		}
	}

	/*
	 * Return the node in the tree which fits the leftEnd of the inRange query.
	 * This could be the event which has ID equal to the specified leftRange or
	 * the smallest event ID greater than it.
	 */
	TreeNode getRangeLeftNode(int ID1) {
		TreeNode leftNode = findNode(ID1);
		if (leftNode == null) {
			// This implies the ID isn't there in tree, so find the next best
			// fit
			if (ID1 > treeMaximum) {
				return null;
			}
			int bestFitForLeftRange = treeMinimum;

			if (ID1 <= treeMinimum) {
				leftNode = findNode(treeMinimum);
			} else {
				while (!(ID1 <= bestFitForLeftRange)) {
					leftNode = next(bestFitForLeftRange, false);
					bestFitForLeftRange = leftNode.key;
				}
			}
			return leftNode;
		} else {
			return leftNode;
		}
	}

	/*
	 * Return the node in the tree which fits the rightEnd of the inRange query.
	 * This could be the event which has ID equal to the specified rightRange or
	 * the greatest event ID less than it.
	 */
	TreeNode getRangeRightNode(int ID2) {
		TreeNode rightNode = findNode(ID2);
		if (rightNode == null) {
			// This implies the ID isn't there in tree, so find the next best
			// fit
			if (ID2 < treeMinimum) {
				return null;
			}
			int bestFitFoRightRange = treeMaximum;

			if (ID2 >= treeMaximum) {
				rightNode = findNode(treeMaximum);
			} else {
				while (!(ID2 >= bestFitFoRightRange)) {
					rightNode = previous(bestFitFoRightRange, false);
					bestFitFoRightRange = rightNode.key;
				}
			}
			return rightNode;
		} else {
			return rightNode;
		}
	}

	/*
	 * Print the total count for IDs between ID1 and ID2 inclusively. Note, ID1
	 * ≤ ID2. Time complexity: O(log n + s) where s is the number of IDs in the
	 * range. NOTE: Since we're using and maintaining an order statistic
	 * augmented variable "subTreeCount" in each Event node in this RedBlackTree
	 * implementation, this query in fact only takes O(log n) time to complete,
	 * regardless of the number of IDs in the specified range ('s').
	 */
	void inRange(int ID1, int ID2) {
		if (ID1 == ID2) {
			// if leftRange equals rightRange, there is only one event
			// associated with that ID, so its "count" would be the active
			// events
			// in that range.
			TreeNode node = findNode(ID1);
			if (node == null) {
				System.out.println(0);
				return;
			} else {
				System.out.println(node.count);
				return;
			}
		} else {
			TreeNode leftNode = getRangeLeftNode(ID1), rightNode = getRangeRightNode(ID2);
			if ((leftNode == null || rightNode == null) || (leftNode.key > rightNode.key)) {
				System.out.println(0);
				return;
			} else if (leftNode == rightNode) {
				// if leftRange equals rightRange, there is only one event
				// associated with that ID, so its "count" would be the active
				// events
				// in that range.
				System.out.println(leftNode.count);
				return;
			}
			ID1 = leftNode.key;
			ID2 = rightNode.key;
			TreeNode leastCommonAncestor = leastCommonAncestor(ID1, ID2);
			int totalCountInRange = 0;

			/*
			 * We use the subTreeCount in each event node, to calculate the
			 * inRange count in O(log n) time as below.
			 */

			if (leastCommonAncestor != leftNode && leastCommonAncestor != rightNode) {
				totalCountInRange = leastCommonAncestor.count + leftNode.count
						+ getSubtreeEventCount(leftNode.rightChild) + rightNode.count
						+ getSubtreeEventCount(rightNode.leftChild);

				{
					TreeNode previous = leftNode;
					TreeNode temp = leftNode.parent;
					while (temp != leastCommonAncestor) {
						if (temp.key >= leftNode.key) { // Add these counts to
														// the
														// range
							totalCountInRange += temp.count;
							if (previous == temp.leftChild) {
								totalCountInRange += getSubtreeEventCount(temp.rightChild);
							}
						}
						previous = temp;
						temp = temp.parent;
					}
				}
				{
					TreeNode previous = rightNode;
					TreeNode temp = rightNode.parent;
					while (temp != leastCommonAncestor) {
						if (temp.key <= rightNode.key) { // Add these counts to
															// the
															// range
							totalCountInRange += temp.count;
							if (previous == temp.rightChild) {
								totalCountInRange += getSubtreeEventCount(temp.leftChild);
							}
						}
						previous = temp;
						temp = temp.parent;
					}
				}
			} else {
				// One of the selected nodes in the range is the least common
				// ancestor. So consider only one branch of it for the range
				// calculation.
				if (leftNode == leastCommonAncestor) {
					// leftNode is the common ancestor, consider only the right
					// tree of the leftNode.
					totalCountInRange = leftNode.count + rightNode.count + getSubtreeEventCount(rightNode.leftChild);
					TreeNode previous = rightNode;
					TreeNode temp = rightNode.parent;
					while (temp != leastCommonAncestor) {
						if (temp.key <= rightNode.key) { // Add these counts to
															// the
															// range
							totalCountInRange += temp.count;
							if (previous == temp.rightChild) {
								totalCountInRange += getSubtreeEventCount(temp.leftChild);
							}
						}
						previous = temp;
						temp = temp.parent;
					}
				} else {
					// rightNode is the common ancestor, consider only its left
					// tree
					totalCountInRange = rightNode.count + leftNode.count + getSubtreeEventCount(leftNode.rightChild);
					TreeNode previous = leftNode;
					TreeNode temp = leftNode.parent;
					while (temp != leastCommonAncestor) {
						if (temp.key >= leftNode.key) { // Add these counts to
														// the
														// range
							totalCountInRange += temp.count;
							if (previous == temp.leftChild) {
								totalCountInRange += getSubtreeEventCount(temp.rightChild);
							}
						}
						previous = temp;
						temp = temp.parent;
					}
				}
			}
			System.out.println(totalCountInRange);
		}
	}

	/*
	 * Return the Event node which is the smallest common ancestor in the tree
	 * for the given two event IDs.
	 */
	TreeNode leastCommonAncestor(int leftID, int rightID) {
		TreeNode temp = root;
		while (temp != null) {
			if (temp.key < leftID && temp.key < rightID) {
				temp = temp.rightChild;
			} else if (temp.key > leftID && temp.key > rightID) {
				temp = temp.leftChild;
			} else if (temp.key >= leftID && temp.key <= rightID) {
				break;
			} else {
				// break because that range is not in the tree.
				break;
			}
		}
		return temp;
	}

	/*
	 * Create a temporary NULL sentinal node and attach it to the parent node
	 * for rebalancing purposes while deletion. This should be deleted after the
	 * complete re-balance process it complete. This is used because there is no
	 * explicit NULL leaf sentinal nodes in this implementation of the Red Black
	 * Tree. For any Event node which has child as null, it's implied to be a
	 * null sentinal leaf of color "BLACK" (isRed= false)
	 */
	TreeNode getNullLeaf(TreeNode parent, boolean onRight) {
		TreeNode nullLeaf = new TreeNode(-1, -1);
		/*
		 * Initialized to -1 to indicate it's not an event node but a null
		 * sentinal leaf node. This is a safe assumption because IDs and counts
		 * (and therefore subTreeCounts) are assumed to be positive integers.
		 */
		nullLeaf.isRed = BLACK; // since all leaf sentinal nodes are black
		nullLeaf.parent = parent;
		if (onRight) {
			parent.rightChild = nullLeaf;
		} else {
			parent.leftChild = nullLeaf;
		}
		return nullLeaf;
	}

	/*
	 * Removes the null leaf from the RBT and removes its parent's references to
	 * it. To be called after the rebalancing has been performed and the added
	 * helper null sentinal leaf node is no longer required.
	 */
	void cleanIfNullLeaf(TreeNode node) {
		if (node.key == -1) {
			deleteNodeReferences(node);
		}
	}

	/*
	 * Binary search tree insert. Time complexity: O(log n). Maintain the value
	 * of the augmented variable "subtreeCount" in constant time as we go down
	 * the tree and place the newly inserted node. Also, call the insert1()
	 * function to check if the newly inserted node satisfies the RedBlack tree
	 * properties, and fix if not.
	 */
	void insert(int key, int count) {
		TreeNode newNode = new TreeNode(key, count);
		if (root != null) {
			TreeNode parent = null, tempNode = root;
			while (tempNode != null) {
				parent = tempNode;
				if (key < tempNode.key) {
					tempNode.subtreeCount += newNode.count; // increase count of
															// ancestors of the
															// newNode with it's
															// count when we go
															// down the tree.
					tempNode = tempNode.leftChild;
				} else {
					tempNode.subtreeCount += newNode.count; // increase count of
															// ancestors of the
															// newNode with it's
															// count when we go
															// down the tree.
					tempNode = tempNode.rightChild;
				}
			}
			if (key < parent.key) {
				parent.leftChild = newNode;
			} else {
				parent.rightChild = newNode;
			}
			newNode.parent = parent;
			if (newNode.key > treeMaximum) {
				treeMaximum = newNode.key;
			}
			if (newNode.key < treeMinimum) {
				treeMinimum = newNode.key;
			}
		} else {
			root = newNode;
			treeMinimum = newNode.key;
			treeMaximum = newNode.key;
		}
		insert1(newNode);
	}

	/*
	 * Find and return the ID of the event with the minimum ID. Time complexity:
	 * O(log n)
	 */
	int findMin() {
		if (root == null) {
			return -1;
		} else {
			TreeNode prev = root, temp = root.leftChild;
			while (temp != null) {
				prev = temp;
				temp = temp.leftChild;
			}
			return prev.key;
		}
	}

	/*
	 * Find and return the ID of the event with the maximum ID. Time complexity:
	 * O(log n)
	 */
	int findMax() {
		if (root == null) {
			return -1;
		} else {
			TreeNode prev = root, temp = root.rightChild;
			while (temp != null) {
				prev = temp;
				temp = temp.rightChild;
			}
			return prev.key;
		}
	}

	/*
	 * Returns the subtreeCount of the node if it exists, if not, return zero.
	 */
	int getSubtreeEventCount(TreeNode node) {
		if (node != null) {
			return node.subtreeCount;
		} else {
			return 0;
		}
	}

	/*
	 * Find node with the given ID, if not found (or if tree is empty), return
	 * null. Time complexity: O(log n)
	 */
	TreeNode findNode(int ID) {
		if (root != null) {
			TreeNode node = root;
			while (node != null && node.key != ID) {
				if (ID < node.key) {
					node = node.leftChild;
				} else {
					node = node.rightChild;
				}
			}
			if (node != null) { // node isn't null implies we've found the node
				return node;
			} else {
				return null; // node not found
			}
		}
		return null; // Tree is empty
	}

	/*
	 * Binary search tree delete after finding the node with the given key.
	 */
	void delete(int key) {
		TreeNode node = findNode(key);

		if (node != null) { // node isn't null implies we've found the node
							// to delete.
			deleteNode(node);
		}
	}

	/*
	 * deletes the given node. Also fixes RedBlackTree violations if any and
	 * calls the delete2() method if further fixes are required. Time
	 * complexity: O(log n)
	 */
	void deleteNode(TreeNode node) {
		if (node != null) {
			int deletedKey = node.key;
			if (node.leftChild != null && node.rightChild != null) {
				// CASE 2: 2 children: If the node has two children replace
				// node with its predecessor, and delete the predecessor
				// recursively.
				TreeNode predecessor = predecessor(node);
				replaceNode(node, predecessor);
				deleteNode(predecessor);
			} else {
				// CASE 3: Deletion of node with "utmost" one child. call
				// delete of red black tree IF the node being deleted is a black
				// node.
				// (if it's red, then no RBT properties are violated)
				boolean moreFixesRequired = false;
				TreeNode child = null;
				if (node.isRed == BLACK) {
					moreFixesRequired = !deleteFix1(node);
				}
				if (node.rightChild != null) {
					child = node.rightChild;
					// CASE 3.1: 1 child: if the node only has rightChild,
					// replace
					// node's parent
					// link to its child.
					if (node.parent == null) {
						root = child;
						moreFixesRequired = false;
					} else if (node.parent.rightChild == node) {
						node.parent.rightChild = child;
					} else {
						node.parent.leftChild = child;
					}
				} else {
					child = node.leftChild;
					// CASE 3.2: 2 child: if the node only has leftChild,
					// replace
					// node's parent
					// link to its child and vice-versa.
					if (node.parent == null) {
						root = child;
						moreFixesRequired = false;
					} else if (node.parent.rightChild == node) {
						if (child == null) {
							// If there are no children, add a null dummy leaf
							// so that re-balancing can take place if required,
							// then delete it.
							child = getNullLeaf(node.parent, true);
						}
						node.parent.rightChild = child;
					} else {
						if (child == null) {
							// If there are no children, add a null dummy leaf
							// so that re-balancing can take place if required,
							// then delete it.
							child = getNullLeaf(node.parent, false);
						}
						node.parent.leftChild = child;
					}
				}
				child.parent = node.parent;
				if (moreFixesRequired) {
					// If child replacing deleted node was previously black, and
					// not the current root
					delete2(child);
					cleanIfNullLeaf(child);
				}
				cleanIfNullLeaf(child);
			}
			if (deletedKey == treeMinimum) {
				treeMinimum = findMin();
			}
			if (deletedKey == treeMaximum) {
				treeMaximum = findMax();
			}
		}
	}

	/*
	 * The delete<i> methods (where 1<=i<=6, listed in order below) handles
	 * different cases of the RedBlack Tree delete scenarios. They all take
	 * constant time to run. Time complexity: O(1)
	 */
	/*
	 * Case 1: N is the new root. In this case, we are done. We removed one
	 * black node from every path, and the new root is black, so the properties
	 * are preserved.
	 */
	void delete1(TreeNode nodeN) {
		if (nodeN.parent != null) {
			delete2(nodeN);
		} else {
			root = nodeN;
		}
	}

	/*
	 * If child replacing deleted node was previously black, and not the current
	 * root. Case 2: S is red. In this case we reverse the colors of P and S,
	 * and then rotate left at P, turning S into N's grandparent. Note that P
	 * has to be black as it had a red child. The resulting subtree has a path
	 * short one black node so we are not done. Now N has a black sibling and a
	 * red parent, so we can proceed to step 4, 5, or 6. (Its new sibling is
	 * black because it was once the child of the red S.) In later cases, we
	 * will relabel N's new sibling as S.
	 */
	void delete2(TreeNode nodeN) {
		TreeNode nodeS = sibling(nodeN);
		if (nodeS.isRed == RED) {
			nodeN.parent.isRed = RED;
			nodeS.isRed = BLACK;
			if (nodeN == nodeN.parent.leftChild) {
				leftRotate(nodeN.parent);
			} else {
				rightRotate(nodeN.parent);
			}
		}
		delete3(nodeN);
	}

	/*
	 * Case 3: P, S, and S's children are black. In this case, we simply repaint
	 * S red. The result is that all paths passing through S, which are
	 * precisely those paths not passing through N, have one less black node.
	 * Because deleting N's original parent made all paths passing through N
	 * have one less black node, this evens things up. However, all paths
	 * through P now have one fewer black node than paths that do not pass
	 * through P, so property 5 (all paths from any given node to its leaf nodes
	 * contain the same number of black nodes) is still violated. To correct
	 * this, we perform the re-balancing procedure on P, starting at case 1.
	 */
	void delete3(TreeNode nodeN) {
		TreeNode nodeS = sibling(nodeN);
		if (nodeN.parent.isRed == BLACK && nodeS.isRed == BLACK
				&& (nodeS.leftChild == null || nodeS.leftChild.isRed == BLACK)
				&& (nodeS.rightChild == null || nodeS.rightChild.isRed == BLACK)) {
			// the children being null implies they're black (assume leaf null
			// nodes are BLACK)
			nodeS.isRed = RED;
			delete1(nodeN.parent);
		} else {
			delete4(nodeN);
		}
	}

	/*
	 * Case 4: S and S's children are black, but P is red. In this case, we
	 * simply exchange the colors of S and P. This does not affect the number of
	 * black nodes on paths going through S, but it does add one to the number
	 * of black nodes on paths going through N, making up for the deleted black
	 * node on those paths.
	 */
	void delete4(TreeNode nodeN) {
		TreeNode nodeS = sibling(nodeN);
		if (nodeN.parent.isRed == RED && nodeS.isRed == BLACK
				&& (nodeS.leftChild == null || nodeS.leftChild.isRed == BLACK)
				&& (nodeS.rightChild == null || nodeS.rightChild.isRed == BLACK)) {
			// the children being null implies they're black (assume leaf null
			// nodes are BLACK)
			nodeS.isRed = RED;
			nodeN.parent.isRed = BLACK;
		} else {
			delete5(nodeN);
		}
	}

	/*
	 * Case 5: S is black, S's left child is red, S's right child is black, and
	 * N is the left child of its parent. In this case we rotate right at S, so
	 * that S's left child becomes S's parent and N's new sibling. We then
	 * exchange the colors of S and its new parent. All paths still have the
	 * same number of black nodes, but now N has a black sibling whose right
	 * child is red, so we fall into case 6. Neither N nor its parent are
	 * affected by this transformation. (Again, for case 6, we relabel N's new
	 * sibling as S.)
	 */
	void delete5(TreeNode nodeN) {
		TreeNode nodeS = sibling(nodeN);
		if (nodeS.isRed == BLACK) {
			/*
			 * this if statement is trivial, due to case 2 (even though case 2
			 * changed the sibling to a sibling's child, the sibling's child
			 * can't be red, since no red parent can have a red child).
			 */
			/*
			 * the following statements just force the red to be on the left of
			 * the left of the parent, or right of the right, so case six will
			 * rotate correctly.
			 */
			if (nodeN == nodeN.parent.leftChild && (nodeS.rightChild == null || nodeS.rightChild.isRed == BLACK)
					&& (nodeS.leftChild != null && nodeS.leftChild.isRed == RED)) {
				// the children being null implies they're black (assume leaf
				// null
				// nodes are BLACK)
				/* this last test is trivial too due to cases 2-4. */
				nodeS.isRed = RED;
				nodeS.leftChild.isRed = BLACK;
				rightRotate(nodeS);
			} else if (nodeN == nodeN.parent.rightChild && (nodeS.leftChild == null || nodeS.leftChild.isRed == BLACK)
					&& (nodeS.rightChild != null && nodeS.rightChild.isRed == RED)) {
				// the children being null implies they're black (assume leaf
				// null
				// nodes are BLACK)
				/* this last test is trivial too due to cases 2-4. */
				nodeS.isRed = RED;
				nodeS.rightChild.isRed = BLACK;
				leftRotate(nodeS);
			}
		}
		delete6(nodeN);
	}

	/*
	 * Case 6: S is black, S's right child is red, and N is the left child of
	 * its parent P. In this case we rotate left at P, so that S becomes the
	 * parent of P and S's right child. We then exchange the colors of P and S,
	 * and make S's right child black. The subtree still has the same color at
	 * its root, so Properties 4 (Both children of every red node are black) and
	 * 5 (All paths from any given node to its leaf nodes contain the same
	 * number of black nodes) are not violated. However, N now has one
	 * additional black ancestor: either P has become black, or it was black and
	 * S was added as a black grandparent. Thus, the paths passing through N
	 * pass through one additional black node.
	 * 
	 * Meanwhile, if a path does not go through N, then there are two
	 * possibilities:
	 * 
	 * It goes through N's new sibling SL, a node with arbitrary color and the
	 * root of the subtree labeled 3 (s. diagram). Then, it must go through S
	 * and P, both formerly and currently, as they have only exchanged colors
	 * and places. Thus the path contains the same number of black nodes. It
	 * goes through N's new uncle, S's right child. Then, it formerly went
	 * through S, S's parent, and S's right child SR (which was red), but now
	 * only goes through S, which has assumed the color of its former parent,
	 * and S's right child, which has changed from red to black (assuming S's
	 * color: black). The net effect is that this path goes through the same
	 * number of black nodes. Either way, the number of black nodes on these
	 * paths does not change. Thus, we have restored Properties 4 (Both children
	 * of every red node are black) and 5 (All paths from any given node to its
	 * leaf nodes contain the same number of black nodes).
	 */
	void delete6(TreeNode nodeN) {
		TreeNode nodeS = sibling(nodeN);

		nodeS.isRed = nodeN.parent.isRed;
		nodeN.parent.isRed = BLACK;

		if (nodeN == nodeN.parent.leftChild) {
			nodeS.rightChild.isRed = BLACK;
			leftRotate(nodeN.parent);
		} else {
			nodeS.leftChild.isRed = BLACK;
			rightRotate(nodeN.parent);
		}
	}

	/*
	 * If the node to be deleted is black with ONE child, and the child is red,
	 * simply repaint the child black. This is a trivial case of the delete
	 * operation which is checked first.
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
	 * "Deletes" a node by removing all references to it and setting its parent
	 * reference to it as null;
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
	 * Copy the contents (event ID and count) of one node to another.
	 */
	void replaceNode(TreeNode replaceeNode, TreeNode replacerNode) {
		replaceeNode.key = replacerNode.key;
		replaceeNode.count = replacerNode.count;
	}

	/*
	 * Returns the successor of the node, i.e. the left-most child in it's right
	 * subtree. Time complexity: O(log n)
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

	/*
	 * Returns the grandparent of a node if available, i.e. parent of its
	 * parent.
	 */
	TreeNode grandparent(TreeNode node) {
		if (node != null && node.parent != null && node.parent.parent != null) {
			return node.parent.parent;
		} else {
			return null;
		}
	}

	/*
	 * Returns the uncle of a node if available, i.e. sibling of its parent.
	 */
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
	 * Returns the sibling of a node if available, i.e. other child of its
	 * parent.
	 */
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
	 * The insert<i> methods (where 1<=i<=5, listed in order below) handles
	 * different cases of the RedBlack Tree insert scenarios. They all take
	 * constant time to run. Time complexity: O(1)
	 */
	/*
	 * Case 1 of red-black tree insertion, node inserted is the first node, if
	 * so make it black.
	 */
	void insert1(TreeNode node) {
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
	 * grandparent's rightChild, then rotate left / right respectively. Maintain
	 * the value of the augmented variable subTreeCount if rotating.
	 */
	void insert4(TreeNode node) {
		TreeNode grandparent = grandparent(node);
		TreeNode parent = node.parent;
		if (grandparent.leftChild == parent && parent.rightChild == node) {
			// left-rotate
			parent.rightChild = node.leftChild;
			if (parent.rightChild != null) {
				parent.rightChild.parent = parent;
			}
			parent.parent = node;
			node.leftChild = parent;
			node.parent = grandparent;
			grandparent.leftChild = node;
			node = node.leftChild;
			int previousParentSubtreeCount = node.subtreeCount;
			/*
			 * since this "node" was the parent before rotation, and its right
			 * node moved above it to become its parent, we need to subtract
			 * that count. Therefore; previousParent's subtreeCount -=
			 * previousRightChild'sSubtreeCount -
			 * previousRightChild'sLeftChild'sSubtreeCount;
			 */
			node.subtreeCount -= node.parent.subtreeCount - getSubtreeEventCount(node.rightChild);
			node.parent.subtreeCount = previousParentSubtreeCount;
		} else if (grandparent.rightChild == parent && parent.leftChild == node) {
			// right-rotate
			parent.leftChild = node.rightChild;
			if (parent.leftChild != null) {
				parent.leftChild.parent = parent;
			}
			parent.parent = node;
			node.rightChild = parent;
			node.parent = grandparent;
			grandparent.rightChild = node;
			node = node.rightChild;
			int previousParentSubtreeCount = node.subtreeCount;
			/*
			 * since this "node" was the parent before rotation, and its left
			 * node moved above it to become its parent, we need to subtract
			 * that count. Therefore; previousParent's subtreeCount -=
			 * previousLeftChild'sSubtreeCount -
			 * previousLeftChild'sRightChild'sSubtreeCount;
			 */
			node.subtreeCount -= node.parent.subtreeCount - getSubtreeEventCount(node.leftChild);
			node.parent.subtreeCount = previousParentSubtreeCount;
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

	/*
	 * Left rotate is used to fix an unbalanced tree which is leaning towards
	 * the right. This takes constant time. Also we need to fix the augmented
	 * variable subTreeCount on rotate. Time complexity: O(1).
	 */
	void leftRotate(TreeNode node) {
		if (node != null && node.rightChild != null) {
			TreeNode rightChild = node.rightChild, grandparent = node.parent;
			node.rightChild = rightChild.leftChild;
			if (node.rightChild != null) {
				node.rightChild.parent = node;
			}
			node.parent = rightChild;
			rightChild.leftChild = node;
			rightChild.parent = grandparent;
			if (grandparent != null) {
				if (node == grandparent.leftChild) {
					grandparent.leftChild = rightChild;
				} else {
					grandparent.rightChild = rightChild;
				}
			} else {
				root = rightChild;
			}
			int previousParentSubtreeCount = node.subtreeCount;
			node.subtreeCount -= node.parent.subtreeCount - getSubtreeEventCount(node.rightChild);
			node.parent.subtreeCount = previousParentSubtreeCount;
		}
	}

	/*
	 * Right rotate is used to fix an unbalanced tree which is leaning towards
	 * the left. This takes constant time. Also we need to fix the augmented
	 * variable subTreeCount on rotate. Time complexity: O(1).
	 */
	void rightRotate(TreeNode node) {
		if (node != null && node.leftChild != null) {
			TreeNode leftChild = node.leftChild, grandparent = node.parent;
			node.leftChild = leftChild.rightChild;
			if (node.leftChild != null) {
				node.leftChild.parent = node;
			}
			node.parent = leftChild;
			leftChild.rightChild = node;
			leftChild.parent = grandparent;
			if (grandparent != null) {
				if (node == grandparent.leftChild) {
					grandparent.leftChild = leftChild;
				} else {
					grandparent.rightChild = leftChild;
				}
			} else {
				root = leftChild;
			}
			int previousParentSubtreeCount = node.subtreeCount;
			node.subtreeCount -= node.parent.subtreeCount - getSubtreeEventCount(node.leftChild);
			node.parent.subtreeCount = previousParentSubtreeCount;
		}
	}

	/*
	 * Helper function to show the tree.
	 */
	void printTree() {
		System.out.println("\n");
		recursivelyPrintTree(root, "");
		System.out.println("\n");
	}

	/*
	 * Recursive function which takes a sorted list and builds a RedBlack tree
	 * from it by recursively splitting the list into two at the middle, and
	 * assigning key, left and right based on the splits. This takes linear time
	 * as it visits each node only once. Also, the RedBlack tree property is
	 * maintained by coloring all the internal nodes at the last level red. This
	 * is done by checking if the current level is at the max level for that
	 * tree (comparing against log of the total number of nodes). Maintain the
	 * value of the augmented variable subTreeCount as well. Time complexity:
	 * O(n).
	 */
	TreeNode sortedArrayToRBBST(TreeNode arr[], int start, int end, int currentHeight, int maxHeight) {
		if (start > end) {
			return null;
		}
		// same as (start+end)/2, avoids overflow.
		int mid = start + (end - start) / 2;
		TreeNode node = arr[mid];
		node.leftChild = sortedArrayToRBBST(arr, start, mid - 1, currentHeight + 1, maxHeight);
		node.rightChild = sortedArrayToRBBST(arr, mid + 1, end, currentHeight + 1, maxHeight);
		if (node.leftChild != null) {
			node.subtreeCount += node.leftChild.subtreeCount;
			node.leftChild.parent = node;
		}
		if (node.rightChild != null) {
			node.subtreeCount += node.rightChild.subtreeCount;
			node.rightChild.parent = node;
		}
		if (currentHeight == maxHeight) {
			/*
			 * The RedBlack tree property is maintained by coloring all the
			 * internal nodes at the last level red. This is done by checking if
			 * the current level is at the max level for that tree (comparing
			 * against log of the total number of nodes)
			 */
			node.isRed = RED;
		}
		return node;
	}

	/*
	 * Find the log base 2 of a number using integer arithmetic.
	 */
	public static int log2(int n) {
		if (n <= 0)
			throw new IllegalArgumentException();
		return 31 - Integer.numberOfLeadingZeros(n);
	}

	/*
	 * Print the nodes of the tree in an in-order looking (left child towards
	 * the bottom and right child towards the top) fashion.
	 */
	private void recursivelyPrintTree(TreeNode node, String indentDots) {
		if (node != null) {
			recursivelyPrintTree(node.rightChild, indentDots + ".");
			System.out.println(indentDots + node.key + ", isRed=" + node.isRed + ", count=" + node.count
					+ ", subTreeCount= " + node.subtreeCount + "\n");
			// System.out.println(indentDots + node.key + "\n");
			recursivelyPrintTree(node.leftChild, indentDots + ".");
		}
	}
}
