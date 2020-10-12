package hk.edu.hkbu.comp;

import java.util.HashMap;
import java.util.Vector;

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
  
    Node insert(Node node,String url,String title, String key, int position) { 
        if (node == null) 
            return (new Node(url,title, key,position) ); 
  
        if (key.compareTo(node.key)<0) 
            node.left = insert(node.left,url,title, key, position); 
        else if (key.compareTo(node.key)>0) 
            node.right = insert(node.right,url,title, key, position); 
        else {
        	String[] array = {url,title};
        	node.addPosition(array,position);
        	//System.out.println(node.key +": "+node.getPosition().toString());
            return node; 
        }
        node.height = 1 + max(height(node.left), 
                              height(node.right)); 
  
        int balance = getBalance(node); 
  
        // Left Left Case 
        if (balance > 1 && key.compareTo(node.left.key)<0) {
            System.out.println("Left Left Case ");
        	return rightRotate(node); 
        }
        // Right Right Case 
        if (balance < -1 && key.compareTo(node.right.key)<0) {
            System.out.println("Right Right Case ");
            return leftRotate(node); 
        }
        // Left Right Case 
        if (balance > 1 && key.compareTo(node.left.key)>0) { 
        	System.out.println("Left Right Case ");
            node.left = leftRotate(node.left); 
            System.out.println("Left Right Case r");
            return rightRotate(node); 
        } 
  
        // Right Left Case 
        if (balance < -1 && key.compareTo(node.right.key)>0) { 
        	System.out.println("Right Left Case ");
            node.right = rightRotate(node.right); 
            System.out.println("Right Left Case  l");
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

class Node { 
    int height; 
    String key;
    HashMap<String[], Vector<Integer>> map = new HashMap<String[], Vector<Integer>>();
    Node left, right; 
  
    Node(String url, String title, String value, int position) { 
        key = value; 
        Vector<Integer> tmp;
        String[] key = {url,title};
        if(map.containsKey(key))
        	addPosition(key,position);
        else {
        	tmp = new Vector<Integer>();
        	tmp.add(position);
        	map.put(key,tmp);
        }
        height = 1; 
    } 
    
    public void addPosition(String[] key,int position) {
    	map.get(key).add(position);
    }
    
    public Vector<Integer> getPosition(String url){
    	return map.get(url);
    }
} 