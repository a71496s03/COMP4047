package hk.edu.hkbu.comp;

import java.util.Vector;

class Node { 
    int height; 
    String key;
    Vector<Integer> position;
    Node left, right; 
  
    Node(String value) { 
        key = value; 
        height = 1; 
    } 
    
    public void addPosition(int position) {
    	this.position.add(position);
    }
    
    public Vector<Integer> getPosition(){
    	return position;
    }
} 
  
class AVLTree { 
  
    Node root; 
  
    int height(Node N) { 
        if (N == null) 
            return 0; 
  
        return N.height; 
    } 
  
    int max(int a, int b) { 
        return (a > b) ? a : b; 
    } 
  
    Node rightRotate(Node y) { 
        Node x = y.left; 
        Node z = x.right; 
   
        x.right = y; 
        y.left = z; 
  
        y.height = max(height(y.left), height(y.right)) + 1; 
        x.height = max(height(x.left), height(x.right)) + 1; 
  
        return x; 
    } 
  
    Node leftRotate(Node x) { 
        Node y = x.right; 
        Node z = y.left; 
  
        y.left = x; 
        x.right = z; 
  
        x.height = max(height(x.left), height(x.right)) + 1; 
        y.height = max(height(y.left), height(y.right)) + 1; 
  
        return y; 
    } 
  
    int getBalance(Node N) { 
        if (N == null) 
            return 0; 
  
        return height(N.left) - height(N.right); 
    } 
  
    Node insert(Node node, String key, int position) { 
    	
        if (node == null) 
            return (new Node(key)); 
  
        if (key.compareTo(node.key)<0) 
            node.left = insert(node.left, key, position); 
        else if (key.compareTo(node.key)>0) 
            node.right = insert(node.right, key, position); 
        else {
        	node.addPosition(position);
            return node; 
        }
        node.height = 1 + max(height(node.left), 
                              height(node.right)); 
  
        int balance = getBalance(node); 
  
        // Left Left Case 
        if (balance > 1 && key.compareTo(node.left.key)<0) 
            return rightRotate(node); 
  
        // Right Right Case 
        if (balance < -1 && key.compareTo(node.right.key)<0) 
            return leftRotate(node); 
  
        // Left Right Case 
        if (balance > 1 && key.compareTo(node.left.key)>0) { 
            node.left = leftRotate(node.left); 
            return rightRotate(node); 
        } 
  
        // Right Left Case 
        if (balance < -1 && key.compareTo(node.right.key)>0) { 
            node.right = rightRotate(node.right); 
            return leftRotate(node); 
        } 
  
        return node; 
    } 
    
    void preOrder(Node node) { 
        if (node != null) { 
            System.out.print(node.key + " "); 
            preOrder(node.left); 
            preOrder(node.right); 
        } 
    } 
}