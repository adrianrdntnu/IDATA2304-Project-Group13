package no.ntnu.group13.greenhouse.logic;

import java.util.ArrayList;
import java.util.List;

import static no.ntnu.group13.greenhouse.logic.LOGIC.round;

/**
 * Binary search tree to store data.
 * Some code copied from:
 * https://www.geeksforgeeks.org/binary-search-tree-set-1-search-and-insertion/
 */
public class BinarySearchTree {

  /**
   * Node class holding information about current node's value, left and right children
   */
  class Node {
    double key;
    Node left;
    Node right;

    public Node(double item) {
      key = item;
      left = right = null;
    }
  }

  Node root; // Root of BST

  // Constructors
  public BinarySearchTree() {
    root = null;
  }

  public BinarySearchTree(double value) {
    root = new Node(value);
  }

  // This method mainly calls insertRec()
  public void insert(double key) {
    root = insertRec(root, key);
  }

  /**
   * Recursive method for inserting a new node to the BST
   *
   * @param root Current node being recursed
   * @param key New node's value
   * @return The root
   */
  Node insertRec(Node root, double key) {

        /* If the tree is empty,
           return a new node */
    if (root == null) {
      root = new Node(key);
      return root;
    }

    /* Otherwise, recur down the tree */
    else if (key < root.key)
      root.left = insertRec(root.left, key);
    else if (key > root.key)
      root.right = insertRec(root.right, key);

    /* return the (unchanged) node pointer */
    return root;
  }

  /**
   * Returns the average value of the binary search tree using morris traversal.
   * Some code copied from:
   * https://www.geeksforgeeks.org/inorder-tree-traversal-without-recursion-and-without-stack/
   *
   * @return The average value
   */
  public double getAverageValue() {
    Node current;
    Node pre;
    double sum = 0;
    int count = 0;

    if (root == null)
      return 0;

    current = root;
    while (current != null) {
      if (current.left == null) {
        sum += current.key;
        count++;
        current = current.right;
      } else {
                /* Find the inorder
                    predecessor of current
                 */
        pre = current.left;
        while (pre.right != null
            && pre.right != current)
          pre = pre.right;

                /* Make current as right
                   child of its
                 * inorder predecessor */
        if (pre.right == null) {
          pre.right = current;
          current = current.left;
        }

                /* Revert the changes made
                   in the 'if' part
                   to restore the original
                   tree i.e., fix
                   the right child of predecessor*/
        else {
          pre.right = null;
          sum += current.key;
          count++;
          current = current.right;
        } /* End of if condition pre->right == NULL
         */

      } /* End of if condition current->left == NULL*/

    } /* End of while */
    double average = sum/count;
    return round(average, 3);
  }

  /**
   * Returns the max value in the binary search tree
   *
   * @return The max value
   */
  public double getMaxValue() {
    Node currentNode = root;
    while (currentNode.right != null) {
      currentNode = currentNode.right;
    }
    return currentNode.key;
  }

  /**
   * Returns the min value in the binary search tree
   *
   * @return The min value
   */
  public double getMinValue() {
    Node currentNode = root;
    while (currentNode.left != null) {
      currentNode = currentNode.left;
    }
    return currentNode.key;
  }

  /**
   * Returns a list of all values sorted from highest to lowest.
   *
   * @return A list of all values sorted from highest to lowest
   */
  public List<Double> getListBigToSmall() {
    List<Double> list = new ArrayList<>();
    sortBigToSmall(root, list);
    return list;
  }

  /**
   * Returns a list of all values sorted from lowest to highest.
   *
   * @return A list of all values sorted from lowest to highest
   */
  public List<Double> getListSmallToBig() {
    List<Double> list = new ArrayList<>();
    sortSmallToBig(root, list);
    return list;
  }

  /**
   * Uses traversal method to go through the tree from the biggest value to smallest and
   * adds the value to a list.
   *
   * @param root node to start traversal from
   * @param list list to add the values
   */
  private void sortBigToSmall(Node root, List<Double> list) {
    if (root != null) {
      sortBigToSmall(root.right, list);
      list.add(root.key);
      sortBigToSmall(root.left, list);
    }
  }

  /**
   * Uses traversal method to go through the tree from the smallest value to biggest and
   * adds the value to a list.
   *
   * @param root node to start traversal from
   * @param list list to add the values
   */
  private void sortSmallToBig(Node root, List<Double> list) {
    if (root != null) {
      sortSmallToBig(root.left, list);
      list.add(root.key);
      sortSmallToBig(root.right, list);
    }
  }

  /**
   * Returns the root node of the tree.
   *
   * @return root node of the tree
   */
  public Node getRootNode() {
    return root;
  }

  /**
   * Used for simple quick testing.
   */
  public static void main(String[] args) {
    BinarySearchTree tree = new BinarySearchTree();

        /* Let us create following BST
              50
           /     \
          30      70
         /  \    /  \
       20   40  60   80 */
    tree.insert(50);
    tree.insert(30);
    tree.insert(20);
    tree.insert(40);
    tree.insert(70);
    tree.insert(60);
    tree.insert(80);

    System.out.println(tree.getAverageValue());
  }
}
